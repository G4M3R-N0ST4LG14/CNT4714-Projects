# Project 1: Multi-threaded Programming in Java

#### Project Description: 
This project simulates a packaging routing system for an automated package shipping operation. The routing system consists of routing stations named S# and conveyor belts named C#. The stations are connected via the belts and each station is connected to the belt of the same number and of the one that is one digit lower, the last and first station are connnected to each other via the last belt. 

A routing station moves groups of packages from one of its connected conveyors to the other. In other words, a station moves a group of packages from its “Input” side to its “Output” side. A station’s workload is the number of times that a routing station will move groups of packages. There are a varied and unspecified number of packages in a package group and each station will have different workloads (number of package groups).

A station must have exclusive access to the requested input and output conveyors during a movement of packages. Two adjacent routing stations cannot be moving packages at the same time. 

Since the package groups that a station moves vary in size, each station will move packages for some random amount of time to simulate the random number of packages in each group. Moving packages is considered to be a workflow. Once a station has moved all of the packages in one group, it will reduce its total workload by 1 and go into an idle state (i.e., sleeping) for a random period of time before moving its next package group. A routing station thread terminates when its workload reaches 0.

Below is a nexample of a routing system with 5 stations labeled S0-S4:

![alt text](https://github.com/G4M3R-N0ST4LG14/CNT4714-Projects/blob/main/Project_1/conveyor_belt.png?raw=true)

#### Project Restrictions: 

1. Use the java.util.concurrent.locks.ReentrantLock interface for the locking system.
2. Do not use a monitor to control the synchronization including the Java synchronization statement
3. Must use an ExecutorService object to manage a FixedThreadPool(MAX), where MAX is the upper limit on the number of stations which we’ll set to be 10 (see below under Input Specification).
4. Station threads must implement the Runnable interface and not extend the Thread class in order to utilize the ExecutorService object mentioned above.

#### Input Specification: 

Program must initially read from a text file (config.txt) to gather configuration information
for the simulator. The first line of the text file will be the number of routing stations to use during
the simulation. Afterwards, there will be one line for each station. These lines will hold the
workload value for each station (i.e, the number of times it needs to move packages on the
conveyor system). Only use integers and the maximum number of stations is 10.

Ex. Input file (config.txt):
>First line creates X number of stations
3
>Subsequent lines set the workload of the current stations starting at 0 to X 
2
3
4

### Output Specifications:

1. An input conveyor is assigned to a routing station:
    > Routing Station Sx: Input conveyor assigned to conveyor number Cn.
2. An output conveyor is assigned to a routing station:
    > Routing Station Sx: Output conveyor assigned to conveyor number Cn.
3. A routing station’s workload is set:
    > Routing Station Sx Has Total Workload of n Package Groups.
4. A routing station is granted access to its input conveyor:
    > Routing Station Sx: Currently holds lock on input conveyor Cn.
5. A routing station is granted access to its output conveyor:
    > Routing Station Sx: Currently holds lock on output conveyor Cm.
6. A routing station unlocks its input conveyor:
    > Routing Station Sx: Unlocks/releases input conveyor Cn.
7. A routing station unlocks its output conveyor:
    > Routing Station Sx: Unlocks/releases output conveyor Cm.
8. A routing station unable to lock its output conveyor and releases its input conveyor lock:
    > Routing Station Sx: UNABLE TO LOCK OUTPUT CONVEYOR Cn. SYNCHRONIZATION ISSUE: Station Sy currently holds the lock on output conveyor Cn – Station Sx releasing lock on input conveyor Cm.
9. A routing station has completed its workload:
    > Routing Station Sx: going offline – work completed! BYE!
10. A routing station successfully moves packages in and out of the routing station:
    > Routing Station Sx: * * CURRENTLY HARD AT WORK MOVING PACKAGES. * *
11. A routing station completes a workflow:
    > Routing Station Sx: Package group completed - n package groups remaining to move.

## File Descriptions

### ReadFile.java

Consists of a class called ReadFile which is used by the Main.java file to read the input for the simulation. 
> Method readIntegers(String file): This is the method called upon to read the integers of a file using an adress inputed as a string and returns an integer array of the read file. 

### Main.java

The file with all the code related to achieving the objectives of this project and running the simulation of the packaging system. 
> Class Main: This class will read the config.txt file from the address set by the user and uses the methods and classes listed below to set up the simulation using the main method. It creates the conveyor belts first so that the routing stations can link to them and automatically begin the routing process when they're initialized. 

> Class ConveyorBelt: This static class is used the generate the conveyor belts and locks in our routing system.
    >> Method getLock: Used by the RoutingStation class to attempt to lock onto the belt 

> Class RoutingStation: This class represent the routing stations and will attemp to empty their workload when initialized. The constructor will provide all the information of the routing station upon initialization.
    >> Method processPackages(): Is used to empty its workload after locking onto its appropriate conveyor belts and will generate a random amount of time to process the worklaod to simulate a random amount of packages.
    >> Method waitBeforeAttemmpting(): Is used to add a random delay after failing to acquire a lock based on its station number before trying to lock on again
    >> Method run(): This is the actual method used by the class to interact with the whole simulation, upon entering the lock acquisition phase it will attempt to acquire its input belt and if successful try to acquire its output belt, if unsuccessful it will wait a random amount of time and try again. If it succeedes in acquiring both it will process its workload and then release both locks once complete, returning to the start of the lock acquisition phase until its workload is 0. If it fails to acquire the output belt it will unlock its input belt and wait a random amount of time until trying again. 