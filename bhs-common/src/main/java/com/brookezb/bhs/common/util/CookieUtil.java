package com.brookezb.bhs.common.util;

import io.vertx.core.http.Cookie;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 * @author brooke_zb
 */
public class CookieUtil {
    private static final String domain;
    private static final boolean secure;

    static {
        domain = ConfigProvider.getConfig().getValue("bhs.cookie.domain", String.class);
        secure = ConfigProvider.getConfig().getValue("bhs.cookie.secure", Boolean.class);
    }

    public static Cookie from(String name, String value) {
        return Cookie.cookie(name, value)
                .setDomain(domain)
                .setSecure(secure)
                .setPath("/");
    }
}
