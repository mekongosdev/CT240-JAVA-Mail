
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.sql.DatabaseMetaData;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sj
 */
public class ConnectSql {
    String url ;
    public ConnectSql() {

       Connection connection = null;
       url = "jdbc:sqlite:javamail.db";
        try
        {            
            // create a database connection
            connection = DriverManager.getConnection(url);           
        
            if(connection != null){
                connection.close();
            }
        }
        catch(SQLException e)
        {
          // connection close failed.
          System.err.println(e);
        }
    }    
    public ResultSet Get(String sql){
        try {
            Connection conn = this.connect();
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(10);
            ResultSet rs = statement.executeQuery(sql);
            return rs;
        } catch (SQLException ex) {
            return null;
        }
    }
    public boolean isUserExists(){
        try {
            int n;
            Connection conn = this.connect();
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(10);
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM User");
            rs.next();
            n = rs.getInt(1);
            conn.close();
            return n!=0;
        } catch (SQLException ex) {
            return false;
        }
    }
    public void execute(String sql){
        try {
            Connection conn = this.connect();
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(10);
            statement.executeUpdate(sql);
            conn.commit();
            conn.close();
        } catch (SQLException ex) {
            System.out.println("not execute");
        }
    }
    private Connection connect() {
        // SQLite connection string        
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    public void insertUser(String u, String p){
        String sql = "INSERT INTO User(username,password) VALUES(?,?)";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u);
            pstmt.setString(2, p);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public String[] getUser(){
        try {
            String[] user = new String[2];
            Connection conn = this.connect();
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(10);
            ResultSet rs = statement.executeQuery("select * from User");
            rs.next();
            user[0]=rs.getString("username");
            user[1]=rs.getString("password");
            conn.close();
            return user;
        } catch (SQLException ex) {
            return null;
        }
    }
    public void insertBox(String n, String l){
        String sql = "INSERT INTO Box (name, link) VALUES(?,?)";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, n);
            pstmt.setString(2, l);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public String[] getListbox(){
        try {
            String[] list = new String[4];
            Connection conn = this.connect();
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(10);
            ResultSet rs= statement.executeQuery("SELECT `link` FROM Box");
            int i= 0;
            while(rs.next()){
                list[i]=rs.getString(1);
                i++;
            }
            conn.close();
            return list;
        } catch (SQLException ex) {
            System.out.println("get listbox error");
            return null;
        }
    }
    public void deleteBox(){
        String sql = "delete from Box";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void deleteInbox(){
        String sql = "delete from Inbox";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public List<Mes> getInbox(){
        try {
            List<Mes> list = new ArrayList<Mes>();
            Connection conn = this.connect();
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(10);
            ResultSet rs= statement.executeQuery("SELECT COUNT(*) FROM Inbox");  
            rs.next();
            if (rs.getInt(1)>0){                
                //get inbox
                rs = statement.executeQuery("select * from Inbox ORDER BY id DESC");
                while(rs.next()){
                    int[] id = {rs.getInt("id")};
                    boolean seen = rs.getBoolean("seen");
                    list.add(new Mes(id, rs.getString("sentdate"), rs.getString("fromAdr"), rs.getString("subject"), seen));
                }
            }
            conn.close();
            return list;
        } catch (SQLException ex) {
            return null;
        }
    }
    public void insertInbox(int id, String sentdate, String fromAdr, String subject, boolean seen){
        String sql = "INSERT INTO Inbox (id, sentdate, fromAdr, subject, seen) VALUES(?,?,?,?,?)";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2,sentdate);
            pstmt.setString(3, fromAdr);
            pstmt.setString(4, subject);
            pstmt.setBoolean(5, seen);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void insertSent(int id, String sentdate, String toAdr, String subject){
        String sql = "INSERT INTO Sent (id, sentdate, toAdr, subject, seen) VALUES(?,?,?,?)";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, sentdate);
            pstmt.setString(3, toAdr);
            pstmt.setString(4, subject);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public List<Mes> getSent(){
        try {
            List<Mes> list = new ArrayList<Mes>();
            Connection conn = this.connect();
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(10);
            ResultSet rs= statement.executeQuery("SELECT COUNT(*) FROM Sent");  
            rs.next();
            if (rs.getInt(1)>0){
                rs = statement.executeQuery("select * from Sent ORDER BY id DESC");
                while(rs.next()){
                    int[] id = {rs.getInt("id")};
                    list.add(new Mes(id, rs.getString("sentdate"),rs.getString("toAdr"), rs.getString("subject"), true));
                }
            }
            conn.close();
            return list;
        } catch (SQLException ex) {
            return null;
        }
    }
    public void deleteSent(){
        String sql = "delete from Sent";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void insertSpam(int id, String sentdate, String fromAdr, String subject){
        String sql = "INSERT INTO Spam (id, sentdate, fromAdr, subject) VALUES(?,?,?,?)";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, sentdate);
            pstmt.setString(3, fromAdr);
            pstmt.setString(4, subject);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public List<Mes> getSpam(){
        try {
            List<Mes> list = new ArrayList<Mes>();
            Connection conn = this.connect();
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(10);
            ResultSet rs= statement.executeQuery("SELECT COUNT(*) FROM Spam");  
            rs.next();
            if (rs.getInt(1)>0){
                rs = statement.executeQuery("select * from Spam ORDER BY id DESC");
                while(rs.next()){
                    int[] id = {rs.getInt("id")};
                    list.add(new Mes(id, rs.getString("sentdate"),rs.getString("fromAdr"), rs.getString("subject"), true));
                }
            }
            conn.close();
            return list;
        } catch (SQLException ex) {
            return null;
        }
    }
    public void deleteSpam(){
        String sql = "delete from Spam";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void insertTrash(int id, String sentdate, String fromAdr, String subject){
        String sql = "INSERT INTO Trash (id, sentdate, fromAdr, subject) VALUES(?,?,?,?)";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, sentdate);
            pstmt.setString(3, fromAdr);
            pstmt.setString(4, subject);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    } 
    public List<Mes> getTrash(){
        try {
            List<Mes> list = new ArrayList<Mes>();
            Connection conn = this.connect();
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(10);
            ResultSet rs= statement.executeQuery("SELECT COUNT(*) FROM Trash");  
            rs.next();
            if (rs.getInt(1)>0){
                rs = statement.executeQuery("select * from Trash ORDER BY id DESC");
                while(rs.next()){
                    int[] id = {rs.getInt("id")};
                    list.add(new Mes(id, rs.getString("sentdate"),rs.getString("fromAdr"), rs.getString("subject"), true));
                }
            }
            conn.close();
            return list;
        } catch (SQLException ex) {
            return null;
        }
    }
    public void deleteTrash(){
        String sql = "delete from Trash";
 
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void createTable(){
        try {
            Connection conn = this.connect();           
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(30);
            statement.execute("create table if not exists User (username string, password string)");
            statement.execute("create table if not exists Box (name string, link string)");
            statement.execute("create table if not exists Inbox (id integer PRIMARY KEY, sentdate string, fromAdr string, subject string, seen boolean)");
            statement.execute("create table if not exists Sent (id integer PRIMARY KEY, sentdate string, toAdr string, subject string)");
            statement.execute("create table if not exists Spam (id integer PRIMARY KEY, sentdate string, fromAdr string, subject string)");
            statement.execute("create table if not exists Trash (id integer PRIMARY KEY, sentdate string, fromAdr string, subject string)");
        } catch (SQLException ex) {
            Logger.getLogger(ConnectSql.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String args[]){
        ConnectSql connect = new ConnectSql();    
        ResultSet checking = connect.Get("SELECT * FROM User");  
        try {
            while(checking.next()){
                System.out.println(checking.getString(1)+"  ");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConnectSql.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
