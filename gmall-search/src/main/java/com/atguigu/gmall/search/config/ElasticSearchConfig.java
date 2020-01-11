package com.atguigu.gmall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lzzzzz
 * @create 2020-01-10 18:48
 */
@Configuration
public class ElasticSearchConfig {

    @Bean
    public RestHighLevelClient getRestHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.190.128", 9200, "http")));
    }
}
