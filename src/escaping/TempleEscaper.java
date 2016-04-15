package escaping;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.Collection;

import game.EscapeState;
import game.Cavern;
import game.Node;
import game.Edge;
//import game.EscapeNode;

/**
 * A class to escape from the Temple of Gloom before time runs out and collect gold on the way
 * @author BBK-PiJ-2015-74
 */

public class TempleEscaper {
	
	private EscapeState currentState;
	private int escaperoutelength;
	private static final int MAX_DISTANCE = 1000000;
	private Set<Node> nodesVisited, neighbours;
	private Node nextNode;
	private Set<Edge> edges;
	private Stack<Node> escapeRoute;

	public TempleEscaper(EscapeState state) {
		this.currentState = state;
		nodesVisited = new HashSet<>();
		neighbours = new HashSet<>();
		edges = new HashSet<>();
		escapeRoute = new Stack<>();
	}
	
//	public void escapeWithGold() {
//		
//		escaperoutelength = 0;
//		final long firstNodeVisited = currentState.getCurrentNode().getId();
//		System.out.println("The first node visited was " + firstNodeVisited);
//		currentState.pickUpGold();
//		recordEscapeRoute();
//		
//		int distancetoexit = 0;
//		Node exit = currentState.getExit(); //the Node corresponding to the exit
//		long exitId = currentState.getExit().getId(); //the Id of the Node corresponding to the exit 
//		Collection<Node> traversableNodes = currentState.getVertices(); // a collection of nodes in the graph, in no particular order
//		
//		
//		System.out.println("The exit to the cavern is at location " + exitId);
//		distancetoexit = currentState.getDistanceToExit();
//		System.out.println("The distance to the exit is " + distancetoexit);
//		
//		nodesVisited.add(currentState.getCurrentNode());
//		printNodesVisited();
//		while (currentState.getCurrentNode() != exit) {
//			
//			System.out.println("Back to beginning of while loop");			
//			int distance = MAX_DISTANCE;
//			
//			neighbours = currentState.getCurrentNode().getNeighbours();
//			
//			Node richNeighbour = findRichNode(neighbours, nodesVisited);
//			Node unvisitedNeighbour = findUnvisitedNode(neighbours, nodesVisited);
//			
//			if (richNeighbour != null ) { //&& distancetoexit < distance) {		
//				distance = distancetoexit; 
//				nextNode = richNeighbour;
//				System.out.println("Rich node found!"+nextNode.getTile().getColumn()+ ","+ nextNode.getTile().getRow());
//				
//			} else if (unvisitedNeighbour != null ){ 
//				distance = distancetoexit; 
//				nextNode = unvisitedNeighbour;
//				System.out.println("Unvisited node found!");
//				
//			} else {
//				nextNode = findBestNeighbourNode(neighbours);
//				System.out.println("Any old neighbour node found!");
//			} 
//			
//			System.out.println("Moving to node with id: " + nextNode.getId()); 
//			System.out.println("Moving from current position: " + currentState.getCurrentNode().getId());
//			System.out.println("\t to new position: " + nextNode.getId());
//			currentState.moveTo(nextNode); 
//			nodesVisited.add(currentState.getCurrentNode());
//			printNodesVisited();
//
//			
//			recordEscapeRoute();
//			if (checkGoldExists()) {
//				currentState.pickUpGold();
//			}				
//			escaperoutelength++;
//			System.out.println("Number of steps taken is " + escaperoutelength);
//
//		}	
//	}
	
	
	public void escapeWithGold() {
		
		escaperoutelength = 0;
		final long firstNodeVisited = currentState.getCurrentNode().getId();
		System.out.println("The first node visited was " + firstNodeVisited);
		currentState.pickUpGold();
		recordEscapeRoute();
		
		Node exitNode = currentState.getExit();
		while (currentState.getCurrentNode() != exitNode) {
			neighbours = currentState.getCurrentNode().getNeighbours();
			nextNode = findBestNeighbourNode(neighbours);
			
			currentState.moveTo(nextNode); 
			nodesVisited.add(currentState.getCurrentNode());
			printNodesVisited();
			
			recordEscapeRoute();
			if (checkGoldExists()) {
				currentState.pickUpGold();
			}				
			escaperoutelength++;
			System.out.println("Number of steps taken is " + escaperoutelength);
		}
	}
	
	
	
