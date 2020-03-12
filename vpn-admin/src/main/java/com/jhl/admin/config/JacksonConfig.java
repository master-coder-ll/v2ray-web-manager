package com.jhl.admin.config;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

public class JacksonConfig extends WebMvcConfigurerAdapter {

/*@Bean
@Primary
public MappingJackson2HttpMessageConverter jacksonMessageConverter(){
    MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
    ObjectMapper mapper = new ObjectMapper();
    Hibernate5Module module = new Hibernate5Module();
    module.enable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);
    mapper.registerModule(module);
    messageConverter.setObjectMapper(mapper);
    return messageConverter;
}*/
}