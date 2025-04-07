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

}