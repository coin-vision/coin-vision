package net.coinshome.coinvision.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:app_config-${spring.profiles.active}.properties")
@ConfigurationProperties(prefix = "coinvision")
public class AppConfig {

    private String dnnRootDir;

    public AppConfig() {
    }

    public String getDnnRootDir() {
        return dnnRootDir;
    }

    public void setDnnRootDir(String dnnRootDir) {
        this.dnnRootDir = dnnRootDir;
    }

}
