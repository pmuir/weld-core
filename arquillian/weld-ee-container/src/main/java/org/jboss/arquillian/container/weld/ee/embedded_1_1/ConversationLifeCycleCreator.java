/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.arquillian.container.weld.ee.embedded_1_1;

import org.jboss.arquillian.spi.Context;
import org.jboss.arquillian.spi.event.Event;
import org.jboss.arquillian.spi.event.suite.EventHandler;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.BoundRequest;
import org.jboss.weld.context.bound.MutableBoundRequest;
import org.jboss.weld.manager.api.WeldManager;

/**
 * SessionLifeCycleController
 *
 * @author <a href="mailto:aknutsen@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ConversationLifeCycleCreator implements EventHandler<Event>
{

   public void callback(Context context, Event event) throws Exception 
   {
      WeldManager manager = context.get(WeldManager.class);
      if(manager == null)
      {
         throw new IllegalStateException("No " + WeldManager.class.getName() + " found in context");
      }
      
      CDIConversationID id = context.get(CDIConversationID.class);
      if(id == null)
      {
         id = new CDIConversationID(null); // when null creates a new empty conversation id. 
      }
      
      BoundRequest boundRequest = new MutableBoundRequest(context.get(CDIRequestMap.class), context.get(CDISessionMap.class));
      context.add(BoundRequest.class, boundRequest);
      
      BoundConversationContext conversationContext = manager.instance().select(BoundConversationContext.class).get();
      conversationContext.associate(boundRequest);
      conversationContext.activate(id.getId());
   }
}
