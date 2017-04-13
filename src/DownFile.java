
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sj
 */
public class DownFile implements Runnable {
    
    private String user;
    private String pass;
    private int id;
    private String FileName;
    private String box;
    private JButton button;
    private Store store;
    private final String saveDirectory = "C:/Users/"+System.getProperty("user.name")+"/My Documents/Mail_Attchment/";
    
    public DownFile (JButton button, String user, String pass, int id, String FileName, String box){
        this.user = user;
        this.pass = pass;
        this.id = id;
        this.FileName = FileName;
        this.box = box;
        this.button = button;
        try {
            //create properties field
            Properties properties = new Properties();
            properties.put("mail.imap.host", "imap.gmail.com");
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.starttls.enable", "true");   
             // Setup authentication, get session
            Session session = Session.getInstance(properties);
            
    //        emailSession.setDebug(true);
            store = session.getStore("imaps");
         
        } catch (NoSuchProviderException e) {
        }
    }
    @Override
    public void run() {
        try {
            button.setIcon(new ImageIcon( getClass().getResource("/image/Loading.gif")));   
            button.revalidate();
            button.repaint();
               
            File f = new File(saveDirectory+ FileName);
            if(!f.exists()){
                System.out.println("Download file:" + saveDirectory + FileName +" :start");
                store.connect("imap.gmail.com", user, pass);
                // create the folder object and open it
                Folder emailFolder = store.getFolder(box);
                emailFolder.open(Folder.READ_ONLY);
                // retrieve the messages from the folder in an array and print it
                Message message = emailFolder.getMessage(id);
                Part p = message;
                Multipart multiPart = (Multipart) p.getContent();
                int numberOfParts = multiPart.getCount();
                for (int partCount = 0; partCount < numberOfParts; partCount++) {
                    MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);                   
                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                        // this part is attachment
                        String fileName = part.getFileName();
                        System.out.println("download: " +fileName);
                        if (fileName.equals(FileName)){
                            System.out.println("download: begin");
                            part.saveFile(saveDirectory + fileName);
                            System.out.println("download: " + saveDirectory + fileName + " :done");
                        }
                    }
                }
                emailFolder.close(false);
                store.close();
            }
            button.setIcon(null); 
            button.revalidate();
            button.repaint();
            try {
                System.out.println("Run file");
                Runtime.getRuntime().exec(new String[] {"cmd.exe", "/C", saveDirectory + FileName});
            } catch (IOException ex) {
                Logger.getLogger(ViewMail.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        } catch (MessagingException | IOException ex) {
            Logger.getLogger(ViewMail.class.getName()).log(Level.SEVERE, null, ex);
        }
        Thread.interrupted();
    }
}
