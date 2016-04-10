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
	
	private ExplorationState state;
	private int pathLength;
	private static final int MAX_DISTANCE = 1000000;
	private Set<Long> tilesVisited;
	private Stack<Long> path;

	public TempleExplorer(ExplorationState state) {
		this.state = state;
		tilesVisited = new HashSet<>(); 
		path = new Stack<>();
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
		
		pathLength = 0;
		long firstTileVisited = state.getCurrentLocation();
		boolean blindAlleyFound = false;
		System.out.println("The first tile visited was " + firstTileVisited);
		
		while ((state.getDistanceToTarget() != 0) && (blindAlleyFound == false)) {
			
			Collection<NodeStatus> neighbours = state.getNeighbours();
			tilesVisited.add(state.getCurrentLocation());
			recordPath();
			int distance = MAX_DISTANCE;
			long nextTile = -1L;
			
			NodeStatus nearestNode = getNearestNeighbour(neighbours, tilesVisited); 	
			if (nearestNode != null && nearestNode.getDistanceToTarget() < distance) { 
					distance = nearestNode.getDistanceToTarget();
					nextTile = nearestNode.getId();
			}
			
			try {
				System.out.println("Moving to tile with id: " + nextTile); 
				System.out.println("Moving from current position: " + state.getCurrentLocation());
				state.moveTo(nextTile); //move to the adjacent tile which has not been visited
				System.out.println("\t to new position: " + state.getCurrentLocation());
				pathLength++;
				System.out.println("Number of steps taken is " + pathLength);
			} catch (IllegalArgumentException ex) {
				blindAlleyFound = true;
				System.out.println("Lara has reached the end of a blind alley");
				System.out.println("Number of steps taken was " + pathLength);
				if (blindAlleyFound) {
					retraceStep();
					// look around again and see if any tiles are unvisited. If they are, carry on
					blindAlleyFound = false;
				}
			}	
		}	
	}
	
	/**
	 * A private method to return the NodeStatus of the nearest non-visited neighbour
	 * @param neighbours
	 * @param tileVisited
	 * @return NodeStatus of the tile which is in the set of neighbours, and is nearest to the Orb
	 */
	private NodeStatus getNearestNeighbour(Collection<NodeStatus> neighbours, Set<Long> tileVisited) {
		return neighbours.stream()
            .sorted(NodeStatus::compareTo) 
            .filter(n -> !tileVisited.contains(n.getId()))
            .findFirst().orElse(null);
	}
	
	/**
	 * A private method to record the path travelled by Lara on the way to claim the Orb.
	 * Looks at the current tile identifier and pushes it on to a Stack, path.
	 * @return path, a stack of all tiles visited during the current exploration.
	 */
	private Stack<Long> recordPath() {
		long currentTileId = state.getCurrentLocation(); 
		path.push(currentTileId);
		return path;
	}
	
	/**
	 * A private method to retrace the steps travelled by Lara when she has hit a blind alley.
	 */
	private void retraceStep() {
		path.pop();
		state.moveTo(path.peek());
	}
	
	// now iterate over this collection instead		
	//Collection<NodeStatus> nearestNodeSet = getAllNearestNeighbours(neighbours, tileVisited); // find nearest neighbours (returns a Collection of NodeStatus object)
	//for (NodeStatus nearest: nearestNodeSet) {
	//	if (nearest != null && nearest.getDistanceToTarget() < distance) {
	//		distance = nearest.getDistanceToTarget();
	//		nextTile = nearest.getId();
	//	}
	
	//if n has been visited already, remove from the set 
	// size of the set is the number of nearest neighbours
	// alternative is come to a fork, set off number of threads as there are number of nearest neighbours
	
}
