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
