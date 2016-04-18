package exploring;

import java.util.Collection;
import java.util.Stack;
import java.util.HashSet;
import java.util.Set;

import game.ExplorationState;
import game.GameState;
import game.NodeStatus;

/**
 * @author BBK-PiJ-2015-74 lburge01
 * @see student/Explorer.java
 * A class to explore the Temple of Gloom and claim the Orb
 */

public class TempleExplorer {
	
	private ExplorationState currentState;
	private static final int MAX_DISTANCE = 1000000;
	private int pathlength;
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
	public void claimTheOrb() {
		
		pathlength = 0;
		System.out.println("Entering the Explore phase. First location visited is " + currentState.getCurrentLocation());
		recordPath();
		
		while (currentState.getDistanceToTarget() != 0) {
			
			Collection<NodeStatus> neighbours = currentState.getNeighbours();
			tilesVisited.add(currentState.getCurrentLocation());
			int distance = MAX_DISTANCE;
			long nextTile = -1L;
			
			NodeStatus neighbourTilesNotVisited = findUnvisitedNeighbour(neighbours, tilesVisited);
			
			if (neighbourTilesNotVisited != null && neighbourTilesNotVisited.getDistanceToTarget() < distance) {
			
				distance = neighbourTilesNotVisited.getDistanceToTarget(); 
				nextTile = neighbourTilesNotVisited.getId();
				currentState.moveTo(nextTile); 
				recordPath();
				pathlength++;
			} else {
				retraceStep();
			}	
		}
		
		if (currentState.getDistanceToTarget() == 0) {
			System.out.println("Explore phase completed for seed " + ((GameState) currentState).getSeed());
		}
	}
	
	/**
	 * A private method to return the NodeStatus of the current tile, and find the tile in the set of neighbours which has not been visited closest to the Orb
	 * @param neighbours, the neighbouring NodeStatuses of  the current location
	 * @param tileVisited, the set of tiles which have been visited by Lara so far
	 * Sorts NodeStatus objects by distance to the Orb using compareTo(other)
	 * @return NodeStatus of the tile which is in the set of neighbours, has not been visited before and is closest to the Orb
	 */
	private NodeStatus findUnvisitedNeighbour(Collection<NodeStatus> neighbours, Set<Long> tileVisited) {
		Set<NodeStatus> unvisitedNeighbour = new HashSet<NodeStatus>();
		for (NodeStatus n:neighbours) {
			if (!tileVisited.contains(n.getId())) 
				unvisitedNeighbour.add(n);
		}
		return unvisitedNeighbour.stream().sorted(NodeStatus::compareTo).findAny().orElse(null);
	}
	
	/**
	 * A private method to record the path travelled by Lara on the way to claim the Orb.
	 * Looks at the current tile identifier and pushes it on to a Stack, path.
	 * @return path, a stack of all tiles visited during the current exploration.
	 * Length of the path can be called for de-bugging purposes using pathStack.peek()
	 */
	private Stack<Long> recordPath() {
		pathStack.push(currentState.getCurrentLocation());
		return pathStack;
	}
	
	/**
	 * A private method to retrace the steps travelled by Lara when she has hit a blind alley.
	 * The steps taken are recorded using recordPath()
	 * To retrace steps, each tile is popped from the top of the Stack, revealing the previous location at the
	 * next position in the stack.
	 */
	private void retraceStep() {
		pathStack.pop();
		currentState.moveTo(pathStack.peek());	
		pathlength++;
	}
}
