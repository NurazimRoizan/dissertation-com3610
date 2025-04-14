import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;
import java.util.stream.Stream;

/**
 * Performs color refinement on a GraphStream graph.
 * Initializes colors based on node degree and refines based on neighbor
 * and non-neighbor color sets until a stable state is reached.
 */
public class GraphColorRefiner {

    private final Graph graph;
    private int iterationCount = 0;
    private boolean refinementComplete = false;

    // Record to represent the signature for partitioning
    // Includes current color, sorted neighbor colors, sorted non-neighbor colors
    // Made public static so it can be potentially accessed if needed, or keep private if not.
    public static record NodeSignature(int currentColor, Set<Integer> neighborColors, Set<Integer> nonNeighborColors, boolean pebbled) {}

    /**
     * Constructor. Takes an existing GraphStream graph.
     * @param graph The graph to perform refinement on.
     */
    public GraphColorRefiner(Graph graph) {
        Objects.requireNonNull(graph, "Graph cannot be null");
        this.graph = graph;
    }

    /**
     * Initializes node color classes based on their degree (number of neighbors).
     * Should be called before starting refinement steps.
     */
    public void initializeByDegree() {
        System.out.println("Initializing node colors by degree...");
        for (Node node : graph) {
            int degree = node.getDegree();
            node.setAttribute("color_class", degree); // Use degree as initial color
            node.setAttribute("next_color_class", degree); // Initialize next color to the same
            // System.out.println("  Node " + node.getId() + ": Degree = " + degree); // Optional debug
        }
        this.iterationCount = 0;
        this.refinementComplete = false;
        System.out.println("Initialization complete. Iteration count reset.");
    }

    /**
     * Performs a single step of the color refinement algorithm.
     * Updates the "color_class" attribute on the nodes.
     *
     * @return true if any node's color class changed during this step, false otherwise.
     */
    public boolean refineSingleStep() {
        if (refinementComplete) {
            return false; // Already stable
        }

        Map<NodeSignature, Integer> signatureToNewColor = new HashMap<>();
        Map<Node, NodeSignature> nodeSignatures = new HashMap<>();
        int nextColorId = 0; // This will assign new IDs starting from 0 based on signatures
        boolean colorsChanged = false;

        // 1. Calculate Signature for each node based on CURRENT colors
        for (Node node : graph) {
            // Ensure attribute exists before getting, handle potential null if graph modified externally
             if (node.getAttribute("color_class") == null) {
                 System.err.println("Warning: Node " + node.getId() + " missing 'color_class'. Initializing to -1.");
                 node.setAttribute("color_class", -1); // Or handle appropriately
                 node.setAttribute("next_color_class", -1);
             }
            int currentColor = node.getAttribute("color_class", Integer.class);


            // Get neighbor colors
            Set<Integer> neighborColors = new TreeSet<>(); // Use TreeSet for canonical order
            //Iterator<Node> neighborIt = node.getNeighborNodeIterator();
            Stream<Node> neighborIt = node.neighborNodes();
            Set<Node> neighbors = new HashSet<>(); // Keep track of neighbors
            neighborIt.forEach(neighbor -> {
                if (neighbor.getAttribute("color_class") != null) { // Check neighbor attribute
                    neighborColors.add(neighbor.getAttribute("color_class", Integer.class));
                 } else {
                     System.err.println("Warning: Neighbor " + neighbor.getId() + " of node " + node.getId() + " missing 'color_class'. Skipping neighbor color.");
                 }
                neighbors.add(neighbor);
            });

            Iterator<Node> allNodes = graph.iterator();// Get all nodes once

            // Get non-neighbor colors
            Set<Integer> nonNeighborColors = new TreeSet<>(); // Use TreeSet for canonical order
            while(allNodes.hasNext()){
                System.out.println("while =================");
                Node otherNode = allNodes.next();
                if (otherNode != node && !neighbors.contains(otherNode)) {
                    if (otherNode.getAttribute("color_class") != null) { // Check other node attribute
                       nonNeighborColors.add(otherNode.getAttribute("color_class", Integer.class));
                    } else {
                         System.err.println("Warning: Non-neighbor " + otherNode.getId() + " of node " + node.getId() + " missing 'color_class'. Skipping non-neighbor color.");
                    }
               }
            }

            // Create the signature
            boolean pebbled = false;
            if (node.hasAttribute("mark")){pebbled = true;}
            NodeSignature signature = new NodeSignature(currentColor, neighborColors, nonNeighborColors, pebbled);
            nodeSignatures.put(node, signature);
        }

        // 2. Determine NEW color class based on signature
        // Assign new contiguous IDs (0, 1, 2...) based on unique signatures found
        Map<NodeSignature, Integer> assignedNewColors = new HashMap<>();
        int currentNewColorId = 0;

        for (Node node : graph) {
            NodeSignature signature = nodeSignatures.get(node);
            if (signature == null) continue; // Should not happen if calculated above, but safe check

            if (!assignedNewColors.containsKey(signature)) {
                assignedNewColors.put(signature, currentNewColorId++);
            }
             if (node.getAttribute("next_color_class") != null) { // Check attribute exists
                node.setAttribute("next_color_class", assignedNewColors.get(signature));
             } else {
                 System.err.println("Warning: Node " + node.getId() + " missing 'next_color_class' during assignment.");
             }
        }

        // 3. Update colors and check if any changed
        for (Node node : graph) {
             if (node.getAttribute("color_class") == null || node.getAttribute("next_color_class") == null) {
                  System.err.println("Warning: Node " + node.getId() + " missing attributes during update check.");
                 continue;
             }
            int currentC = node.getAttribute("color_class", Integer.class);
            int nextC = node.getAttribute("next_color_class", Integer.class);
            if (currentC != nextC) {
                colorsChanged = true;
                node.setAttribute("color_class", nextC); // Update the actual color class
            }
        }

        if (colorsChanged) {
            iterationCount++;
        } else {
            refinementComplete = true; // No changes means we reached a stable state
            System.out.println("Refinement stable after " + iterationCount + " iterations.");
        }

        return colorsChanged;
    }

    /**
     * Runs the refinement process step-by-step until no more changes occur.
     * Make sure to call initializeByDegree() before this method.
     */
    public void refineUntilStable() {
        System.out.println("Starting refinement until stable...");
        boolean changed;
        do {
            changed = refineSingleStep();
        } while (changed);
        System.out.println("Refinement complete.");
    }

    /**
     * Gets the underlying GraphStream graph.
     * @return The graph instance.
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Gets the number of refinement iterations performed so far.
     * @return The iteration count.
     */
    public int getIterationCount() {
        return iterationCount;
    }

    /**
     * Checks if the refinement process has reached a stable state.
     * @return true if stable, false otherwise.
     */
    public boolean isStable() {
        return refinementComplete;
    }
}