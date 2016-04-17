package escaping;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Collection;
import java.util.Comparator;

import game.EscapeState;
import game.GameState;
import game.Cavern;
import game.Node;
import game.Edge;

/**
 * A class to escape from the Temple of Gloom before time runs out and collect gold on the way
 * @author BBK-PiJ-2015-74
 */

public class TempleEscaper {
	
	private EscapeState currentState;
	private int escaperoutelength;
	private static final int MAX_DISTANCE = 1000000;
	private Set<Node> nodesVisited, neighbours;
	private Node closestNode, closestNodeUnvisited;
	private Set<Edge> edges;
	private Stack<Node> escapeRoute;
	private Collection<Node> allTraversableNodes;

	public TempleEscaper(EscapeState state) {
		this.currentState = state;
		nodesVisited = new HashSet<>();
		neighbours = new HashSet<>();
		edges = new HashSet<>();
		escapeRoute = new Stack<>();
	}
		
	public void escapeWithGold() {
		
		escaperoutelength = 0;
		System.out.println("Beginning the explore phase with seed " + ((GameState) currentState).getSeed());
		System.out.println("The first node visited was " + currentState.getCurrentNode().getId());
		if (goldExists()) {
			currentState.pickUpGold();
		}
		recordEscapeRoute();
		
		Node exitNode = currentState.getExit();

		while (currentState.getCurrentNode() != exitNode) {
			
			neighbours = currentState.getCurrentNode().getNeighbours();
			closestNode = findClosestUnvisitedNeighbourNode(neighbours, nodesVisited);
			//closestNode = findNeighbourNodeClosestToExit(neighbours);
			allTraversableNodes = currentState.getVertices();
			
			// try and pick up gold nearby before moving on 
			Set<Node> richNeighbours = findRichNodes(neighbours, nodesVisited);
			//Node richestNeighbour = findRichestNeighbour(neighbours, nodesVisited);
			Node currentNode = currentState.getCurrentNode();
		
			for (Node n:richNeighbours) {
				currentState.moveTo(n);
				nodesVisited.add(currentState.getCurrentNode());
				if (goldExists()) { 
					currentState.pickUpGold(); 
				}
				//int distance = currentState.getDistanceToExit();
			currentState.moveTo(currentNode); // if the richNode is closer to the exit than the current Node, stay here
			}
			
			currentState.moveTo(closestNode); 
			nodesVisited.add(currentState.getCurrentNode());
			
			recordEscapeRoute();
			if (goldExists()) {
				currentState.pickUpGold();
			}				
			escaperoutelength++;
			//System.out.println("Number of steps taken is " + escaperoutelength);
		}
	}
	

	/**
	 * A private method to return the neighbours which are rich and have not yet been visited
	 * @param neighbours, the neighbour nodes to the current location
	 * @param nodesVisited, the set of nodes which have been visited by Princess Zelda so far
	 * @return richNode a node which has not yet been visited and contains gold
	 */
	private Set<Node> findRichNodes(Set<Node> neighbours, Set<Node> nodesVisited) {
		Set<Node> rich = new HashSet<>();
		for (Node n:neighbours ) {
			if (n.getTile().getGold()>0 && !n.getTile().getGoldPickedUp() && !nodesVisited.contains(n)) { //n.getId??
				rich.add(n);
			}
		}
		return rich;
	}

	/**
	 * A private method to return a single rich unvisited neighbour
	 * @param neighbours, the neighbour nodes to the current location
	 * @param nodesVisited, the set of nodes which have been visited by Princess Zelda so far
	 * @return singleRichNode a node which has not yet been visited and contains gold
	 */
	private Node findRichestNeighbour(Set<Node> neighbours, Set<Node> nodesVisited) {
		Set<Node> rich = new HashSet<>();
		for (Node n:neighbours ) {
			if (n.getTile().getGold()>0 && !n.getTile().getGoldPickedUp() && !nodesVisited.contains(n)) { //n.getId??
				rich.add(n);
			}
		}
		Comparator<Node> byGoldAmount = (Node n1, Node n2) -> Integer.compare(n1.getTile().getGold(), n2.getTile().getGold());
		Node richestNode = rich.parallelStream().sorted(byGoldAmount).findFirst().orElse(null);
		return richestNode;
	}
	
