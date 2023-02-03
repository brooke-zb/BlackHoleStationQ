package com.brookezb.bhs.app.task;

import com.brookezb.bhs.service.ArticleService;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CaffeineCache;
import io.quarkus.logging.Log;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Set;

/**
 * 定时任务: 持久化文章阅读量
 *
 * @author brooke_zb
 */
public class PersistArticleViewsTask {
    @Inject
    ArticleService articleService;

    @CacheName("article-views-cache")
    Cache cache;

    void shutdown(@Observes ShutdownEvent ev) {
        persistArticleViews().await().indefinitely(); // 应用关闭前等待持久化完成
    }

    @Scheduled(cron = "${bhs.task.cron.persist-article-views:0 0 0 * * ?}")
    Uni<Void> persistArticleViews() {
        var caffeineCache = cache.as(CaffeineCache.class);
        Log.info("start to persist article views");
        return Multi.createFrom().iterable(caffeineCache.keySet())
                .call(key -> caffeineCache.<Object, Set<String>>get(key, _key -> null)
                        .call(views -> caffeineCache.invalidate(key))
                        .call(views -> articleService.updateViews((Long) key, views.size()))
                        .invoke(views -> Log.infof("- persistence success: id=%d, increment=%d", key, views.size()))
                )
                .collect().asList()
                .invoke(keys -> Log.infof("persistence over, persist %d article(s) in total", keys.size()))
                .replaceWithVoid();
    }
}
