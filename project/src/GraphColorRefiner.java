import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;
import java.util.stream.Stream;

/**
 * Performs DISJOINT color refinement on TWO GraphStream graphs concurrently.
 */
public class GraphColorRefiner {

    private final Graph graph1;
    private final Graph graph2;
    private int iterationCount = 0;
    // Track stability independently for each graph
    private boolean refinementComplete1 = false;
    private boolean refinementComplete2 = false;

    // Signature record for single-graph refinement
    private record NodeSignature(int currentColor, Set<Integer> neighborColors, Set<Integer> nonNeighborColors, int pebbled) {}

    /**
     * Constructor for disjoint refinement on two graphs.
     * @param graph1 The first graph.
     * @param graph2 The second graph.
     */
    public GraphColorRefiner(Graph graph1, Graph graph2) {
        Objects.requireNonNull(graph1, "Graph 1 cannot be null");
        Objects.requireNonNull(graph2, "Graph 2 cannot be null");
        this.graph1 = graph1;
        this.graph2 = graph2;
    }

    /**
     * Initializes node color classes based on their degree IN THEIR RESPECTIVE GRAPHS.
     */
    public void initializeByDegree() {
        System.out.println("Initializing node colors by degree for both graphs (independently)...");
        initializeSingleGraphByDegree(graph1, 1);
        initializeSingleGraphByDegree(graph2, 2);

        this.iterationCount = 0;
        this.refinementComplete1 = false; // Reset stability flags
        this.refinementComplete2 = false;
        System.out.println("Initialization complete. Iteration count reset.");
    }

    // Helper to initialize a single graph
    private void initializeSingleGraphByDegree(Graph graph, int graphNum) {
         System.out.println(" Initializing Graph " + graphNum + " (ID: " + graph.getId() + ")");
         for (Node node : graph) {
            int degree = node.getDegree();
            node.setAttribute("color_class", degree);
            node.setAttribute("next_color_class", degree);
        }
    }

    public void initializeNodeSameColors() {
        initializeNodeSameColors(graph1);
        initializeNodeSameColors(graph2);
        
        iterationCount = 0;
        this.refinementComplete1 = false; // Reset stability flags
        this.refinementComplete2 = false;
    }

    public void initializeNodeSameColors(Graph graph) {
        for (Node node : graph) {
            node.setAttribute("color_class", 0); // Initial color class is 0
            node.setAttribute("next_color_class", 0); // Initialize next color
        }
        iterationCount = 0;
        this.refinementComplete1 = false; // Reset stability flags
        this.refinementComplete2 = false;
    }

    /**
     * Performs a single refinement step independently for graph1 (if not stable)
     * and for graph2 (if not stable).
     *
     * @return true if the color class of any node changed in EITHER graph during this step, false otherwise.
     */
    public boolean refineSingleStep() {
        boolean changed1 = false;
        boolean changed2 = false;

        // Refine graph 1 if it hasn't reached stability yet
        if (!refinementComplete1) {
            // System.out.println("Refining step for Graph 1..."); // Debug
            changed1 = refineSingleGraphStep(graph1);
            if (!changed1) {
                refinementComplete1 = true; // Graph 1 is now stable
                System.out.println("Graph 1 (ID: " + graph1.getId() + ") refinement stable.");
            }
        }

        // Refine graph 2 if it hasn't reached stability yet
        if (!refinementComplete2) {
             // System.out.println("Refining step for Graph 2..."); // Debug
            changed2 = refineSingleGraphStep(graph2);
            if (!changed2) {
                refinementComplete2 = true; // Graph 2 is now stable
                 System.out.println("Graph 2 (ID: " + graph2.getId() + ") refinement stable.");
            }
        }

        boolean overallChanged = changed1 || changed2;

        // Increment shared iteration count if anything changed
        if (overallChanged) {
            iterationCount++;
        }

        return overallChanged;
    }

