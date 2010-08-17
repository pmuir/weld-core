/**
 * 
 */
package org.jboss.weld.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Scanning
{
   
   public final List<Pattern> includes;
   public final List<Pattern> excludes;
   
   public Scanning(List<Pattern> includes, List<Pattern> excludes)
   {
      this.includes = new ArrayList<Pattern>(includes);
      this.excludes = new ArrayList<Pattern>(excludes);
   }
   
   public Scanning()
   {
      this.includes = Collections.emptyList();
      this.excludes = Collections.emptyList();
   }
   
   public List<Pattern> getExcludes()
   {
      return Collections.unmodifiableList(excludes);
   }
   
   public List<Pattern> getIncludes()
   {
      return Collections.unmodifiableList(includes);
   }
   
}