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
package org.jboss.weld.xml;

import static org.jboss.weld.xml.BeansXmlParser.EXTENSIONS_NAMESPACE;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jboss.weld.resources.spi.ResourceLoader;
import org.jboss.weld.util.dom.NodeListIterable;
import org.jboss.weld.util.reflection.Reflections;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author Pete Muir
 * 
 */
class ScanningElement
{
   
   private URL file;
   private Element element;
   private List<Pattern> includes;
   private List<Pattern> excludes;

   ScanningElement(URL file, Element element, ResourceLoader resourceLoader)
   {
      super();
      this.file = file;
      this.element = element;
      this.includes = parseElements("include", element, resourceLoader);
      this.excludes = parseElements("exclude", element, resourceLoader);
   }
   
   private static List<Pattern> parseElements(String elementName, Element rootElement, ResourceLoader resourceLoader)
   {
      List<Pattern> patterns = new ArrayList<Pattern>();
      for (Node child : new NodeListIterable(rootElement.getElementsByTagNameNS(EXTENSIONS_NAMESPACE, "include")))
      {
         if (!child.hasAttributes())
         {
            throw new WeldXmlException(key, args);
         }
         Node patternNode = child.getAttributes().getNamedItem("pattern");
         if (patternNode == null || patternNode.getTextContent() == null)
         {
            throw new WeldXmlException(key, args);
         }
         Node ifNode = child.getAttributes().getNamedItem("if");
         boolean ignorePattern = false;
         if (ifNode != null && ifNode.getTextContent() != null)
         {
            boolean invertCondition = false;
            String iff = ifNode.getTextContent();
            if (iff.startsWith("!"))
            {
               invertCondition = true;
               iff = iff.substring(1);
            }
            Method ifMethod = Reflections.parseMethod(iff, resourceLoader);
            if (!(Boolean.class.equals(ifMethod.getReturnType()) || boolean.class.equals(ifMethod.getReturnType())))
            {
               throw new WeldXmlException(key, args);
            }
            Boolean ifMethodResult = ifMethod.invoke(null); 
            if (ifMethodResult == null)
            {
               throw new WeldXmlException(key, args);
            }
            ignorePattern = invertCondition ? !ifMethodResult : ifMethodResult;
         }
         if (!ignorePattern)
         {
            Pattern pattern = Pattern.compile(patternNode.getTextContent());
            patterns.add(pattern);
         }
      }
      return patterns;
   }
   
   public List<Pattern> getExcludes()
   {
      return excludes;
   }
   
   public List<Pattern> getIncludes()
   {
      return includes;
   }


   public URL getFile()
   {
      return file;
   }

   public Element getElement()
   {
      return element;
   }

   @Override
   public String toString()
   {
      return "File: " + getFile() + "; Node: " + getElement();
   }

}