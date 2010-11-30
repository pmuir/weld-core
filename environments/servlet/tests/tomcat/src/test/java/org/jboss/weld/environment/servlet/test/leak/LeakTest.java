package org.jboss.weld.environment.servlet.test.leak;

import static org.jboss.weld.environment.servlet.test.util.TomcatDeployments.CONTEXT_XML;
import static org.junit.Assert.fail;

import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.environment.servlet.test.leak.unresolvable.Bar;
import org.jboss.weld.environment.servlet.test.util.Deployments;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class LeakTest
{

   @Deployment
   public static WebArchive deployment()
   {
      return Deployments.baseDeployment().add(CONTEXT_XML, "META-INF/context.xml");
   }
   
   @Inject Bar bar;
   
   @Test
   public void testLeakFromUnresolvable()
   {
      fail();
   }

}
