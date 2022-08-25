package com.pshterev.microservices.bookservice.webfilter;

import com.pshterev.microservices.bookservice.DebugUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
@Component
public class InvocationsFilter implements Filter {

   private static final Logger LOGGER = LoggerFactory.getLogger(InvocationsFilter.class);

   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
           throws IOException, ServletException {

      HttpServletRequest httpRequest = (HttpServletRequest) request;
      String path = httpRequest.getRequestURI();
      LOGGER.debug("request path: {}", httpRequest.getRequestURI());
      HttpServletResponse httpServletResponse = (HttpServletResponse) response;
      httpServletResponse.setHeader("num_invocations", DebugUtil.inv(path) + "_for_" + path);
      filterChain.doFilter(request, response);
   }
}