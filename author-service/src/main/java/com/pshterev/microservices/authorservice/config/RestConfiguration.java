package com.pshterev.microservices.authorservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Type;

@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {

   private static Logger LOGGER = LoggerFactory.getLogger(RestConfiguration.class);

   @Autowired
   private EntityManager entityManager;

   @Override
   public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
      Class[] classes = entityManager.getMetamodel()
              .getEntities().stream().map(Type::getJavaType).toArray(Class[]::new);

      LOGGER.info("Expose Ids for classes: {}", classes);
      config.exposeIdsFor(classes);
   }

}