	/**
	 * A private method to return the neighbours which have not been visited.Method not used yet, but could prove useful!
	 * @param neighbours
	 * @param nodesVisited
	 * @return a node out of the set of neighbours, which has not been visited
	 */
	private Set<Node> findUnvisitedNodes(Set<Node> neighbours, Set<Node> nodesVisited) {
		Set<Node> unvisited = new HashSet<>();
		for (Node n:neighbours ) {
			if (!nodesVisited.contains(n))  
					unvisited.add(n);
		}
		return unvisited;
		//return unvisited.parallelStream().findAny().orElse(null);
	}
	
	/**
	 * Method not yet used at this point
	 * @param allTraversableNodes
	 * @return sortedRichNodes, a set of nodes sorted by the amount of gold they contain.
	 */
	private Set<Node> sortedRichNodes(Collection<Node> allTraversableNodes) {
		Set<Node> sortedRichNodes = new HashSet<>();	
		Comparator<Node> byGoldAmount = (Node n1, Node n2) -> Integer.compare(n1.getTile().getGold(), n2.getTile().getGold());
		sortedRichNodes = allTraversableNodes.parallelStream().sorted(byGoldAmount).collect(Collectors.toSet());
		return sortedRichNodes;
	}

	/**
	 * A private method to find the neighbouring node which is closest to the exit of the cavern
	 * @param neighbours
	 * @return the node in the set of neighbours which is closest to the exit
	 * Needs to be modified so that, if visited before, we don't go there
	 */
	private Node findNeighbourNodeClosestToExit(Set<Node> neighbours) {	
		Cavern cavern = currentState.getEscapeCavern();		
		//int shortestPath = MAX_DISTANCE;
		int shortestPath = currentState.getDistanceToExit();
		Node closestNodeToExit = null;
		for (Node n:neighbours) {
			if (cavern.minPathLengthToTarget(n) < shortestPath) {
				closestNodeToExit = n;
				//shortestPath = cavern.minPathLengthToTarget(closestNodeToExit);
				shortestPath = currentState.getDistanceToExit();
			}
		}		
		return closestNodeToExit;
	}

	/**
	 * A private method to find the neighbouring node which is closest to the exit of the cavern
	 * @param neighbours
	 * @return the node in the set of neighbours which is closest to the exit
	 * Needs to be modified so that, if visited before, we don't go there
	 */
	private Node findClosestUnvisitedNeighbourNode(Set<Node> neighbours, Set<Node> nodesVisited) {
		
		Set<Node> unvis = findUnvisitedNodes(neighbours, nodesVisited);
		Cavern cavern = currentState.getEscapeCavern();		
		int shortestPath = currentState.getDistanceToExit();
		Node closestUnvisitedNode = null;
		
		for (Node n:unvis) {
			if (cavern.minPathLengthToTarget(n) < shortestPath) {
				closestUnvisitedNode = n;
				shortestPath = currentState.getDistanceToExit();
			}
		}
		return closestUnvisitedNode;
	}
	
	
	/**
	 * A private method to record the route we have travelled through the cavern 
	 * @return escapeRoute, a stack of nodes which contains a record of all the nodes Princess Zelda has travelled through
	 */
	private Stack<Node> recordEscapeRoute() {
		escapeRoute.push(currentState.getCurrentNode());
		//System.out.println("Recording escape route: location "+ escapeRoute.peek().getId());
		return escapeRoute;
	}
	
	private boolean goldExists() {
		if (currentState.getCurrentNode().getTile().getGold()<=0) {
			return false;
		} else {
			return true;
		}
	}
	
} // end of class

