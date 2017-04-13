
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sj
 */
public class FetchingMail implements Runnable {
    private final int[] id;
    private final Store store;
    private final String box;
    private final String host = "imap.gmail.com";
    private final String user;
    private final String pass;
    private final String saveDirectory = "C:/Users/"+System.getProperty("user.name")+"/My Documents/Mail_Attchment/";
    private final MainForm mf;
    
    private String mes;
    private String type;
    private final JPanel attachment;
    
    
    public FetchingMail(MainForm mf, int[] id, Session emailSession, String box, String user, String pass, JPanel attachment) throws NoSuchProviderException{
        this.attachment = attachment;
        attachment.removeAll();
        this.id = id;
        this.store = emailSession.getStore("imaps");        
        this.user = user;
        this.pass = pass;
        this.mf = mf;
        this.box = box;
        type = "text/html";
        
    }
    
    @Override
    public void run() {
        try {
            store.connect(host, user, pass);
            // create the folder object and open it
            Folder emailFolder = store.getFolder(box);
            emailFolder.open(Folder.READ_WRITE);

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages(id);
            Message message = messages[0];
            // TODO add your handling code here:
            message.setFlag(Flags.Flag.SEEN, true);
            
            Address[] a;
            
            String from = "";
            if(!box.equals(mf.listbox[1])){
                if (message.getFrom().length > 1) {
                    a=message.getFrom();
                    for (Address f:a)
                       if (f.toString()!="") from = from+" , "+f;
                }else if (message.getFrom()[0].toString().contains("=?UTF-8?")){
                    String[] temp = message.getFrom()[0].toString().split(" ");
                    from = temp[1].replace("<", "").replace(">", "");                
                }else from = message.getFrom()[0].toString();
            }else{
                if (message.getAllRecipients()[0].toString().contains("=?UTF-8?")){
                    String[] temp = message.getAllRecipients()[0].toString().split(" ");
                    from = temp[1].replace("<", "").replace(">", "");                
                }else from = message.getAllRecipients()[0].toString();
            }
            
            String bcc = "";
            if (message.getRecipients(Message.RecipientType.TO).length > 1) {
                a = message.getRecipients(Message.RecipientType.TO);
                for (Address b:a)
                    if (b.toString()!="")
                        if(b.toString().contains("=?UTF-8?")){
                            String[] temp = b.toString().split(" ");
                            String name = temp[1];
                            name = name.replace("<", "").replace(">", "");     
                            
                            if (name != user){
                                if(bcc!=""){
                                    bcc = bcc+ " , " +name;
                                }else bcc=name;
                            }
                        }
            }else if (message.getRecipients(Message.RecipientType.TO)[0].toString().contains("=?UTF-8?")){
                String[] temp = message.getRecipients(Message.RecipientType.TO)[0].toString().split(" ");
                bcc = temp[1].replace("<", "").replace(">", "");                
            }else bcc = message.getRecipients(Message.RecipientType.TO)[0].toString();
            
            String subject = message.getSubject();
            
            writePart(message); 
            
            mf.fetchingMail(id[0], from, bcc, subject, mes, type);
            
            emailFolder.close(false);
            store.close();

        } catch (MessagingException | IOException ex) {
            Logger.getLogger(ViewMail.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ViewMail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void writePart(Part p) throws Exception {      
        String contentType = p.getContentType();
        String messageContent = "";
        String attachFiles = "";
        
        if (contentType.contains("multipart")) {
            // content may contain attachments
            Multipart multiPart = (Multipart) p.getContent();
            int numberOfParts = multiPart.getCount();
             System.out.println("***"+numberOfParts);            
            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                
                //System.out.println("#M>"+partCount+"-"+part.getContentType()+"\n"+part.getContent().toString());
                
                if (part.getContentType().contains("multipart"))
                    writePart(part);
                else if (part.getContentType().contains("TEXT/PLAIN")){     
                    type = "text/plain";                   
                    Object content = part.getContent();   
                    if (content != null && content.toString().trim()!="") {
                        messageContent = content.toString();
                        System.out.println("#1");
                    }    
                }
                else if (part.getContentType().contains("TEXT/HTML")){                      
                    if (part.getContent().toString().contains("<!DOCTYPE")){
                        messageContent = part.getContent().toString();
                        type = "text/html";
                         System.out.println("#2");
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
            //System.out.println("#T>"+p.getContentType());
                    
            Object content = p.getContent();
            if (content != null && content.toString().trim()!="") {
                messageContent = content.toString();
                type = "text/plain";   
                 System.out.println("#4");
            }    
        } else if (contentType.contains("TEXT/HTML")) {
            
            Object content = p.getContent();
            if (content != null && content.toString().contains("<!DOCTYPE")) {
                messageContent = content.toString();
                type = "text/html";
                 System.out.println("#5");
            }           
        }
        
        if (!"".equals(messageContent.trim())){
            System.out.println("#end - "+type+" - ["+messageContent+"]");
            mes = messageContent;
        }
   }
    
    private void AttachFileButton(String fileName){
        JButton aFile = new JButton();
        aFile.setText(fileName); 
        
        aFile.setContentAreaFilled(false);
        aFile.setForeground(Color.WHITE);
        aFile.setBackground(new java.awt.Color(51,51,51));
        aFile.setOpaque(true);
        aFile.setBorderPainted(false);
        
        
        aFile.addMouseListener(new java.awt.event.MouseAdapter() {                        
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                //tra thu muc 
                File dir = new File(saveDirectory);    
                // attempt to create the directory here
                if (dir.exists())
                {
                  // creating the directory succeeded
                  System.out.println("directory exists successfully");
                }
                else
                {
                  dir.mkdir();
                  System.out.println("New directory was created successfully");
                }      
                //mở luồng down file
                (new Thread(new DownFile(aFile, user, pass, id[0], fileName, box))).start();            
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {                           
                aFile.setBackground( new java.awt.Color(51,102,102));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                aFile.setBackground(new java.awt.Color(51,51,51));
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                aFile.setBackground( new java.awt.Color(204,255,255));
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {                            
                aFile.setBackground( new java.awt.Color(51,51,51));
            }
        });
        attachment.add(aFile);
        System.out.println("add file");
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
}
