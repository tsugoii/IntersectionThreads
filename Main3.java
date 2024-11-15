/* ProjectName: Main3.java
Dev: Forry
Date: 05/04/22
Purpose: GUI that displays traffic information
Uses:
    Event handlers
    Listeners
    Concurrency
    Threads
    Viewing ports/panels
    Basic distance formulas such as distance = Speed * time
Displays (in separate threads):
    Current time stamps in 1 second intervals
    Real-time Traffic light display for three major intersections
    X, Y positions and speed of up to 3 cars as they traverse each of the 3 intersections
Other:
    Loop and use buttons that provide the ability to start, pause, stop, and continue
    Ability to add more cars and intersections
    Straight distance between each traffic light of 1000 meters
    Traveling in a straight line so Y = 0 for X,Y positions
    Assume cars will stop on a dime for red lights, and continue through yellow lights and green lights
    Document all assumptions and limitations
 */
package com.company;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main3 {
    enum Control {
        STOP,
        PAUSE,
        START
    }

    public static Control SimulationControl = Control.STOP;
    static ArrayList<Car> cars = new ArrayList<>(3);
    static ArrayList<TrafficLightSimulator> intersections = new ArrayList<>(3);
    static ArrayList<Thread> threads = new ArrayList<>(7);
    static Map<String, Thread> threadMap = new HashMap<>(7);

    //Car Number, X Coord, Y Coord, speed?

    public static void main(String[] args) {
        menuGUI();
    }

    static void menuGUI() {
        JFrame frame = new JFrame("Traffic Simulator");
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(1, 5, 25, 25));
        JPanel carPanel = new JPanel();
        carPanel.setLayout(new GridLayout(1, 4, 25, 25));
        JPanel intersectionPanel = new JPanel();
        intersectionPanel.setLayout(new GridLayout(1, 4, 25, 25));
        JButton startButton = new JButton("Click Me To Start");
        JButton pauseButton = new JButton("Pause?");
        JButton stopButton = new JButton("Stop");
        JButton continueButton = new JButton("Continue?");
        JButton addCarButton = new JButton("Add a Vehicle");
        JButton subCarButton = new JButton("Subtract a Vehicle");
        JButton addIntersectionButton = new JButton("Add an Intersection");
        JButton subIntersectionButton = new JButton("Subtract an Intersection");
        JTextField carNum = new JTextField("Total Number of vehicles is: 0");
        carNum.setBounds(10, 410, 200, 25);
        JTextField intNum = new JTextField("Total Number of intersections is: 0");
        intNum.setBounds(210, 410, 200, 25);
        JTextField timeField = new JTextField("Current Time: ");
        timeField.setEditable(false);
        timeField.setBounds(600, 700, 200, 25);
        FileClock clock1 = new FileClock(timeField);
        Thread clockThread = new Thread(clock1);
        threads.add(clockThread);
        Random r = new Random();
        startButton.addActionListener(e -> {
            if (SimulationControl != Control.START) {
                SimulationControl = Control.START;
                threads.forEach(t -> {
                    if (t.isAlive() == false) {
                        t.start();
                    }
                });
                startButton.setText("Started");
                stopButton.setText("Stop?");
            }
        });
        startButton.setBounds(10, 10, 200, 25);
        pauseButton.addActionListener(e -> {
            if (SimulationControl != Control.PAUSE) {
                //threads keep looping but don't act when they see pause
                SimulationControl = Control.PAUSE;
                pauseButton.setText("Paused");
                continueButton.setText("Continue?");
            }
        });
        pauseButton.setBounds(210, 10, 200, 25);
        stopButton.addActionListener(e -> {
            if (SimulationControl != Control.STOP) {
                SimulationControl = Control.STOP;
                threads.forEach(t -> t.interrupt());
                cars.forEach(c -> {
                    c.reset();
                    Thread t = threadMap.get(c.Title);
                    t.interrupt();
                    threads.remove(t);
                    Thread t2 = new Thread(c);
                    threadMap.put(c.Title, t2);
                    threads.add(t2);
                });
                intersections.forEach(tl -> {
                    tl.reset();
                    Thread t = threadMap.get(tl.Title);
                    t.interrupt();
                    threads.remove(t);
                    Thread t2 = new Thread(tl);
                    threadMap.put(tl.Title, t2);
                    threads.add(t2);
                });
                //threads exit when they see stop
                stopButton.setText("Stopped");
                startButton.setText("Click Me To Start");
                continueButton.setText("Continue?");
            }
        });
        stopButton.setBounds(410, 10, 200, 25);
        continueButton.addActionListener(e -> {
            if (SimulationControl != Control.START) {
                SimulationControl = Control.START;
                pauseButton.setText("Pause?");
                continueButton.setText("Continue?");
            }
        });
        continueButton.setBounds(610, 10, 200, 25);
        addCarButton.addActionListener(e -> {
            if (numberOfEntriesBetween(cars, 0, 2)) {
                Car c = new Car(0,
                        0,
                        r.nextInt(41) + 80,
                        "Car " + (cars.size() + 1),
                        intersections);//speed between 80 and 120 kmh

                Thread t = new Thread(c);
                if (SimulationControl == Control.START) {
                    t.start();
                }
                threads.add(t);
                threadMap.put(c.Title, t);
                cars.add(cars.size(), c);
                c.textArea.setLineWrap(true);
                carPanel.add(c.textArea);
                carNum.setText("Total vehicles: " + cars.size());
                carPanel.revalidate();
                carPanel.repaint();
            } else {
                carNum.setText("Maximum vehicles: 3");
            }
        });
        addCarButton.setBounds(10, 210, 200, 25);
        subCarButton.addActionListener(e -> {
            if (numberOfEntriesBetween(cars, 1, 3)) {
                Car c = cars.get(cars.size() - 1);
                stopThread(c.Title);
                carPanel.remove(c.textArea);
                cars.remove(c);
                carNum.setText("Total vehicles: " + cars.size());
                carPanel.revalidate();
                carPanel.repaint();
            } else {
                carNum.setText("Minimum vehicles: 0");
            }
        });
        subCarButton.setBounds(210, 210, 200, 25);
        addIntersectionButton.addActionListener(e -> {
            if (numberOfEntriesBetween(intersections, 0, 2)) {
                TrafficLightColor tlc = TrafficLightColor.values()[r.nextInt(3)];
                TrafficLightSimulator s = new TrafficLightSimulator(tlc, (intersections.size() * 1000) + 500, "Traffic Light " + (intersections.size() + 1));
                Thread t = new Thread(s);
                if (SimulationControl == Control.START) {
                    t.start();
                }
                threads.add(t);
                threadMap.put(s.Title, t);
                s.textArea.setLineWrap(true);
                intersectionPanel.add(s.textArea);
                intersections.add(s); //revisit
                intNum.setText("Total intersections : " + intersections.size());
                intersectionPanel.revalidate();
                intersectionPanel.repaint();
            } else {
                intNum.setText("Maximum intersections: 3");
            }
        });
        addIntersectionButton.setBounds(410, 210, 200, 25);
        subIntersectionButton.addActionListener(e -> {
            if (numberOfEntriesBetween(intersections, 1, 3)) {
                TrafficLightSimulator tl = intersections.get(intersections.size() - 1);
                stopThread(tl.Title);
                intersections.remove(tl);
                intersectionPanel.remove(tl.textArea);
                intersectionPanel.revalidate();
                intersectionPanel.repaint();
                intNum.setText("Total intersections: " + intersections.size());
            } else {
                intNum.setText("Minimum intersections: 0");
            }
        });
        subIntersectionButton.setBounds(610, 210, 200, 25);
        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        controlPanel.add(stopButton);
        controlPanel.add(continueButton);
        controlPanel.add(timeField);
        carPanel.add(addCarButton);
        carPanel.add(subCarButton);
        carPanel.add(carNum);
        intersectionPanel.add(addIntersectionButton);
        intersectionPanel.add(subIntersectionButton);
        intersectionPanel.add(intNum);
        frame.add(controlPanel);
        frame.add(carPanel);
        frame.add(intersectionPanel);
        frame.setSize(1000, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 1, 0, 25));
        frame.setVisible(true);
    }

    static boolean numberOfEntriesBetween(ArrayList arr, int start, int end) {
        return arr.size() >= start && arr.size() <= end;
    }

    static void stopThread(String title) {
        Thread t = threadMap.get(title);
        t.interrupt();
        threads.remove(t);
    }
}