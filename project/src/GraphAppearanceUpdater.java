import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import javax.swing.SwingUtilities; // For invokeLater
import java.util.List;
import java.util.Objects;

/**
 * A utility class with static methods to update the visual appearance
 * of GraphStream graphs based on node attributes.
 * Operates independently on the graphs provided.
 */
public final class GraphAppearanceUpdater {

    private GraphAppearanceUpdater() {} // Private constructor

    private static final List<String> COLOR_PALETTE = List.of(
            "red", "blue", "green", "yellow", "orange", "purple", "cyan", "magenta",
            "lime", "pink", "teal", "lavender", "brown", "beige", "maroon", "olive"
    );

    /**
     * Updates the visual appearance (color and label) of nodes in BOTH provided graphs
     * based on their respective "color_class" integer attributes.
     *
     * Ensures the attribute setting happens on the Swing Event Dispatch Thread.
     *
     * @param graph1 The first GraphStream graph. Must not be null.
     * @param graph2 The second GraphStream graph. Must not be null.
     */
    public static void updateNodeAppearance(Graph graph1, Graph graph2) {
        Objects.requireNonNull(graph1, "Graph 1 cannot be null");
        Objects.requireNonNull(graph2, "Graph 2 cannot be null");

        // Schedule update for both graphs on the EDT
        SwingUtilities.invokeLater(() -> {
            // System.out.println("Updating appearance for two graphs on EDT..."); // Debug
            updateSingleGraphAppearance(graph1, 1); // Update graph 1
            updateSingleGraphAppearance(graph2, 2); // Update graph 2
            // System.out.println("Appearance update for two graphs complete on EDT."); // Debug
        });
    }

    /** Internal helper to update a single graph */
    private static void updateSingleGraphAppearance(Graph graph, int graphNum) {
         // System.out.println(" Updating graph " + graphNum + " (ID: " + graph.getId() + ")");
        for (Node node : graph) {
            if (node.hasAttribute("color_class")) {
                try {
                    int colorClass = node.getAttribute("color_class", Integer.class);
                    String colorName = COLOR_PALETTE.get(Math.abs(colorClass) % COLOR_PALETTE.size());
                    node.setAttribute("ui.style", "fill-color: " + colorName + ";");
                    //node.setAttribute("ui.label", node.getId() + " (" + colorClass + ")");
                } catch (Exception e) {
                    System.err.println("Error processing 'color_class' for node " + node.getId() + " in graph " + graphNum + ": " + e.getMessage());
                    node.setAttribute("ui.style", "fill-color: black;");
                    //node.setAttribute("ui.label", node.getId() + " (Error)");
                }
            } else {
                node.setAttribute("ui.style", "fill-color: gray;");
                //node.setAttribute("ui.label", node.getId() + " (N/A)");
            }
        }
    }

    /**
     * Ensures that both graphs have a basic stylesheet applied.
     * @param graph1 The first graph. Must not be null.
     * @param graph2 The second graph. Must not be null.
     */
    public static void ensureBaseStylesheet(Graph graph1, Graph graph2) {
        Objects.requireNonNull(graph1, "Graph 1 cannot be null");
        Objects.requireNonNull(graph2, "Graph 2 cannot be null");
        ensureSingleGraphStylesheet(graph1, 1);
        ensureSingleGraphStylesheet(graph2, 2);
    }

    /** Internal helper for single graph stylesheet check */
    private static void ensureSingleGraphStylesheet(Graph graph, int graphNum) {
         if (graph.getAttribute("ui.stylesheet") == null) {
            System.out.println("Applying default base stylesheet to graph " + graphNum + " (ID: " + graph.getId() + ")");
            graph.setAttribute("ui.stylesheet", getBaseStylesheet());
        }
    }

    private static String getBaseStylesheet() {
        return "node {" +
               "   size: 15px;" +
               "   fill-mode: plain;" +
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