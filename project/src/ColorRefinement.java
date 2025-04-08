import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*; // For testing with SingleGraph if needed
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements the Color Refinement algorithm (1-WL Test) for GraphStream graphs.
 * It computes a stable coloring for the disjoint union of two graphs.
 */
public class ColorRefinement {

    // Attribute names used internally and for the final result
    private static final String CURRENT_COLOR_ATTR = "temp.color_refinement.current_color";
    private static final String NEXT_COLOR_ATTR = "temp.color_refinement.next_color";
    public static final String FINAL_COLOR_ATTR = "color_refinement.final_color"; // Public for easy access

    /**
     * Performs iterative color refinement (1-WL test) on the disjoint union of two graphs.
     * Stores the final stable color as an integer attribute (FINAL_COLOR_ATTR) on each node.
     *
     * @param g1 The first graph.
     * @param g2 The second graph.
     * @param initialColorByDegree If true, initializes colors by node degree; otherwise, all nodes start with color 0.
     * @return A Map mapping Node ID (String) to its final stable color (Integer) for all nodes in both graphs. Returns null if inputs are invalid.
     */
    public static Map<String, Integer> refine(Graph g1, Graph g2, boolean initialColorByDegree) {
        if (g1 == null || g2 == null) {
            System.err.println("Error: Input graphs cannot be null.");
            return null;
        }

        // Combine nodes from both graphs for unified processing
        List<Node> allNodes = new ArrayList<>();
        g1.nodes().forEach(allNodes::add);
        g2.nodes().forEach(allNodes::add);

        if (allNodes.isEmpty()) {
             System.out.println("Warning: No nodes found in the graphs.");
            return new HashMap<>(); // Return empty map for empty graphs
        }


        // --- 1. Initialization ---
        Map<String, Integer> signatureToColorMap = new HashMap<>();
        int nextColorId = 0; // Counter for assigning new color IDs

        // Assign initial colors based on the chosen strategy
        for (Node node : allNodes) {
            int initialColorValue;
            String signature; // Represents the feature used for initial color

            if (initialColorByDegree) {
                signature = "degree=" + node.getDegree();
            } else {
                signature = "uniform_start_color"; // All nodes get the same initial signature
            }

            // Map the signature to a unique integer color ID
            if (!signatureToColorMap.containsKey(signature)) {
                signatureToColorMap.put(signature, nextColorId++);
            }
            initialColorValue = signatureToColorMap.get(signature);
            node.setAttribute(CURRENT_COLOR_ATTR, initialColorValue);
            // Clean up previous results if any
            node.removeAttribute(FINAL_COLOR_ATTR);
            node.removeAttribute(NEXT_COLOR_ATTR);
        }
        System.out.println("Initial coloring done. Unique initial colors found: " + nextColorId);


        // --- 2. Iteration Loop ---
        boolean changed = true;
        int iteration = 0;
        int maxIterations = allNodes.size() + 2; // Theoretical max, usually stabilizes much faster

        while (changed && iteration < maxIterations) {
            iteration++;
            changed = false;
            signatureToColorMap.clear(); // Reset map for new signatures in this round
            // nextColorId continues to increment ensuring unique colors across iterations if needed,
            // although reusing color IDs based on signatures within an iteration is typical.
            // Let's reset nextColorId too, to keep color values smaller, as they only
            // need to be distinct *within* an iteration's mapping.
            nextColorId = 0;


            Map<Node, String> nodeSignaturesThisIteration = new HashMap<>();

            // --- Calculate signature for all nodes based on CURRENT colors ---
            for (Node node : allNodes) {
                int currentNodeColor = (int) node.getAttribute(CURRENT_COLOR_ATTR);

                // Collect neighbor colors (use stream API for conciseness and sorting)
                List<Integer> neighborColors = node.neighborNodes()
                    .map(neighbor -> (Integer) neighbor.getAttribute(CURRENT_COLOR_ATTR))
                    .sorted() // Sort to make the multiset representation canonical
                    .collect(Collectors.toList());

                // Create the signature string combining node's color and its neighbors' colors
                // Format: "nodeColor-[neighbor1Color,neighbor2Color,...]"
                String signature = currentNodeColor + "-" + neighborColors.toString();
                nodeSignaturesThisIteration.put(node, signature); // Store signature for later lookup

                // Assign a unique ID *for this iteration* to this signature if it's new
                if (!signatureToColorMap.containsKey(signature)) {
                    signatureToColorMap.put(signature, nextColorId++);
                }
            }

             // --- Determine next color and check for changes ---
             for (Node node : allNodes) {
                 int currentColor = (int) node.getAttribute(CURRENT_COLOR_ATTR);
                 String signature = nodeSignaturesThisIteration.get(node);
                 int nextPotentialColor = signatureToColorMap.get(signature); // Get the color assigned to this signature

                 node.setAttribute(NEXT_COLOR_ATTR, nextPotentialColor); // Store tentative next color

                 // Check if this node's color will change
                 if (currentColor != nextPotentialColor) {
                     changed = true;
                 }
             }

             // If changes occurred, apply the next colors to become the current colors for the next iteration
             if (changed) {
                 for (Node node : allNodes) {
                     int nextColor = (int) node.getAttribute(NEXT_COLOR_ATTR);
                     node.setAttribute(CURRENT_COLOR_ATTR, nextColor);
                 }
             }
            // Optional: Add debug output for iteration count and changes
            // System.out.println("Iteration " + iteration + " completed. Changed: " + changed + ". Unique signatures this round: " + signatureToColorMap.size());

        } // End while loop (stabilization or max iterations)

        if (iteration >= maxIterations && changed) {
             System.err.println("Warning: Color refinement did not stabilize within " + maxIterations + " iterations.");
        } else {
            System.out.println("Color Refinement stabilized after " + iteration + " iterations.");
        }

        // --- 3. Finalization ---
        Map<String, Integer> finalColorMapResult = new HashMap<>();
        for (Node node : allNodes) {
            int finalColor = (int) node.getAttribute(CURRENT_COLOR_ATTR);
            // Clean up temporary attributes
            node.removeAttribute(NEXT_COLOR_ATTR);
            node.removeAttribute(CURRENT_COLOR_ATTR);
            // Set the final, persistent attribute
            node.setAttribute(FINAL_COLOR_ATTR, finalColor);

            // Populate the result map
            finalColorMapResult.put(node.getId(), finalColor);
        }

        return finalColorMapResult;
    }

