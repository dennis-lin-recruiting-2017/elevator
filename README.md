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

## Scheduling Algorithm used:
I used an optimistic/greedy version of the "first-come, first served" algorithm
to model how most real-world elevators seem to work.  The key differences
between my algorithm and the FCFS version are as follows:

1.  While serving a request, if the elevator has space, it will pick up requests
that show up en route to the original destination and update its itinerary.
2.  Once the new riders board, the elevator will update its final destination to
be the furthest floor of all its riders (i.e. highest destination floor for an
ascending elevator, lowest destination floor for a descending one).

I did also consider a carousel-like system imspired by the Yamanote Line in
Tokyo, Japan (https://en.wikipedia.org/wiki/Yamanote_Line) -- it was incorrectly
called "round robin" in my code because of time pressures.  Thie type of line
would probably be better suited for a demand distribution that was more constant,
whereas the optimistic/greedy FCFS version that I chose would be better suited
for loads of a multi-modal nature (i.e. multiple rush hours overa period of time).

## Implementation notes:
Unfortunately, I was not able to complete the entire implementation in under 3
hours.  I did include a suite of working unit tests to prove the correctness of
the elevators operating on their own.  I was in the midst of creating the suite
of unit tests to prove the simulations could run correctly for contrived
scenarios of two elevators working in tandem, but I could not get these to work
in the allotted time.

## How to execute

This is a standard Apache Maven project.  All the tests (both unit and end-to-end)
can be executed with the "mvn test" command. 
