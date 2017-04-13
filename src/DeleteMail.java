
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
public class DeleteMail implements Runnable {
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
    
    public DeleteMail(MainForm mf, int[] id, Session emailSession, String box, String user, String pass) throws NoSuchProviderException{
        this.id = id;
        this.store = emailSession.getStore("imaps");        
        this.user = user;
        this.pass = pass;
        this.mf = mf;
        this.box = box;
    }
    
    @Override
    public void run() {
        try {
            store.connect(host, user, pass);
            // create the folder object and open it
            Folder emailFolder = store.getFolder(box);
            emailFolder.open(Folder.READ_WRITE);

            // retrieve the messages from the folder in an array and print it
            Message message = emailFolder.getMessage(id[0]);
            // TODO add your handling code here:
            message.setFlag(Flags.Flag.DELETED, true);
                        
            emailFolder.close(false);
            store.close();
            
            mf.setHideMailInfo(true);
            mf.delete(id[0]);
            
        } catch (MessagingException ex) {
            System.out.println("can not delete mail");
            Thread.interrupted();
        } catch (Exception ex) {
            Thread.interrupted();
        }
    }
}
