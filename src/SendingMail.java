/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sj
 */
public class SendingMail implements Runnable {
    SendMail sm;
    public SendingMail (SendMail sm){
        this.sm = sm;
    }
    
    @Override
    public void run() {
        System.out.println("start");
        sm.SendEmail();
        System.out.println("end");
        Thread.interrupted();
    }
    
}
