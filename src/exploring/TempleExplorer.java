package exploring;

import java.util.Collection;
import java.util.Stack;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import game.ExplorationState;
import game.NodeStatus;

/**
 * @author BBK-PiJ-2015-74 lburge01
 * @see student/Explorer.java
 */

// need to be consistent between Tile and Node

public class TempleExplorer {
	
	private ExplorationState currentState;
	private int pathlength;
	private static final int MAX_DISTANCE = 1000000;
	private Set<Long> tilesVisited;
	private Stack<Long> pathStack;
	private Boolean blindAlleyFound;

	public TempleExplorer(ExplorationState state) {
		this.currentState = state;
		tilesVisited = new HashSet<>(); 
		pathStack = new Stack<>();
	}
	
	/**
	 * A method to claim the Orb.
	 * This method does the following:
	 * Finds the neighbours of the current ExplorationState (neighbours)
	 * Adds the id of the tile visited to a HashSet;
	 * Records the path taken by adding the ids of the tiles visited to a stack;
	 * Finds the neighbour in the Collection<NodeStatus> neighbour which is closest to the Orb based on the grid;
	 * Checks that the nearest node is closer than any node previously found
	 * Provides the id of the node, and moves to it
	 * If a blind alley is found, retraces steps to a tile at which adjacent there is a tile that has not been visited
	 */
	public void claimTheOrb() {
		
		pathlength = 0;
		long firstTileVisited = currentState.getCurrentLocation();
		blindAlleyFound = false;
		System.out.println("The first tile visited was " + firstTileVisited);
		recordPath();
		
		while (currentState.getDistanceToTarget() != 0) { //deleted && blindAlleyFound == false
			
			System.out.println("Back to beginning of while loop");
			Collection<NodeStatus> neighbours = currentState.getNeighbours();
			tilesVisited.add(currentState.getCurrentLocation());
			//recordPath();
			int distance = MAX_DISTANCE;
			long nextTile = -1L;
			
			NodeStatus tileNotVisitedClosestToOrb = getNearestNeighbour(neighbours, tilesVisited);
			System.out.println("Entering if statement");
			//Finds the tile which has not been visited with the shortest path to the Orb
			// returns the NodeStatus of the tile, or else null
			// if null, sets blindAlleyFound to true
			
			if (tileNotVisitedClosestToOrb != null && tileNotVisitedClosestToOrb.getDistanceToTarget() < distance) { 
					distance = tileNotVisitedClosestToOrb.getDistanceToTarget();
					nextTile = tileNotVisitedClosestToOrb.getId();
					System.out.println("Moving to tile with id: " + nextTile); 
					System.out.println("Moving from current position: " + currentState.getCurrentLocation());
					currentState.moveTo(nextTile); //move to the adjacent tile which has not been visited
					recordPath();
					System.out.println("\t to new position: " + currentState.getCurrentLocation());
					pathlength++;
					System.out.println("Number of steps taken is " + pathlength);
			//} else if (tileNotVisitedClosestToOrb != null){
			//	retraceStep();
			} else {
				retraceStep();
			}
			
			
		}// end of while loop
		
	} // end of method claimTheOrb
	
	/**
	 * A private method to return the NodeStatus of the nearest non-visited neighbour
	 * @param neighbours
	 * @param tileVisited
	 * @return NodeStatus of the tile which is in the set of neighbours, is nearest to the Orb, and has not been visited before
	 */
	private NodeStatus getNearestNeighbour(Collection<NodeStatus> neighbours, Set<Long> tileVisited) {
		System.out.println("Calling getNearestNeighbour");
		NodeStatus tileNotVisited = neighbours.stream()
            .sorted(NodeStatus::compareTo) 
            .filter(n -> !tileVisited.contains(n.getId()))
            .findFirst().orElse(null);
		if (tileNotVisited == null) { // tile has already been visited
			System.out.println("Blind alley has been found");
			blindAlleyFound = true;
		}
		return tileNotVisited;
	}
	
	/**
	 * A private method to record the path travelled by Lara on the way to claim the Orb.
	 * Looks at the current tile identifier and pushes it on to a Stack, path.
	 * @return path, a stack of all tiles visited during the current exploration.
	 */
	private Stack<Long> recordPath() {
		pathStack.push(currentState.getCurrentLocation());
		System.out.println("Recording path: location "+ pathStack.peek());
		return pathStack;
	}
	
	/**
	 * A private method to retrace the steps travelled by Lara when she has hit a blind alley.
	 */
	private void retraceStep() {
		System.out.println("Calling retraceStep() method");
		System.out.println("The current location is " + currentState.getCurrentLocation());
		pathStack.pop();
		System.out.println("The current location is " + currentState.getCurrentLocation());
		System.out.println("The next location is " + pathStack.peek());
		currentState.moveTo(pathStack.peek());	
		blindAlleyFound = false;
		pathlength++;
	}

}