    // // --- Example Usage (main method for testing) ---
    // public static void main(String[] args) {
    //     // Set up GraphStream UI
    //     System.setProperty("org.graphstream.ui", "swing");

    //     // --- Create two example graphs ---
    //     // Graph 1: A cycle graph C4 with one node having an extra edge (a "tail")
    //     Graph g1 = new SingleGraph("G1");
    //     g1.addNode("A1").setAttribute("ui.label", "A1");
    //     g1.addNode("B1").setAttribute("ui.label", "B1");
    //     g1.addNode("C1").setAttribute("ui.label", "C1");
    //     g1.addNode("D1").setAttribute("ui.label", "D1");
    //     g1.addNode("E1").setAttribute("ui.label", "E1"); // Tail node
    //     g1.addEdge("e1", "A1", "B1");
    //     g1.addEdge("e2", "B1", "C1");
    //     g1.addEdge("e3", "C1", "D1");
    //     g1.addEdge("e4", "D1", "A1"); // Cycle edge
    //     g1.addEdge("e5", "A1", "E1"); // Tail edge

    //     // Graph 2: A simple cycle graph C5 (different structure)
    //     Graph g2 = new SingleGraph("G2");
    //     g2.addNode("A2").setAttribute("ui.label", "A2");
    //     g2.addNode("B2").setAttribute("ui.label", "B2");
    //     g2.addNode("C2").setAttribute("ui.label", "C2");
    //     g2.addNode("D2").setAttribute("ui.label", "D2");
    //     g2.addNode("E2").setAttribute("ui.label", "E2");
    //     g2.addEdge("f1", "A2", "B2");
    //     g2.addEdge("f2", "B2", "C2");
    //     g2.addEdge("f3", "C2", "D2");
    //     g2.addEdge("f4", "D2", "E2");
    //     //g2.addEdge("f5", "E2", "A2"); // Cycle edge

    //     // --- Perform color refinement ---
    //     // Initialize by degree (true) or uniformly (false)
    //     Map<String, Integer> finalColors = refine(g1, g2, true);

