package escaping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import game.EscapeState;
import game.GameState;
import game.Cavern;
import game.Node;

/**
 * A class to escape from the Temple of Gloom before time runs out and collect gold on the way
 * @author BBK-PiJ-2015-74
 */

public class TempleEscaper {
	
	private EscapeState currentState;
	private int escaperoutelength;
	private static final int MAX_DISTANCE = 1000000;
	private Set<Node> nodesVisited, neighbours;
	private Node closestNode;
	private Stack<Node> escapeRoute;

	public TempleEscaper(EscapeState state) {
		this.currentState = state;
		nodesVisited = new HashSet<>();
		neighbours = new HashSet<>();
		escapeRoute = new Stack<>();
	}
	
	/**
	 * This method calculates the shortest path to the exit, and roams around neighbouring nodes with gold,
	 * 	collecting it along the way
	 *  Because this uses the shortest path, we shouldn't need to use the time remaining
	 *  
	 */
	public void escapeWithGold1() {
		
		escaperoutelength = 0;
		System.out.println("Beginning the escape phase with seed " + ((GameState) currentState).getSeed());
		System.out.println("The first node visited was " + currentState.getCurrentNode().getId());
		if (goldExists()) {
			currentState.pickUpGold();
		}
		recordEscapeRoute();
		
		Node exitNode = currentState.getExit();

		
		while (currentState.getCurrentNode() != exitNode) { 
			
			neighbours = currentState.getCurrentNode().getNeighbours();
			
			//Calculate the node in the set of neighbours which is closest to the exit
			closestNode = findClosestNode(neighbours);
			
			Node currentNode = currentState.getCurrentNode();
			
			System.out.println("currentNode is " + currentNode.getTile().getRow() + "," + currentNode.getTile().getColumn());
			System.out.println("Distance to exit is :" + currentState.getDistanceToExit() +"\n");
			
			// try and pick up gold nearby before moving on, by looking at rich neighbours around the shortest path 
			Set<Node> richNeighbours = findRichNodes(neighbours);
			
			for (Node n:richNeighbours) {
				currentState.moveTo(n);
				escaperoutelength++;
				System.out.println("Rich Neighbour visited was: " + n.getTile().getRow() + "," + n.getTile().getColumn());
				System.out.println("Distance to exit is :" + currentState.getDistanceToExit() +"\n");
				nodesVisited.add(currentState.getCurrentNode());
				recordEscapeRoute();
				if (goldExists()) { 
					currentState.pickUpGold(); 
				}
				currentState.moveTo(currentNode); // move back to the currentNode to avoid an illegal state exception
				escaperoutelength++;
				System.out.println("Moving back to currentNode: " + currentNode.getTile().getRow() + "," + currentNode.getTile().getColumn());
				System.out.println("Distance to exit is :" + currentState.getDistanceToExit() +"\n");
			}
			
			// Now move to the closest node to the exit in the set of neighbours, and start the loop again
			currentState.moveTo(closestNode); 
			System.out.println("Moving to the closestNode: " + closestNode.getTile().getRow() + "," + closestNode.getTile().getColumn());
			System.out.println("Distance to exit is :" + currentState.getDistanceToExit() +"\n");
			nodesVisited.add(currentState.getCurrentNode());	
			recordEscapeRoute();
			if (goldExists()) {
				currentState.pickUpGold();
			}				
			escaperoutelength++;
		}
	}
	
