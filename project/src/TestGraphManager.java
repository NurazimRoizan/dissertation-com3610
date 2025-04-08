import org.graphstream.algorithm.generator.*;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class TestGraphManager{
    protected static String styleSheet =
        "node {" +
            // "     shape: box;" +
            "   stroke-mode: plain;" +
            //"   text-color: white;" +
            "   size: 10px, 15px;" +
            "   stroke-color: black;" +
            "   size: 20px; fill-color: rgb(100,255,100), rgba(255, 255, 255, 0); fill-mode: gradient-radial;" +
            // "   shadow-mode: gradient-radial; shadow-width: 5px; shadow-color: #EEF, #000; shadow-offset: 2px;" +
            "}" +
        "node.marked {" +
            "   fill-color: red;" +
            "}" +
        "node.colour {" +
            "   fill-mode: dyn-plain;" +
            // "   fill-color: red, darkgreen, white, blue, magenta, #444;" +
            "   fill-color: red, rgb(255, 200, 100), yellow, rgb(50, 200, 0), green, blue, darkblue, rgb(200, 100, 200), rgb(180, 0, 180), rgb(100, 200, 200), rgb(255, 102, 0);" +
            "}" +
        "node.colour0 {" +
            "   fill-color: gray;" +
            "}" +
        "node.spoiler {" +
            "   stroke-mode: plain;" +
            "   stroke-color: yellow;" +
            "   shape: triangle;" +
            "   stroke-width: 2px;" +
            "}" +
        "node.duplicator {" +
            "   stroke-mode: plain;" +
            "   shape: triangle;" +
            "   stroke-color: yellow;" +
            "   stroke-width: 2px;" +
            "}" +
        "edge {" +
            "   fill-color: brown;" +
            // "shape: cubic-curve;" +
            "}" +
        "graph {" +
            "   fill-color: #001329, #1C3353, red;" +
            "   fill-mode: gradient-vertical;" +
            "}";
    public static Graph createGraph(String name, Generator gen , int maxNode) {
                //Generator gen = new DorogovtsevMendesGenerator();
                //Generator gen = new BananaTreeGenerator();
                //Generator gen = new BarabasiAlbertGenerator(1);
                Graph graph = new SingleGraph(name);
                gen.addSink(graph);
                gen.begin();
                for (int i = 0; i < maxNode; i++) {
                    gen.nextEvents();
                }
                gen.end();
        
                graph.setAttribute("ui.stylesheet", styleSheet); 
                for (Node node : graph) {
                    node.setAttribute("ui.label", node.getId());
                    node.setAttribute("ui.class", "unmarked");
                }
                return graph;
    }
    public static Graph createGraph(String name, Generator gen) {
        return createGraph(name, gen, 10);
    }

    public static Graph createExampleGraph1(){
        Graph g1 = new SingleGraph("G1_CFI"); // C6 with P2 attached to each node

        // Build G1
        Node[] cycle1 = new Node[6];
        for (int i = 0; i < 6; i++) {
            cycle1[i] = g1.addNode(String.valueOf(i));
            cycle1[i].setAttribute("ui.label", String.valueOf(i));
            // Add P2 gadget
            Node p1 = g1.addNode(String.valueOf(i+10));
            Node l1 = g1.addNode(String.valueOf(i+20));
            p1.setAttribute("ui.label", String.valueOf(i+10));
            l1.setAttribute("ui.label", String.valueOf(i+20));
            g1.addEdge("g1_c" + i + "_p" + i, cycle1[i], p1);
            g1.addEdge("g1_p" + i + "_l" + i, p1, l1);
        }
        for (int i = 0; i < 6; i++) {
            g1.addEdge("g1_cycle_" + i, cycle1[i], cycle1[(i + 1) % 6]);
        }
        g1.setAttribute("ui.stylesheet", styleSheet); 
        for (Node node : g1) {
            node.setAttribute("ui.label", node.getId());
            node.setAttribute("ui.class", "unmarked");
        }
        return g1;
    }
    public static Graph createExampleGraph2(){
        Graph g2 = new SingleGraph("G2_CFI"); // 2x C3 with P2 attached to each node

        // Build G2
        Node[] cycle2a = new Node[3];
        for (int i = 0; i < 3; i++) {
            // First C3
            cycle2a[i] = g2.addNode(String.valueOf(i));
             cycle2a[i].setAttribute("ui.label", String.valueOf(i));
            Node p2a = g2.addNode(String.valueOf(i+10));
            Node l2a = g2.addNode(String.valueOf(i+20));
            p2a.setAttribute("ui.label", String.valueOf(i+10));
            l2a.setAttribute("ui.label", String.valueOf(i+20));
            g2.addEdge("g2a_c" + i + "_p" + i, cycle2a[i], p2a);
            g2.addEdge("g2a_p" + i + "_l" + i, p2a, l2a);
        }
         for (int i = 0; i < 3; i++) {
             g2.addEdge("g2a_cycle_" + i, cycle2a[i], cycle2a[(i + 1) % 3]);
             //g2.addEdge("g2b_cycle_" + i, cycle2b[i], cycle2b[(i + 1) % 3]);
         }
        g2.setAttribute("ui.stylesheet", styleSheet); 
        for (Node node : g2) {
            node.setAttribute("ui.label", node.getId());
            node.setAttribute("ui.class", "unmarked");
        }
        return g2;
    }

    /**
     * Creates G1 for the CFI-inspired example:
     * A C6 cycle with a P2 gadget (path) attached to each cycle node.
     * Node IDs: Cycle nodes 0-5, Path nodes 10-15, Leaf nodes 20-25.
     * @return Graph G1.
     */
    public static Graph createCFI_G1() {
        Graph g1 = new SingleGraph("G1_CFI");

        Node[] cycle1 = new Node[6];
        for (int i = 0; i < 6; i++) {
            String cId = String.valueOf(i);
            String pId = String.valueOf(i + 10);
            String lId = String.valueOf(i + 20);

            cycle1[i] = g1.addNode(cId);
            cycle1[i].setAttribute("ui.label", cId);
            Node p1 = g1.addNode(pId);
            Node l1 = g1.addNode(lId);
            p1.setAttribute("ui.label", pId);
            l1.setAttribute("ui.label", lId);

            g1.addEdge("g1_c" + i + "_p" + i, cycle1[i], p1);
            g1.addEdge("g1_p" + i + "_l" + i, p1, l1);
        }
        for (int i = 0; i < 6; i++) {
            g1.addEdge("g1_cycle_" + i, cycle1[i], cycle1[(i + 1) % 6]);
        }

        g1.setAttribute("ui.stylesheet", styleSheet);
        for (Node node : g1) {
            node.setAttribute("ui.class", "unmarked");
        }
        return g1;
    }

    /**
     * Creates G2 for the CFI-inspired example:
     * Two disjoint C3 cycles, each with a P2 gadget attached to each cycle node.
     * Node IDs: CycleA 100-102, GadgetA Path 110-112, GadgetA Leaf 120-122
     * CycleB 200-202, GadgetB Path 210-212, GadgetB Leaf 220-222
     * @return Graph G2.
     */
    public static Graph createCFI_G2() {
        Graph g2 = new SingleGraph("G2_CFI");

        Node[] cycle2a = new Node[3];
        Node[] cycle2b = new Node[3];

        // First C3 + Gadgets
        for (int i = 0; i < 3; i++) {
            String cId = String.valueOf(i + 100);
            String pId = String.valueOf(i + 110);
            String lId = String.valueOf(i + 120);

            cycle2a[i] = g2.addNode(cId);
            cycle2a[i].setAttribute("ui.label", cId);
            Node p2a = g2.addNode(pId);
            Node l2a = g2.addNode(lId);
            p2a.setAttribute("ui.label", pId);
            l2a.setAttribute("ui.label", lId);

            g2.addEdge("g2a_c" + i + "_p" + i, cycle2a[i], p2a);
            g2.addEdge("g2a_p" + i + "_l" + i, p2a, l2a);
        }
        for (int i = 0; i < 3; i++) {
            g2.addEdge("g2a_cycle_" + i, cycle2a[i], cycle2a[(i + 1) % 3]);
        }

        // Second C3 + Gadgets
        for (int i = 0; i < 3; i++) {
            String cId = String.valueOf(i + 200);
            String pId = String.valueOf(i + 210);
            String lId = String.valueOf(i + 220);

            cycle2b[i] = g2.addNode(cId);
            cycle2b[i].setAttribute("ui.label", cId);
            Node p2b = g2.addNode(pId);
            Node l2b = g2.addNode(lId);
            p2b.setAttribute("ui.label", pId);
            l2b.setAttribute("ui.label", lId);

            g2.addEdge("g2b_c" + i + "_p" + i, cycle2b[i], p2b);
            g2.addEdge("g2b_p" + i + "_l" + i, p2b, l2b);
        }
        for (int i = 0; i < 3; i++) {
            g2.addEdge("g2b_cycle_" + i, cycle2b[i], cycle2b[(i + 1) % 3]);
        }

        g2.setAttribute("ui.stylesheet", styleSheet);
        for (Node node : g2) {
            node.setAttribute("ui.class", "unmarked");
        }
        return g2;
    }


    /**
     * Creates a simple cycle graph C4 (Square).
     * Node IDs: 0, 1, 2, 3.
     * @param graphId The ID for the Graph object.
     * @return A C4 graph.
     */
    public static Graph createCycleC4(String graphId) {
        Graph g = new SingleGraph(graphId);
        g.addNode("0").setAttribute("ui.label", "0");
        g.addNode("1").setAttribute("ui.label", "1");
        g.addNode("2").setAttribute("ui.label", "2");
        g.addNode("3").setAttribute("ui.label", "3");
        g.addEdge("e01", "0", "1");
        g.addEdge("e12", "1", "2");
        g.addEdge("e23", "2", "3");
        g.addEdge("e30", "3", "0");

        g.setAttribute("ui.stylesheet", styleSheet);
         for (Node node : g) {
             node.setAttribute("ui.class", "unmarked");
         }
        return g;
    }

     /**
     * Creates a simple path graph P4.
     * Node IDs: 10, 11, 12, 13.
     * @param graphId The ID for the Graph object.
     * @return A P4 graph.
     */
    public static Graph createPathP4(String graphId) {
        Graph g = new SingleGraph(graphId);
        g.addNode("10").setAttribute("ui.label", "10");
        g.addNode("11").setAttribute("ui.label", "11");
        g.addNode("12").setAttribute("ui.label", "12");
        g.addNode("13").setAttribute("ui.label", "13");
        g.addEdge("e1011", "10", "11");
        g.addEdge("e1112", "11", "12");
        g.addEdge("e1213", "12", "13");

        g.setAttribute("ui.stylesheet", styleSheet);
        for (Node node : g) {
             node.setAttribute("ui.class", "unmarked");
         }
        return g;
    }

    /**
     * Creates the Prism graph (C3 x K2), a 3-regular graph on 6 vertices.
     * Node IDs: 0, 1, 2 (first triangle), 3, 4, 5 (second triangle).
     * @param graphId The ID for the Graph object.
     * @return The Prism graph.
     */
    public static Graph createPrismGraph(String graphId) {
        Graph g = new SingleGraph(graphId);
        // Nodes
        for(int i=0; i<6; i++) {
            g.addNode(String.valueOf(i)).setAttribute("ui.label", String.valueOf(i));
        }
        // Triangle 1 edges
        g.addEdge("e01", "0", "1");
        g.addEdge("e12", "1", "2");
        g.addEdge("e20", "2", "0");
        // Triangle 2 edges
        g.addEdge("e34", "3", "4");
        g.addEdge("e45", "4", "5");
        g.addEdge("e53", "5", "3");
        // Connecting edges
        g.addEdge("e03", "0", "3");
        g.addEdge("e14", "1", "4");
        g.addEdge("e25", "2", "5");

        g.setAttribute("ui.stylesheet", styleSheet);
         for (Node node : g) {
             node.setAttribute("ui.class", "unmarked");
         }
        return g;
    }

    /**
     * Creates the K3,3 graph, a 3-regular bipartite graph on 6 vertices.
     * Node IDs: 10, 11, 12 (partition U), 13, 14, 15 (partition V).
     * @param graphId The ID for the Graph object.
     * @return The K3,3 graph.
     */
    public static Graph createK33Graph(String graphId) {
        Graph g = new SingleGraph(graphId);
        // Partition U
        Node u1 = g.addNode("10"); u1.setAttribute("ui.label", "10");
        Node u2 = g.addNode("11"); u2.setAttribute("ui.label", "11");
        Node u3 = g.addNode("12"); u3.setAttribute("ui.label", "12");
        // Partition V
        Node v1 = g.addNode("13"); v1.setAttribute("ui.label", "13");
        Node v2 = g.addNode("14"); v2.setAttribute("ui.label", "14");
        Node v3 = g.addNode("15"); v3.setAttribute("ui.label", "15");

        // Edges (all pairs between U and V)
        g.addEdge("e1013", u1, v1); g.addEdge("e1014", u1, v2); g.addEdge("e1015", u1, v3);
        g.addEdge("e1113", u2, v1); g.addEdge("e1114", u2, v2); g.addEdge("e1115", u2, v3);
        g.addEdge("e1213", u3, v1); g.addEdge("e1214", u3, v2); g.addEdge("e1215", u3, v3);

        g.setAttribute("ui.stylesheet", styleSheet);
         for (Node node : g) {
             node.setAttribute("ui.class", "unmarked");
         }
        return g;
    }

     /**
     * Creates a simple Star graph K1,3 (center connected to 3 leaves).
     * Node IDs: 20 (center), 21, 22, 23 (leaves).
     * @param graphId The ID for the Graph object.
     * @return A K1,3 graph.
     */
    public static Graph createStarK13(String graphId) {
        Graph g = new SingleGraph(graphId);
        Node center = g.addNode("20"); center.setAttribute("ui.label", "20");
        Node l1 = g.addNode("21"); l1.setAttribute("ui.label", "21");
        Node l2 = g.addNode("22"); l2.setAttribute("ui.label", "22");
        Node l3 = g.addNode("23"); l3.setAttribute("ui.label", "23");

        g.addEdge("e2021", center, l1);
        g.addEdge("e2022", center, l2);
        g.addEdge("e2023", center, l3);

        g.setAttribute("ui.stylesheet", styleSheet);
        for (Node node : g) {
             node.setAttribute("ui.class", "unmarked");
         }
        return g;
    }


     // Example of how to use these:
    //  public static void main(String[] args) {
    //      System.setProperty("org.graphstream.ui", "swing");

    //      Graph cfi1 = createCFI_G1();
    //      Graph cfi2 = createCFI_G2();
    //      System.out.println("Created CFI G1 (Nodes: " + cfi1.getNodeCount() + ", Edges: " + cfi1.getEdgeCount() + ")");
    //      System.out.println("Created CFI G2 (Nodes: " + cfi2.getNodeCount() + ", Edges: " + cfi2.getEdgeCount() + ")");
    //      // cfi1.display();
    //      // cfi2.display();


    //      Graph c4_1 = createCycleC4("IsoC4_1");
    //      Graph c4_2 = createCycleC4("IsoC4_2"); // Isomorphic pair
    //      System.out.println("Created two C4 graphs.");
    //      // c4_1.display();
    //      // c4_2.display();


    //      Graph c4 = createCycleC4("NonIso_C4");
    //      Graph p4 = createPathP4("NonIso_P4"); // Non-isomorphic pair
    //      System.out.println("Created C4 and P4 graphs.");
    //      // c4.display();
    //      // p4.display();


    //      Graph prism = createPrismGraph("RegNonIso_Prism");
    //      Graph k33 = createK33Graph("RegNonIso_K33"); // Regular non-isomorphic pair
    //      System.out.println("Created Prism and K3,3 graphs.");
    //      prism.display(false); // Keep window open
    //      k33.display();

    //      Graph tree1 = createPathP4("Tree_P4");
    //      Graph tree2 = createStarK13("Tree_K13"); // Non-isomorphic trees (4 nodes each)
    //      System.out.println("Created P4 and K1,3 tree graphs.");
    //      // tree1.display();
    //      // tree2.display();
    //  }

}