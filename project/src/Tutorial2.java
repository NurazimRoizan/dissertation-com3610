import java.util.Iterator;

import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.algorithm.DynamicAlgorithm;
import org.graphstream.algorithm.generator.BananaTreeGenerator;
import org.graphstream.algorithm.generator.Generator;

public class Tutorial2 {
    public static void main(String args[]) {
        Generator gen = new BananaTreeGenerator();
		Graph graph = new SingleGraph("Tutorial 2");
        gen.addSink(graph);
        gen.begin();
        for(int i = 0; i < 10; i++)
            gen.nextEvents();
        gen.end();

        // // --- auto create fucntion
        // graph.setStrict(false);
        // graph.setAutoCreate( true );
        // graph.addEdge( "AB", "A", "B" );
        // graph.addEdge( "BC", "B", "C" );
        // graph.addEdge( "CA", "C", "A" );
        // // -- auto function ends

        DegreesAlgorithm a = new DegreesAlgorithm() ; // My algorithm
        a.init(graph);
        a.compute();
        System.out.println(a.getAverageDegree());

        System.setProperty("org.graphstream.ui", "swing"); 
        graph.display(); 
    
	}


    public void explore(Node source) {
        Iterator<? extends Node> k = source.getBreadthFirstIterator();

        while (k.hasNext()) {
            Node next = k.next();
            next.setAttribute("ui.class", "marked");
            sleep();
        }
    }
    
    protected void sleep() {
        try { Thread.sleep(1000); } catch (Exception e) {}
    }

    protected String styleSheet =
    "node {" +
    "	fill-color: black;" +
    "}" +
    "node.marked {" +
    "	fill-color: red;" +
    "}";
}
