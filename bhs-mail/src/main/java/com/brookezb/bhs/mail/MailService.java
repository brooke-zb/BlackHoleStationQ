package com.brookezb.bhs.mail;

import com.brookezb.bhs.mail.config.MailServiceConfig;
import com.brookezb.bhs.mail.constant.MailConstants;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.io.IOUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author brooke_zb
 */
@ApplicationScoped
@JBossLog(topic = "bhs-mail")
public class MailService {
    @Inject
    ReactiveMailer mailer;
    MailServiceConfig config;
    private final String template;
    private final String[] templateKeyword = {"${title}", "${content}", "${link}", "${admin}"};

    public MailService(MailServiceConfig config) throws IOException {
        this.config = config;
        try {
            this.template = IOUtils.resourceToString("/templates/mail.html", StandardCharsets.UTF_8, MailConstants.class.getClassLoader());
        } catch (IOException e) {
            throw new IOException("邮件服务初始化失败: " + e.getMessage());
        }
    }

    public void sendReplyMail(String to, String nickname, String link) {
        String cleanNickname = nickname.replace("<", "&lt;").replace(">", "&gt;");
        var map = Map.of(
                templateKeyword[0], config.replyTitle(),
                templateKeyword[1], String.format(config.replyContentFormat(), cleanNickname),
                templateKeyword[2], link,
                templateKeyword[3], config.adminMail()
        );
        mailer.send(Mail.withHtml(to, config.replySubject(), parseTemplate(map)))
                .subscribe().with(
                        ignored -> log.info("reply mail send success"),
                        failure -> log.error("reply mail send fail: " + failure.getMessage())
                );
    }

    public void sendAuditMail(String link) {
        var map = Map.of(
                templateKeyword[0], config.auditTitle(),
                templateKeyword[1], config.auditContent(),
                templateKeyword[2], link,
                templateKeyword[3], config.adminMail()
        );
        mailer.send(Mail.withHtml(config.adminMail(), config.auditSubject(), parseTemplate(map)))
                .subscribe().with(
                        ignored -> log.info("audit mail send success"),
                        failure -> log.error("audit mail send fail: " + failure.getMessage())
                );
    }

    private String parseTemplate(Map<String, String> templateMap) {
        // 模板内容替换
        String result = template;
        for (var entry : templateMap.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
