import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.*;

public class Tutorial1 {
    public static void main(String args[]) {
		Graph graph = new SingleGraph("Tutorial 1");
        // graph.addNode("A" );
        // graph.addNode("B" );
        // graph.addNode("C" );
        // graph.addEdge("AB", "A", "B");
        // graph.addEdge("BC", "B", "C");
        // graph.addEdge("CA", "C", "A");

        // --- auto create fucntion
        graph.setStrict(false);
        graph.setAutoCreate( true );
        graph.addEdge( "AB", "A", "B" );
        graph.addEdge( "BC", "B", "C" );
        graph.addEdge( "CA", "C", "A" );
        // -- auto function ends

        System.setProperty("org.graphstream.ui", "swing"); 
        graph.display();

        //print all nodes
        for(Node n:graph) {
            System.out.println(n.getId());
        }
        //simirlarly
        for (int i = 0; i < graph.getNodeCount(); i++) {
            Node node = graph.getNode(i);
            System.out.println(node.getId());
        }
        //print all edges
        graph.edges().forEach(e -> {
            System.out.println(e.getId());
        });
        //a;ternative to print all nodes
        graph.nodes().forEach(n -> {
            System.out.println(n.getId());
        });

        // int n = graph.getNodeCount();
        // int adjacencyMatrix[][] = new int[n][n];
        // for (int i = 0; i < n; i++)
        //     for (int j = 0; j < n; j++)
        //         adjacencyMatrix[i][j] = graph.getNode(i).hasEdgeBetween(j) ? 1 : 0; 
        // System.out.println(adjacencyMatrix[1][2]);

        
	}
}
