# cw-temple
@author BBK-PiJ-2015-74 lburge01

Coursework 4 for the Birkbeck MSc in Computer Science
Started Sat 9th April 2016, completed 18 April 2016

*****************************************************************
Package gui

@see gui.ROOT

In order to get this code working, the user needs to update the file location of the res folder
in interface ROOT.java
Not updating the file location will give an IO fileNotFound exception.

****************************************************************
Package student

@see student.Explorer
Basic methods written which call methods in package escaping and package exploring
I implemented two methods for escapeWithGold()
These are explained below. The first finds the shortest path and roams around neighbouring nodes collecting gold
The second finds any neighbouring node with the most gold and roams around the grid, before 
heading for the exit if time is running out.
Technically I should have put these in different classes (or used a switch-case statement) but they re-use the same methods,
so for the sake of time I left them in a single class
escapeWithGold(1) can be run by changing the method number in student.Explorer

Sometimes using TXT.Main to run 100 seeds randomly reports errors with running out of time in the Escape phase, but when I run these
via GUI.main they work fine.

****************************************************************

Package exploring

@see exploring.TempleExplorer
	/**
	 * A method to claim the Orb.
	 * This method does the following:
	 * Finds the neighbours of the current ExplorationState (neighbours)
	 * Adds the id of the tiles visited to a HashSet;
	 * Finds the neighbour which has not yet been visited;
	 * Calculates a distance to target, getDistanceToTarget() for the neighbour tile and compares this to previous minimum distance
	 * Moves to the neighbour tile with a distance to the Orb less than the previously recorded distance, based on Dijkastra's algorithm
	 * 
	 * getDistanceToTarget() is a method in interface ExplorationState, implemented in GameState
	 * I've written methods in GameState to implement this using Djikstra's, Pythagorus or Manhattan distance 
	 * This implementation uses Djikstra's
	 * 
	 * I also tried with the previously recorded distance = Manhattan distance (based on the grid), but this recorded a lower score
	 * Records the path taken by adding the ids of the tiles visited to a stack;
	 * If a blind alley is found, retraces steps to a tile at which adjacent there is a tile that has not been visited
	 */ 
********************************************************************
Package escaping

@see escaping.TempleEscaper
	/**
	 * method escapeWithGold1() 
	 * This method calculates the shortest path to the exit, and roams around neighbouring nodes with gold,
	 * 	collecting it along the way
	 *  Because this uses the shortest path, we shouldn't need to use the time remaining
	 */

Method escapeWithGold1 gives an average score of approx 6700 over 100 seeds

	/**
	 * method escapeWithGold2()
	 * A different heuristic which gives better results
	 * This method checks the time remaining and if there is enough time remaining, heads for the richest nodes
	 * in the neighbourhood no matter where they are. If we can't find rich nodes, we head for unvisited neighbours
	 * and then the closest to the exit.
	 * DistanceToExit() uses Djikstra's algorithm on the current state by making a method in Cavern.java public
	 * @see#Cavern {@code public int minPathLengthToTarget(Node start)}
	 * If a blind alley is found, we retrace steps using a stack, or head for the closest node to the exit
	 * The closest node again is calculated using the shortest path based on Djikstra's algorithm
	 * One the time left has fallen below a certain threshold, we head for the exit using the shortest path
	 * collecting gold along the way
	 * I tried to find the node with the most gold (greedyNode) and then head towards it, but can't find a way
	 * to calculate the path to the node without adding a field to Node which gave the prospect of multiple bugs
	 * Occasionally using Txt_Main I find seeds where the program tells me I have run out of steps, but
	 * if I run these from the GUI, it seems this is not the case
	 */

Method escapeWithGold2 gives an average score of approx 13500 over 100 seeds (this is the method called currently)
	
********************************************************************
Package game

game.GameState.java implements ExplorationState and EscapeState
Corresponding changes made to these interfaces to accommodate new methods.

Added getters for escapeCavern (to allow the use of Dijkstra's algorithm which is implemented in this class)
Added getter for seed to help with de-bugging

Added the following methods to get better optimised calculations of the distance to the Orb:
private int computeDistanceToTargetPythagorus(int row, int col)
private int computeDistanceToTargetDijkstas(int row, int col)

public int getDistanceToTarget() can use Pythagorus or Dijkstras and calculates the distance to the Orb in the Explore phase
Collection<NodeStatus> getNeighbours uses Pythagorus (could equally use Dijkstras)

public int getDistanceToExit() uses Dijkstra's algorithm to calculate the distance to the exit
from the current State in the Escape phase
Required some hacking of the code in Cavern to allow this method to be used (i.e.method made public and getter added)

*********************************************************************
Package game

game.Cavern

I made one modification, which was making the package-private method int minPathLengthToTarget(Node start) public,
to enable it to be used for the Escape phase.

**********************************************************************
Package game

game.Node

Refactored one method, getExit() to getEdges() to make the code more readable.
It would have been really useful if this class had a distance field, like NodeStatus, so that Node objects
could have been compared to each other in terms of their distance to the target.
However, I couldn't see an obvious way to do this. I tried changing the constructor but this led to 
bugs with methods using Node objects so I left this alone.

************************************************************************
Problems encountered and potential solutions

Sometimes LaraCroft gets stuck in regions of the graph which look close to the Orb, but in fact
are blind alleys. With more time, I would use the locations of the walls to keep her away from blind alleys,
or use more sophisticated graph exploration algorithms to calculate shortest traversable routes.
One problem with the set-up is that the Explorer can only discover her current state -
she is effectively blind apart from her neighbours.

In the Escape phase, presumably there must be a simple way of calculating the distance between nodes and the distance to the exit
from a particular node using the edges surrounding that node, but the problem is that only the current
node can be accessed rather than the previous node or the next node, as Nodes are accessed from the current state.
All following nodes must be adjacent to the current node, and this made calculating paths really difficult.

If I had more time, I would have liked to try more complex methods to navigate around the graph 
using the length and weight of edges to find optimal routes containing gold (greedyNodes).
The main problem with the construction of the Node and GameState classes is that the explorer/escaper is effectively 'blind'
and can only 'see' his direct neighbours. This makes navigating the graph problematic as the explorer/escaper gets stuck
in zones without gold, or zones which are blind alleys and don't lead to the Orb.
In addition getNeighbours() only results in 4 neighbours rather than 8: the diagonal neighbours are omitted.
Priority queues could have been used to order the nodes in terms of their gold count.

Also, I didn't use the Edge class which contains useful methods for navigation of the graph.
Something to play with over the summer! 

@author BBK-PiJ-2015-74 lburge01







	
	

	