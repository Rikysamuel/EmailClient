import ecdsa.BigPoint;
import ecdsa.ECElGamal;
import functions.CheckingMails;
import functions.ReplyToEmail;
import functions.SendEmail;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Properties;
import javafx.util.Pair;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import rubikcipher.RubikCipher;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Rikysamuel
 */
public final class EmailClient extends javax.swing.JFrame { 
    String hostServer;
    String mailStoreType;
    String account;
    String accpass;
    
    DefaultTableModel model;
    SendEmail se;
    CheckingMails cm;

    ReplyToEmail re;
    
    int selectedRowInbox;

    
    /* ECDSA Attributes */
    ECElGamal el;

    
    /**
     * Creates new form EmailClient
     */
    public EmailClient() {
        selectedRowInbox = 0;
        se = new SendEmail();
        cm = new CheckingMails();
        re = new ReplyToEmail();
        setUpEnvironment();
        
        initComponents();

        InboxContent.getColumnModel().getColumn(3).setMinWidth(0);
        InboxContent.getColumnModel().getColumn(3).setMaxWidth(0);

        initLayout();
        addPopUpMenu();
        
        model = (DefaultTableModel) InboxContent.getModel();
    }
    
    public void initLayout() {
        jTextField2.setEnabled(false);
        jButton5.setEnabled(false);
        jTextField3.setEnabled(false);
        jLabel10.setEnabled(false);
        jLabel11.setEnabled(false);
        jLabel8.setEnabled(false);
    }
    
    public void setUpEnvironment(){
        // Sending email

        se.from = "rikysamueltan@gmail.com";
        se.username = "tarnosupratman@gmail.com";//change accordingly
        se.password = "085722064771";//change accordingly
        se.host = "smtp.gmail.com";
        
        // Retrieve email
        cm.host = "imap.gmail.com";// change accordingly
        cm.storeType = "imap";
        cm.user = "rikysamueltan@gmail.com";// change accordingly
        cm.password = "";// change accordingly

        // Reply email
        re.imapHost = cm.host;
        re.smtpHost = se.host;
        re.storeType = cm.storeType;
        re.user = cm.user;
        re.password = cm.password;
    }

    public void fetchEmail(){
        cm.check();
        Collections.reverse(cm.emails);
        System.out.println(cm.emails.size());

        cm.emails.stream().forEach((email) -> {
            addInbox(email);
        });
    }
    
    public boolean send(){
        String message = Body.getText();        
        String encKey = jTextField3.getText();
//        System.out.println("Plain message:\n" + message);
        
        // Signature
        if(signatureCheckbox.isSelected()) {
            // Get hash from message
            SHA1 sha = new SHA1();
            String hash = sha.digest(message.getBytes());
//            System.out.println("Hash:\n" + hash);
            
            // Generate signature from hash
            ECElGamal el = new ECElGamal();
            el.setPrivateKey(new BigInteger(jTextField2.getText(), 16));
            Pair<BigInteger, BigInteger> signature = el.generateSignature(new BigInteger(hash, 16));
//            System.out.println("Signature:\n" + signature.getKey().toString(16) + "\n" + signature.getValue().toString(16));
            // Append signature to message
            message += "<ds>\n" + signature.getKey().toString(16) + " " + signature.getValue().toString(16) + "\n</ds>";
            
        }
        
        if(encryptionCheckbox.isSelected()) {
            RubikCipher rc = new RubikCipher();
            message = rc.EcbEncrypt(message, encKey);
        }
//        System.out.println("Final message:\n" + message);
        RubikCipher rc = new RubikCipher();
        
//        System.out.println("Decrypt: " + rc.EcbDecrypt(message, encKey));
        return se.send(Recipient.getText(), Subject.getText(), message);
    }
    
    public void addInbox(Object[] rowData){
        model.addRow(rowData);
    }
    