	/**
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
	
	public void escapeWithGold2() {
		
		escaperoutelength = 0;
		System.out.println("Beginning the escape phase with seed " + ((GameState) currentState).getSeed());
		System.out.println("The first node visited was " + currentState.getCurrentNode().getId());
		if (goldExists()) {
			currentState.pickUpGold();
		}
		nodesVisited.add(currentState.getCurrentNode());
		recordEscapeRoute();
		
		Node exitNode = currentState.getExit();
		int timeleft = currentState.getTimeRemaining();
		double optimisationfactor = 1.40;
		
		// check the time remaining and compare to the distance to exit
		while (currentState.getCurrentNode() != exitNode && timeleft > currentState.getDistanceToExit()*optimisationfactor) {
			
			neighbours = currentState.getCurrentNode().getNeighbours();
			Node richestNode = findRichestNeighbour(neighbours);
			Node unvisitedNeighbour = findUnvisitedNode(neighbours, nodesVisited);
			closestNode = findClosestNode(neighbours);
			
			if (richestNode != null) {
				currentState.moveTo(richestNode);
				recordEscapeRoute();
				nodesVisited.add(currentState.getCurrentNode());
			} else if (unvisitedNeighbour != null){
				currentState.moveTo(unvisitedNeighbour);
				recordEscapeRoute();
				nodesVisited.add(currentState.getCurrentNode());
			} else {
				currentState.moveTo(closestNode); //the retraceStep() method here worked equally well
				recordEscapeRoute();
				nodesVisited.add(currentState.getCurrentNode());
			}
			if (goldExists()) {
				currentState.pickUpGold();
			}				
			escaperoutelength++;
			timeleft = currentState.getTimeRemaining();
		}
		
		// finally head home collecting gold along the way
		while (currentState.getCurrentNode() != exitNode) {

			neighbours = currentState.getCurrentNode().getNeighbours();
			closestNode = findClosestNode(neighbours);
			currentState.moveTo(closestNode);
			nodesVisited.add(currentState.getCurrentNode());	
			recordEscapeRoute();
			if (goldExists()) {
				currentState.pickUpGold();
			}				
			escaperoutelength++;
		}
	}

	/**
	 * A private method to return the neighbours which are rich
	 * @param neighbours, the neighbour nodes to the current location
	 * @param nodesVisited, the set of nodes which have been visited by Princess Zelda so far
	 * @return richNode a node which has not yet been visited and contains gold
	 */
	private Set<Node> findRichNodes(Set<Node> neighbours) {
		Set<Node> rich = new HashSet<>();
		for (Node n:neighbours ) {
			if (n.getTile().getGold()>0 && !n.getTile().getGoldPickedUp()) {
				rich.add(n);
			}
		}
		return rich;
	}
	
	/**
	 * A private method to find the neighbouring node which is closest to the exit of the cavern
	 * @see Cavern - Uses Djikstra's algorithm
	 * @param neighbours
	 * @return the node in the set of neighbours which is closest to the exit
	 */
	private Node findClosestNode(Set<Node> neighbours) { 
		
		Cavern cavern = currentState.getEscapeCavern();		
		int shortestPath = MAX_DISTANCE;
		Node bestNode = null;
		for (Node n:neighbours) {
			if (cavern.minPathLengthToTarget(n) < shortestPath) {
				bestNode = n;
				shortestPath = currentState.getDistanceToExit();
			}
		}		
		return bestNode;
	}
	
