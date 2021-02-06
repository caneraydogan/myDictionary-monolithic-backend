package com.caner.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;


public class WebMvcConfigurerAdapterImpl extends WebMvcConfigurerAdapter {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter(mapper);
        converters.add(messageConverter);
        super.configureMessageConverters(converters);
    }
}
