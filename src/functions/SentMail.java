package functions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Rikysamuel
 */
public class SentMail {
    public String user;
    public String password;
    
    public List<Object[]> emails;
    List<Object> temp;
    
    public SentMail(){
        emails = new ArrayList<>();
        temp = new ArrayList<>();
    }
    
    public void fetch(){
        Properties props = new Properties();
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.connectiontimeout", "5000");
        props.put("mail.imaps.timeout", "5000");
        props.setProperty("mail.store.protocol", "imaps");

        try {
            Session session = Session.getDefaultInstance(props);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", user, password);

            Folder inbox = store.getFolder("[Gmail]/Sent Mail");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();

            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
                String a = message.getSubject();
                String b = message.getFrom()[0].toString();
                String c = message.getContent().toString();
                String d = String.valueOf(i);
                emails.add(new String[]{a,b,c,d});
            }
        } catch (IOException ex) {
            Logger.getLogger(SentMail.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(SentMail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
