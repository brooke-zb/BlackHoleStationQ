package com.brookezb.bhs.security.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.util.List;

/**
 * @author brooke_zb
 */
@ConfigMapping(prefix = "bhs.security.csrf")
public interface CSRFConfig {
    @WithName("token-secret")
    @WithDefault("o4$j04=?lwi`eZ#w`L1/QmTQkjV9e=}l")
    String secret();

    @WithName("include-prefixes")
    List<String> includePrefixes();

    @WithName("exclude-paths")
    List<String> excludePaths();
}