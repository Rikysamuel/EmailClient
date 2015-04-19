package functions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class CheckingMails {
    
   public String host;
   public String storeType;
   public String user;
   public String password;
    
   public List<String[]> emails;
   public Message[] messages;
    
   public CheckingMails(){
       emails = new ArrayList<>();
   }

   public void check()
   {
      try {

      //create properties field
      Properties properties = new Properties();

      properties.put("mail.imaps.host", host);
      properties.put("mail.imaps.port", "993");
      properties.put("mail.imaps.connectiontimeout", "5000");
      properties.put("mail.imaps.timeout", "5000");
      
      Session emailSession = Session.getDefaultInstance(properties);
  
      //create the IMAP store object and connect with the pop server
      Store store = emailSession.getStore("imaps");

      store.connect(host, user, password);

      //create the folder object and open it
      Folder emailFolder = store.getFolder("INBOX");
      emailFolder.open(Folder.READ_ONLY);
      
      // retrieve the messages from the folder in an array and print it
      messages = emailFolder.getMessages();

      for (int i = 0, n = messages.length; i < n; i++) {
         Message message = messages[i];
         String a = message.getSubject();
         String b = message.getFrom()[0].toString();
         String c = message.getContent().toString();
         String d = String.valueOf(i);
         emails.add(new String[]{a,b,c,d});

      }

      //close the store and folder objects
//      emailFolder.close(false);
//      store.close();

      } catch (NoSuchProviderException e) {
      } catch (MessagingException | IOException e) {
      }
   }
}