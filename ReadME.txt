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

I've done a basic implementation which causes a runtime error as follows:

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

Need to check this out before moving further!

This exception happens when Lara meets a blind alley
In this case, all the previous tiles have been visited, so the next node she can reach without being visited before is not adjacent

Idea: Calculate the path length
Set off a thread which calculates the path length to the Orb for each route
If we hit a blind alley, catch the exception
However, the only way we can calculate a path length is by exploring - the threads can't do this, only Lara can?

Find the shortest route with multiple threads and then follow the one that's the shortest using moveTo

Alternatively, we could retrace steps 


Running this seed:
Lara always runs the same way. But in some cases she has a non-deterministic choice of which nodes to choose from
-s -9218123272869372841





	