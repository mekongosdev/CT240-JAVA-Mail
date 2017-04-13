
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sj
 */
public final class SyncSpam implements Runnable {
    
    private int limit = 20;
    
    private Session session;
    private Store Store;
    private final String user;
    private final String pass;
    private final String host;
    private final String box;
    
    private List<Mes> MessagesSync;    
    
    MainForm mf;
    ConnectSql connect;
    public SyncSpam(MainForm mf, String username, String password, String host){  
        this.MessagesSync = new ArrayList<>();
        connect = new ConnectSql();
        box = mf.listbox[2];
        Properties properties = new Properties();
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.starttls.enable", "true");   
         // Setup authentication, get session
        session = Session.getInstance(properties);
        
//        emailSession.setDebug(true);
        try{
        //create the IMAP store object and connect with the imap server
        Store = session.getStore("imaps"); 
        }catch (NoSuchProviderException e){
            System.out.println("not create connect");
        }
        this.mf = mf;
        this.user= username;
        this.pass = password;
        this.host = host;
    }
    
    @Override
    public void run() {        
        try{
            System.out.println("Syncing Spam.......");
            Store.connect(host, user, pass);
            Folder folder = Store.getDefaultFolder().getFolder(box);
            folder.open(Folder.READ_ONLY);

            Message[] messages = folder.getMessages();
            mf.isInbox(box);
            mf.setNumberMail(messages.length, box);
            //JButton[] MailButton = new JButton[messages.length];
            for (int i = messages.length - 1, n = (i - limit)>=0?(i- limit):-1 ; i > n; i--) {                                       
                System.out.println("Get message "+(i+1));

                Message message = messages[i];           

                Date sentDate = message.getSentDate();
                String FromName = message.getFrom()[0].toString();
                String subject = message.getSubject();

                if(FromName.contains("=?UTF-8?")){
                    String[] temp = FromName.split(" ");
                    FromName = temp[1];
                    FromName = FromName.replace("<", "").replace(">", "");
                }                               
                int[] id = {(i+1)};

                MessagesSync.add(new Mes(id, sentDate.toString(), FromName, subject, true));
                if(!mf.isSynced(box)){
                    List<Mes> temp = new ArrayList<>();
                    temp.add(new Mes(id, sentDate.toString(), FromName, subject, true));
                    mf.showMailSync(temp, box);
                }
            }              
            folder.close(false);
            Store.close();
        }catch(MessagingException e){
            System.out.println("not Syncing Spam....");
        }
        if (mf.isSynced(box)){
            mf.clearMessageBox(box);
            mf.showMailSync(MessagesSync, box);
        }
        mf.setMailSynced(MessagesSync,box);
        this.syncDB();
        System.out.println("sync end....");
        Thread.interrupted();
    }
    private void syncDB(){
        System.out.println("save DB");
        connect.deleteSpam();
        for (Mes message: MessagesSync){
            connect.insertSpam(message.id[0],message.date.toString(),message.from,message.subject);
        }
        System.out.println("saved");
    }
}
