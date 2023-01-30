package com.brookezb.bhs.mail;

import com.brookezb.bhs.mail.constant.MailConstants;
import com.brookezb.bhs.mail.config.MailServiceConfig;
import io.quarkus.logging.Log;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
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
                        ignored -> Log.info("回复邮件发送成功"),
                        failure -> Log.error("回复邮件发送失败: " + failure.getMessage())
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
                        ignored -> Log.info("审核邮件发送成功"),
                        failure -> Log.error("审核邮件发送失败: " + failure.getMessage())
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
