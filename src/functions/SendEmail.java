package functions;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

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
         props.put("mail.smtp.port", "587");
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

    public boolean send(String to, String cc, String subject, String msgBody, String filename){
        setProps();
        setSession();
        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            
            // Set CC: header field of the header.
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));

            // Set Subject: header field
            message.setSubject(subject);

            if (filename.equals("")){
                // Now set the actual message
                message.setText(msgBody);

                // Send message
                Transport.send(message);

                return true;
            } else{
                // Create the message part
                BodyPart messageBodyPart = new MimeBodyPart();

                // Now set the actual message
                messageBodyPart.setText(msgBody);

                // Create a multipart message
                Multipart multipart = new MimeMultipart();

                // Set text message part
                multipart.addBodyPart(messageBodyPart);
                
                // Part two is attachment
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(filename);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(filename);
                multipart.addBodyPart(messageBodyPart);
                
                // Send the complete message parts
                message.setContent(multipart);

                // Send message
                Transport.send(message);
                return true;
            }

        } catch (MessagingException e) {
           throw new RuntimeException(e);
        }
    }
}