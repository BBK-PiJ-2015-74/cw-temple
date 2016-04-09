package exploring;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import game.ExplorationState;
import game.NodeStatus;

/**
 * @author BBK-PiJ-2015-74 lburge01
 * @see student/Explorer.java
 */

public class TempleExplorer {
	
	private ExplorationState state;

	public TempleExplorer(ExplorationState state) {
		this.state = state;
	}
	
	/**
	 * A basic method to claim the Orb.
	 */
	public void claimTheOrb() {
		
		Set<Long> tileVisited = new LinkedHashSet<>(); // creates an empty hashset of tiles that have been visited
		
		while (state.getDistanceToTarget() != 0) {
			Collection<NodeStatus> neighbours = state.getNeighbours(); // find your neighbours
			tileVisited.add(state.getCurrentLocation()); //add current location to tileVisited set
			int distance = Integer.MAX_VALUE; // very big integer
			long nextTile = -1L; // initialize a long id
			NodeStatus nearest = getNearestNeighbour(neighbours, tileVisited); // find nearest neighbour (returns a NodeStatus)
			
			if (nearest != null && nearest.getDistanceToTarget() < distance) { // just checks that nearest is a sensible number 
					distance = nearest.getDistanceToTarget();
					nextTile = nearest.getId();
			}

			System.out.println("Moving to tile with id: " + nextTile); //this just makes him go backwards and forwards to same location
			System.out.println("Moving from current position: " + state.getCurrentLocation());
			state.moveTo(nextTile);
			System.out.println("\t to new position: " + state.getCurrentLocation());
		}	
	}
	
	/**
	 * A private method to return
	 * @param neighbours
	 * @param tileVisited
	 * @return NodeStatus of the tile which is the nearest neighbour
	 */
	private NodeStatus getNearestNeighbour(Collection<NodeStatus> neighbours, Set<Long> tileVisited) {
    return neighbours.stream()
            .sorted(NodeStatus::compareTo)
            .filter(n -> !tileVisited.contains(n.getId()))
            .findFirst().orElse(null);
	}

}
