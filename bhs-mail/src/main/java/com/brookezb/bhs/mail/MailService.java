package com.brookezb.bhs.mail;

import com.brookezb.bhs.mail.config.MailServiceConfig;
import io.quarkus.mailer.MailTemplate;
import io.quarkus.qute.Location;
import lombok.extern.jbosslog.JBossLog;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author brooke_zb
 */
@ApplicationScoped
@JBossLog(topic = "bhs-mail")
public class MailService {
    @Inject
    MailServiceConfig config;

    @Inject
    @Location("mail")
    MailTemplate template;

    public void sendReplyMail(String to, String nickname, Long articleId) {
        String cleanNickname = nickname.replace("<", "&lt;").replace(">", "&gt;");
        template.to(to).subject(config.replySubject())
                .data("title", config.replyTitle())
                .data("content", String.format(config.replyContentFormat(), cleanNickname))
                .data("link", config.blogLink() + "/articles/" + articleId)
                .data("admin", config.adminMail())
                .send().subscribe().with(
                        ignored -> log.info("reply mail send success"),
                        failure -> log.error("reply mail send fail: " + failure.getMessage())
                );
    }

    public void sendAuditMail() {
        template.to(config.adminMail()).subject(config.auditSubject())
                .data("title", config.auditTitle())
                .data("content", config.auditContent())
                .data("link", config.blogLink() + "/admin/comments")
                .data("admin", config.adminMail())
                .send().subscribe().with(
                        ignored -> log.info("audit mail send success"),
                        failure -> log.error("audit mail send fail: " + failure.getMessage())
                );
    }
}
