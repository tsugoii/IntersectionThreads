// Try This 12-1
package com.company;
// A simulation of a traffic light that uses 
// an enumeration to describe the light's color. 

import javax.swing.*;

// An enumeration of the colors of a traffic light.
enum TrafficLightColor {
    RED, GREEN, YELLOW
}

// A computerized traffic light. 
class TrafficLightSimulator implements Runnable {
    private TrafficLightColor tlc; // holds the current traffic light color
    private TrafficLightColor original;
    private boolean stop = false; // set to true to stop the simulation
    private boolean changed = false; // true when the light has changed
    public JTextArea textArea; //maintains reference for its text field
    double XCoordinate;
    public String Title;
    private String status;

    TrafficLightSimulator(TrafficLightColor init, double xCoord, String title) {
        XCoordinate = xCoord;
        Title = title;
        tlc = init;
        original = init;
        this.textArea = new JTextArea();
        status = "Created";
        display();
        status = tlc.name();
    }

    public void display() {
        textArea.setText(Title +
                "\nXCoord: " + XCoordinate +
                "\nStatus: " + status);
    }


    public void reset() {
        tlc = original;
        status = "Created";
        display();
    }

    // Start up the light.
    public void run() {
        display();
        while (!stop) {
            switch (tlc) {
                case GREEN:
                    sleep(10000); // green for 10 seconds
                    break;
                case YELLOW:
                    sleep(2000);  // yellow for 2 seconds
                    break;
                case RED:
                    sleep(12000); // red for 12 seconds
                    break;
            }

            changeColor();
        }
    }

    public void sleep(int millis) {
        int count = 0;
        try {
            while (!Thread.currentThread().isInterrupted() && count < millis) {
                while (Main3.SimulationControl == Main3.Control.PAUSE) {
                    if (Main3.SimulationControl == Main3.Control.STOP) {
                        this.cancel();
                        return;
                    }
                    Thread.sleep(200);
                }
                Thread.sleep(200);
                count += 200;
            }
        } catch (Exception e) {
            return;
        }
    }

    // Change color.
    synchronized void changeColor() {
        switch (tlc) {
            case RED:
                tlc = TrafficLightColor.GREEN;
                break;
            case YELLOW:
                tlc = TrafficLightColor.RED;
                break;
            case GREEN:
                tlc = TrafficLightColor.YELLOW;
        }
        status = tlc.name();
        changed = true;
        display();
        notify(); // signal that the light has changed
    }

    // Wait until a light change occurs.
    synchronized void waitForChange() {
        try {
            while (!changed)
                wait(); // wait for light to change
            changed = false;
        } catch (InterruptedException exc) {
            System.out.println(exc);
        }
    }

    // Return current color.
    synchronized TrafficLightColor getColor() {
        return tlc;
    }

    // Stop the traffic light.
    synchronized void cancel() {
        stop = true;
    }
}

//class TrafficLightDemo {
//    public static void main(String args[]) {
//        TrafficLightSimulator tl =
//                new TrafficLightSimulator(TrafficLightColor.GREEN);
//
//        Thread thrd = new Thread(tl);
//        thrd.start();
//
//        while (Main3.SimulationControl != Main3.Control.STOP) {
//            while (Main3.SimulationControl == Main3.Control.PAUSE) {
//                if(Main3.SimulationControl == Main3.Control.STOP) {
//                    tl.cancel();
//                    return;
//                }
//            }
//            System.out.println(tl.getColor());
//            tl.waitForChange();
//        }
//
//        tl.cancel();
//    }
//}
// Try This 12-1

// A simulation of a traffic light that uses
// an enumeration to describe the light's color.

//// An enumeration of the colors of a traffic light.
//enum TrafficLightColor {
//    RED, GREEN, YELLOW
//}
//
//// A computerized traffic light.
//class TrafficLightSimulator implements Runnable {
//    private TrafficLightColor tlc; // holds the current traffic light color
//    private boolean stop = false; // set to true to stop the simulation
//    private boolean changed = false; // true when the light has changed
//
//    TrafficLightSimulator(TrafficLightColor init) {
//        tlc = init;
//    }
//
//    TrafficLightSimulator() {
//        tlc = TrafficLightColor.RED;
//    }
//
//    // Start up the light.
//    public void run() {
//        while(!stop) {
//            try {
//                switch(tlc) {
//                    case GREEN:
//                        Thread.sleep(10000); // green for 10 seconds
//                        break;
//                    case YELLOW:
//                        Thread.sleep(2000);  // yellow for 2 seconds
//                        break;
//                    case RED:
//                        Thread.sleep(12000); // red for 12 seconds
//                        break;
//                }
//            } catch(InterruptedException exc) {
//                System.out.println(exc);
//            }
//            changeColor();
//        }
//    }
//
//    // Change color.
//    synchronized void changeColor() {
//        switch(tlc) {
//            case RED:
//                tlc = TrafficLightColor.GREEN;
//                break;
//            case YELLOW:
//                tlc = TrafficLightColor.RED;
//                break;
//            case GREEN:
//                tlc = TrafficLightColor.YELLOW;
//        }
//
//        changed = true;
//        notify(); // signal that the light has changed
//    }
//
//    // Wait until a light change occurs.
//    synchronized void waitForChange() {
//        try {
//            while(!changed)
//                wait(); // wait for light to change
//            changed = false;
//        } catch(InterruptedException exc) {
//            System.out.println(exc);
//        }
//    }
//
//    // Return current color.
//    synchronized TrafficLightColor getColor() {
//        return tlc;
//    }
//
//    // Stop the traffic light.
//    synchronized void cancel() {
//        stop = true;
//    }
//}
//
//class TrafficLightDemo {
//    public static void main(String args[]) {
//        TrafficLightSimulator tl =
//                new TrafficLightSimulator(TrafficLightColor.GREEN);
//
//        Thread thrd = new Thread(tl);
//        thrd.start();
//
//        for(int i=0; i < 9; i++) {
//            System.out.println(tl.getColor());
//            tl.waitForChange();
//        }
//
//        tl.cancel();
//    }
//}