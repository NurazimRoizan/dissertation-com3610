import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List; // Import List
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.swing_viewer.SwingViewer; // Correct import
import org.graphstream.ui.swing_viewer.ViewPanel; // Correct import
import org.graphstream.ui.view.Viewer; // Correct import

public class NonCountingColourRefinementAlgorithm extends JFrame {

    private Graph graph;
    private ViewPanel viewPanel;
    private Viewer viewer;
    private int iterationCount = 0;
    private boolean refinementComplete = false;

    // Define a color palette for visualization
    private static final List<String> COLOR_PALETTE = List.of(
            "red", "blue", "green", "yellow", "orange", "purple", "cyan", "magenta",
            "lime", "pink", "teal", "lavender", "brown", "beige", "maroon", "olive"
            // Add more colors if needed
    );

    // Record to represent the signature for partitioning
    // Includes current color, sorted neighbor colors, sorted non-neighbor colors
    private record NodeSignature(int currentColor, Set<Integer> neighborColors, Set<Integer> nonNeighborColors) {}

    public NonCountingColourRefinementAlgorithm() {
        super("Color Refinement Variant (Neighbors & Non-Neighbors)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the window

        // --- Graph Setup ---
        System.setProperty("org.graphstream.ui", "swing"); // Use Swing UI
        graph = new SingleGraph("ColorRefinementGraph");
        createExampleGraph(); // Populate the graph
        initializeNodeColorsByDegree();
        //initializeNodeColors(); // Set initial color (e.g., 0 for all)

        // --- GraphStream Viewer Setup ---
        viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout(); // Enable layout algorithm
        viewPanel = (ViewPanel) viewer.addDefaultView(false); // false means no JFrame wrapper
        viewPanel.setPreferredSize(new Dimension(600, 500));

        // --- Control Panel ---
        JPanel controlPanel = new JPanel();
        JButton stepButton = new JButton("Refine Step");
        JButton runAllButton = new JButton("Run Full Refinement");
        JLabel statusLabel = new JLabel("Iteration: 0. Initial state.");

        stepButton.addActionListener(e -> {
            if (!refinementComplete) {
                boolean changed = refineStep();
                iterationCount++;
                if (!changed) {
                    refinementComplete = true;
                    statusLabel.setText("Iteration: " + iterationCount + ". Stable state reached.");
                    stepButton.setEnabled(false);
                    runAllButton.setEnabled(false);
                } else {
                    statusLabel.setText("Iteration: " + iterationCount + ". Colors refined.");
                }
            }
        });

        runAllButton.addActionListener(e -> {
            if (refinementComplete) return;

            boolean changed = true;
            while (changed && !refinementComplete) {
                changed = refineStep();
                iterationCount++;
                 if (!changed) {
                    refinementComplete = true;
                 }
                 // Small delay to allow visualization update (optional)
                 try { Thread.sleep(100); } catch (InterruptedException ie) {Thread.currentThread().interrupt();}
            }
             statusLabel.setText("Iteration: " + iterationCount + ". Stable state reached.");
             stepButton.setEnabled(false);
             runAllButton.setEnabled(false);
        });

        controlPanel.add(stepButton);
        controlPanel.add(runAllButton);
        controlPanel.add(statusLabel);

        // --- Layout ---
        setLayout(new BorderLayout());
        add(viewPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        // Initial display update
        updateGraphAppearance();
    }

    private void createExampleGraph() {
        // Example: A simple graph (e.g., a path graph or a cycle)
        graph.setStrict(false); // Allow auto-creation of nodes on edge add
        graph.setAutoCreate(true);

        // A small graph for demonstration
        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CD", "C", "D");
        graph.addEdge("DE", "D", "E");
        graph.addEdge("EF", "E", "F");
        graph.addEdge("FA", "F", "A"); // Cycle A-B-C-D-E-F-A
        graph.addEdge("AC", "A", "C"); // Chord
        graph.addEdge("BD", "B", "D"); // Chord

        // Add an isolated node
        //graph.addNode("G");

        // Basic styling
        graph.setAttribute("ui.stylesheet", getStylesheet());
    }

     private String getStylesheet() {
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

    /**
     * Initializes node color classes based on their degree (number of neighbors).
     */
    private void initializeNodeColorsByDegree() {
        System.out.println("Initializing node colors by degree...");
        for (Node node : graph) {
            int degree = node.getDegree();
            node.setAttribute("color_class", degree); // Use degree as initial color
            node.setAttribute("next_color_class", degree); // Initialize next color to the same
            System.out.println("  Node " + node.getId() + ": Degree = " + degree);
        }
        iterationCount = 0;
        refinementComplete = false;
    }


    private void initializeNodeColors() {
        for (Node node : graph) {
            node.setAttribute("color_class", 0); // Initial color class is 0
            node.setAttribute("next_color_class", 0); // Initialize next color
        }
        iterationCount = 0;
        refinementComplete = false;
    }

    private void updateGraphAppearance() {
        for (Node node : graph) {
            int colorClass = (int) node.getAttribute("color_class");
            String colorName = COLOR_PALETTE.get(colorClass % COLOR_PALETTE.size());
            node.setAttribute("ui.style", "fill-color: " + colorName + ";");
            node.setAttribute("ui.label", node.getId() + " (" + colorClass + ")"); // Show node ID and color class
        }
    }

    private boolean refineStep() {
        Map<NodeSignature, Integer> signatureToNewColor = new HashMap<>();
        Map<Node, NodeSignature> nodeSignatures = new HashMap<>();
        int nextColorId = 0;
        boolean colorsChanged = false;
        

        // 1. Calculate Signature for each node
        for (Node node : graph) {
            int currentColor = (int) node.getAttribute("color_class");

            // Get neighbor colors
            Set<Integer> neighborColors = new TreeSet<>(); // Use TreeSet for canonical order

            Stream<Node> neighborIt = node.neighborNodes();
            Set<Node> neighbours = new HashSet<>(); // Keep track of neighbors
            neighborIt.forEach(neighbour -> {
                neighborColors.add((Integer) neighbour.getAttribute("color_class"));
                neighbours.add(neighbour);
            });

            Iterator<Node> allNodes = graph.iterator();// Get all nodes once
            // Get non-neighbor colors
            Set<Integer> nonNeighborColors = new TreeSet<>(); // Use TreeSet for canonical order
            while(allNodes.hasNext()){
                System.out.println("while =================");
                Node otherNode = allNodes.next();
                if (otherNode != node && !neighbours.contains(otherNode)) {
                    nonNeighborColors.add((Integer) otherNode.getAttribute("color_class"));
                    System.out.println("adding "+ otherNode.getId()+"into non neighbour");
                }
            }

            // Create the signature
            NodeSignature signature = new NodeSignature(currentColor, neighborColors, nonNeighborColors);
            nodeSignatures.put(node, signature);
            System.out.println("Node " + node.getId() + " Sig: " + signature); // Debug print
        }

        // 2. Determine new color class based on signature
        for (Node node : graph) {
            NodeSignature signature = nodeSignatures.get(node);
            if (!signatureToNewColor.containsKey(signature)) {
                signatureToNewColor.put(signature, nextColorId++);
            }
            node.setAttribute("next_color_class", signatureToNewColor.get(signature));
        }

        // 3. Update colors and check if any changed
        for (Node node : graph) {
            int currentC = (int) node.getAttribute("color_class");
            int nextC = (int) node.getAttribute("next_color_class");
            if (currentC != nextC) {
                colorsChanged = true;
                node.setAttribute("color_class", nextC);
            }
        }

        // 4. Update visual appearance
        updateGraphAppearance();

        return colorsChanged;
    }

    public static void main(String[] args) {
        // Run the application on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            NonCountingColourRefinementAlgorithm app = new NonCountingColourRefinementAlgorithm();
            app.setVisible(true);
        });
    }
}