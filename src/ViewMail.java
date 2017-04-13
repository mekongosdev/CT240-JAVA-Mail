/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;


import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

/**
 *
 * @author Sj
 */
public class ViewMail extends javax.swing.JFrame {
    private int[] id;
    private Store store;
    private String box;
    private String host = "imap.gmail.com";
    private String user;
    private String pass;
    private String saveDirectory = "C:/Users/"+System.getProperty("user.name")+"/My Documents/Mail_Attchment/";
    /**
     * Creates new form ViewMail
     */
    public ViewMail() {
        initComponents();
    }
    public ViewMail(int[] id, Session emailSession, String box, String user, String pass) throws NoSuchProviderException {
        this.id = id;
        this.store = emailSession.getStore("imaps");
        this.box = box;
        this.user = user;
        this.pass = pass;
        initComponents();
    }
    
    public void writePart(Part p) throws Exception {      
        String contentType = p.getContentType();
        String messageContent = "";
        String attachFiles = "";
        
        if (contentType.contains("multipart")) {
            // content may contain attachments
            Multipart multiPart = (Multipart) p.getContent();
            int numberOfParts = multiPart.getCount();
             System.out.println(numberOfParts);            
            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                System.out.println("#M>"+partCount+"-"+part.getContentType()+"\n"+part.getContent().toString());
                if (part.getContentType().contains("multipart"))
                    writePart(part);
                else if (part.getContentType().contains("TEXT/PLAIN")){     
                    this.tMessage.setContentType("text/plain");                   
                    messageContent = part.getContent().toString();                   
                }
                else if (part.getContentType().contains("TEXT/HTML")){                      
                    if (part.getContent().toString().contains("<!DOCTYPE")){
                        messageContent = part.getContent().toString();
                        this.tMessage.setContentType("text/html");
                    }
                }                 
                else if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    // this part is attachment
                    String fileName = part.getFileName();
                    attachFiles += fileName + ", ";
                    AttachFileButton(fileName);
                    //part.saveFile(saveDirectory + File.separator + fileName);
                }else if (Part.INLINE.equalsIgnoreCase(part.getDisposition())) {
                    // this part is attachment
                    String fileName = part.getFileName();
                    attachFiles += fileName + ", ";
                   // mbp.saveFile(saveDirectory + File.separator + fileName);
                }
            }

           
        } else if (contentType.contains("TEXT/PLAIN") ){
            System.out.println("#T>"+p.getContentType());
            this.tMessage.setContentType("text/plain");           
            Object content = p.getContent();
            if (content != null) {
                messageContent = content.toString();
            }    
        } else if (contentType.contains("TEXT/HTML")) {
            this.tMessage.setContentType("text/html");
            Object content = p.getContent();
            if (content != null) {
                messageContent = content.toString();
            }           
        }
        System.out.println("#end - "+messageContent);
        if (messageContent != ""){
            tMessage.setText(messageContent);
        }
   }
    
    private void AttachFileButton(String fileName){
        JButton aFile = new JButton();
        aFile.setText(fileName); 
        aFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //tra thu muc 
                File dir = new File(saveDirectory);    
                // attempt to create the directory here
                boolean successful = dir.mkdir();
                if (successful)
                {
                  // creating the directory succeeded
                  System.out.println("directory was created successfully");
                }
                else
                {
                  // creating the directory failed
                  System.out.println("failed trying to create the directory");
                }      
                
                DownFile(fileName);
                try {
                    Runtime.getRuntime().exec(new String[] {"cmd.exe", "/C", saveDirectory + fileName});
                } catch (IOException ex) {
                    Logger.getLogger(ViewMail.class.getName()).log(Level.SEVERE, null, ex);
                }
            }                
        });
        attachment.add(aFile);
    }
    private void DownFile(String file){
        try {
            System.out.println("Download file:" + saveDirectory + file +" :start");
            store.connect(host, user, pass);
            // create the folder object and open it
            Folder emailFolder = store.getFolder(box);
            emailFolder.open(Folder.READ_ONLY);
            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages(id);
            Part p = messages[0];
            Multipart multiPart = (Multipart) p.getContent();
            int numberOfParts = multiPart.getCount();
            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);                   
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    // this part is attachment
                   
                    String fileName = part.getFileName();
                    System.out.println("download: " +fileName);
                    if (fileName.equals(file)){
                        System.out.println("download: begin");
                        part.saveFile(saveDirectory + fileName);
                        System.out.println("download: " + saveDirectory + fileName + " :done");
                    }
                }
//                    }else if (Part.INLINE.equalsIgnoreCase(part.getDisposition())) {
//                        // this part is attachment
//                        String fileName = part.getFileName();                        
//                        mbp.saveFile(saveDirectory + File.separator + fileName);
//                    }
            }
            
            emailFolder.close(false);
            store.close();
        
        } catch (MessagingException | IOException ex) {
            Logger.getLogger(ViewMail.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ViewMail.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tFrom = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tTitle = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tMessage = new javax.swing.JTextPane();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        attachment = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Xem Mail");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText("Từ:");

        jLabel3.setText("Nội dung:");

        tFrom.setEditable(false);

        jLabel4.setText("Tiêu đề:");

        tTitle.setEditable(false);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        tMessage.setEditable(false);
        tMessage.setContentType("text/html"); // NOI18N
        tMessage.setToolTipText("");
        tMessage.setAutoscrolls(false);
        tMessage.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tMessage.setDoubleBuffered(true);
        jScrollPane2.setViewportView(tMessage);

        jLabel2.setText("Đính kèm:");

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        attachment.setBackground(new java.awt.Color(255, 255, 255));
        attachment.setLayout(new javax.swing.BoxLayout(attachment, javax.swing.BoxLayout.LINE_AXIS));
        jScrollPane1.setViewportView(attachment);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tTitle)
                            .addComponent(tFrom))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addContainerGap(616, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2))
                        .addGap(11, 11, 11))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            store.connect(host, user, pass);
            // create the folder object and open it
            Folder emailFolder = store.getFolder(box);
            emailFolder.open(Folder.READ_ONLY);

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages(id);
            Message message = messages[0];
            // TODO add your handling code here:
//            message.setFlag(Flags.Flag.SEEN, true);
            tFrom.setText(message.getFrom()[0].toString());
            tTitle.setText(message.getSubject());
            
            writePart(message); 
            
            tMessage.setCaretPosition(0);
            
            emailFolder.close(false);
            store.close();

        } catch (MessagingException | IOException ex) {
            Logger.getLogger(ViewMail.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ViewMail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_formWindowClosed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
     
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ViewMail().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel attachment;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField tFrom;
    private javax.swing.JTextPane tMessage;
    private javax.swing.JTextField tTitle;
    // End of variables declaration//GEN-END:variables
}
