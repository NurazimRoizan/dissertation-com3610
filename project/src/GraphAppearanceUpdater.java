import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import javax.swing.SwingUtilities; // For invokeLater
import java.util.List;
import java.util.Objects;

/**
 * A utility class with static methods to update the visual appearance
 * of GraphStream graphs based on node attributes.
 * Assumes the graph is potentially displayed in a Swing GUI.
 */
public final class GraphAppearanceUpdater { // final to prevent subclassing utility class

    // Private constructor to prevent instantiation of utility class
    private GraphAppearanceUpdater() {}

    // Define the color palette (can be customized)
    private static final List<String> COLOR_PALETTE = List.of(
            "red", "blue", "green", "yellow", "orange", "purple", "cyan", "magenta",
            "lime", "pink", "teal", "lavender", "brown", "beige", "maroon", "olive"
            // Add more colors if needed
    );

    /**
     * Updates the visual appearance (color and label) of nodes in the given graph
     * based on their "color_class" integer attribute.
     *
     * This method ensures the attribute setting happens on the Swing Event Dispatch Thread
     * for safety if the graph is actively displayed in a GUI.
     *
     * @param graph The GraphStream graph whose node appearances should be updated. Must not be null.
     */
    public static void updateNodeAppearance(Graph graph) {
        Objects.requireNonNull(graph, "Graph cannot be null");

        // Ensure UI attribute updates happen on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // System.out.println("Updating graph node appearance on EDT..."); // Optional debug
            for (Node node : graph) {
                // Check if the attribute exists before trying to access it
                if (node.hasAttribute("color_class")) {
                    try {
                        // Read the color class attribute
                        int colorClass = node.getAttribute("color_class", Integer.class);
                        // Select color from palette (use absolute value and modulo)
                        String colorName = COLOR_PALETTE.get(Math.abs(colorClass) % COLOR_PALETTE.size());
                        // Apply style for color
                        node.setAttribute("ui.style", "fill-color: " + colorName + ";");
                        // Update label to show ID and color class
                        //node.setAttribute("ui.label", node.getId() + " (" + colorClass + ")");

                    } catch (Exception e) {
                        // Handle cases where attribute exists but isn't an Integer or other issues
                        System.err.println("Error processing 'color_class' for node " + node.getId() + ": " + e.getMessage());
                        node.setAttribute("ui.style", "fill-color: black;"); // Default error color
                        //node.setAttribute("ui.label", node.getId() + " (Error)");
                    }
                } else {
                    // Node is missing the attribute - apply a default appearance
                    node.setAttribute("ui.style", "fill-color: gray;"); // Default color for unclassified
                    //node.setAttribute("ui.label", node.getId() + " (N/A)");
                }
            }
             // System.out.println("Node appearance update complete on EDT."); // Optional debug
        });
    }

    /**
     * Ensures that the graph has a basic stylesheet applied, which is necessary
     * for properties like "fill-color" to work correctly.
     * If no "ui.stylesheet" attribute exists, it adds a default one.
     *
     * @param graph The graph to check and potentially update. Must not be null.
     */
    public static void ensureBaseStylesheet(Graph graph) {
        Objects.requireNonNull(graph, "Graph cannot be null");
        // Check and apply on the calling thread, as it's usually done during setup
        if (graph.getAttribute("ui.stylesheet") == null) {
            System.out.println("Applying default base stylesheet to graph: " + graph.getId()); // Info
            graph.setAttribute("ui.stylesheet", getBaseStylesheet());
        }
    }

    /**
     * Returns a basic GraphStream stylesheet string.
     * @return A default stylesheet string.
     */
    private static String getBaseStylesheet() {
        // Provides basic node/edge appearance, includes 'fill-mode: plain;'
        // which is important for 'fill-color' to take effect.
        return "node {" +
               "   size: 15px;" +
               "   fill-mode: plain;" + // Important for fill-color
               "   text-size: 12;" +
               "   text-alignment: above;" +
               "   stroke-mode: plain;" +
               "   stroke-color: black;" +
               "   stroke-width: 1px;" +
               "}" +
               "edge {" +
               "   size: 1px;" +
               "   fill-color: grey;" +
               "}";
    }
}