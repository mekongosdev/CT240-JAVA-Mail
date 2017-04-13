/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sj
 */
public class CheckLogin implements Runnable {
    private login l;
    public CheckLogin(login l){
        this.l = l;
    }
    @Override
    public void run() {
        l.Checking();
        Thread.interrupted();
    }
    
}
