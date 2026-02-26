package com.cathaybk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * REST Client Configuration
 * 配置 RestTemplate 用於呼叫外部 API
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // 設定連線逾時時間：10 秒
        factory.setConnectTimeout(10000);

        // 設定讀取逾時時間：10 秒
        factory.setReadTimeout(10000);

        return new RestTemplate(factory);
    }
}

