/*
    Name: Arturo Lara
    Course: CNT 4714 Summer 2024
    Assignment title: Project 1 – Multi-threaded programming in Java
    Date: June 16, 2024
    Class: Enterprise Computing
*/

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        // Insert file address in quotation marks
        String file = "";
        // Takes the numbers of the file and puts them into an array
        int[] integers = ReadFile.readIntegers(file);
        int numberOfStations = integers[0];
        // Create conveyor belts
        List<ConveyorBelt> conveyorBelts = new ArrayList<>();
        for (int i = 1; i <= numberOfStations; i++) {
            conveyorBelts.add(new ConveyorBelt(i - 1));
        }
        // Create the Routing Stations
        for (int i = 0; i <= numberOfStations; i++) {

        }
    }

    static class ConveyorBelt {
        private int number;
        private final ReentrantLock lock = new ReentrantLock();
        public ConveyorBelt(int number) {
        this.number = number;
        }

        public int getNumber() {
            return number;
        }
        public ReentrantLock getLock() {
            return lock;
        }
    }

    static class RoutingStation implements Runnable{
        // The variables required to differentiate each thread
        private final int stationNumber;
        private int workload;
        private final ConveyorBelt inputBelt;
        private final ConveyorBelt outputBelt;
        private final String intro;

        // Constructor that will also output each variable's value after setting them
        public RoutingStation(int stationNumber, int workload, ConveyorBelt inputBelt, ConveyorBelt outputBelt) {
            this.stationNumber = stationNumber;
            this.intro = "Routing Station S" + stationNumber +": ";
            System.out.println("% % % % ROUTING STATION " + stationNumber + "Coming Online - Initializing Conveyors % % % %\n");
            this.inputBelt = inputBelt;
            System.out.println("\t" + intro + "Input Conveyor assigned to conveyor number C" + inputBelt.getNumber() + ".");
            this.outputBelt = outputBelt;
            System.out.println("\t" + intro + "Output Conveyor assigned to conveyor number C" + outputBelt.getNumber() + ".");
            this.workload = workload;
            System.out.println("\t" + intro + "Workload Set. Station S" +
            stationNumber + " has a total of " + workload + " to move. \n");
            System.out.println(intro + "Now Online And Ready To Move Packages\n\n\n");
        }

        // Method used to simulate the processing time of the packages of a station once it locks both input and output conveyors
        private void processPackages() {
            try {
                System.out.println("\n* * * * * * " + intro + "Holds locks on both input Conveyor C" + inputBelt.getNumber() + " and output Conveyor C" + outputBelt.getNumber() + ". * * * * * *\n");
                System.out.println(intro + "Currently moving packes into station on input conveyor C" + inputBelt.getNumber() + ".");
                // To simulate a random amount of time I use Math.random to generate a double from 0.0 to 1.0
                // It is then multiplied by 5 and added by 1 and converted to an integer to give us a range of 1 to 5
                int processingTime = (int) (Math.random() * 5) + 1;
                // It is then multiplied by 1000 and causes the thread to sleep for that many milliseconds
                Thread.sleep(processingTime * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // A method simply used to delay the thread whenever it can't acquire a lock
        private void waitBeforeAttempting() {
            try {
                // The thread will sleep on the number of seconds based on its number plus 1
                Thread.sleep((stationNumber + 1) * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Where all of the thread's package processing occurs
        @Override
        public void run() {
            // While there is at least one package still in the workload the loop will keep the station processing
            int lockPhase = 0;
            while(workload > 0) {
                if (lockPhase == 0){
                    System.out.println(intro + "Entering Lock Acquisition Phase.");
                    lockPhase = 1;
                }

                if(inputBelt.getLock().tryLock()) {
                    try {
                        System.out.println(intro + "Currently holds lock on input conveyor C" + inputBelt.getNumber() + ".");
                        // If the station gets a lock on the input belt it will try the output belt
                        if(outputBelt.getLock().tryLock()) {
                            try {
                                // If successful it will now attempt to process the packages
                                System.out.println(intro + "Currently holds lock on output conveyor C" + outputBelt.getNumber() + ".");
                                lockPhase = 0;
                                processPackages();
                                // The workload counter is now incremented by -1
                                workload--;
                                System.out.println(intro + "Package group completed - " + workload + " package groups remaining to move");
                            } finally {
                                outputBelt.getLock().unlock();
                                System.out.println(intro + "Unlocks/releases output conveyor C" + outputBelt.getNumber());
                            }
                        } else {
                            System.out.println(intro + "UNABLE TO LOCK OUTPUT CONVEYOR C" + outputBelt.getNumber() + ".");
                            System.out.println("\tSYNCHRONIZATION ISSUE: Station S" + outputBelt.getNumber() + " currently holds the lock on output conveyor C" + outputBelt.getNumber() + " - Station S" + stationNumber + " releasing lock on input conveyor C" + inputBelt.getNumber() + ".\n");
                        }
                    } finally {
                        inputBelt.getLock().unlock();
                        System.out.println(intro + "Unlocks/releases input conveyor C" + inputBelt.getNumber());
                        waitBeforeAttempting();
                    }
                } else {
                    // Here I couldn't find any particular instances for if the input fails so here's my contingency
                    System.out.println(intro + " UNABLE TO LOCK INPUT CONVEYOR C" + inputBelt.getNumber() + ".");
                    waitBeforeAttempting();
                }
            }
        System.out.println("# # # # # " + intro + "WORKLOAD SUCCESSFULLY COMPLETED. * * * Routing Station 0 preparing to go offline. # # # # #\n\n\n");
        }
    }
}