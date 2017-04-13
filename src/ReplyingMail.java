/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sj
 */
public class ReplyingMail implements Runnable {
    ReplyMail rm;
    public ReplyingMail (ReplyMail rm){
        this.rm = rm;
    }
    
    @Override
    public void run() {
        System.out.println("start");
        rm.SendEmail();
        System.out.println("end");
        Thread.interrupted();
    }
    
}