    //     // --- Output and Visualization ---
    //     if (finalColors != null) {
    //         System.out.println("\nFinal Colors Map (NodeID -> Color):");
    //         finalColors.forEach((nodeId, color) ->
    //             System.out.println("  Node " + nodeId + ": Color " + color)
    //         );

    //         // Assign colors visually for display using a predefined color palette
    //         String[] uiColors = {"blue", "red", "green", "yellow", "purple", "orange", "pink", "cyan", "magenta", "gray", "brown", "lime"};
    //         g1.nodes().forEach(n -> {
    //              Object colorAttr = n.getAttribute(FINAL_COLOR_ATTR);
    //              if (colorAttr instanceof Integer) {
    //                  int colorIndex = (Integer) colorAttr;
    //                  String colorString = uiColors[Math.abs(colorIndex) % uiColors.length]; // Use modulo and abs
    //                  n.setAttribute("ui.style", "fill-color: " + colorString + "; size: 20px; text-size: 16px;");
    //                  n.setAttribute("ui.label", n.getId() + " [" + colorIndex + "]"); // Show color in label
    //              } else {
    //                  n.setAttribute("ui.label", n.getId() + " [NoColor]");
    //              }

    //         });
    //          g2.nodes().forEach(n -> {
    //             Object colorAttr = n.getAttribute(FINAL_COLOR_ATTR);
    //              if (colorAttr instanceof Integer) {
    //                  int colorIndex = (Integer) colorAttr;
    //                  String colorString = uiColors[Math.abs(colorIndex) % uiColors.length]; // Use modulo and abs
    //                  n.setAttribute("ui.style", "fill-color: " + colorString + "; size: 20px; text-size: 16px;");
    //                  n.setAttribute("ui.label", n.getId() + " [" + colorIndex + "]"); // Show color in label
    //               } else {
    //                  n.setAttribute("ui.label", n.getId() + " [NoColor]");
    //              }
    //         });

    //         System.out.println("\nDisplaying graphs. Nodes are colored by final refinement color.");
    //         g1.display(); // Display G1 (don't close automatically)
    //         g2.display();    // Display G2 (closing this closes the application)
    //     }
    // }