    private void addPopUpMenu(){
        //sets the popup menu so it will show
        InboxContent.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event) {
                selectedRowInbox = InboxContent.rowAtPoint(event.getPoint());
                
                String subject = (InboxContent.getValueAt(selectedRowInbox, 0)!=null) ? InboxContent.getValueAt(selectedRowInbox, 0).toString() : "";
                String from = (InboxContent.getValueAt(selectedRowInbox, 1)!=null) ? InboxContent.getValueAt(selectedRowInbox, 1).toString() : "";
                String message = (InboxContent.getValueAt(selectedRowInbox, 2)!= null) ? InboxContent.getValueAt(selectedRowInbox, 2).toString() : "";
                String id = (InboxContent.getValueAt(selectedRowInbox, 3)!= null) ? InboxContent.getValueAt(selectedRowInbox, 3).toString() : "";
                
                jLabel5.setText(subject);
                jLabel7.setText(from);
                jTextArea1.setText(message);
            }
        });
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBar1 = new java.awt.MenuBar();
        menu1 = new java.awt.Menu();
        menu2 = new java.awt.Menu();
        MenuButton = new javax.swing.ButtonGroup();
        jFileChooser1 = new javax.swing.JFileChooser();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        Compose = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        Recipient = new javax.swing.JTextField();
        CC = new javax.swing.JTextField();
        Subject = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        Body = new javax.swing.JTextArea();
        Send = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        signatureCheckbox = new javax.swing.JCheckBox();
        encryptionCheckbox = new javax.swing.JCheckBox();
        jTextField2 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        Inbox = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        InboxContent = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        Draft = new javax.swing.JPanel();
        Sent = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        menu1.setLabel("File");
        menuBar1.add(menu1);

        menu2.setLabel("Edit");
        menuBar1.add(menu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("To:");

        jLabel2.setText("CC:");

        jLabel3.setText("Subject:");

        Body.setColumns(20);
        Body.setRows(5);
        jScrollPane1.setViewportView(Body);

        Send.setText("Send");
        Send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 704, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 51, Short.MAX_VALUE)
        );

        jButton5.setText("Browse...");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel10.setText("Private Key");

        signatureCheckbox.setText("Use Signature");
        signatureCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signatureCheckboxActionPerformed(evt);
            }
        });

        encryptionCheckbox.setText("Use Encryption");
        encryptionCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                encryptionCheckboxActionPerformed(evt);
            }
        });

        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jLabel8.setText("filename");

        jLabel11.setText("Encryption Key");

        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ComposeLayout = new javax.swing.GroupLayout(Compose);
        Compose.setLayout(ComposeLayout);
        ComposeLayout.setHorizontalGroup(
            ComposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ComposeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ComposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ComposeLayout.createSequentialGroup()
                        .addGroup(ComposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ComposeLayout.createSequentialGroup()
                                .addGroup(ComposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(encryptionCheckbox)
                                    .addComponent(signatureCheckbox))
                                .addGap(298, 298, 298))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ComposeLayout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addGroup(ComposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(ComposeLayout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(ComposeLayout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addComponent(jLabel8)
                        .addGap(174, 174, 174)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ComposeLayout.createSequentialGroup()
                        .addGroup(ComposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(ComposeLayout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22)
                                .addComponent(Recipient))
                            .addGroup(ComposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ComposeLayout.createSequentialGroup()
                                    .addGap(2, 2, 2)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(30, 30, 30)
                                    .addComponent(CC, javax.swing.GroupLayout.PREFERRED_SIZE, 628, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(Send, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(ComposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ComposeLayout.createSequentialGroup()
                                        .addGap(4, 4, 4)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(8, 8, 8)
                                        .addComponent(Subject, javax.swing.GroupLayout.PREFERRED_SIZE, 628, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 690, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        ComposeLayout.setVerticalGroup(
            ComposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ComposeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(signatureCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ComposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(ComposeLayout.createSequentialGroup()
                        .addGroup(ComposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton5)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(encryptionCheckbox)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ComposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ComposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ComposeLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel1))
                    .addComponent(Recipient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(ComposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ComposeLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel2))
                    .addComponent(CC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(ComposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ComposeLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel3))
                    .addComponent(Subject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Send, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Compose Email", Compose);

        jScrollPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane2MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jScrollPane2MouseReleased(evt);
            }
        });

        InboxContent.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Subject", "From", "Message", "id"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(InboxContent);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setText("Message Body");
        jTextArea1.setEnabled(false);
        jScrollPane3.setViewportView(jTextArea1);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel5.setText("Subject");

        jLabel6.setText("From:");

        jLabel7.setText("Someone");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)))
        );

        jButton1.setText("Sync");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Reply");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Delete");

        jButton4.setText("Forward");

        javax.swing.GroupLayout InboxLayout = new javax.swing.GroupLayout(Inbox);
        Inbox.setLayout(InboxLayout);
        InboxLayout.setHorizontalGroup(
            InboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 709, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane3)
            .addGroup(InboxLayout.createSequentialGroup()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        InboxLayout.setVerticalGroup(
            InboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InboxLayout.createSequentialGroup()
                .addGroup(InboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Inbox", Inbox);

        javax.swing.GroupLayout DraftLayout = new javax.swing.GroupLayout(Draft);
        Draft.setLayout(DraftLayout);
        DraftLayout.setHorizontalGroup(
            DraftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 709, Short.MAX_VALUE)
        );
        DraftLayout.setVerticalGroup(
            DraftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 577, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Draft", Draft);

        javax.swing.GroupLayout SentLayout = new javax.swing.GroupLayout(Sent);
        Sent.setLayout(SentLayout);
        SentLayout.setHorizontalGroup(
            SentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 709, Short.MAX_VALUE)
        );
        SentLayout.setVerticalGroup(
            SentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 577, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Sent Email", Sent);

        jButton7.setText("Generate Key Pair");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Simpan Private Key");
        jButton8.setEnabled(false);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Simpan Public Key");
        jButton9.setEnabled(false);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton7)
                    .addComponent(jButton9)
                    .addComponent(jButton8))
                .addContainerGap(541, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton9)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Generate Key", jPanel2);

        jLabel4.setFont(new java.awt.Font("Tahoma", 3, 48)); // NOI18N
        jLabel4.setText("EMAIL CLIENT");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(325, 325, 325)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 730, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    

    private void SendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendActionPerformed
        try{
            if (send()){
                JOptionPane.showMessageDialog(this, "Message sent successfully....", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (HeadlessException e){
            System.err.println(e);
        }
    }//GEN-LAST:event_SendActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        fetchEmail();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jScrollPane2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane2MouseClicked
    }//GEN-LAST:event_jScrollPane2MouseClicked

    private void jScrollPane2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane2MouseReleased
    }//GEN-LAST:event_jScrollPane2MouseReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        System.out.println("Selected Row: " + selectedRowInbox);
        
        re.setUp();
        int ret = re.getMessageData(Integer.valueOf(cm.emails.get(selectedRowInbox)[3]), cm.messages);
        if (ret!=1){
            JOptionPane.showConfirmDialog(null, "Message not Found!", "Error", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE);
        }
        
        ReplyPanel myPanel = new ReplyPanel();
        
        myPanel.fromMail = re.msgInfo.get(0);
        myPanel.replyToMail = re.msgInfo.get(1);
        myPanel.toMail = re.msgInfo.get(2);
        myPanel.subjectMail = re.msgInfo.get(3);
        myPanel.sentMail = re.msgInfo.get(4);
        myPanel.fillBlank();
        
        int result = JOptionPane.showConfirmDialog(null, myPanel, "Reply", JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION){
            int id = Integer.valueOf(cm.emails.get(selectedRowInbox)[3]);
//            re.Reply(id , cm.messages[id], myPanel.body);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // Generate private & public key
        el = new ECElGamal();
        BigInteger privateKey = el.generatePrivateKey();
        BigPoint publicKey = el.generatePublicKey(privateKey);
        
        JOptionPane.showMessageDialog(this, "Key pair generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        jButton8.setEnabled(true);
        jButton9.setEnabled(true);
        System.out.println("priv: " + el.getPrivateKey().toString());
        System.out.println("pub: " + el.getPublicKey().x + ", " + el.getPublicKey().y);

    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        int returnVal = jFileChooser1.showSaveDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                // Dapatkan lokasi save
                String path = jFileChooser1.getSelectedFile().getAbsolutePath();
                File privateKeyFile = new File(path);

                // if file doesnt exists, then create it
                if (!privateKeyFile.exists()) {
                    privateKeyFile.createNewFile();
                }

                FileWriter fw = new FileWriter(privateKeyFile.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(el.getPrivateKey().toString());
                bw.close();
            } catch (IOException ex) {
                System.out.println("Error processing file." + ex);
            }
        } else {
            System.out.println("File access cancelled by user.");
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        int returnVal = jFileChooser1.showSaveDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                // Dapatkan lokasi save
                String path = jFileChooser1.getSelectedFile().getAbsolutePath();
                File publicKeyFile = new File(path);

                // if file doesnt exists, then create it
                if (!publicKeyFile.exists()) {
                    publicKeyFile.createNewFile();
                }

                FileWriter fw = new FileWriter(publicKeyFile.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(el.getPublicKey().x + " " + el.getPublicKey().y);
                bw.close();
            } catch (IOException ex) {
                System.out.println("Error processing file." + ex);
            }
        } else {
            System.out.println("File access cancelled by user.");
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        jTextField2.setText("");
        int returnVal = jFileChooser1.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File privateKeyFile = jFileChooser1.getSelectedFile();
            jLabel8.setText(privateKeyFile.getName());
            Charset charset = Charset.forName("UTF-8");
            try {
                BufferedReader reader = Files.newBufferedReader(privateKeyFile.toPath(), charset);
                String privKey = reader.readLine();
                BigInteger pK = new BigInteger(privKey);
                jTextField2.setText(pK.toString(16));
            } catch (IOException ex) {
                System.out.println("Error reading file:" + ex);
            }
            
        } else {
            System.out.println("File access cancelled by user.");
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void signatureCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signatureCheckboxActionPerformed
        if(signatureCheckbox.isSelected()) {
            jTextField2.setEnabled(true);
            jButton5.setEnabled(true);
            jLabel10.setEnabled(true);
            jLabel8.setEnabled(true);
        } else {
            jTextField2.setEnabled(false);
            jButton5.setEnabled(false);
            jLabel10.setEnabled(false);
            jLabel8.setEnabled(false);
        }
    }//GEN-LAST:event_signatureCheckboxActionPerformed

    private void encryptionCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_encryptionCheckboxActionPerformed
        if(encryptionCheckbox.isSelected()) {
            jTextField3.setEnabled(true);
            jLabel11.setEnabled(true);
        } else {
            jTextField3.setEnabled(false);
            jLabel11.setEnabled(false);
        }
    }//GEN-LAST:event_encryptionCheckboxActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EmailClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new EmailClient().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea Body;
    private javax.swing.JTextField CC;
    private javax.swing.JPanel Compose;
    private javax.swing.JPanel Draft;
    private javax.swing.JPanel Inbox;
    private javax.swing.JTable InboxContent;
    private javax.swing.ButtonGroup MenuButton;
    private javax.swing.JTextField Recipient;
    private javax.swing.JButton Send;
    private javax.swing.JPanel Sent;
    private javax.swing.JTextField Subject;
    private javax.swing.JCheckBox encryptionCheckbox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private java.awt.Menu menu1;
    private java.awt.Menu menu2;
    private java.awt.MenuBar menuBar1;
    private javax.swing.JCheckBox signatureCheckbox;
    // End of variables declaration//GEN-END:variables
}
