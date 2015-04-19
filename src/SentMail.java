
import java.util.Properties;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

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
    public static void main(String[] args){
        Properties props = new Properties();

    props.setProperty("mail.store.protocol", "imaps");

    try {
        Session session = Session.getInstance(props, null);
        Store store = session.getStore();
        store.connect("imap.gmail.com", "rikysamueltan@gmail.com", "Brigade_101");
        
        Folder[] folderList = store.getFolder("[Gmail]").list();
        for (int i = 0; i < folderList.length; i++) {
            System.out.println(folderList[i].getFullName());
        }
        Folder inbox = store.getFolder("[Gmail]/Sent Mail");
        inbox.open(Folder.READ_ONLY);
        Message msg = inbox.getMessage(inbox.getMessageCount());
        Address[] in = msg.getFrom();
        for (Address address : in) {
            System.out.println("FROM:" + address.toString());
        }
        Multipart mp = (Multipart) msg.getContent();
        BodyPart bp = mp.getBodyPart(0);
        System.out.println("Bcc User NAme :"+InternetAddress.toString(msg.getRecipients(Message.RecipientType.BCC)));
        System.out.println("SENT DATE:" + msg.getSentDate());
        System.out.println("SUBJECT:" + msg.getSubject());
        System.out.println("CONTENT:" + bp.getContent());
    } catch (Exception mex) {
        mex.printStackTrace();
    }
    }
}
