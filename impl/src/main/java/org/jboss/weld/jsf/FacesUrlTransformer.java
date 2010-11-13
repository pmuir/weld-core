/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.jsf;

import javax.faces.context.FacesContext;

/**
 * Helper class for preparing JSF URLs which include the conversation id.
 * 
 * TODO This class has the potential to be better designed to make it fit more
 * use cases.
 * 
 * @author Nicklas Karlsson
 * @author Dan Allen
 */
public class FacesUrlTransformer
{

   private static final String SKIP_TRANSFORM = FacesUrlTransformer.class.getName() + ".skipTransform";

   private static final String HTTP_PROTOCOL_URL_PREFIX = "http://";
   private static final String HTTPS_PROTOCOL_URL_PREFIX = "https://";
   private static final String QUERY_STRING_DELIMITER = "?";
   private static final String PARAMETER_PAIR_DELIMITER = "&";
   private static final String PARAMETER_ASSIGNMENT_OPERATOR = "=";

   private String url;
   private final FacesContext context;

   public FacesUrlTransformer(String url, FacesContext facesContext)
   {
      this.url = url;
      this.context = facesContext;
   }

   public FacesUrlTransformer appendConversationIdIfNecessary(String cidParameterName, String cid)
   {
      appendParameterIfNeeded(url, cidParameterName, cid);
      return this;
   }

   private static String appendParameterIfNeeded(String url, String parameterName, String parameterValue)
   {
      int queryStringIndex = url.indexOf(QUERY_STRING_DELIMITER);
      // if there is no query string or there is a query string but the param is
      // absent, then append it
      if (queryStringIndex < 0 || url.indexOf(parameterName + PARAMETER_ASSIGNMENT_OPERATOR, queryStringIndex) < 0)
      {
         StringBuilder builder = new StringBuilder(url);
         if (queryStringIndex < 0)
         {
            builder.append(QUERY_STRING_DELIMITER);
         }
         else
         {
            builder.append(PARAMETER_PAIR_DELIMITER);
         }
         builder.append(parameterName).append(PARAMETER_ASSIGNMENT_OPERATOR);
         if (parameterName != null)
         {
            builder.append(parameterValue);
         }
         return builder.toString();
      }
      else
      {
         return url;
      }
   }

   private static String removeParameterIfNeeded(String url, String parameterName)
   {
      int queryStringIndex = url.indexOf(QUERY_STRING_DELIMITER);
      // if there is no query string or there is a query string but the param is
      // absent, then append it
      if (queryStringIndex > 0)
      {
         int parameterIndex = url.indexOf(parameterName + PARAMETER_ASSIGNMENT_OPERATOR, queryStringIndex);
         if (parameterIndex > 0)
         {
            StringBuilder builder = new StringBuilder();
            builder.append(url.substring(0, parameterIndex));
            int nextParameterIndex = url.indexOf("=", parameterIndex) + 1;
            builder.append(url.substring(nextParameterIndex));
            return builder.toString();
         }
      }
      return url;
   }

   public FacesUrlTransformer skipTransform()
   {
      appendParameterIfNeeded(url, SKIP_TRANSFORM, null);
      return this;
   }

   public FacesUrlTransformer removeSkipTransform(String cidParameterName)
   {
      removeParameterIfNeeded(url, SKIP_TRANSFORM);
      return this;
   }

   public String getUrl()
   {
      return url;
   }

   public FacesUrlTransformer toRedirectViewId()
   {
      if (isUrlAbsolute())
      {
         String requestPath = context.getExternalContext().getRequestContextPath();
         url = url.substring(url.indexOf(requestPath) + requestPath.length());
      }
      else
      {
         int lastSlash = url.lastIndexOf("/");
         if (lastSlash > 0)
         {
            url = url.substring(lastSlash);
         }
      }
      return this;
   }

   public FacesUrlTransformer toActionUrl()
   {
      url = context.getApplication().getViewHandler().getActionURL(context, url);
      return this;
   }

   public String encode()
   {
      return context.getExternalContext().encodeActionURL(url);
   }

   private boolean isUrlAbsolute()
   {
      // TODO: any API call to do this?
      return url.startsWith(HTTP_PROTOCOL_URL_PREFIX) || url.startsWith(HTTPS_PROTOCOL_URL_PREFIX);
   }
}