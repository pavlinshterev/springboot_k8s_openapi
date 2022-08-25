package com.pshterev.microservices.bookservice;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DebugUtil {
   private static final Map<String, AtomicInteger> invocations = new ConcurrentHashMap<>();

   public static int inv(String requestPath) {
      if (requestPath == null || requestPath.length() == 0) {
         throw new RuntimeException("requestPath cannot be empty");
      }
      return invocations.computeIfAbsent(requestPath, (k) -> new AtomicInteger(0)).incrementAndGet();
   }
}
