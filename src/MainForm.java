/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Color;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.swing.border.LineBorder;
/**
 *
 * @author Sj
 */
public class MainForm extends javax.swing.JFrame {
    /**
     * Creates new form MainForm
     */ 
    private String host;// change accordingly
    private String mailStoreType;
    private String username;// change accordingly
    private String password;// change accordingly
    private Properties properties;
    private Session emailSession;
    private Store store;
    
    private String box;
    public String[] listbox;
    
    private int idMail;
    
    //luong xu li
    private Thread sync;
    private Thread fetching;  
    private Thread syncSent;
    private Thread syncSpam;
    private Thread syncTrash;
    
    
    private List<Mes> inbox = new ArrayList<Mes>();
    private List<Mes> sent = new ArrayList<Mes>();
    private List<Mes> trash = new ArrayList<Mes>();
    private List<Mes> spam = new ArrayList<Mes>();

    private ConnectSql conn;
        
    public MainForm() {
        initComponents();
    }
    
    public MainForm(String user, String pass) {
        initComponents();
        username = user;
        password = pass;
    }
       
    
    //---------------------------------------------------------------------------------
    //xử lý luồng đồng bộ mail
          
    public void clearMessageBox(String box){
        System.out.println("<#>"+box);
        if(this.box.equals(box)){
            jPanel1.removeAll();
            jPanel1.revalidate();
            jPanel1.repaint();
        }
    }
    
    public void showMailSync(List<Mes> MessagesSync, String box){  
        if(this.box.equals(box)) {    
            if (MessagesSync.size()==1) Synced.setText((Integer.parseInt(Synced.getText())+1)+"");
            else Synced.setText(MessagesSync.size()+"");
            if(!MessagesSync.isEmpty()){
                for(Mes message:MessagesSync){
                    createMessageBox(message);
                }
            }
        }        
    }
    public void setMailSynced(List<Mes> MessagesSync, String box){
        if (box.equals(listbox[0])){
            System.out.println(listbox[0]);
            inbox.clear();
            inbox.addAll(MessagesSync);
        }
        else if (box.equals(listbox[1])){
            System.out.println(listbox[1]);
            sent.clear();
            sent.addAll(MessagesSync);
        }
        else if (box.equals(listbox[2])){
            System.out.println(listbox[2]);
            spam.clear();
            spam.addAll(MessagesSync);
        }
        else if (box.equals(listbox[3])){
            System.out.println(listbox[3]);
            trash.clear();
            trash.addAll(MessagesSync);
        }
    }
    public void setListBox(String[] list){
        listbox = new String[4];
        for (int i=0; i<4; i++){
            listbox[i] = list[i];
        }
    }
    public boolean isSynced(String box){        
       if(this.box.equals(box)) {
           //System.out.println(">>"+box);
           return !inbox.isEmpty();
       }
       else if(this.box.equals(box)) {
           //System.out.println(">>"+box);
           return !sent.isEmpty();
       }
       else if(this.box.equals(box)) {
           //System.out.println(">>"+box);
           return !spam.isEmpty();
       }
       else if(this.box.equals(box)) {
           //System.out.println(box);
           return !trash.isEmpty();
       }
       else {return true;} 
    }
    
    private void createMessageBox(Mes message){
        createMessageBox(message.id, message.from, message.subject, message.date, message.seen);
    }
     public void createMessageBox(int[] id, String from, String subject, String sentdate, boolean seen){
        java.awt.Color color = new java.awt.Color(51,51,51);
        if (!seen){
            color = new java.awt.Color(51,204,204);
        }  
        JPanel MessageBox = new JPanel();
        MessageBox.setLayout(new BoxLayout(MessageBox, BoxLayout.Y_AXIS));
        MessageBox.setBackground(color);
        MessageBox.setForeground(Color.white);    
        //attachment.setPreferredSize(new Dimension(440,20));
        MessageBox.setBorder(new LineBorder(Color.white, 1));                   

        JButton MailButton = new JButton();

        MailButton.setContentAreaFilled(false);
        MailButton.setForeground(Color.WHITE);
        MailButton.setBackground(color);
        MailButton.setOpaque(true);
        MailButton.setBorderPainted(false);       
        if (box.equals(listbox[1])){
            MailButton.setText("<html>"+sentdate+"<br><b>To: " + from +"</b><br>Subject: " + subject+"</html>");
        }else MailButton.setText("<html>"+sentdate+"<br><b>From: " + from +"</b><br>Subject: " + subject+"</html>");
        MailButton.setHorizontalAlignment(SwingConstants.LEFT);                                

        MailButton.addMouseListener(new java.awt.event.MouseAdapter() {
            java.awt.Color BGcolor;
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    //                                View(id, emailSession, "INBOX", username, password);
                    MailInfo.setVisible(false);
                    attachment.removeAll();
                    fetching = new Thread(new FetchingMail(MainForm.this,id, emailSession, box, username, password, attachment));
                    fetching.start();
                    if (!seen && Integer.parseInt(Unread.getText())>0)
                        Unread.setText("" + (Integer.parseInt(Unread.getText())-1));
                } catch (NoSuchProviderException ex) {
                    Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                BGcolor = MailButton.getBackground();
                MailButton.setBackground( new java.awt.Color(51,102,102));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                MailButton.setBackground(BGcolor);
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                MailButton.setBackground( new java.awt.Color(204,255,255));
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                BGcolor = new java.awt.Color(51,51,51);
                MailButton.setBackground( BGcolor );
            }
        });

