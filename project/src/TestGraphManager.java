import org.graphstream.algorithm.generator.*;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class TestGraphManager{
    protected static String styleSheet =
        "node {" +
                // "     shape: box;" +
                "   stroke-mode: plain;" +
                "   size: 10px, 15px;" +
                "   stroke-color: yellow;" +
                "   size: 20px; fill-color: rgb(100,255,100), rgba(50,50,50,0); fill-mode: gradient-radial;" +
                // "   shadow-mode: gradient-radial; shadow-width: 5px; shadow-color: #EEF, #000; shadow-offset: 2px;" +
                "}" +
                "node.marked {" +
                "   fill-color: red;" +
                "}" +
                "node.colour {" +
                "   fill-mode: dyn-plain;" +
                // "   fill-color: red, darkgreen, white, blue, magenta, #444;" +
                "   fill-color: red, rgb(255, 200, 100), yellow, rgb(50, 200, 0), green, blue, darkblue, rgb(200, 100, 200), rgb(180, 0, 180), rgb(100, 200, 200);" +
                "}" +
                "node.colour0 {" +
                "   fill-color: gray;" +
                "}" +
                "node.interpo {" +
                "   fill-mode: dyn-plain;" +
                "   fill-color: blue, red, green;" +
                "}" +
                "edge {" +
                "   fill-color: brown;" +
                // "shape: cubic-curve;" +
                "}" +
                "graph {" +
                "   fill-color: #001329, #1C3353, red;" +
                "   fill-mode: gradient-vertical;" +
                "}";
    
    public static Graph createGraph(String name) {
        //DorogovtsevMendesGenerator gen = new DorogovtsevMendesGenerator();
        //Generator gen = new BananaTreeGenerator();
        Generator gen = new BarabasiAlbertGenerator(1);
        Graph graph = new SingleGraph(name);
        gen.addSink(graph);
        gen.begin();
        for (int i = 0; i < 20; i++) {
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

}