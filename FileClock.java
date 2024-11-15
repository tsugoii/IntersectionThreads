package com.company;

import java.util.Date;
import javax.swing.*;

class FileClock implements Runnable {
    JTextField jtf;
    public FileClock(JTextField jtf){
        this.jtf = jtf;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.printf("%s\n", new Date());
            Date d1 = new Date();
            jtf.setText(d1.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.printf("The FileClock has been interrupted");
            }
        }
    }
}
          