        MessageBox.add(MailButton);
        MessageBox.revalidate();

        jPanel1.add(MessageBox);
        jPanel1.revalidate();
        jPanel1.repaint();
        jScrollPane1.revalidate();
        jScrollPane1.repaint();
    }
    
    //-------------------------------------------------------
    //xử lý luồn lấy nội dung mail
    public void fetchingMail(int id, String from, String bcc, String subject, String message, String type){
        this.MailInfo.setVisible(true);
        this.From.setText(from);
        this.Bcc.setText(bcc);
        this.Subject.setText(subject);
        System.out.println(type);
        this.Mes.setContentType(type);
        this.Mes.setText(message);
        //đưa con trỏ về đầu dòng
        this.Mes.setCaretPosition(0);     
        if (box.equals(listbox[1])){
            jLabel7.setText("To:");
        }else  jLabel7.setText("From:");
        this.idMail = id;
    }
    public boolean haveChanged(List<Mes> list, String box){        
        if(this.box.equals(box)) {
            return checkList(inbox, list);
        }
        else if(this.box.equals(box)) {
            //System.out.println(">>"+box);
            return checkList(sent, list);
        }
        else if(this.box.equals(box)) {
            //System.out.println(">>"+box);
            return checkList(spam, list);
        }
        else if(this.box.equals(box)) {
            //System.out.println(box);
            return checkList(trash, list);
        }
        else {return false;} 
    }
    private boolean checkList(List<Mes> list, List<Mes> list2){
        if (list.size() != list2.size()) return true;
        else {
            for (int i = 0, n = list.size(); i<n; i++){
                //System.out.println(list.get(i).equals(list2.get(i)));
                if (!list.get(i).equals(list2.get(i))){                    
                    return true;
                }
            }
        }
        return false;
    }
    
    public void setNumberMail(int n, String box){
        if (box.equals(this.box))
            NumberMails.setText(n+"");
    }
    public void setHideMailInfo(boolean b){
        MailInfo.setVisible(!b);
    }
    public void isInbox(String b){
        if(box.equals(listbox[0])){
            Unread.setVisible(true);
            jLabel7.setVisible(true);
        }
        else {
            Unread.setVisible(false);
            jLabel7.setVisible(false);
        }
    }
    public void setUnread(int n){
        Unread.setText(n+"");
    }
    public void delete(int id){
        if (box.equals(listbox[0])){
            System.out.println(listbox[0]);
            deleted(id,inbox);
        }
        else if (box.equals(listbox[1])){
            System.out.println(listbox[1]);
            deleted(id,sent);
        }
        else if (box.equals(listbox[2])){
            System.out.println(listbox[2]);
            deleted(id,spam);
        }
        else if (box.equals(listbox[3])){
            System.out.println(listbox[3]);
            deleted(id,trash);
        }
    }
    private void deleted(int id, List<Mes> list){
        List<Mes> temp = new ArrayList<Mes>();
        for(Mes mes: list){
            if(mes.id[0] < id){
                temp.add(new Mes(mes.id, mes.date, mes.from, mes.subject, mes.seen));
            }else if(mes.id[0] > id){
                int[] i = {mes.id[0]-1};
                temp.add(new Mes(i, mes.date, mes.from, mes.subject, mes.seen));
            }
        }
        setMailSynced(temp, box);
        clearMessageBox(box);
        showMailSync(temp, box);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Inbox = new javax.swing.JButton();
        SendBox = new javax.swing.JButton();
        Spam = new javax.swing.JButton();
        SendMail = new javax.swing.JButton();
        Trash = new javax.swing.JButton();
        Logout = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        refeshButton = new javax.swing.JButton();
        CloseButton = new javax.swing.JLabel();
        MinimizeButton = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        NumberMails = new javax.swing.JLabel();
        Unread = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        Label = new javax.swing.JLabel();
        Synced = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        MailInfo = new javax.swing.JPanel();
        Reply = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        attachment = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        From = new javax.swing.JTextField();
        Subject = new javax.swing.JTextField();
        Bcc = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Mes = new javax.swing.JTextPane();
        Forward = new javax.swing.JButton();
        Delete = new javax.swing.JButton();
        MailContent = new javax.swing.JLabel();
        Border = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("JavaMail");
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(782, 534));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Inbox.setBackground(new java.awt.Color(51, 51, 51));
        Inbox.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        Inbox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/UI/inbox.png"))); // NOI18N
        Inbox.setToolTipText("INBOX");
        Inbox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Inbox.setBorderPainted(false);
        Inbox.setContentAreaFilled(false);
        Inbox.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Inbox.setDefaultCapable(false);
        Inbox.setOpaque(true);
        Inbox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                InboxMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                InboxMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                InboxMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                InboxMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                InboxMouseReleased(evt);
            }
        });
        getContentPane().add(Inbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 90, 50, 50));

        SendBox.setBackground(new java.awt.Color(51, 102, 102));
        SendBox.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/UI/sent.png"))); // NOI18N
        SendBox.setToolTipText("Sent");
        SendBox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        SendBox.setBorderPainted(false);
        SendBox.setContentAreaFilled(false);
        SendBox.setHideActionText(true);
        SendBox.setOpaque(true);
        SendBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SendBoxMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                SendBoxMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                SendBoxMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                SendBoxMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                SendBoxMouseReleased(evt);
            }
        });
        getContentPane().add(SendBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 140, 50, 50));

        Spam.setBackground(new java.awt.Color(51, 102, 102));
        Spam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/UI/spam.png"))); // NOI18N
        Spam.setToolTipText("Spam");
        Spam.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Spam.setBorderPainted(false);
        Spam.setContentAreaFilled(false);
        Spam.setOpaque(true);
        Spam.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SpamMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                SpamMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                SpamMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                SpamMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                SpamMouseReleased(evt);
            }
        });
        getContentPane().add(Spam, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 190, 50, 50));

        SendMail.setBackground(new java.awt.Color(152, 204, 204));
        SendMail.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        SendMail.setForeground(new java.awt.Color(255, 255, 255));
        SendMail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/UI/SentButton.png"))); // NOI18N
        SendMail.setToolTipText("Send Mail");
        SendMail.setBorderPainted(false);
        SendMail.setContentAreaFilled(false);
        SendMail.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        SendMail.setOpaque(true);
        SendMail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                SendMailMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                SendMailMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                SendMailMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                SendMailMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                SendMailMouseReleased(evt);
            }
        });
        getContentPane().add(SendMail, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 24, 50, 66));

        Trash.setBackground(new java.awt.Color(51, 102, 102));
        Trash.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/UI/trash.png"))); // NOI18N
        Trash.setToolTipText("Trash ");
        Trash.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Trash.setBorderPainted(false);
        Trash.setContentAreaFilled(false);
        Trash.setOpaque(true);
        Trash.setPreferredSize(new java.awt.Dimension(50, 50));
        Trash.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TrashMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                TrashMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                TrashMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                TrashMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                TrashMouseReleased(evt);
            }
        });
        getContentPane().add(Trash, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 240, 50, 50));

        Logout.setBackground(new java.awt.Color(255, 102, 102));
        Logout.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        Logout.setForeground(new java.awt.Color(255, 255, 255));
        Logout.setText("Logout");
        Logout.setToolTipText("Logout");
        Logout.setBorder(null);
        Logout.setBorderPainted(false);
        Logout.setContentAreaFilled(false);
        Logout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Logout.setMargin(new java.awt.Insets(0, 0, 0, 0));
        Logout.setOpaque(true);
        Logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LogoutMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                LogoutMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                LogoutMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                LogoutMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                LogoutMouseReleased(evt);
            }
        });
        getContentPane().add(Logout, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 510, 50, 20));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(jPanel1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(55, 70, 290, 460));
        jScrollPane1.getAccessibleContext().setAccessibleDescription("");

        refeshButton.setBackground(new java.awt.Color(51, 51, 51));
        refeshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/UI/Refesh.png"))); // NOI18N
        refeshButton.setToolTipText("Refesh");
        refeshButton.setBorderPainted(false);
        refeshButton.setContentAreaFilled(false);
        refeshButton.setOpaque(true);
        refeshButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                refeshButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refeshButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                refeshButtonMouseExited(evt);
            }
        });
        getContentPane().add(refeshButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 30, 30, 30));

        CloseButton.setBackground(new java.awt.Color(51, 51, 51));
        CloseButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        CloseButton.setForeground(new java.awt.Color(255, 255, 255));
        CloseButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CloseButton.setLabelFor(this);
        CloseButton.setText("X");
        CloseButton.setToolTipText("Close");
        CloseButton.setMaximumSize(new java.awt.Dimension(24, 24));
        CloseButton.setMinimumSize(new java.awt.Dimension(24, 24));
        CloseButton.setOpaque(true);
        CloseButton.setPreferredSize(new java.awt.Dimension(24, 24));
        CloseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CloseButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                CloseButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                CloseButtonMouseExited(evt);
            }
        });
        getContentPane().add(CloseButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 0, -1, -1));

        MinimizeButton.setBackground(new java.awt.Color(51, 51, 51));
        MinimizeButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        MinimizeButton.setForeground(new java.awt.Color(255, 255, 255));
        MinimizeButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MinimizeButton.setLabelFor(this);
        MinimizeButton.setText("_");
        MinimizeButton.setToolTipText("Minimize");
        MinimizeButton.setMaximumSize(new java.awt.Dimension(24, 24));
        MinimizeButton.setMinimumSize(new java.awt.Dimension(24, 24));
        MinimizeButton.setOpaque(true);
        MinimizeButton.setPreferredSize(new java.awt.Dimension(24, 24));
        MinimizeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                MinimizeButtonMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                MinimizeButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                MinimizeButtonMouseExited(evt);
            }
        });
        getContentPane().add(MinimizeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(736, 0, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Number mails:");
        jLabel2.setToolTipText(null
        );
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, -1, -1));

        NumberMails.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        NumberMails.setForeground(new java.awt.Color(255, 255, 255));
        NumberMails.setText("0");
        NumberMails.setToolTipText(null
        );
        getContentPane().add(NumberMails, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 29, -1, -1));

        Unread.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        Unread.setForeground(new java.awt.Color(255, 255, 255));
        Unread.setText("0");
        Unread.setToolTipText(null
        );
        getContentPane().add(Unread, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 50, -1, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Unread:");
        jLabel3.setToolTipText(null
        );
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, -1, -1));

        Label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        Label.setForeground(new java.awt.Color(255, 255, 255));
        Label.setText("Sync (limit = 20):");
        Label.setToolTipText(null
        );
        getContentPane().add(Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 50, -1, -1));

        Synced.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        Synced.setForeground(new java.awt.Color(255, 255, 255));
        Synced.setText("0");
        Synced.setToolTipText(null
        );
        getContentPane().add(Synced, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, -1, -1));

        jLabel1.setBackground(new java.awt.Color(51, 51, 51));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setLabelFor(this);
        jLabel1.setText("   JavaMail");
        jLabel1.setOpaque(true);
        jLabel1.setPreferredSize(new java.awt.Dimension(69, 24));
        jLabel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jLabel1MouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jLabel1MouseMoved(evt);
            }
        });
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 736, -1));

        jLabel4.setBackground(new java.awt.Color(51, 102, 102));
        jLabel4.setToolTipText(null);
        jLabel4.setOpaque(true);
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 24, 50, 509));

        jLabel5.setBackground(new java.awt.Color(61, 61, 61));
        jLabel5.setToolTipText(null);
        jLabel5.setOpaque(true);
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 24, 300, 509));

        MailInfo.setBackground(new java.awt.Color(102, 102, 102));
        MailInfo.setPreferredSize(new java.awt.Dimension(433, 509));

        Reply.setBackground(new java.awt.Color(51, 51, 51));
        Reply.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/UI/reply.png"))); // NOI18N
        Reply.setToolTipText("Reply");
        Reply.setBorderPainted(false);
        Reply.setContentAreaFilled(false);
        Reply.setOpaque(true);
        Reply.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ReplyMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ReplyMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ReplyMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ReplyMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ReplyMouseReleased(evt);
            }
        });

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        attachment.setBackground(new java.awt.Color(255, 255, 255));
        attachment.setLayout(new javax.swing.BoxLayout(attachment, javax.swing.BoxLayout.LINE_AXIS));

        jLabel12.setBackground(new java.awt.Color(102, 102, 102));
        jLabel12.setToolTipText(null
        );
        jLabel12.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jLabel12.setOpaque(true);
        attachment.add(jLabel12);

        jScrollPane4.setViewportView(attachment);

        From.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));

        Subject.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));

        Bcc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Bcc :");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("From :");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Subject:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Message:");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Attachments:");
        jLabel10.setToolTipText("");

        jScrollPane5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        Mes.setEditable(false);
        Mes.setContentType("text/html"); // NOI18N
        Mes.setToolTipText("");
        Mes.setAutoscrolls(false);
        Mes.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Mes.setDoubleBuffered(true);
        jScrollPane5.setViewportView(Mes);

        Forward.setBackground(new java.awt.Color(51, 51, 51));
        Forward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/UI/forward.png"))); // NOI18N
        Forward.setToolTipText("Forward");
        Forward.setBorderPainted(false);
        Forward.setContentAreaFilled(false);
        Forward.setOpaque(true);
        Forward.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ForwardMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ForwardMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ForwardMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ForwardMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                ForwardMouseReleased(evt);
            }
        });

        Delete.setBackground(new java.awt.Color(51, 51, 51));
        Delete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/UI/trash.png"))); // NOI18N
        Delete.setToolTipText("Delete");
        Delete.setBorderPainted(false);
        Delete.setContentAreaFilled(false);
        Delete.setOpaque(true);
        Delete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                DeleteMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                DeleteMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                DeleteMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                DeleteMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                DeleteMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout MailInfoLayout = new javax.swing.GroupLayout(MailInfo);
        MailInfo.setLayout(MailInfoLayout);
        MailInfoLayout.setHorizontalGroup(
            MailInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MailInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MailInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MailInfoLayout.createSequentialGroup()
                        .addGroup(MailInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(MailInfoLayout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(MailInfoLayout.createSequentialGroup()
                                .addGroup(MailInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addGroup(MailInfoLayout.createSequentialGroup()
                                        .addGap(5, 5, 5)
                                        .addComponent(Reply, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(MailInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(From)
                                    .addGroup(MailInfoLayout.createSequentialGroup()
                                        .addComponent(Forward, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(Delete, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(10, 10, 10))
                    .addGroup(MailInfoLayout.createSequentialGroup()
                        .addGroup(MailInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(MailInfoLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(MailInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addGroup(MailInfoLayout.createSequentialGroup()
                                        .addGroup(MailInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel8)
                                            .addComponent(jLabel6))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(MailInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(Bcc, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                                            .addComponent(Subject))))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(MailInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(MailInfoLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        MailInfoLayout.setVerticalGroup(
            MailInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MailInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MailInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Reply, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Forward, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Delete, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(MailInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MailInfoLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel7))
                    .addComponent(From, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(MailInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MailInfoLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel6))
                    .addComponent(Bcc, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(MailInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(Subject, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jLabel9)
                .addGap(274, 274, 274)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(MailInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(MailInfoLayout.createSequentialGroup()
                    .addGap(161, 161, 161)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(97, Short.MAX_VALUE)))
        );

        getContentPane().add(MailInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 24, -1, -1));

        MailContent.setBackground(new java.awt.Color(102, 102, 102));
        MailContent.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MailContent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/M@il.png"))); // NOI18N
        MailContent.setToolTipText(null);
        MailContent.setOpaque(true);
        MailContent.setPreferredSize(new java.awt.Dimension(433, 509));
        getContentPane().add(MailContent, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 24, 433, 509));

        Border.setToolTipText(null);
        Border.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(Border, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 23, 784, 511));

        setSize(new java.awt.Dimension(784, 535));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        box = "INBOX";
        host = "imap.gmail.com";// change accordingly
        mailStoreType = "imaps";
//        username = "sj.dev.96@gmail.com";// change accordingly
//        password = "thi3nminh";// change accordingly
        
        try {

            //create properties field
            properties = new Properties();
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.starttls.enable", "true");   
             // Setup authentication, get session
            emailSession = Session.getInstance(properties);

    //        emailSession.setDebug(true);
         
        } catch (Exception e) {
        }
         
        conn = new ConnectSql();
                
        MailInfo.setVisible(false);
        
        showMailSync(inbox, box);
        
        //khoi dong luong dong bo
        sync = new Thread(new SyncInbox(this,username, password, host));
        sync.start();
        
    }//GEN-LAST:event_formWindowOpened

    private void SendMailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SendMailMouseClicked
        SendMail sendmail = new SendMail(username, password);
        sendmail.setVisible(true);
    }//GEN-LAST:event_SendMailMouseClicked

    private void refeshButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refeshButtonMouseClicked
        if (box.equals(listbox[0])){
           sync.interrupt();
           sync = new Thread(new SyncInbox(this, username, password,host));
           sync.start();
        }
        else if (box.equals(listbox[1])){
           syncSent.interrupt();
           syncSent = new Thread(new SyncSent(this, username, password,host));
           syncSent.start();
        }
        else if (box.equals(listbox[2])){
           syncSpam.interrupt();
           syncSpam = new Thread(new SyncSpam(this, username, password,host));
           syncSpam.start();
        }
        else if (box.equals(listbox[3])){
           syncTrash.interrupt();
           syncTrash = new Thread(new SyncTrash(this, username, password,host));
           syncTrash.start();
        }
    }//GEN-LAST:event_refeshButtonMouseClicked

    //UI move window form
    private int xMouse;
    private int yMouse;
    
    private void jLabel1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        
        this.setLocation(x-xMouse, y-yMouse);   
    }//GEN-LAST:event_jLabel1MouseDragged

    private void jLabel1MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseMoved
        xMouse = evt.getX();
        yMouse = evt.getY();
    }//GEN-LAST:event_jLabel1MouseMoved

    private void MinimizeButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MinimizeButtonMouseClicked
        this.setState(Frame.ICONIFIED);
    }//GEN-LAST:event_MinimizeButtonMouseClicked

    private void MinimizeButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MinimizeButtonMouseEntered
        MinimizeButton.setBackground(new java.awt.Color(204,204,204));
    }//GEN-LAST:event_MinimizeButtonMouseEntered

    private void MinimizeButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MinimizeButtonMouseExited
        MinimizeButton.setBackground(new java.awt.Color(51,51,51));
    }//GEN-LAST:event_MinimizeButtonMouseExited

    private void CloseButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CloseButtonMouseClicked
        System.exit(0);
    }//GEN-LAST:event_CloseButtonMouseClicked

    private void CloseButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CloseButtonMouseEntered
        CloseButton.setBackground(new java.awt.Color(255,102,102));
    }//GEN-LAST:event_CloseButtonMouseEntered

    private void CloseButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CloseButtonMouseExited
        CloseButton.setBackground(new java.awt.Color(51,51,51));
    }//GEN-LAST:event_CloseButtonMouseExited

    private void SendMailMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SendMailMouseEntered
        SendMail.setBackground(new java.awt.Color(204,255,255));
    }//GEN-LAST:event_SendMailMouseEntered

    private void SendMailMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SendMailMouseExited
        SendMail.setBackground(new java.awt.Color(153,204,204));
    }//GEN-LAST:event_SendMailMouseExited

    private void SendMailMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SendMailMousePressed
        SendMail.setBackground(new java.awt.Color(240,255,255));
    }//GEN-LAST:event_SendMailMousePressed

    private void SendMailMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SendMailMouseReleased
        SendMail.setBackground(new java.awt.Color(204,255,255));
    }//GEN-LAST:event_SendMailMouseReleased

    private void refeshButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refeshButtonMouseEntered
        if (refeshButton.isEnabled())
            refeshButton.setBackground(new java.awt.Color(102, 204, 204));
    }//GEN-LAST:event_refeshButtonMouseEntered

    private void refeshButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refeshButtonMouseExited
        refeshButton.setBackground(new java.awt.Color(51,51,51));
    }//GEN-LAST:event_refeshButtonMouseExited

    private void LogoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LogoutMouseClicked
        
        sync.interrupt();   
        conn.execute("drop table if exists User");
        conn.execute("drop table if exists Inbox");
        conn.execute("drop table if exists Sent");
        conn.execute("drop table if exists Spam");
        conn.execute("drop table if exists Trash");
        conn.createTable();
        login l = new login();
        l.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_LogoutMouseClicked

    private void LogoutMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LogoutMouseEntered
        Logout.setBackground(new java.awt.Color(255,204,204));
    }//GEN-LAST:event_LogoutMouseEntered

    private void LogoutMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LogoutMouseExited
        Logout.setBackground(new java.awt.Color(255, 102, 102));
    }//GEN-LAST:event_LogoutMouseExited

    private void LogoutMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LogoutMousePressed
        Logout.setBackground(new java.awt.Color(255, 240, 240));
    }//GEN-LAST:event_LogoutMousePressed

    private void LogoutMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LogoutMouseReleased
        Logout.setBackground(new java.awt.Color(255,204,204));
    }//GEN-LAST:event_LogoutMouseReleased

    private void refeshBoxButton(){
        java.awt.Color color = new java.awt.Color(51,102,102);
        Inbox.setBackground(color);
        SendBox.setBackground(color);
        Spam.setBackground(color);
        Trash.setBackground(color);
    }
    private void InboxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InboxMouseClicked
        if (!box.equals("INBOX")){
            box = "INBOX";        
            MailInfo.setVisible(false);
            refeshBoxButton();
            Inbox.setBackground(new java.awt.Color(51,51,51));
            clearMessageBox(box);
            showMailSync(inbox, box);
        }
    }//GEN-LAST:event_InboxMouseClicked

    private void InboxMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InboxMouseEntered
        if (!box.equals("INBOX")){            
            Inbox.setBackground(new java.awt.Color(0,204,204));
        }
    }//GEN-LAST:event_InboxMouseEntered

    private void InboxMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InboxMouseExited
        if (!box.equals("INBOX")){            
            Inbox.setBackground(new java.awt.Color(51,102,102));
        }
    }//GEN-LAST:event_InboxMouseExited

    private void InboxMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InboxMousePressed
        if (!box.equals("INBOX")){            
            Inbox.setBackground(new java.awt.Color(0,255,255));
        }
    }//GEN-LAST:event_InboxMousePressed

    private void InboxMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InboxMouseReleased
        if (!box.equals("INBOX")){            
            Inbox.setBackground(new java.awt.Color(0,204,204));
        }
    }//GEN-LAST:event_InboxMouseReleased

    private void SendBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SendBoxMouseClicked
        if (!box.equals(listbox[1])){            
            box = listbox[1];
            MailInfo.setVisible(false);
            refeshBoxButton();
            SendBox.setBackground(new java.awt.Color(51,51,51));
            clearMessageBox(box);
            showMailSync(sent, box);
            syncSent =new Thread(new SyncSent(this,username,password,host));
            syncSent.start();
        }
    }//GEN-LAST:event_SendBoxMouseClicked

    private void SendBoxMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SendBoxMouseEntered
        if (!box.equals(listbox[1])){
            SendBox.setBackground(new java.awt.Color(0,204,204));
        }
    }//GEN-LAST:event_SendBoxMouseEntered

    private void SendBoxMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SendBoxMouseExited
        if (!box.equals(listbox[1])){
            SendBox.setBackground(new java.awt.Color(51,102,102));
        }
    }//GEN-LAST:event_SendBoxMouseExited

    private void SendBoxMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SendBoxMousePressed
        if (!box.equals(listbox[1])){
            SendBox.setBackground(new java.awt.Color(0,255,255));
        }
    }//GEN-LAST:event_SendBoxMousePressed

    private void SendBoxMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SendBoxMouseReleased
        if (!box.equals(listbox[1])){
            SendBox.setBackground(new java.awt.Color(0,204,204));
        }
    }//GEN-LAST:event_SendBoxMouseReleased

    private void SpamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SpamMouseClicked
        if (!box.equals(listbox[2])){            
            box = listbox[2];
            refeshBoxButton();
            MailInfo.setVisible(false);
            Spam.setBackground(new java.awt.Color(51,51,51));
            clearMessageBox(box);
            showMailSync(spam, box);
            syncSpam = new Thread(new SyncSpam(this, username, password, host));
            syncSpam.start();
        }
    }//GEN-LAST:event_SpamMouseClicked

    private void SpamMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SpamMouseEntered
        if (!box.equals(listbox[2])){           
            Spam.setBackground(new java.awt.Color(0,204,204));
        }
    }//GEN-LAST:event_SpamMouseEntered

    private void SpamMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SpamMouseExited
        if (!box.equals(listbox[2])){           
            Spam.setBackground(new java.awt.Color(51,102,102));
        }
    }//GEN-LAST:event_SpamMouseExited

    private void SpamMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SpamMousePressed
        if (!box.equals(listbox[2])){           
            Spam.setBackground(new java.awt.Color(0,255,255));
        }
    }//GEN-LAST:event_SpamMousePressed

    private void SpamMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SpamMouseReleased
        if (!box.equals(listbox[2])){           
            Spam.setBackground(new java.awt.Color(0,204,204));
        }
    }//GEN-LAST:event_SpamMouseReleased

    private void TrashMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TrashMouseClicked
        if (!box.equals(listbox[3])){            
            box = listbox[3];
            MailInfo.setVisible(false);
            refeshBoxButton();
            Trash.setBackground(new java.awt.Color(51,51,51));
            clearMessageBox(box);
            showMailSync(trash, box);
            syncTrash = new Thread(new SyncTrash(this, username, password, host));
            syncTrash.start();
        }
    }//GEN-LAST:event_TrashMouseClicked

    private void TrashMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TrashMouseEntered
        if (!box.equals(listbox[3])){  
            Trash.setBackground(new java.awt.Color(0,204,204));
        }
    }//GEN-LAST:event_TrashMouseEntered

    private void TrashMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TrashMouseExited
        if (!box.equals(listbox[3])){  
            Trash.setBackground(new java.awt.Color(51,102,102));
        }
    }//GEN-LAST:event_TrashMouseExited

    private void TrashMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TrashMousePressed
        if (!box.equals(listbox[3])){  
            Trash.setBackground(new java.awt.Color(0,255,255));
        }
    }//GEN-LAST:event_TrashMousePressed

    private void TrashMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TrashMouseReleased
        if (!box.equals(listbox[3])){  
            Trash.setBackground(new java.awt.Color(0,204,204));
        }
    }//GEN-LAST:event_TrashMouseReleased

    private void ReplyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ReplyMouseClicked
        ReplyMail rm = new ReplyMail(idMail, emailSession, box, username, password, From.getText(), Subject.getText());
        rm.setVisible(true);
    }//GEN-LAST:event_ReplyMouseClicked

    private void ReplyMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ReplyMouseEntered
        Reply.setBackground(new java.awt.Color(102, 204, 204));
    }//GEN-LAST:event_ReplyMouseEntered

    private void ReplyMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ReplyMouseExited
        Reply.setBackground(new java.awt.Color(51,51,51));
    }//GEN-LAST:event_ReplyMouseExited

    private void ReplyMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ReplyMousePressed
        Reply.setBackground(new java.awt.Color(204,204,204));
    }//GEN-LAST:event_ReplyMousePressed

    private void ReplyMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ReplyMouseReleased
        Reply.setBackground(new java.awt.Color(102, 204, 204));
    }//GEN-LAST:event_ReplyMouseReleased

    private void ForwardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ForwardMouseClicked
        ForwardMail fm = new ForwardMail(idMail, emailSession,box, username, password);
        fm.setVisible(true);
    }//GEN-LAST:event_ForwardMouseClicked

    private void ForwardMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ForwardMouseEntered
        Forward.setBackground(new java.awt.Color(102, 204, 204));
    }//GEN-LAST:event_ForwardMouseEntered

    private void ForwardMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ForwardMouseExited
        Forward.setBackground(new java.awt.Color(51,51,51));
    }//GEN-LAST:event_ForwardMouseExited

    private void ForwardMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ForwardMousePressed
        Forward.setBackground(new java.awt.Color(204,204,204));
    }//GEN-LAST:event_ForwardMousePressed

    private void ForwardMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ForwardMouseReleased
        Forward.setBackground(new java.awt.Color(102, 204, 204));
    }//GEN-LAST:event_ForwardMouseReleased

    private void DeleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DeleteMouseClicked
        int[] id = {idMail};
        try {
            (new Thread(new DeleteMail(this,id, emailSession,box,username, password))).start();
        } catch (NoSuchProviderException ex) {
            System.out.println("cannot delete");
        }
    }//GEN-LAST:event_DeleteMouseClicked

    private void DeleteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DeleteMouseEntered
        Delete.setBackground(new java.awt.Color(102, 204, 204));
    }//GEN-LAST:event_DeleteMouseEntered

    private void DeleteMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DeleteMouseExited
        Delete.setBackground(new java.awt.Color(51,51,51));
    }//GEN-LAST:event_DeleteMouseExited

    private void DeleteMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DeleteMousePressed
        Delete.setBackground(new java.awt.Color(204,204,204));
    }//GEN-LAST:event_DeleteMousePressed

    private void DeleteMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DeleteMouseReleased
        Delete.setBackground(new java.awt.Color(102, 204, 204));
    }//GEN-LAST:event_DeleteMouseReleased

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Bcc;
    private javax.swing.JLabel Border;
    private javax.swing.JLabel CloseButton;
    private javax.swing.JButton Delete;
    private javax.swing.JButton Forward;
    private javax.swing.JTextField From;
    private javax.swing.JButton Inbox;
    private javax.swing.JLabel Label;
    private javax.swing.JButton Logout;
    private javax.swing.JLabel MailContent;
    private javax.swing.JPanel MailInfo;
    private javax.swing.JTextPane Mes;
    private javax.swing.JLabel MinimizeButton;
    private javax.swing.JLabel NumberMails;
    private javax.swing.JButton Reply;
    private javax.swing.JButton SendBox;
    private javax.swing.JButton SendMail;
    private javax.swing.JButton Spam;
    private javax.swing.JTextField Subject;
    private javax.swing.JLabel Synced;
    private javax.swing.JButton Trash;
    private javax.swing.JLabel Unread;
    private javax.swing.JPanel attachment;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JButton refeshButton;
    // End of variables declaration//GEN-END:variables
}
