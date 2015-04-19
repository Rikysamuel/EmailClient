/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package functions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ReplyToEmail {
    public String imapHost;
    public String smtpHost;
    public String storeType;
    public String user;
    public String password;
    
    public List<String> msgInfo;
    
//    Message message;
//    Date date;
//    Session session;
//    Folder folder;
//    Store store;
    
    public ReplyToEmail(){
//        date = null;
        msgInfo = new ArrayList<>();
    }
    
    public void setUp() 
    {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imap.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "imap.smtp.com");
        properties.put("mail.smtp.port", "587");
        Session session = Session.getDefaultInstance(properties);

    }
    
    public int getMessageData(int messageId, Message[] messages){
        Date date = null;
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imap");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imap.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        Session session = Session.getDefaultInstance(properties);
        
        msgInfo.clear();
        System.out.println("message id:" + messageId);
        try {
            // Get a Store object and connect to the current host
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", "rikysamueltan@gmail.com", "Brigade_101");//change the user and password accordingly
            
            Folder folder = store.getFolder("INBOX");
            if (!folder.exists()) {
                return -1;
            }
            folder.open(Folder.READ_ONLY);
            
            if (messages.length != 0) {
                Message message = messages[messageId];
                date = message.getSentDate();
                // Get all the information from the message
                String from = InternetAddress.toString(message.getFrom());
                if (from != null) {
                    msgInfo.add(from);  // Index-0: From
                }
                String replyTo = InternetAddress.toString(message.getReplyTo());
                if (replyTo != null) {
                    msgInfo.add(replyTo);   // Index-1: ReplyTo
                }
                String to = InternetAddress.toString(message.getRecipients(Message.RecipientType.TO));
                if (to != null) {
                    msgInfo.add(to);    // Index-2: To
                }
                
                String subject = message.getSubject();
                if (subject != null) {
                    msgInfo.add(subject);   // Index-3: Subject
                }
                Date sent = message.getSentDate();
                if (sent != null) {
                    msgInfo.add(sent.toString());   // Index-4: sentDate
                }
                
                Message replyMessage = new MimeMessage(session);
                replyMessage = (MimeMessage) message.reply(false);
                replyMessage.setFrom(new InternetAddress(msgInfo.get(2)));
                replyMessage.setText("Thanks");
                replyMessage.setReplyTo(message.getReplyTo());

                // Send the message by authenticating the SMTP server
                // Create a Transport instance and call the sendMessage
                Transport t = session.getTransport("smtp");
                try {
                    //connect to the smpt server using transport instance
                    //change the user and password accordingly
                    t.connect("rikysamueltan@gmail.com", "Brigade_101");
                    t.sendMessage(replyMessage, replyMessage.getAllRecipients());
                } finally {
                    t.close();
                }

                System.out.println("success");
                
            }
            return 1;
            
        } catch (MessagingException e) {
            System.err.println(e);
        }
        return -1;
    }
    
    public boolean Reply(String replyMsg){
//        try {
//            Message replyMessage = new MimeMessage(session);
//            replyMessage = (MimeMessage) message.reply(false);
//            replyMessage.setFrom(new InternetAddress(msgInfo.get(2)));
//            replyMessage.setText("Thanks");
//            replyMessage.setReplyTo(message.getReplyTo());
//            
//            // Send the message by authenticating the SMTP server
//            // Create a Transport instance and call the sendMessage
//            Transport t = session.getTransport("smtp");
//            try {
//                //connect to the smpt server using transport instance
//                //change the user and password accordingly
//                t.connect(user, password);
//                t.sendMessage(replyMessage, replyMessage.getAllRecipients());
//            } finally {
//                t.close();
//            }
//            
//            System.out.println("success");
//            
//            return true;
//        }  catch (MessagingException ex) {
//            System.out.println(ex);
//        }
        return false;
     }

}
