package com.brookezb.bhs.mail.config;

import com.brookezb.bhs.mail.constant.MailConstants;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

/**
 * @author brooke_zb
 */
@ConfigMapping(prefix = "bhs.mail")
public interface MailServiceConfig {
    @WithName("admin-mail")
    String adminMail();

    @WithName("reply-subject")
    String replySubject();

    @WithName("audit-subject")
    String auditSubject();

    @WithName("reply-title")
    @WithDefault(MailConstants.Reply.TITLE)
    String replyTitle();

    @WithName("reply-content-format")
    @WithDefault(MailConstants.Reply.CONTENT_FORMAT)
    String replyContentFormat();

    @WithName("audit-title")
    @WithDefault(MailConstants.Audit.TITLE)
    String auditTitle();

    @WithName("audit-content")
    @WithDefault(MailConstants.Audit.CONTENT)
    String auditContent();
}
