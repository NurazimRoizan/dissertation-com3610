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
            "   size: 35px; fill-color: rgb(100,255,100), rgba(255, 255, 255, 0); fill-mode: gradient-radial;" +
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
            "size: 2px;" +
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
     * Creates Graph G1: A 2x2 grid base {0,1,2,3} with a "handle" {4,5}
     * attached to nodes 0 and 1. Node 5 is a leaf.
     * Contains C4 subgraph {0,1,3,2}.
     * @return G1 graph.
     */
    public static Graph createPartialIso_G1_GridHandle() {
        Graph g = new SingleGraph("G1_GridHandle");
        // Grid nodes (H1)
        g.addNode("0").setAttribute("ui.label", "0");
        g.addNode("1").setAttribute("ui.label", "1");
        g.addNode("2").setAttribute("ui.label", "2");
        g.addNode("3").setAttribute("ui.label", "3");
        // Handle nodes
        g.addNode("4").setAttribute("ui.label", "4");
        g.addNode("5").setAttribute("ui.label", "5");

        // Grid edges (Cycle C4)
        g.addEdge("e01", "0", "1");
        g.addEdge("e13", "1", "3");
        g.addEdge("e32", "3", "2");
        g.addEdge("e20", "2", "0");

        // Handle edges
        g.addEdge("e04", "0", "4");
        g.addEdge("e14", "1", "4");
        g.addEdge("e45", "4", "5"); // Node 5 is degree 1

        g.setAttribute("ui.stylesheet", styleSheet);
        for (Node node : g) { node.setAttribute("ui.class", "unmarked"); }
        return g;
    }

    /**
     * Creates Graph G2: A 2x2 grid base {0,1,2,3} with two "bridges"
     * using nodes {4,5} connecting opposite pairs of grid nodes.
     * Contains C4 subgraph {0,1,3,2}. Non-isomorphic to G1.
     * @return G2 graph.
     */
    public static Graph createPartialIso_G2_GridBridges() {
        Graph g = new SingleGraph("G2_GridBridges");
         // Grid nodes (H2)
        g.addNode("0").setAttribute("ui.label", "0");
        g.addNode("1").setAttribute("ui.label", "1");
        g.addNode("2").setAttribute("ui.label", "2");
        g.addNode("3").setAttribute("ui.label", "3");
        // Bridge nodes
        g.addNode("4").setAttribute("ui.label", "4");
        g.addNode("5").setAttribute("ui.label", "5");

        // Grid edges (Cycle C4)
        g.addEdge("e01", "0", "1");
        g.addEdge("e13", "1", "3");
        g.addEdge("e32", "3", "2");
        g.addEdge("e20", "2", "0");

        // Bridge edges
        g.addEdge("e04", "0", "4"); // Bridge 1 attaches to 0, 2
        g.addEdge("e24", "2", "4");
        g.addEdge("e15", "1", "5"); // Bridge 2 attaches to 1, 3
        g.addEdge("e35", "3", "5");

        g.setAttribute("ui.stylesheet", styleSheet);
        for (Node node : g) { node.setAttribute("ui.class", "unmarked"); }
        return g;
    }

    // --- Pair 5: Structures attached to a K4 Clique ---
    // H1/H2: Subgraph induced by nodes {0, 1, 2, 3} forming a K4 clique.

    /**
     * Creates Graph G1: A K4 clique {0,1,2,3} with a P2 path {4,5}
     * where node 4 connects to both nodes 0 and 1 of the K4.
     * Contains K4 subgraph {0,1,2,3}.
     * @return G1 graph.
     */
    public static Graph createPartialIso_G1_K4Handle() {
        Graph g = new SingleGraph("G1_K4Handle");
        Node[] k4nodes = new Node[4];
        for (int i = 0; i < 4; i++) {
            k4nodes[i] = g.addNode(String.valueOf(i));
            k4nodes[i].setAttribute("ui.label", String.valueOf(i));
        }
        // K4 edges (H1)
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                g.addEdge("k4_" + i + "_" + j, k4nodes[i], k4nodes[j]);
            }
        }
        // Handle nodes
        Node n4 = g.addNode("4"); n4.setAttribute("ui.label", "4");
        Node n5 = g.addNode("5"); n5.setAttribute("ui.label", "5");
        // Handle edges
        g.addEdge("e04", k4nodes[0], n4); // Node 4 connects to K4 nodes 0 and 1
        g.addEdge("e14", k4nodes[1], n4);
        g.addEdge("e45", n4, n5); // Path P2 attached via node 4

        g.setAttribute("ui.stylesheet", styleSheet);
        for (Node node : g) { node.setAttribute("ui.class", "unmarked"); }
        return g;
    }

    /**
     * Creates Graph G2: A K4 clique {0,1,2,3} with two separate P1 paths ("stubs")
     * attached to nodes 0 and 1 respectively.
     * Contains K4 subgraph {0,1,2,3}. Non-isomorphic to G1.
     * @return G2 graph.
     */
    public static Graph createPartialIso_G2_K4Stubs() {
        Graph g = new SingleGraph("G2_K4Stubs");
        Node[] k4nodes = new Node[4];
        for (int i = 0; i < 4; i++) {
            k4nodes[i] = g.addNode(String.valueOf(i));
            k4nodes[i].setAttribute("ui.label", String.valueOf(i));
        }
        // K4 edges (H2)
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                g.addEdge("k4_" + i + "_" + j, k4nodes[i], k4nodes[j]);
            }
        }
        // Stub nodes
        Node n4 = g.addNode("4"); n4.setAttribute("ui.label", "4");
        Node n5 = g.addNode("5"); n5.setAttribute("ui.label", "5");
        // Stub edges
        g.addEdge("e04", k4nodes[0], n4); // Stub 1 attached to K4 node 0
        g.addEdge("e15", k4nodes[1], n5); // Stub 2 attached to K4 node 1

        g.setAttribute("ui.stylesheet", styleSheet);
        for (Node node : g) { node.setAttribute("ui.class", "unmarked"); }
        return g;
    }
    public static Graph createPartialIso_G2_K4Stubs2() {
        Graph g = new SingleGraph("G2_K4Stubsaa");
        Node[] k4nodes = new Node[4];
        for (int i = 0; i < 4; i++) {
            k4nodes[i] = g.addNode(String.valueOf(i));
            k4nodes[i].setAttribute("ui.label", String.valueOf(i)+10);
        }
        // K4 edges (H2)
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                g.addEdge("k4_" + i + "_" + j, k4nodes[i], k4nodes[j]);
            }
        }
        // Stub nodes
        Node n4 = g.addNode("4"); n4.setAttribute("ui.label", "40");
        Node n5 = g.addNode("5"); n5.setAttribute("ui.label", "50");
        // Stub edges
        g.addEdge("e04", k4nodes[0], n4); // Stub 1 attached to K4 node 0
        g.addEdge("e15", k4nodes[1], n5); // Stub 2 attached to K4 node 1

        g.setAttribute("ui.stylesheet", styleSheet);
        for (Node node : g) { node.setAttribute("ui.class", "unmarked"); }
        return g;
    }

    // --- Pair 6: Bridged Disjoint Triangles ---
    // H1/H2: Two disjoint C3 triangles {0,1,2} and {3,4,5}.

    /**
     * Creates Graph G1: Two disjoint C3 triangles bridged by edges (0,3) and (1,4).
     * Contains two C3s {0,1,2} and {3,4,5} as subgraphs.
     * @return G1 graph.
     */
    public static Graph createPartialIso_G1_BridgeC3_V1() {
        Graph g = new SingleGraph("G1_BridgeC3_V1");
        // Triangle 1 nodes
        g.addNode("0").setAttribute("ui.label", "0");
        g.addNode("1").setAttribute("ui.label", "1");
        g.addNode("2").setAttribute("ui.label", "2");
        // Triangle 2 nodes
        g.addNode("3").setAttribute("ui.label", "3");
        g.addNode("4").setAttribute("ui.label", "4");
        g.addNode("5").setAttribute("ui.label", "5");

        // Triangle 1 edges
        g.addEdge("e01", "0", "1"); g.addEdge("e12", "1", "2"); g.addEdge("e20", "2", "0");
        // Triangle 2 edges
        g.addEdge("e34", "3", "4"); g.addEdge("e45", "4", "5"); g.addEdge("e53", "5", "3");

        // Bridge edges V1
        g.addEdge("b03", "0", "3");
        g.addEdge("b14", "1", "4");

        g.setAttribute("ui.stylesheet", styleSheet);
        for (Node node : g) { node.setAttribute("ui.class", "unmarked"); }
        return g;
    }

    /**
     * Creates Graph G2: Two disjoint C3 triangles bridged differently by edges (0,3) and (0,4).
     * Contains two C3s {0,1,2} and {3,4,5} as subgraphs. Non-isomorphic to G1.
     * @return G2 graph.
     */
    public static Graph createPartialIso_G2_BridgeC3_V2() {
        Graph g = new SingleGraph("G2_BridgeC3_V2");
        // Triangle 1 nodes
        g.addNode("0").setAttribute("ui.label", "0");
        g.addNode("1").setAttribute("ui.label", "1");
        g.addNode("2").setAttribute("ui.label", "2");
        // Triangle 2 nodes
        g.addNode("3").setAttribute("ui.label", "3");
        g.addNode("4").setAttribute("ui.label", "4");
        g.addNode("5").setAttribute("ui.label", "5");

        // Triangle 1 edges
        g.addEdge("e01", "0", "1"); g.addEdge("e12", "1", "2"); g.addEdge("e20", "2", "0");
        // Triangle 2 edges
        g.addEdge("e34", "3", "4"); g.addEdge("e45", "4", "5"); g.addEdge("e53", "5", "3");

        // Bridge edges V2 (Node 0 connects to two nodes in the other triangle)
        g.addEdge("b03", "0", "3");
        g.addEdge("b04", "0", "4"); // Different bridge

        g.setAttribute("ui.stylesheet", styleSheet);
        for (Node node : g) { node.setAttribute("ui.class", "unmarked"); }
        return g;
    }


}