	private void printNodesVisited() {
		System.out.println("visited:");
		for(Node n:nodesVisited) {
			System.out.println(n.getTile().getRow()+","+n.getTile().getColumn());
		}
	}

	/**
	 * A private method to return the node in the set of neighbours which has not been visited and has gold
	 * @param neighbours, the neighbour nodes to the current location
	 * @param nodesVisited, the set of nodes which have been visited by Princess Zelda so far
	 * @return richNode a node which has not yet been visited and contains gold
	 */
//	private Node findRichNode(Set<Node> neighbours, Set<Node> nodesVisited) {
//		Node richNode = neighbours.stream()
//				.filter(n -> n.getTile().getGold()>=0 && !n.getTile().getGoldPickedUp())
//				.filter(n -> !nodesVisited.contains(n.getId()))
//				.findAny().orElse(null);
//		return richNode;
//	}

	private Node findRichNode(Set<Node> neighbours, Set<Node> nodesVisited) {
		Set<Node> rich = new HashSet<Node>();
		for (Node neigh:neighbours ) {
			if (neigh.getTile().getGold()>0 && !neigh.getTile().getGoldPickedUp() && !nodesVisited.contains(neigh.getId())) {
				rich.add(neigh);
			}
		}
		return rich.stream().findAny().orElse(null);
	}

	
	private Node findUnvisitedNode(Set<Node> neighbours, Set<Node> nodesVisited) {
		Set<Node> unvis = new HashSet<Node>();
		for (Node neigh:neighbours ) {
			if (!nodesVisited.contains(neigh))  
					unvis.add(neigh);
		}
		return unvis.stream().findAny().orElse(null);
		
		
//		Node neighbourNode = neighbours.stream()
//				.filter(n -> !nodesVisited.contains(n.getId()))
//				.findAny().orElse(null);
//		return neighbourNode;
	}
	
	private Node findBestNeighbourNode(Set<Node> neighbours) {
		System.out.println("The number of neighbours is" + neighbours.size());
		
		Cavern c = currentState.getEscapeCavern();
		
		int bestDist = MAX_DISTANCE;
		Node bestNode = null;
		for (Node n:neighbours) {
			if (c.minPathLengthToTarget(n) < bestDist) {
				bestNode = n;
				bestDist = currentState.getDistanceToExit();
			}
		}		
		return bestNode;
	}

	
	
	
	//	private Node findBestNeighbourNode(Set<Node> neighbours) {
//		System.out.println("The number of neighbours is" + neighbours.size());
//		Node neighbourNode = neighbours.stream()
//				.findAny().orElse(null);
//		return neighbourNode;
//	}
	

	/**
	 * A private method to calculate the paths which PrincessZelda can travel through, traversing each node only once
	 * @return
	 */
//	private Collection<Node> calculateTraversableEscape() {
//		Node currentNode = currentState.getCurrentNode();
//		Set<Edge> nodeEdges = currentNode.getEdges(); // gives the set of all edges coming from the current node
//		return 
//	}
	
	private Stack<Node> recordEscapeRoute() {
		escapeRoute.push(currentState.getCurrentNode());
		System.out.println("Recording escape route: location "+ escapeRoute.peek().getId());
		return escapeRoute;
	}
	
	/**
	 * A private method to retrace the steps travelled by Princess Zelda when she has hit a blind alley.
	 */
	
	private void retraceStep() {
		//System.out.println("Calling retraceStep() method");
		escapeRoute.pop();
		System.out.println("The current location is " + currentState.getCurrentNode().getId());
		System.out.println("The next location is " + escapeRoute.peek().getId());
		currentState.moveTo(escapeRoute.peek());	
		escaperoutelength++;
	}
	
	private boolean checkGoldExists() {
		if (currentState.getCurrentNode().getTile().getGold()<=0) {
			return false;
		} else {
			return true;
		}
	}
	
} // end of class

