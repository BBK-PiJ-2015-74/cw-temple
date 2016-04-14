package escaping;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Collection;

import game.EscapeState;
import game.GameState;
import game.Node;
import game.NodeStatus;
import game.Edge;

/**
 * A class to escape from the Temple of Gloom before time runs out and collect gold on the way
 * @author lucieburgess
 *
 */

public class TempleEscaper {
	
	private EscapeState currentState;
	private int escaperoutelength;
	private int mindistancetoexit;
	private static final int MAX_DISTANCE = 1000000;
	private Set<Node> nodesVisited, neighbours;
	private Set<Edge> edges;
	private Stack<Long> escapeRoute;

	public TempleEscaper(EscapeState state) {
		this.currentState = state;
		nodesVisited = new HashSet<>();
		neighbours = new HashSet<>();
		edges = new HashSet<>();
		escapeRoute = new Stack<>();
	}
	
	public void escapeWithGold() {
		
		escaperoutelength = 0;
		long firstNodeVisited = currentState.getCurrentNode().getId();
		System.out.println("The first node visited was " + firstNodeVisited);
		recordEscapeRoute();
		
		Node exit = currentState.getExit(); //the Node corresponding to the exit
		long exitId = currentState.getExit().getId(); //the Id of the Node corresponding to the exit 
		Collection<Node> traversableNodes = currentState.getVertices(); // a collection of nodes in the graph, in no particular order
		
		//find a way to calculate a traversable path to the exit
		// compute the set of all traversable paths to the exit,
		// and then calculate the shortest path
		
		while (currentState.getDistanceToExit() != 0) {
			
			//System.out.println("Back to beginning of while loop");
			neighbours = currentState.getCurrentNode().getNeighbours(); // finds all neighbouring nodes
			nodesVisited.add(currentState.getCurrentNode());
			int distance = MAX_DISTANCE;
			long nextTile = -1L;
			
			Node neighbourNodesNotVisited = findNeighbourNodesNotVisited(neighbours, nodesVisited);
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
	 * A private method to return the node in the set of neighbours which has not been visited and is nearest to the exit of the cavern
	 * @param neighbours, the neighbouring tiles to the current location
	 * @param tileVisited, the set of tiles which have been visited by Lara so far
	 * @return NodeStatus of the tile which is in the set of neighbours and has not been visited before
	 */
	private Node findNeighbourNodesNotVisited(Set<Node> neighbours, Set<Node> nodeVisited) {
		Node nodeNotVisited = neighbours.stream()
			.sorted()
            .filter(n -> !nodeVisited.contains(n.getId()))
            .findAny().orElse(null); //findFirst() or findAny() doesn't seem to make any difference
		if (nodeNotVisited == null) { // all neighbouring nodes have already been visited
			System.out.println("Blind alley has been found");
		}
		return nodeNotVisited; 
	}	

	/**
	 * A private method to calculate the paths which PrincessZelda can travel through, traversing each node only once
	 * @return
	 */
//	private Collection<Node> calculateTraversableEscape() {
//		Node currentNode = currentState.getCurrentNode();
//		Set<Edge> nodeEdges = currentNode.getEdges(); // gives the set of all edges coming from the current node
//		return 
//	}
	
	private Stack<Long> recordEscapeRoute() {
		escapeRoute.push(currentState.getCurrentNode().getId());
		System.out.println("Recording escape route: location "+ escapeRoute.peek());
		return escapeRoute;
	}
	
	
		
}

