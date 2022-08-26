package com.pshterev.microservices.common.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class StringUtil {

   public static String toString(Enumeration<?> enumeration, String separator) {
      StringBuilder sb = new StringBuilder();
      if (enumeration.hasMoreElements()) {
         sb.append(enumeration.nextElement());
      }
      while (enumeration.hasMoreElements()) {
         sb.append(separator).append(enumeration.nextElement());
      }
      return sb.toString();
   }

   public static String getHeadersStr(HttpServletRequest request) {
      StringBuilder sb = new StringBuilder();
      sb.append("\n");
      Enumeration<String> headerNames = request.getHeaderNames();
      while (headerNames.hasMoreElements()) {
         String header = headerNames.nextElement();
         sb.append(header).append(" : ").append(toString(request.getHeaders(header), ",")).append("\n");
      }
      return sb.toString();
   }
}
