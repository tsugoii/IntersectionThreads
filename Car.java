package com.company;

import javax.swing.*;
import java.util.ArrayList;

public class Car implements Runnable {
    double Speed;
    double OriginalSpeed;
    double XCoordinate;
    double YCoordinate;
    String Title;
    public JTextArea textArea; //maintains reference for its text field
    public ArrayList<TrafficLightSimulator> lights; //maintains reference for its text field

    public Car(double xCoord, double yCoord, double speed, String title, ArrayList<TrafficLightSimulator> lights) {
        XCoordinate = xCoord;
        YCoordinate = yCoord;
        Speed = speed;
        OriginalSpeed = speed;
        Title = title;
        this.textArea = new JTextArea();
        display();
        this.lights = lights;
    }

    public double GetDistanceTraveled(double time) {
        return time * Speed;
    }

    public double getSpeed() {
        return Speed;
    }

    public double getXCoordinate() {
        return XCoordinate;
    }

    public double getYCoordinate() {
        return YCoordinate;
    }

    public void setSpeed(double speed) {
        Speed = speed;
    }

    public void setXCoordinate(double xCoordinate) {
        XCoordinate = xCoordinate;
    }

    public void setYCoordinate(double yCoordinate) {
        YCoordinate = yCoordinate;
    }

    public void display() {
        String status = XCoordinate == 0
                ?
                "Created"
                :
                Main3.SimulationControl == Main3.Control.PAUSE
                        ?
                        "Paused"
                        :
                        Speed == 0
                                ?
                                "Stopped at Light " + ((int) (XCoordinate / 1000) + 1)
                                : "Moving";

        textArea.setText(Title +
                "\nXCoord: " + XCoordinate +
                "\nSpeed: " + Speed + " kph" +
                "\nStatus: " + status);
    }

    public void reset() {
        Speed = OriginalSpeed;
        XCoordinate = 0;
        display();
    }

    public void run() {
        //converting from kilometers per hour to milliseconds to travel one meter
        //kilometers -> meters = multiply by 1000
        //hours -> milliseconds = multiply by 60minutes * 60seconds * 1000milliseconds = 60 * 60 * 1000 = 3,600,000
        // kilometers    1000
        // ---------- * ---------- = kph / 3600 = meters per millisecond
        // hours         3,600,000

        //to get milliseconds per meter, take reciprocal of result
        int timeToMove1Meter = (int) (1 / (getSpeed() / 3600.0));
        int lightIndex = 0;
        TrafficLightSimulator t;
        Main3.Control control = Main3.SimulationControl;
        try {
            while (!Thread.currentThread().isInterrupted() && control != Main3.Control.STOP) {
                while (control == Main3.Control.PAUSE) {
                    if (control == Main3.Control.STOP) {
                        return;
                    }
                    Thread.sleep(200);
                    control = Main3.SimulationControl;
                }

                XCoordinate++;
                lightIndex = (int) (XCoordinate / 1000);

                if (lightIndex < lights.size()) {
                    t = lights.get(lightIndex);
                    if (t.XCoordinate == XCoordinate) {
                        if (t.getColor() == TrafficLightColor.RED) {
                            setSpeed(0);
                            display();
                            while (t.getColor() == TrafficLightColor.RED) {
                                while (control == Main3.Control.PAUSE) {
                                    if (control == Main3.Control.STOP) {
                                        return;
                                    }
                                    Thread.sleep(200);
                                    control = Main3.SimulationControl;
                                }

                                Thread.sleep(50);
                            }
                            setSpeed(OriginalSpeed);
                        }
                    }
                }
                display();
                Thread.sleep(timeToMove1Meter);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
    }
}
