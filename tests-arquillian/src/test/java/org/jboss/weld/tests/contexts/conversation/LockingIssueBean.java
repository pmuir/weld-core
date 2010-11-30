package org.jboss.weld.tests.contexts.conversation;

import java.io.Serializable;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

@Model
@ConversationScoped
public class LockingIssueBean implements Serializable
{
   
   @Inject Conversation conversation;
   
   private String name;
   
   public LockingIssueBean()
   {
      name = "Gavin";
   }
   
   public void dummy()
   {
      throw new NullPointerException();
   }
   
   public String start()
   {
      conversation.begin();
      name = "Pete";
      return "start";
   }
   
   public String getName()
   {
      return name;
   }
   
   public String getCid()
   {
      return conversation.getId();
   }
   
}
