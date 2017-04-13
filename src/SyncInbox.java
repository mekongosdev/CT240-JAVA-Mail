
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
public final class SyncInbox implements Runnable {

    private int limit = 20;
    private int sleeptime = 0;
    
    private Session Inbox;
    private Store inboxStore;
    private final String user;
    private final String pass;
    private final String host;
    private final String box;
    
    private List<Mes> MessagesSync;    
    
    MainForm mf;
    ConnectSql connect;
    public SyncInbox(MainForm mf, String username, String password, String host){  
        this.MessagesSync = new ArrayList<>();
        connect = new ConnectSql();
        box = "INBOX";
        Properties properties = new Properties();
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.starttls.enable", "true");   
         // Setup authentication, get session
        Inbox = Session.getInstance(properties);
        
//        emailSession.setDebug(true);
        try{
        //create the IMAP store object and connect with the imap server
        inboxStore = Inbox.getStore("imaps"); 
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
        while(true){
            try{
                System.out.println("Syncing.......");
                inboxStore.connect(host, user, pass);
                Folder inbox = inboxStore.getDefaultFolder().getFolder(box);
                inbox.open(Folder.READ_ONLY);

                Message[] messages = inbox.getMessages();
                mf.isInbox(box);
                mf.setNumberMail(messages.length, box);
                mf.setUnread(inbox.getUnreadMessageCount());
                //JButton[] MailButton = new JButton[messages.length];
                for (int i = messages.length - 1, n = (i - limit)>=0?(i- limit):-1 ; i > n; i--) {                                       
//                    System.out.println("Get message "+(i+1));
//                    
                    Message message = messages[i];           

                    Date sentDate = message.getSentDate();
                    String FromName = message.getFrom()[0].toString();
                    String subject = message.getSubject();

                    Flags seen = message.getFlags();

                    if(FromName.contains("=?UTF-8?")){
                        String[] temp = FromName.split(" ");
                        FromName = temp[1];
                        FromName = FromName.replace("<", "").replace(">", "");
                    }                               
                    int[] id = {(i+1)};

                    MessagesSync.add(new Mes(id, sentDate.toString(), FromName, subject, seen.contains(Flags.Flag.SEEN)));
                    if(!mf.isSynced(box)){
                        List<Mes> temp = new ArrayList<>();
                        temp.add(new Mes(id, sentDate.toString(), FromName, subject, seen.contains(Flags.Flag.SEEN)));
                        mf.showMailSync(temp, box);
                    }
                }              
                inbox.close(false);
                inboxStore.close();
            }catch(MessagingException e){
                System.out.println("not Syncing INBOX....");
            }
                System.out.println("check: "+mf.haveChanged(MessagesSync, box));
            if(mf.haveChanged(MessagesSync, box)){
                if (mf.isSynced(box)){
                    mf.clearMessageBox(box);                
                    mf.showMailSync(MessagesSync, box);
                }
                mf.setMailSynced(MessagesSync,box);
                syncDB();
            }
            MessagesSync.clear();
            try {
                System.out.println("sleep sync to 30s....");
                Thread.sleep(sleeptime*1000);
            } catch (InterruptedException ex) {
                System.out.println("not sleep");
            }
        }
    }
    private void syncDB(){
        connect.deleteInbox();
        for (Mes message: MessagesSync){            
            connect.insertInbox(message.id[0],message.date.toString(),message.from,message.subject,message.seen);
        }
    }
}
