import functions.DraftEmail;
import ecdsa.BigPoint;
import ecdsa.ECElGamal;
import functions.CheckingMails;
import functions.ReplyToEmail;
import functions.SendEmail;
import functions.SentMail;
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
import javafx.util.Pair;
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
    DefaultTableModel sentModel;
    DefaultTableModel draftModel;
    SendEmail se;
    CheckingMails cm;
    ReplyToEmail re;
    SentMail sm;
    DraftEmail de;
    
    int selectedRowInbox;
    int selectedRowSent;
    int selectedRowDraft;

    
    /* ECDSA Attributes */
    ECElGamal el;
    BigInteger r;
    BigInteger s;

    
    /**
     * Creates new form EmailClient
     */
    public EmailClient() {
        selectedRowInbox = 0;
        selectedRowSent = 0;
        selectedRowDraft = 0;
        se = new SendEmail();
        cm = new CheckingMails();
        re = new ReplyToEmail();
        sm = new SentMail();
        de = new DraftEmail();
        setUpEnvironment();
        
        initComponents();

        initLayout();
        addPopUpMenu();
        
        model = (DefaultTableModel) InboxContent.getModel();
        sentModel = (DefaultTableModel) SentContent.getModel();
        draftModel = (DefaultTableModel) DraftContent.getModel();
    }
    
    /**
     * Initiate layout
     */
    public void initLayout() {
        InboxContent.getColumnModel().getColumn(3).setMinWidth(0);
        InboxContent.getColumnModel().getColumn(3).setMaxWidth(0);
        
        SentContent.getColumnModel().getColumn(3).setMinWidth(0);
        SentContent.getColumnModel().getColumn(3).setMaxWidth(0);
        
        DraftContent.getColumnModel().getColumn(3).setMinWidth(0);
        DraftContent.getColumnModel().getColumn(3).setMaxWidth(0);
        
        hashField.setEnabled(false);
        jButton5.setEnabled(false);
        jTextField3.setEnabled(false);
        jLabel10.setEnabled(false);
        jLabel11.setEnabled(false);
        jLabel8.setEnabled(false);
    }
    
    /**
     * Environment Setting before use the app
     */
    public void setUpEnvironment(){
        // Sending email
        se.from = "tarnosupratman@gmail.com";
        se.username = "tarnosupratman@gmail.com";//change accordingly
        se.password = "085722064771";//change accordingly
        se.host = "smtp.gmail.com";
        
        // Retrieve email
        cm.host = "imap.gmail.com";// change accordingly
        cm.storeType = "imap";
        cm.user = "rikysamueltan@gmail.com";// change accordingly
        cm.password = "Brigade_101";// change accordingly

        // Reply email
        re.imapHost = cm.host;
        re.smtpHost = se.host;
        re.storeType = cm.storeType;
        re.user = cm.user;
        re.password = cm.password;
        
        // Retrieve Sent email
        sm.user = cm.user;
        sm.password = cm.password;
        
        // Retrieve Draft emai
        de.user = cm.user;
        de.password = cm.password;
    }
    
    /**
     * Method used to fetch all messages in "Inbox" folder from server
     */
    public void fetchEmail(){
        cm.emails.clear();
        cm.check();
        Collections.reverse(cm.emails);
        System.out.println(cm.emails.size());

        cm.emails.stream().forEach((email) -> {
            addInbox(email);
        });
    }
    
    /**
     * Method used to fetch all messages in "Sent Mail" folder from Server
     */
    public void fetchSentMail(){
        sm.emails.clear();
        sm.fetch();
        Collections.reverse(sm.emails);
        
        sm.emails.stream().forEach((email) -> {
            addSent(email);
        });
    }
    
    /**
     * Method used to fetch all messages in "Drafts" folder fom Server
     */
    public void fetchDraftEmail(){
        de.emails.clear();
        de.fetch();
        Collections.reverse(de.emails);
        
        de.emails.stream().forEach((email) -> {
            addDraft(email);
        });
    }
    
    /**
     * Method used to send message to a specific recipient
     * @return success status
     */
    public boolean send(){
        String message = Body.getText();        
        String key = jTextField3.getText();
        
        // Signature
        if(signatureCheckbox.isSelected()) {
            // Get hash from message
            SHA1 sha = new SHA1();
            String hash = sha.digest(message.getBytes());
            
            // Generate signature from hash
            ECElGamal elgamal = new ECElGamal();
            elgamal.setPrivateKey(new BigInteger(hashField.getText(), 16));
            Pair<BigInteger, BigInteger> signature = elgamal.generateSignature(new BigInteger(hash, 16));
            
            // Append signature to message
            message += "<ds>" + signature.getKey().toString(16) + " " + signature.getValue().toString(16) + "</ds>";
            
        }
        
        if(encryptionCheckbox.isSelected()) {
            RubikCipher rc = new RubikCipher();
            message = rc.EcbEncrypt(message, key);
        }
        RubikCipher rc = new RubikCipher();
        if (AttachedFile.getText().equals("Filename")){
            return se.send(Recipient.getText(), CC.getText(), Subject.getText(), message, "");
        } else{
            return se.send(Recipient.getText(), CC.getText(), Subject.getText(), message, AttachedFile.getText());
        }
    }
    
    /**
     * add every mail fetched to the InboxContent
     * @param rowData list of fetched mails
     */
    public void addInbox(Object[] rowData){
        model.addRow(rowData);
    }
    
    /**
     * add every mail fetched to the SentContent
     * @param rowData list of fetched mails
     */
    public void addSent(Object[] rowData){
        sentModel.addRow(rowData);
    }
    
    /**
     * add every mail fetched to the DraftContent
     * @param rowData 
     */
    public void addDraft(Object[] rowData){
        draftModel.addRow(rowData);
    }
    
    /**
     * InboxContent element Click Event Listener
     */
    private void addPopUpMenu(){
        InboxContent.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event) {
                selectedRowInbox = InboxContent.rowAtPoint(event.getPoint());
                
                String subject = (InboxContent.getValueAt(selectedRowInbox, 0)!=null) ? InboxContent.getValueAt(selectedRowInbox, 0).toString() : "";
                String from = (InboxContent.getValueAt(selectedRowInbox, 1)!=null) ? InboxContent.getValueAt(selectedRowInbox, 1).toString() : "";
                String message = (InboxContent.getValueAt(selectedRowInbox, 2)!= null) ? InboxContent.getValueAt(selectedRowInbox, 2).toString() : "";
                
                jLabel5.setText(subject);
                jLabel7.setText(from);
                MessageBody.setText(message);
            }
        });
        
        SentContent.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event){
                selectedRowSent = SentContent.rowAtPoint(event.getPoint());
                
                String subject = (SentContent.getValueAt(selectedRowSent, 0)!=null) ? SentContent.getValueAt(selectedRowSent, 0).toString() : "";
                String from = (SentContent.getValueAt(selectedRowSent, 1)!=null) ? SentContent.getValueAt(selectedRowSent, 1).toString() : "";
                String message = (SentContent.getValueAt(selectedRowSent, 2)!= null) ? SentContent.getValueAt(selectedRowSent, 2).toString() : "";
                String id = (SentContent.getValueAt(selectedRowSent, 3)!= null) ? SentContent.getValueAt(selectedRowSent, 3).toString() : "";
                
                SubjectSent.setText(subject);
                SomeoneSent.setText(from);
                BodySent.setText(message);
                
            }
        });
        
        DraftContent.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event){
                selectedRowDraft = DraftContent.rowAtPoint(event.getPoint());
                
                String subject = (DraftContent.getValueAt(selectedRowDraft, 0)!=null) ? DraftContent.getValueAt(selectedRowDraft, 0).toString() : "";
                String from = (DraftContent.getValueAt(selectedRowDraft, 1)!=null) ? DraftContent.getValueAt(selectedRowDraft, 1).toString() : "";
                String message = (DraftContent.getValueAt(selectedRowDraft, 2)!= null) ? DraftContent.getValueAt(selectedRowDraft, 2).toString() : "";
                String id = (DraftContent.getValueAt(selectedRowDraft, 3)!= null) ? DraftContent.getValueAt(selectedRowDraft, 3).toString() : "";
                
                SubjectDraft.setText(subject);
                SomeoneDraft.setText(from);
                BodyDraft.setText(message);
                
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
        hashField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton12 = new javax.swing.JButton();
        AttachedFile = new javax.swing.JLabel();
        Inbox = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        InboxContent = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        encKey = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        MessageBody = new javax.swing.JEditorPane();
        Draft = new javax.swing.JPanel();
        jButton14 = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        DraftContent = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        SubjectDraft = new javax.swing.JLabel();
        SomeoneDraft = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        BodyDraft = new javax.swing.JEditorPane();
        Sent = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        BodySent = new javax.swing.JEditorPane();
        jPanel6 = new javax.swing.JPanel();
        SubjectSent = new javax.swing.JLabel();
        SomeoneSent = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        SentContent = new javax.swing.JTable();
        jButton13 = new javax.swing.JButton();
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

        hashField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hashFieldActionPerformed(evt);
            }
        });

        jLabel8.setText("filename");

        jLabel11.setText("Encryption Key");

        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jButton12.setText("AttachFile");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        AttachedFile.setText("Filename");

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
                                        .addComponent(hashField, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ComposeLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(CC, javax.swing.GroupLayout.PREFERRED_SIZE, 628, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(ComposeLayout.createSequentialGroup()
                                .addComponent(Send, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(AttachedFile, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(ComposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ComposeLayout.createSequentialGroup()
                                    .addGap(4, 4, 4)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(8, 8, 8)
                                    .addComponent(Subject, javax.swing.GroupLayout.PREFERRED_SIZE, 628, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 690, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                            .addComponent(hashField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addGroup(ComposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Send, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton12)
                    .addComponent(AttachedFile))
                .addContainerGap())
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

        jButton6.setText("Decrypt");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel9.setText("Ecnryption Key");

        jLabel12.setText("Public Key");

        jButton10.setText("Verify");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("Browse file...");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jLabel13.setText("Filename");

        jLabel14.setFont(new java.awt.Font("Lucida Grande", 1, 24)); // NOI18N

        MessageBody.setEditable(false);
        MessageBody.setText("Message Content");
        jScrollPane6.setViewportView(MessageBody);

        javax.swing.GroupLayout InboxLayout = new javax.swing.GroupLayout(Inbox);
        Inbox.setLayout(InboxLayout);
        InboxLayout.setHorizontalGroup(
            InboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(InboxLayout.createSequentialGroup()
                .addComponent(jButton1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(InboxLayout.createSequentialGroup()
                .addGroup(InboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(InboxLayout.createSequentialGroup()
                        .addGroup(InboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel12))
                        .addGap(12, 12, 12)
                        .addGroup(InboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(InboxLayout.createSequentialGroup()
                                .addGroup(InboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(InboxLayout.createSequentialGroup()
                                        .addComponent(jButton11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel13)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 132, Short.MAX_VALUE))
                            .addGroup(InboxLayout.createSequentialGroup()
                                .addComponent(encKey, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel14))))
                    .addComponent(jScrollPane6)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        InboxLayout.setVerticalGroup(
            InboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InboxLayout.createSequentialGroup()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(InboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(InboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(encKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(jButton6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(InboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(InboxLayout.createSequentialGroup()
                        .addGroup(InboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(InboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton11)
                            .addComponent(jLabel13)))
                    .addGroup(InboxLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)))
                .addGap(129, 129, 129))
        );

        jTabbedPane1.addTab("Inbox", Inbox);

        jButton14.setText("Sync");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jScrollPane7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane7MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jScrollPane7MouseReleased(evt);
            }
        });

        DraftContent.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Subject", "From", "Message", "id"
            }
        ));
        jScrollPane7.setViewportView(DraftContent);

        SubjectDraft.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        SubjectDraft.setText("Subject");

        SomeoneDraft.setText("Someone");

        jLabel23.setText("From:");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SubjectDraft)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addGap(12, 12, 12)
                        .addComponent(SomeoneDraft))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(SubjectDraft)
                .addGap(20, 20, 20)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23)
                    .addComponent(SomeoneDraft)))
        );

        BodyDraft.setEditable(false);
        BodyDraft.setText("Message Content");
        BodyDraft.setToolTipText("");
        jScrollPane8.setViewportView(BodyDraft);

        javax.swing.GroupLayout DraftLayout = new javax.swing.GroupLayout(Draft);
        Draft.setLayout(DraftLayout);
        DraftLayout.setHorizontalGroup(
            DraftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(DraftLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DraftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 705, Short.MAX_VALUE)
                    .addGroup(DraftLayout.createSequentialGroup()
                        .addComponent(jButton14)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        DraftLayout.setVerticalGroup(
            DraftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DraftLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jButton14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Draft", Draft);

        BodySent.setEditable(false);
        BodySent.setText("Message Content");
        BodySent.setToolTipText("");
        jScrollPane5.setViewportView(BodySent);

        SubjectSent.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        SubjectSent.setText("Subject");

        SomeoneSent.setText("Someone");

        jLabel22.setText("From:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SubjectSent)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addGap(12, 12, 12)
                        .addComponent(SomeoneSent))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(SubjectSent)
                .addGap(20, 20, 20)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(SomeoneSent)))
        );

        jScrollPane4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane4MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jScrollPane4MouseReleased(evt);
            }
        });

        SentContent.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Subject", "From", "Message", "id"
            }
        ));
        jScrollPane4.setViewportView(SentContent);

        jButton13.setText("Sync");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SentLayout = new javax.swing.GroupLayout(Sent);
        Sent.setLayout(SentLayout);
        SentLayout.setHorizontalGroup(
            SentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(SentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 705, Short.MAX_VALUE)
                    .addGroup(SentLayout.createSequentialGroup()
                        .addComponent(jButton13)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        SentLayout.setVerticalGroup(
            SentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SentLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jButton13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                .addContainerGap())
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
                .addContainerGap(1198, Short.MAX_VALUE))
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
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 620, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    

    private void SendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendActionPerformed
        try{
            if (send()){
                JOptionPane.showMessageDialog(this, "Message sent successfully....", "Success", JOptionPane.INFORMATION_MESSAGE);
                hashField.setText("");
                jLabel8.setText("filename");
                signatureCheckbox.setSelected(false);
                encryptionCheckbox.setSelected(false);
                jTextField3.setText("");
                Recipient.setText("");
                CC.setText("");
                Subject.setText("");
                Body.setText("");
                AttachedFile.setText("Filename");
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
        hashField.setText("");
        int returnVal = jFileChooser1.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File privateKeyFile = jFileChooser1.getSelectedFile();
            jLabel8.setText(privateKeyFile.getName());
            Charset charset = Charset.forName("UTF-8");
            try {
                BufferedReader reader = Files.newBufferedReader(privateKeyFile.toPath(), charset);
                String privKey = reader.readLine();
                BigInteger pK = new BigInteger(privKey);
                hashField.setText(pK.toString(16));
            } catch (IOException ex) {
                System.out.println("Error reading file:" + ex);
            }
            
        } else {
            System.out.println("File access cancelled by user.");
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void signatureCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signatureCheckboxActionPerformed
        if(signatureCheckbox.isSelected()) {
            hashField.setEnabled(true);
            jButton5.setEnabled(true);
            jLabel10.setEnabled(true);
            jLabel8.setEnabled(true);
        } else {
            hashField.setEnabled(false);
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

    private void hashFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hashFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hashFieldActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed
    
    private BigPoint getMessageSignature() {
        String message = MessageBody.getText();
        String signature= new String();
        // Get signature raw
        for(int i=0;i<message.length()-3;++i) {
            if(message.substring(i, i+4).equalsIgnoreCase("<ds>")) {
                i+=4;
                while(message.charAt(i)!='<') {
                    signature+=message.charAt(i);
                    ++i;
                }
                break;
            }
        }
        
        // Get r and s
        BigPoint ds = new BigPoint();
        String temp = new String();
        for(int i=0;i<signature.length();++i) {
            if(signature.charAt(i)==' ') {
                ds.x = new BigInteger(temp, 16);
                temp ="";
            } else {
                temp += signature.charAt(i);
            }
        }        
        ds.y = new BigInteger(temp, 16);
        
        return ds;
    }
    
    private BigInteger getMessageHash() {
        String message = MessageBody.getText();
        String msgBody = "";
        
        // Get message body
        int i=0;
        while(i<message.length()-3 && !message.substring(i, i+4).equalsIgnoreCase("<ds>")) {
            msgBody+=message.charAt(i);
            ++i;
        }
        
        // Get hash
        SHA1 sha = new SHA1();
        BigInteger hash = new BigInteger(sha.digest(msgBody.getBytes()), 16);
        
        return hash;
    }
    
    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // Get public key from user
        String pubKey = jTextField4.getText();
        int i=0; String temp="";
        while(pubKey.charAt(i) != ' ') {
            temp += pubKey.charAt(i);
            ++i;
        }
        ++i;
        BigInteger r = new BigInteger(temp, 16);
        
        temp="";
        for(int j=i;j<pubKey.length();++j) {
            temp += pubKey.charAt(j);
        }
        BigInteger s = new BigInteger(temp, 16);
        BigPoint publicKey = new BigPoint(r,s);
        
        // Get message hash
        BigInteger hash = getMessageHash();
        
        // Get signature from message
        BigPoint Signature = getMessageSignature();
        ECElGamal el = new ECElGamal();
        boolean isVerified = el.verifySignature(Signature.x, Signature.y, hash, publicKey);
        if(isVerified)
            jLabel14.setText("Verified!");
        else
            jLabel14.setText("Invalid!");
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        RubikCipher rc = new RubikCipher();
        String plainText = rc.EcbDecrypt(cm.emails.get(selectedRowInbox)[2], encKey.getText());
        
//        JOptionPane.showConfirmDialog(null, plainText, "Decrypted text", JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION);
        MessageBody.setText(plainText);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        int returnVal = jFileChooser1.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            jTextField4.setText("");
            File publicKeyFile = jFileChooser1.getSelectedFile();
            jLabel13.setText(publicKeyFile.getName());
            Charset charset = Charset.forName("UTF-8");
            try {
                BufferedReader reader = Files.newBufferedReader(publicKeyFile.toPath(), charset);
                String pubKey = reader.readLine();
                
                int i=0; String r="";
                while(pubKey.charAt(i) != ' ') {
                    r += pubKey.charAt(i);
                    ++i;
                }
                ++i;
                
                String s="";
                for(int j=i;j<pubKey.length();++j) {
                    s += pubKey.charAt(j);
                }
                
                BigPoint publicKey = new BigPoint(new BigInteger(r), new BigInteger(s));
                BigInteger temp = publicKey.x;
                BigInteger temp2 = publicKey.y;
                jTextField4.setText(temp.toString(16) + " " + temp2.toString(16));
            } catch (IOException ex) {
                System.out.println("Error reading file:" + publicKeyFile.getAbsolutePath());
            }
            
        } else {
            System.out.println("File access cancelled by user.");
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        int returnVal = jFileChooser1.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File attachment = jFileChooser1.getSelectedFile();
            AttachedFile.setText(attachment.getAbsolutePath());
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jScrollPane4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane4MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane4MouseClicked

    private void jScrollPane4MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane4MouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane4MouseReleased

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        fetchSentMail();
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        fetchDraftEmail();
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jScrollPane7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane7MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane7MouseClicked

    private void jScrollPane7MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane7MouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane7MouseReleased

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
    private javax.swing.JLabel AttachedFile;
    private javax.swing.JTextArea Body;
    private javax.swing.JEditorPane BodyDraft;
    private javax.swing.JEditorPane BodySent;
    private javax.swing.JTextField CC;
    private javax.swing.JPanel Compose;
    private javax.swing.JPanel Draft;
    private javax.swing.JTable DraftContent;
    private javax.swing.JPanel Inbox;
    private javax.swing.JTable InboxContent;
    private javax.swing.ButtonGroup MenuButton;
    private javax.swing.JEditorPane MessageBody;
    private javax.swing.JTextField Recipient;
    private javax.swing.JButton Send;
    private javax.swing.JPanel Sent;
    private javax.swing.JTable SentContent;
    private javax.swing.JLabel SomeoneDraft;
    private javax.swing.JLabel SomeoneSent;
    private javax.swing.JTextField Subject;
    private javax.swing.JLabel SubjectDraft;
    private javax.swing.JLabel SubjectSent;
    private javax.swing.JTextField encKey;
    private javax.swing.JCheckBox encryptionCheckbox;
    private javax.swing.JTextField hashField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private java.awt.Menu menu1;
    private java.awt.Menu menu2;
    private java.awt.MenuBar menuBar1;
    private javax.swing.JCheckBox signatureCheckbox;
    // End of variables declaration//GEN-END:variables
}
