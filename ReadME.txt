# cw-temple
Coursework 4 for the Birkbeck MSc in Computer Science
Started Sat 9th April 2016

In order to get this code working, the user needs to update the file location of the res folder
in interface ROOT.java
Not updating the file location will give an IO fileNotFound exception.

********************
Package game

game.GameState.java implements ExplorationState and EscapeState

ExplorationState is a simple interface, with methods:
	long getCurrentLocation();
	Collection<NodeStatus> getNeighbours();
	int getDistanceToTarget(); (this is the distance along the grid not the graph)
	 void moveTo(long id);
	
*******************
Package exploring

I've set up a package 'exploring' with a new class TempleExplorer which gives the solution to the method Explore(ExplorationState state)
in student.Explorer

exloring.TempleExplorer contains the code necessary to help George Osborne claim the Orb.
However, Lara Croft is better looking, and female, so in the interests of female solidarity I've renamed him.

The first basic implementation I tried caused a runtime error as follows:

Your code caused an error  during the explore phase. Please see console output.
We will move on to the escape phase anyway, but your solution is not correct!
Here is the error that occurred.
java.lang.IllegalArgumentException: moveTo: Node must be adjacent to position
	at game.GameState.moveTo(GameState.java:222)
	at exploring.TempleExplorer.claimTheOrb(TempleExplorer.java:44)
	at student.Explorer.explore(Explorer.java:42)
	at game.GameState.explore(GameState.java:132)
	at game.GameState.run(GameState.java:117)
	at game.GameState.runNewGame(GameState.java:108)
	at main.GUImain.main(GUImain.java:15)

This exception occurred when Lara meets a blind alley
In this case, all the previous tiles have been visited, so the next node she can reach which has not been visited causes an IllegalStateException
from the moveTo(long id) method in the Explore phase. 

So when I hit a blind alley (i.e. all neighbouring tiles have been previously visited) I set up a retraceStep() method.

Test seed for this part of the code was: -s -4289361413204218885
										 -s -9218123272869372841 

The path is recorded using recordPath() which puts the tile Ids on a stack, and then unstacks them when the step is re-traced.

Concurrency: Not sure how to get this working concurrently, since this would involve spawning multiple Laras, but she can only
moveTo() an adjacent tile ...

*******Problematic seeds in the Explore phase**********************************

It looks like, in some cases, Lara has a non-deterministic choice of which unvisited tiles to choose from.
It looks non-deterministic, but she always chooses the one with the shortest path to the Orb, based on
getDistanceToTarget() (@see GameState#getDistanceToTarget()) and
Collection<NodeStatus> getNeighbours() (@see GameState#getNeighbours())

Playing around with this to optimise it:
Using Pythagorus to calculate the distance to the target really improves the speed of claiming the Orb,
demonstrated using the following problematic seed:
-s -242848591514604047 

For this, I had to amend the following:
NodeStatus constructor (to allow amending rows and columns independently) -
This didn't work, because calculating the distance to the Orb using only rows or columns independently,
only gets Lara to the correct row or column, respectively.

Can delete the addition of distancerows and distancecolumns to the NodeStatus constructor, as this dooesn't add anything.

***************************************************************************************
Problematic seed:

Seed : -242848591514604047

This now works
But running the method multiple times on multiple seeds doesn't reach the exit for some seeds
Princess Zelda is taking too many steps!




	