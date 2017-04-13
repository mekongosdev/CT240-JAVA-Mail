
import java.util.Arrays;
import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sj
 */
public class Mes {
    public Mes (Mes m){
            this.date = m.date;
            this.subject = m.subject;
            this.from = m.from;
            this.seen = m.seen;
            this.id = m.id;
    }
    public Mes(int[] i, String d, String f, String s, boolean seen){
        this.date = d;
        this.subject = s;
        this.from = f;
        this.seen = seen;
        this.id = i;
    }
    public int[] id;
    public String date;
    public String subject;
    public String from;
    public boolean seen;

//    public boolean equals(Mes m){
//        return (Arrays.equals(this.id, m.id)&&this.from.equals(m.from)&&this.date.equals(m.date)&&this.seen==m.seen&&this.subject.equals(m.subject));
//    }
}
