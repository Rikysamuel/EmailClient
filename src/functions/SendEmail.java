package functions;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public final class SendEmail {
   public String from;
   public String username;
   public String password;
   public String host;
   
   Properties props;
   Session session;
   
   public SendEmail(){
        props = new Properties();
   }
   
   public void setProps(){
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "25");
   }
    
   public void setSession() {
        // Get the Session object.
        session = Session.getInstance(props,
           new javax.mail.Authenticator() {
              @Override
              protected PasswordAuthentication getPasswordAuthentication() {
                 return new PasswordAuthentication(username, password);
             }
           });
    }
   
   public boolean send(String to, String subject, String msgBody){
       setProps();
       setSession();
       try {
	   // Create a default MimeMessage object.
	   Message message = new MimeMessage(session);
	
	   // Set From: header field of the header.
	   message.setFrom(new InternetAddress(from));
	
	   // Set To: header field of the header.
	   message.setRecipients(Message.RecipientType.TO,
               InternetAddress.parse(to));
	
	   // Set Subject: header field
	   message.setSubject(subject);
	
	   // Now set the actual message
	   message.setText(msgBody);

	   // Send message
	   Transport.send(message);

	   return true;

      } catch (MessagingException e) {
         throw new RuntimeException(e);
      }
   }
}