    // Inside main or a test method
    public static void runCFIExample() {
        System.setProperty("org.graphstream.ui", "swing");
        System.out.println("\n--- Example: CFI-Inspired Graphs ---");
        Graph g1 = new SingleGraph("G1_CFI"); // C6 with P2 attached to each node
        Graph g2 = new SingleGraph("G2_CFI"); // 2x C3 with P2 attached to each node

        // Build G1
        Node[] cycle1 = new Node[6];
        for (int i = 0; i < 6; i++) {
            cycle1[i] = g1.addNode("c1_" + i);
            cycle1[i].setAttribute("ui.label", "c1_" + i);
            // Add P2 gadget
            Node p1 = g1.addNode("p1_" + i);
            Node l1 = g1.addNode("l1_" + i);
            p1.setAttribute("ui.label", "p1_" + i);
            l1.setAttribute("ui.label", "l1_" + i);
            g1.addEdge("g1_c" + i + "_p" + i, cycle1[i], p1);
            g1.addEdge("g1_p" + i + "_l" + i, p1, l1);
        }
        for (int i = 0; i < 6; i++) {
            g1.addEdge("g1_cycle_" + i, cycle1[i], cycle1[(i + 1) % 6]);
        }

        // Build G2
        Node[] cycle2a = new Node[3];
        Node[] cycle2b = new Node[3];
        for (int i = 0; i < 3; i++) {
            // First C3
            cycle2a[i] = g2.addNode("c2a_" + i);
             cycle2a[i].setAttribute("ui.label", "c2a_" + i);
            Node p2a = g2.addNode("p2a_" + i);
            Node l2a = g2.addNode("l2a_" + i);
            p2a.setAttribute("ui.label", "p2a_" + i);
            l2a.setAttribute("ui.label", "l2a_" + i);
            g2.addEdge("g2a_c" + i + "_p" + i, cycle2a[i], p2a);
            g2.addEdge("g2a_p" + i + "_l" + i, p2a, l2a);
            // Second C3
            cycle2b[i] = g2.addNode("c2b_" + i);
             cycle2b[i].setAttribute("ui.label", "c2b_" + i);
            Node p2b = g2.addNode("p2b_" + i);
            Node l2b = g2.addNode("l2b_" + i);
            p2b.setAttribute("ui.label", "p2b_" + i);
            l2b.setAttribute("ui.label", "l2b_" + i);
            g2.addEdge("g2b_c" + i + "_p" + i, cycle2b[i], p2b);
            g2.addEdge("g2b_p" + i + "_l" + i, p2b, l2b);
        }
         for (int i = 0; i < 3; i++) {
             g2.addEdge("g2a_cycle_" + i, cycle2a[i], cycle2a[(i + 1) % 3]);
             g2.addEdge("g2b_cycle_" + i, cycle2b[i], cycle2b[(i + 1) % 3]);
         }


        // Perform color refinement (use degree init, should yield same initial partitioning)
        Map<String, Integer> finalColorsCFI = refine(g1, g2, true);

        // Check colors of corresponding gadget nodes (e.g., p1_0 vs p2a_0, l1_0 vs l2a_0)
        System.out.println("\nChecking Colors for Isomorphic Subgraphs (P2 gadgets):");
        Integer color_p1_0 = finalColorsCFI.get("p1_0");
        Integer color_l1_0 = finalColorsCFI.get("l1_0");
        Integer color_p2a_0 = finalColorsCFI.get("p2a_0"); // Corresponding gadget in G2's first triangle
        Integer color_l2a_0 = finalColorsCFI.get("l2a_0");

        System.out.println("G1 (Gadget 0): p1_0 color = " + color_p1_0 + ", l1_0 color = " + color_l1_0);
        System.out.println("G2 (Gadget 0a): p2a_0 color = " + color_p2a_0 + ", l2a_0 color = " + color_l2a_0);

        if (color_p1_0 != null && color_p2a_0 != null && color_p1_0.equals(color_p2a_0)) {
             System.out.println("Result: Nodes p1_0 and p2a_0 HAVE the same final color.");
        } else {
             System.out.println("Result: Nodes p1_0 and p2a_0 have DIFFERENT final colors.");
        }
         if (color_l1_0 != null && color_l2a_0 != null && color_l1_0.equals(color_l2a_0)) {
             System.out.println("Result: Nodes l1_0 and l2a_0 HAVE the same final color.");
        } else {
             System.out.println("Result: Nodes l1_0 and l2a_0 have DIFFERENT final colors.");
        }

         // Add visualization code if desired

                 // --- Output and Visualization ---
        if (finalColorsCFI != null) {
            System.out.println("\nFinal Colors Map (NodeID -> Color):");
            finalColorsCFI.forEach((nodeId, color) ->
                System.out.println("  Node " + nodeId + ": Color " + color)
            );

            // Assign colors visually for display using a predefined color palette
            String[] uiColors = {"blue", "red", "green", "yellow", "purple", "orange", "pink", "cyan", "magenta", "gray", "brown", "lime"};
            g1.nodes().forEach(n -> {
                 Object colorAttr = n.getAttribute(FINAL_COLOR_ATTR);
                 if (colorAttr instanceof Integer) {
                     int colorIndex = (Integer) colorAttr;
                     String colorString = uiColors[Math.abs(colorIndex) % uiColors.length]; // Use modulo and abs
                     n.setAttribute("ui.style", "fill-color: " + colorString + "; size: 20px; text-size: 16px;");
                     n.setAttribute("ui.label", n.getId() + " [" + colorIndex + "]"); // Show color in label
                 } else {
                     n.setAttribute("ui.label", n.getId() + " [NoColor]");
                 }

            });
             g2.nodes().forEach(n -> {
                Object colorAttr = n.getAttribute(FINAL_COLOR_ATTR);
                 if (colorAttr instanceof Integer) {
                     int colorIndex = (Integer) colorAttr;
                     String colorString = uiColors[Math.abs(colorIndex) % uiColors.length]; // Use modulo and abs
                     n.setAttribute("ui.style", "fill-color: " + colorString + "; size: 20px; text-size: 16px;");
                     n.setAttribute("ui.label", n.getId() + " [" + colorIndex + "]"); // Show color in label
                  } else {
                     n.setAttribute("ui.label", n.getId() + " [NoColor]");
                 }
            });
        }
         // ...
         g1.display();
         g2.display();
    }

    // Add this method call within the main method or a test runner
    // public static void main(String[] args) { ... runCFIExample(); ... }

    public static void main(String[] args){
        runCFIExample();
    }
}