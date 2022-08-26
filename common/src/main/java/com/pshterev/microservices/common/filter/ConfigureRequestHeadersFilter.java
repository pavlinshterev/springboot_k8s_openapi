package com.pshterev.microservices.common.filter;

import com.pshterev.microservices.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConfigureRequestHeadersFilter extends OncePerRequestFilter {

   private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureRequestHeadersFilter.class);

   private final Map<String, List<String>> newHeaderValuesMap;

   public ConfigureRequestHeadersFilter(Map<String, List<String>> newHeaderValuesMap) {
      LOGGER.info("Created ConfigureRequestHeadersFilter");
      this.newHeaderValuesMap = newHeaderValuesMap;
   }

   @Override
   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
           throws ServletException, IOException {

      if (!request.getRequestURI().contains("actuator")) {

         LOGGER.debug("Original request headers:");
         LOGGER.debug(StringUtil.getHeadersStr(request));

         ServletRequestWrapper newRequest = new ServletRequestWrapper(request);
         newHeaderValuesMap.forEach((header, values) -> {
            LOGGER.debug("Changing request header '{}' from: {}, to: {}.",
                    header,
                    StringUtil.toString(newRequest.getHeaders(header), ","),
                    StringUtils.toStringArray(values));
            newRequest.headers.put(header, values);
         });

         LOGGER.debug("Modified request headers:");
         LOGGER.debug(StringUtil.getHeadersStr(newRequest));

         filterChain.doFilter(newRequest, response);
      } else {
         filterChain.doFilter(request, response);
      }
   }

   private static class ServletRequestWrapper extends HttpServletRequestWrapper {

      private final Map<String, List<String>> headers;

      public ServletRequestWrapper(HttpServletRequest request) {
         super(request);
         this.headers = initHeaders(request);
      }

      private static Map<String, List<String>> initHeaders(HttpServletRequest request) {
         Map<String, List<String>> headers = new LinkedCaseInsensitiveMap<>(Locale.ENGLISH);
         Enumeration<String> names = request.getHeaderNames();
         while (names.hasMoreElements()) {
            String name = names.nextElement();
            headers.put(name, Collections.list(request.getHeaders(name)));
         }
         return headers;
      }

      @Override
      @Nullable
      public String getHeader(String name) {
         List<String> value = this.headers.get(name);
         return (CollectionUtils.isEmpty(value) ? null : value.get(0));
      }

      @Override
      public Enumeration<String> getHeaders(String name) {
         List<String> value = this.headers.get(name);
         return (Collections.enumeration(value != null ? value : Collections.emptySet()));
      }

      @Override
      public Enumeration<String> getHeaderNames() {
         return Collections.enumeration(this.headers.keySet());
      }

      @Override
      public int getIntHeader(String name) {
         return Integer.valueOf(getHeader(name));
      }
   }

}
