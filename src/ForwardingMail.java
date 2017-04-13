/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sj
 */
public class ForwardingMail implements Runnable {
    ForwardMail fm;
    public ForwardingMail (ForwardMail rm){
        this.fm = rm;
    }
    
    @Override
    public void run() {
        System.out.println("start");
        fm.SendEmail();
        System.out.println("end");
        Thread.interrupted();
    }
    
}
