package game;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An instance represents a node in the graph --the cavern.
 */
public class Node {
    /**
     * The least positive fresh identifier available
     */
    private static long FRESH_ID = 0;
    //private static int MAX_DISTANCE = 100000;

    /**
     * The unique numerical identifier of this Node
     */
    private final long id;
    //private final int distance; // field added to get the distance to the exit
    
    /**
     * Represents the edges outgoing from this Node
     */
    private final Set<Edge> edges;
    private final Set<Node> neighbours;

    private final Set<Edge> unmodifiableEdges;
    private final Set<Node> unmodifiableNeighbours;

    /**
     * Extra state that belongs to this node
     */
    private final Tile tile;

    /* package */ Node(Tile t) {
        this(FRESH_ID, t); // constructor changed; distance added 
    }

    /**
     * Constructor: an instance with id givenId and tile t
     */
    /* package */ Node(long givenId, Tile t) {
        id = givenId;
        //distance = dist; // constructor changed
        FRESH_ID = Math.max(FRESH_ID, givenId + 1);
        edges = new HashSet<>();
        neighbours = new HashSet<>();

        unmodifiableEdges = Collections.unmodifiableSet(edges);
        unmodifiableNeighbours = Collections.unmodifiableSet(neighbours);

        tile = t;
    }

    /**
     * Add edge e to the graph.
     */
    /* package */ void addEdge(Edge e) {
        edges.add(e);
        neighbours.add(e.getOther(this));
    }

    /**
     * Return the unique Identifier of this Node.
     */
    public long getId() {
        return id;
    }

    /**
     * Method added to be able to calculate the shortest path to the exit using Djikstra's algorithm
     * @return the Node's field distance, an int
     */
//    public int getDistance() {
//    	return distance;
//    }
    
//    //Method added here
//    /**
//     * Return a negative number, 0, or a positive number depending on
//     * whether this is closer to, at the same distance, or farther from the Orb.
//     */
//    public int compareTo(Node other) {
//        return Integer.compare(distance, other.distance);
//    }
    
    /**
     * Returns the Edge of this Node that connects to q.
     * .     * Throw an IllegalArgumentException if there is no such edge.
     */
    public Edge getEdge(Node q) {
        for (Edge e : edges) {
            if (e.getDest().equals(q)) {
                return e;
            }
        }
        throw new IllegalArgumentException("getEdge: Node must be a neighbour of this Node");
    }

    /**
     * Return an unmodifiable set of Edges leaving this Node.
     */
    // re-factored this method to getEdges() - was getExit() which looks like a mistake
    public Set<Edge> getEdges() {
        return unmodifiableEdges;
    }

    /**
     * Return an unmodifiable set Nodes neighbouring this Node.
     */
    public Set<Node> getNeighbours() {
        return unmodifiableNeighbours;
    }

    /**
     * Return the Tile corresponding to this Node.
     */
    public Tile getTile() {
        return tile;
    }

    /**
     * Return true if ob is a Node with the same id as this one.
     */
    @Override
    public boolean equals(Object ob) {
        if (ob == this) {
            return true;
        }
        if (!(ob instanceof Node)) {
            return false;
        }
        return id == ((Node) ob).id;
    }

    /**
     * Return a hash code for this node, based solely on its id.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
}