	/**
	 * A private method to return a single rich unvisited neighbour
	 * @param neighbours, the neighbour nodes to the current location
	 * @param nodesVisited, the set of nodes which have been visited by Princess Zelda so far
	 * @return singleRichNode a node which has not yet been visited and contains gold
	 */
	private Node findRichestNeighbour(Set<Node> neighbours) {
		Set<Node> rich = new HashSet<>();
		for (Node n:neighbours ) {
			if (n.getTile().getGold()>0 && !n.getTile().getGoldPickedUp()) {
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
	private Node findUnvisitedNode(Set<Node> neighbours, Set<Node> nodesVisited) {	
		Node unvisitedNeighbour = neighbours.stream()
				.filter(n -> !nodesVisited.contains(n))
				.findAny().orElse(null);
		return unvisitedNeighbour;
	}
	
	/**
	 * A private method to return the neighbours which have not been visited.
	 * @param neighbours
	 * @param nodesVisited, the set of nodes which have been visited by Princess Zelda so far
	 * @return a node out of the set of neighbours, which has not been visited
	 */
	private Set<Node> findUnvisitedNeighbours(Set<Node> neighbours, Set<Node> nodesVisited) {	
		Set<Node> unvisitedNeighbour = new HashSet<>();
		for (Node n:neighbours) {
			if (!nodesVisited.contains(n)) unvisitedNeighbour.add(n);
		}	
		return unvisitedNeighbour.stream().collect(Collectors.toSet());
	}

	/**
	 * A private method to return the neighbours which are rich and have not been visited
	 * @param neighbours, the neighbour nodes to the current location
	 * @param nodesVisited, the set of nodes which have been visited by Princess Zelda so far
	 * @return richNode a node which has not yet been visited and contains gold
	 */
	@SuppressWarnings("unused")
	private Set<Node> findRichNodes(Set<Node> neighbours, Set<Node> nodesVisited) {
		Set<Node> rich = new HashSet<>();
		for (Node n:neighbours ) {
			if (n.getTile().getGold()>0 && !n.getTile().getGoldPickedUp() && !nodesVisited.contains(n.getId())) { //n.getId??
				rich.add(n);
			}
		}
		return rich;
	}	
	
	/**
	 * A method to find any neighour in the set of neighbouring nodes.
	 * @param neighbours
	 * @return any neighbour from the set of neighbouring nodes
	 */
	@SuppressWarnings("unused")
	private Node findNeighbourNode(Set<Node> neighbours) {	
		Node anyNeighbour = neighbours.stream()
				.findAny().orElse(null);
		return anyNeighbour;
	}
	
	/**
	 * @param allTraversableNodes
	 * @return sortedRichNodes, a set of nodes sorted by the amount of gold they contain.
	 * allTraversableNodes = currentState.getVertices();
	 * Node greedyNode = findGreedyNode(currentState.getVertices());
	 */
	@SuppressWarnings("unused")
	private Node findGreedyNode(Collection<Node> allTraversableNodes) {
		List<Node> richList = new ArrayList<>();	
		Comparator<Node> byGoldAmount = (Node n1, Node n2) -> Integer.compare(n1.getTile().getGold(), n2.getTile().getGold());
		richList = allTraversableNodes.parallelStream().sorted(byGoldAmount).collect(Collectors.toList());
		Node greedyNode = richList.get(0);
		return greedyNode;
	}
	
	/**
	 * A private method to find the neighbouring node which is closest to the exit of the cavern and hasn't been visited
	 * @param neighbours the set of neighbouring nodes
	 * @param nodesVisited, the set of all visited nodes
	 * @return the node in the set of neighbours which is closest to the exit and hasn't been visited before
	 */
	@SuppressWarnings("unused")
	private Node findClosestUnvisitedNeighbour(Set<Node> neighbours, Set<Node> nodesVisited) {
		
		Set<Node> unvis = findUnvisitedNeighbours(neighbours, nodesVisited);
		Cavern cavern = currentState.getEscapeCavern();	
		int shortestPath = MAX_DISTANCE;
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
		return escapeRoute;
	}
	
	/**
	 * A private method to retrace steps by popping previous locations off a stack, and moving to the node on the top of the stack
	 * Useful when all neighbouring nodes have been visited and a blind alley has been found
	 */
	@SuppressWarnings("unused")
	private void retraceStep() {
		escapeRoute.pop();
		currentState.moveTo(escapeRoute.peek());
		escaperoutelength++;
	}
	/**
	 * A private method to check whether there is gold on a tile and whether it  has been picked up.
	 * Necessary because getGold gives the value of the gold even if it has been collected
	 * @return true if there is gold on the tile and it has not been collected
	 */
	private boolean goldExists() {
		if (currentState.getCurrentNode().getTile().getGold()>0 && !(currentState.getCurrentNode().getTile().getGoldPickedUp())) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Method used during de-bugging
	 * @param chosen, the set of nodes to be printed
	 */
	@SuppressWarnings("unused")
	private void printNodes(Set<Node> chosen) {
	System.out.println("Printing rich neighbours :");
		for(Node n:chosen) {
			System.out.println(n.getTile().getRow() + "," + n.getTile().getColumn());
		}
		System.out.println("\n");
	}
	
	/**
	 * Method using during de-bugging
	 * @param n, the node to be printed
	 */
	@SuppressWarnings("unused")
	private void printSingleNode(Node n) {
		System.out.println("Printing rich neighbour :");
		System.out.println(n.getTile().getRow() + "," + n.getTile().getColumn() + "\n");
	}
	
}

