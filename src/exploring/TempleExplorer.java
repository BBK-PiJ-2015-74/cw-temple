package exploring;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import game.ExplorationState;
import game.NodeStatus;

/**
 * @author BBK-PiJ-2015-74 lburge01
 * @see student/Explorer.java
 */

public class TempleExplorer {
	
	private ExplorationState state;
	private long firstTileVisited;
	private int pathLength;
	private static final int MAX_DISTANCE = 1000000;

	public TempleExplorer(ExplorationState state) {
		this.state = state;
	}
	
	/**
	 * A basic method to claim the Orb.
	 */
	public void claimTheOrb() {
		
		Set<Long> tileVisited = new HashSet<>(); // creates an empty hashset of tiles that have been visited
		pathLength = 0;
		firstTileVisited = state.getCurrentLocation();
		boolean blindAlleyFound = false;
		System.out.println("The first tile visited was " + firstTileVisited);
		
		while ((state.getDistanceToTarget() != 0) && (blindAlleyFound == false)) {
			
			Collection<NodeStatus> neighbours = state.getNeighbours(); // find neighbours
			tileVisited.add(state.getCurrentLocation()); //add current location to tileVisited set
			int distance = MAX_DISTANCE; // initialize distance to a big number
			long nextTile = -1L; // initialize a long id
			
//			NodeStatus nearestNode = getNearestNeighbour(neighbours, tileVisited);//find nearest neighbour, but there could be more than one. This uses findFirst()		
//			if (nearestNode != null && nearestNode.getDistanceToTarget() < distance) { //Looks for the node that is likely to be closest to the target
//					distance = nearestNode.getDistanceToTarget();
//					nextTile = nearestNode.getId();
//			}

			
			// now iterate over this collection instead		
			Collection<NodeStatus> nearestNodeSet = getAllNearestNeighbours(neighbours, tileVisited); // find nearest neighbours (returns a Collection of NodeStatus object)
			for (NodeStatus nearest: nearestNodeSet) {
				if (nearest != null && nearest.getDistanceToTarget() < distance) {
					distance = nearest.getDistanceToTarget();
					nextTile = nearest.getId();
				}
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
			}

			
//			if (blindAlleyFound) {
//				retraceSteps(); // not yet implemented
//		
//			}
			
		}	
	}
	
	/**
	 * A private method to return the NodeStatus of the nearest non-visited neighbour
	 * @param neighbours
	 * @param tileVisited
	 * @return NodeStatus of the tile which is the nearest neighbour
	 */
	private NodeStatus getNearestNeighbour(Collection<NodeStatus> neighbours, Set<Long> tileVisited) {
		return neighbours.stream()
            .sorted(NodeStatus::compareTo) // don't sort, replace with a random that ranges over the size of the set and picks that element (random between 0 and n-1)
            .filter(n -> !tileVisited.contains(n.getId()))
            .findFirst().orElse(null);
	}
	
	private Collection<NodeStatus> getAllNearestNeighbours(Collection<NodeStatus> neighbours, Set<Long> tileVisited) {
		return neighbours.stream() 
			.filter(n -> !tileVisited.contains(n.getId()))
			.collect(Collectors.toCollection(HashSet::new)); 
	}
	
//if n has been visited already, remove from the set 
	// size of the set is the number of nearest neighbours
	// alternative is come to a fork, set off number of threads as there are number of nearest neighbours
	
}