    /**
     * Performs a refinement step on a SINGLE graph.
     * Calculates signatures, assigns next colors, and updates current colors
     * based only on the data within that graph.
     * @param graph The graph to refine.
     * @return true if any node's color changed in this graph, false otherwise.
     */
    private boolean refineSingleGraphStep(Graph graph) {
        Map<NodeSignature, Integer> signatureToNewColor = new HashMap<>();
        Map<Node, NodeSignature> nodeSignatures = new HashMap<>();
        int nextColorId = 0;
        boolean colorsChanged = false;

        // 1. Calculate Signatures for each node in this graph
        for (Node node : graph) {
             if (node.getAttribute("color_class") == null) {
                 System.err.println("Warning: Node " + node.getId() + " in graph " + graph.getId() + " missing 'color_class'. Initializing to -1.");
                 node.setAttribute("color_class", -1);
                 node.setAttribute("next_color_class", -1);
             }
            int currentColor = node.getAttribute("color_class", Integer.class);

            // Get neighbor colors (only from this graph)
            Set<Integer> neighborColors = new TreeSet<>();
            Stream<Node> neighborIt = node.neighborNodes();
            Set<Node> neighbors = new HashSet<>();
            neighborIt.forEach(neighbor -> {
                if (neighbor.getAttribute("color_class") != null) {
                    neighborColors.add(neighbor.getAttribute("color_class", Integer.class));
               }
               neighbors.add(neighbor);
            });
            Iterator<Node> allNodes = graph.iterator();

            // Get non-neighbor colors (only from this graph)
            Set<Integer> nonNeighborColors = new TreeSet<>();
            while(allNodes.hasNext()){
                Node otherNode = allNodes.next();
                if (otherNode != node && !neighbors.contains(otherNode)) {
                    if (otherNode.getAttribute("color_class") != null) {
                        nonNeighborColors.add(otherNode.getAttribute("color_class", Integer.class));
                    }
                }
            }
            int pebbled = 0;
            if (node.hasAttribute("mark")){pebbled = 1;}
            NodeSignature signature = new NodeSignature(currentColor, neighborColors, nonNeighborColors, pebbled);
            nodeSignatures.put(node, signature);
        }

        // 2. Determine new color class based on signature (for this graph)
        for (Node node : graph) {
            NodeSignature signature = nodeSignatures.get(node);
             if (signature == null) continue; 

            if (!signatureToNewColor.containsKey(signature)) {
                signatureToNewColor.put(signature, nextColorId++);
            }
            if (node.hasAttribute("next_color_class")) {
                node.setAttribute("next_color_class", signatureToNewColor.get(signature));
            }
        }

        // 3. Update colors and check if any changed (in this graph)
        for (Node node : graph) {
             if (!node.hasAttribute("color_class") || !node.hasAttribute("next_color_class")) continue;

            int currentC = node.getAttribute("color_class", Integer.class);
            int nextC = node.getAttribute("next_color_class", Integer.class);
            if (currentC != nextC) {
                colorsChanged = true;
                node.setAttribute("color_class", nextC);
            }
        }
        return colorsChanged;
    }


    /**
     * Runs the independent refinement processes step-by-step until BOTH are stable.
     */
    public void refineUntilStable() {
        System.out.println("Starting disjoint refinement until both graphs are stable...");
        // refineSingleStep continues as long as either graph changes
        while (refineSingleStep()) {
            // Loop continues while refineSingleStep returns true (meaning something changed)
             // System.out.println(" Iteration " + iterationCount + " completed."); // Optional debug
        }
        System.out.println("Disjoint refinement complete for both graphs after " + iterationCount + " total iterations.");
    }

    // --- Getters ---
    public Graph getGraph1() { return graph1; }
    public Graph getGraph2() { return graph2; }
    public int getIterationCount() { return iterationCount; }

    /**
     * Checks if the refinement process has reached a stable state for BOTH graphs.
     * @return true if both graphs are stable, false otherwise.
     */
    public boolean isStable() {
        return refinementComplete1 && refinementComplete2;
    }

     /**
      * Checks if the refinement process for a specific graph is stable.
      * @param graphIdentifier 1 for graph1, 2 for graph2
      * @return true if the specified graph's refinement is stable.
      * @throws IllegalArgumentException if identifier is not 1 or 2.
      */
     public boolean isGraphStable(int graphIdentifier) {
         if (graphIdentifier == 1) {
             return refinementComplete1;
         } else if (graphIdentifier == 2) {
             return refinementComplete2;
         } else {
             throw new IllegalArgumentException("Graph identifier must be 1 or 2.");
         }
     }
}