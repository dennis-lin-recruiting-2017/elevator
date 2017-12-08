# Elevator simulation problem.

## Design approach
Because of the requirement to time-step the simulation, I chose to implement
this exercise as a discrete event simulation (https://en.wikipedia.org/wiki/Discrete_event_simulation).
The intent was to eventually capture data to perform standard queuing theory
analyses (https://en.wikipedia.org/wiki/Queueing_theory) on user wait times,
elevator utilization, and ultimately a cost-benefit pricing model that can be used
to tune scheduling algorithms.  From a software engineering perspective, this approach
also has the advantage of being extremely testable and easy to debug.  

A "Simulation" will consist of a list of elevators and a list of the
aforementioned pickup requests (a pickup request is equivalent to a human rider).
Pickup requests on each floor are generated according to some preset distribution.
Each pickup request will have a timestamp associated with it to show when the
request will become active and be ready for processing.
Time is represented as a double, were 1.0 is considerd to be 1.0 seconds of
real world time.  The limit on the number of elevators and events that the
simulation can simulate are bounded by CPU and memory. 

## Assumptions
- One group of elevators very close to each other
- Passenger behavior
  - All passengers travel independently of each other (no inseparable couples or families)
  - Passengers do not change their mind once they board an the elevator.
  - Passengers will always ride to their final destnation (i.e. no getting off early and taking the stairs).
  - All passengers weigh the same (i.e. 1 unit)
  - Passenger size (i.e. area taken up) not implemented yet, so it is ignored
- Elevator fungibility - if two elevators are idle and waiting on the same floor, then the one with the lower
index in the array will be picked.  In real life, this might cause undue wear and tear on some elevators, but it
is mathematically equivalent from a queueing theory perspective and greatly simplifies the implementation.

## Elevator Implementations
- I provided an elevator modelled after the common elevator.  While serving a request, if the elevator has space,
it will pick up requests that show up en route to the original destination and update its itinerary.
- I did consider a "first-come, first-served" elevator.  However, if it seemed like it would quickly turn into 
a standard elevator if it was allowed to carry multiple customers, each with a different destination floor.

## Scheduler Implementations:
1.  A greedy scheduler, where elevators take the first available floor with waiting passengers.
2.  A scheduler that schedules sends each idle elevator to closest floor with waiting passengers.

I did also consider a carousel-like system imspired by the Yamanote Line in
Tokyo, Japan (https://en.wikipedia.org/wiki/Yamanote_Line).  Thie type of line
would probably be better suited for a demand distribution that was more constant,
whereas the optimistic/greedy FCFS version that I chose would be better suited
for loads of a multi-modal nature (i.e. multiple rush hours overa period of time).

## Possible extentions:
1.  Extend the benefit of passengers and/or cargo (priority, max number, max weight, etc.)
2.  I tested with all elevators being the same.  There could be elevators that only travelled certain floors.

## Implementation notes:
- A simulation can consist of multiple types of elevators, but they must all share the same scheduling logic.
- All elevators differ in the way they change states (i.e. when idle, when loading, and when crossing floors).
- 1 elevator implementation (OpportunisticElevator) and 2 scheduler (RoundRobin and GreedyMinimizeEmptyElevator) implementations are provided.
- There are automated tests that prove the correctness of some contrived scenarios with one or two elevators.

## How to execute

This is a standard Apache Maven project.  
- To run the tests (both unit and end-to-end) - "mvn test""
- To run simulation #1 - "mvn exec:java --Dexec.mainClass=com.dennis.interviews.elevators.scenarios.SimulationScenario01"
- To run simulation #2 - "mvn exec:java --Dexec.mainClass=com.dennis.interviews.elevators.scenarios.SimulationScenario02"
"
