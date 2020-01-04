package com.jhl.admin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
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