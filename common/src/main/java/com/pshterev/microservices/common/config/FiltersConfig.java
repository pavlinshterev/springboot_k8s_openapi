package com.pshterev.microservices.common.config;

import com.pshterev.microservices.common.filter.ConfigureRequestHeadersFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class FiltersConfig {

   private static Logger LOGGER = LoggerFactory.getLogger(FiltersConfig.class);

   @Bean
   public FilterRegistrationBean<ConfigureRequestHeadersFilter> configureRequestHeadersFilter(
           @Value("${server.port:1}") String serverPort) {
      LOGGER.info(">>>> Creating ConfigureRequestHeadersFilter <<<<");
      Map<String, List<String>> newHeaderValuesMap = new HashMap<>();
      newHeaderValuesMap.put("x-forwarded-port", List.of(serverPort));

      ConfigureRequestHeadersFilter filter = new ConfigureRequestHeadersFilter(newHeaderValuesMap);
      FilterRegistrationBean<ConfigureRequestHeadersFilter> registration = new FilterRegistrationBean<>(filter);
      registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR);
      registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
      registration.setUrlPatterns(List.of("/*"));
      return registration;
   }

}