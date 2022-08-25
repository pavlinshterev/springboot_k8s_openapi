package com.pshterev.microservices.authorservice.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {
   // TODO change to false based on certain criteria to report bad status
   private boolean isHealthy = true;

   public CustomHealthIndicator() {
   }

   @Override
   public Health health() {
      return isHealthy ? Health.up().build() : Health.down().build();
   }
}