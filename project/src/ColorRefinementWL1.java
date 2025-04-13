import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph; // For example usage
import org.graphstream.ui.view.Viewer; // For example usage
import org.graphstream.ui.layout.Layout; // For example usage
import org.graphstream.ui.layout.springbox.implementations.SpringBox; // For example usage


import java.util.*;
import java.util.stream.Stream;

/**
 * Implements the Color Refinement algorithm (1-dimensional Weisfeiler-Leman or WL-1)
 * which is equivalent to the distinguishing power of the 2-pebble game.
 *
 * This variant iteratively refines node colors based on the multiset of their
 * neighbors' current colors.
 */
public class ColorRefinementWL1 {
    Map<String, String> currentColors;

    // Attribute name to store the color information on nodes
    private static final String COLOR_ATTRIBUTE = "wl_color";
    // Optional: Attribute for initial labels (if you want to use them)
    // private static final String INITIAL_LABEL_ATTRIBUTE = "ui.label"; // or "label"

    /**
     * Computes the stable coloring of a graph using the WL-1 algorithm.
     *
     * @param graph The input GraphStream graph. The graph structure is not modified,
     * but node attributes might be added/updated if storeColorsOnNodes is true.
     * @param storeColorsOnNodes If true, the final color string will be stored
     * in the COLOR_ATTRIBUTE of each node.
     * @return A Map where keys are Node IDs and values are the final stable color strings.
     */
    public static Map<String, String> computeStableColoring(Graph graph, boolean storeColorsOnNodes) {
        if (graph == null || graph.getNodeCount() == 0) {
            return Collections.emptyMap();
        }

        Map<String, String> currentColors = initializeColors(graph);
        boolean changed = true;
        int iteration = 0;

        System.out.println("Starting WL-1 Color Refinement...");
        // Print initial colors (optional)
        // System.out.println("Iteration 0 (Initial): " + currentColors);


        while (changed) {
            iteration++;
            changed = false;
            Map<String, String> nextColors = new HashMap<>();

            // Use streams for potentially cleaner iteration (or use traditional loops)
            for (Node node : graph) {
                String nodeId = node.getId();
                String currentColor = currentColors.get(nodeId);

                final Map<String, String> colorsForLambda = currentColors;

                // 1. Collect colors of neighbors
                List<String> neighborColors = new ArrayList<>();
                // Use iterator explicitly for clarity with getEachNeighbor()
                Stream<Node> neighbourNodes = node.neighborNodes();
                neighbourNodes.forEach(neighbourNode -> {
                    neighborColors.add(colorsForLambda.get(neighbourNode.getId()));
                });

                // 2. Sort neighbor colors to represent the multiset canonically
                Collections.sort(neighborColors);

                // 3. Create the signature: node's current color + sorted neighbor colors
                // Using a simple separator. Could use hashing for more compact colors.
                String signature = currentColor + "-" + String.join(",", neighborColors);

                // 4. The signature becomes the new color for the next iteration
                String newColor = signature; // In a more complex version, you might hash this signature
                nextColors.put(nodeId, newColor);

                // 5. Check if the color changed for this node
                System.out.println("==========================================");
                if (!newColor.equals(currentColor)) {
                    changed = true;
                }
            }

            // Update colors for the next round
            currentColors = nextColors;
            // System.out.println("Iteration " + iteration + ": " + (changed ? "Changed" : "Stable"));
            //  //Optional: Print colors per iteration
            //  System.out.println("  Colors: " + currentColors);
        }

        System.out.println("WL-1 Stable coloring reached after " + iteration + " iterations.");

        // Optional: Store the final colors as node attributes
        if (storeColorsOnNodes) {
            Map<String, String> finalColors = currentColors; // Need final variable for lambda
            graph.nodes().forEach(node -> node.setAttribute(COLOR_ATTRIBUTE, finalColors.get(node.getId())));
        }

        return currentColors;
    }

    /**
     * Initializes the colors for all nodes.
     * Default initialization uses the node's degree.
     * Can be adapted to use initial node labels if present.
     *
     * @param graph The input graph.
     * @return A map from Node ID to initial color string.
     */
    private static Map<String, String> initializeColors(Graph graph) {
        Map<String, String> initialColors = new HashMap<>();
        for (Node node : graph) {
            // Initial color based on degree - common practice for WL-1
            String initialColor = String.valueOf(node.getDegree());

            // --- Alternative: Use node labels if they exist ---
            // Object label = node.getAttribute(INITIAL_LABEL_ATTRIBUTE);
            // if (label != null) {
            //     initialColor = label.toString();
            // } else {
            //     // Fallback if no label (could still use degree or a default)
            //     initialColor = "DEFAULT_LABEL"; // Or String.valueOf(node.getDegree());
            // }
            // --- End Alternative ---

            initialColors.put(node.getId(), initialColor);
        }
        return initialColors;
    }

    /**
     * Helper method to assign distinct visual colors based on WL color classes.
     * This is purely for visualization.
     * @param graph The graph with WL colors computed.
     * @param finalWLColors The map returned by computeStableColoring.
     */
    public static void applyVisualColors(Graph graph, Map<String, String> finalWLColors) {
         if (finalWLColors.isEmpty()) return;

         // Map unique WL color strings to distinct visual colors
         Map<String, String> wlColorToVizColor = new HashMap<>();
         String[] vizColors = {"red", "blue", "green", "yellow", "purple", "orange", "pink", "cyan", "magenta", "brown", "grey"};
         int colorIndex = 0;

         // Assign a unique visual color to each unique WL color class found
         for (String wlColor : new HashSet<>(finalWLColors.values())) {
              if (!wlColorToVizColor.containsKey(wlColor)) {
                  wlColorToVizColor.put(wlColor, vizColors[colorIndex % vizColors.length]);
                  colorIndex++;
              }
         }

         // Apply the visual styles to the nodes
         graph.nodes().forEach(node -> {
              String wlColor = finalWLColors.get(node.getId());
              String vizColor = wlColorToVizColor.get(wlColor);
              if (vizColor != null) {
                  node.setAttribute("ui.style", "fill-color: " + vizColor + ";");
                  // Optionally add the WL color string as a label for inspection
                   node.setAttribute("ui.label", node.getId() + " [" + wlColor.substring(0, Math.min(wlColor.length(), 6)) + "...]"); // Show prefix
                   // node.setAttribute("ui.label", node.getId()); // Just show ID
              } else {
                   node.setAttribute("ui.style", "fill-color: black;"); // Default if something went wrong
                   node.setAttribute("ui.label", node.getId());
              }
         });
        System.out.println("Applied " + wlColorToVizColor.size() + " distinct visual colors based on WL classes.");
    }


    // --- Example Usage ---
    public static void main(String[] args) {
        // Required for GraphStream visualization
        System.setProperty("org.graphstream.ui", "swing"); // Use "swing" or "javafx"

        Graph graph = new SingleGraph("Presence Color Refinement");
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addNode("D");
        graph.addEdge("AB", "A", "B");
        graph.addEdge("AC", "A", "C");
        graph.addEdge("BD", "B", "D");
        graph.addEdge("CD", "C", "D");

        Map<String, String> result = computeStableColoring(graph, true);
        applyVisualColors(graph, result);
    

        graph.display();
    }
}