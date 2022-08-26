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

      // nginx ingress controller sets the x-forwarded-port header value to the port on which
      // the nginx listens which is not the same as the node port (that is the port on which the
      // APIs are publicly available). By manually overriding the value for 'x-forwarded-port'
      // header we help Spring Data Rest to produce correct REST links. The value that we use is
      // the 'server.port' property from Spring configuration (which is the port on which the
      // Tomcat server is listening inside the container) and for convenience this is the same
      // port on which the nginx load balancer listens to and which is used to access the services
      // from outside the K8s cluster machine

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