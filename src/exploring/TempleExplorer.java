package exploring;

import java.util.Collection;
import java.util.Stack;
import java.util.HashSet;
import java.util.Set;

import game.ExplorationState;
import game.NodeStatus;

/**
 * @author BBK-PiJ-2015-74 lburge01
 * @see student/Explorer.java
 * A class to explore the Temple of Gloom and claim the Orb
 */

public class TempleExplorer {
	
	private ExplorationState currentState;
	private int pathlength;
	private static final int MAX_DISTANCE = 1000000;
	private Set<Long> tilesVisited;
	private Stack<Long> pathStack;

	public TempleExplorer(ExplorationState state) {
		this.currentState = state;
		tilesVisited = new HashSet<>(); 
		pathStack = new Stack<>();
	}
	
	/**
	 * A method to claim the Orb.
	 * This method does the following:
	 * Finds the neighbours of the current ExplorationState (neighbours)
	 * Adds the id of the tiles visited to a HashSet;
	 * Finds the neighbour in the Collection<NodeStatus> neighbours which have not yet been visited;
	 * Calculates a distance to target for the neighbour tile and compares this to a previously recorded distance;
	 * Moves to the neighbour tile with a distance to the Orb less than the previously recorded distance, based on the grid
	 * Records the path taken by adding the ids of the tiles visited to a stack;
	 * If a blind alley is found, retraces steps to a tile at which adjacent there is a tile that has not been visited
	 */
	public void claimTheOrb() {
		
		pathlength = 0;
		long firstTileVisited = currentState.getCurrentLocation();
		System.out.println("The first tile visited was " + firstTileVisited);
		recordPath();
		
		while (currentState.getDistanceToTarget() != 0) {
			
			//System.out.println("Back to beginning of while loop");
			Collection<NodeStatus> neighbours = currentState.getNeighbours();
			tilesVisited.add(currentState.getCurrentLocation());
			int distance = MAX_DISTANCE;
			long nextTile = -1L;
			
			NodeStatus neighbourTilesNotVisited = findNeighbourTilesNotVisited(neighbours, tilesVisited);
			//System.out.println("Entering if statement");
			//neighbourTilesNotVisited == null if all neighbouring tiles to the current location have been visited
			
			if (neighbourTilesNotVisited != null && neighbourTilesNotVisited.getDistanceToTarget() < distance) {
			// finds the neighbourTileNotVisited which is closest to the Orb based on the grid
			// works on grid not graph: could be optimised
			// Using Dijkstras (method in Cavern.java) gives same bonus score as using Pythagorus
				distance = neighbourTilesNotVisited.getDistanceToTarget();
				nextTile = neighbourTilesNotVisited.getId();
				System.out.println("Moving to tile with id: " + nextTile); 
				System.out.println("Moving from current position: " + currentState.getCurrentLocation());
				currentState.moveTo(nextTile); 
				recordPath();
				System.out.println("\t to new position: " + currentState.getCurrentLocation());
				pathlength++;
				System.out.println("Number of steps taken is " + pathlength);
			} else {
				retraceStep();
			}	
		}	
	}
	
	/**
	 * A private method to return the NodeStatus of the current tile, and find the tile in the set of neighbours which has not been visited closest to the Orb
	 * @param neighbours, the neighbouring tiles to the current location
	 * @param tileVisited, the set of tiles which have been visited by Lara so far
	 * @return NodeStatus of the tile which is in the set of neighbours and has not been visited before
	 */
	private NodeStatus findNeighbourTilesNotVisited(Collection<NodeStatus> neighbours, Set<Long> tileVisited) {
		//System.out.println("calling findNeighbourTilesNotVisited sorted by NodeStatus");
		NodeStatus tileNotVisited = neighbours.stream()
            .sorted(NodeStatus::compareTo) // uses compareTo method in NodeStatus class
            .filter(n -> !tileVisited.contains(n.getId()))
            .findAny().orElse(null); //findFirst() or findAny() doesn't make any difference
		if (tileNotVisited == null) { // all neighbouring tiles have already been visited
			System.out.println("Blind alley has been found");
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
		//System.out.println("Recording path: location "+ pathStack.peek());
		return pathStack;
	}
	
	/**
	 * A private method to retrace the steps travelled by Lara when she has hit a blind alley.
	 */
	private void retraceStep() {
		//System.out.println("Calling retraceStep() method");
		pathStack.pop();
		System.out.println("The current location is " + currentState.getCurrentLocation());
		System.out.println("The next location is " + pathStack.peek());
		currentState.moveTo(pathStack.peek());	
		pathlength++;
	}

}
