//package com.core_sync.agile_service.config;
//
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import org.springdoc.core.models.GroupedOpenApi;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class OpenApiConfig {
//
//    @Bean
//    public OpenAPI baseOpenAPI() {
//        return new OpenAPI()
//                .info(new Info()
//                        .title("Attack On Monday API")
//                        .description("Final Project Backend OpenAPI 문서")
//                        .version("v1.0.0"));
//    }
//
//    // 필요에 따라 경로/패키지 맞춰 그룹 추가
//    @Bean
//    public GroupedOpenApi accountGroup() {
//        return GroupedOpenApi.builder()
//                .group("account")
//                .pathsToMatch("/api/accounts/**")
//                .build();
//    }
//
//    @Bean
//    public GroupedOpenApi interviewGroup() {
//        return GroupedOpenApi.builder()
//                .group("interview")
//                .pathsToMatch("/api/interviews/**")
//                .build();
//    }
//}
