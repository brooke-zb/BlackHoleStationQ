package com.brookezb.bhs.service.config;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import io.quarkus.runtime.Startup;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author brooke_zb
 */
public class SensitiveWordConfiguration {
    @Startup
    @ApplicationScoped
    public SensitiveWordBs getSensitiveWords() {
        return SensitiveWordBs.newInstance()
                .enableEmailCheck(false)
                .init();
    }
}
