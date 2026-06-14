package com.grey.rdv_manager_api;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;
//202606 add to intialize variable List
import java.util.List;

@SpringBootApplication(scanBasePackages = "com.grey.rdv_manager_api")
@EnableMongoRepositories
public class RdvManagerApi {
    public static void main(String[] args) {
        SpringApplication.run(RdvManagerApi.class, args);
    }

    @Bean
    @SuppressWarnings("unchecked")
    public FilterRegistrationBean simpleCorsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        //Origin settings are disabled
        //config.setAllowCredentials(true);
        //config.setAllowedOrigins(Collections.singletonList("*"));
        //config.setAllowedMethods(Collections.singletonList("*"));
        //config.setAllowedHeaders(Collections.singletonList("*"));

        //202606 New config insert here for modern browsers
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of(
            "http://localhost:3000",   // local frontend dev
            "http://localhost:4200",   // Angular default
            "https://yourdomain.com"   // production — replace this
        ));
        // 202606 explicit methods instead of wildcard
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        // 202606 explicit headers instead of wildcard
        config.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));
        //end new part
        
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}