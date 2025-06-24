package com.habithustle.habithustle_backend.util;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;


import java.io.IOException;
import java.util.Properties;

@org.springframework.context.annotation.Configuration
public class ImageKitConfig {
    @Value("${imagekit.public-key}")
    private String publicKey;

    @Value("${imagekit.private-key}")
    private String privateKey;

    @Value("${imagekit.endpoint}")
    private String urlEndpoint;

    @Bean
    public ImageKit imageKit() {
        Configuration config = new Configuration(publicKey, privateKey, urlEndpoint);
        ImageKit imageKit = ImageKit.getInstance();
        imageKit.setConfig(config);
        return imageKit;
    }
}
