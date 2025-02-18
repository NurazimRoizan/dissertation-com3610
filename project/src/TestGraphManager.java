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
                "node.colour0 {" +
                "   fill-color: gray;" +
                "}" +
                "node.colour1 {" +
                "   fill-color: yellow;" +
                "}" +
                "node.colour2 {" +
                "   fill-color: green;" +
                "}" +
                "node.colour3 {" +
                "   fill-color: purple;" +
                "}" +
                "node.colour4 {" +
                "   fill-color: pink;" +
                "}" +
                "node.colour5 {" +
                "   fill-color: cyan;" +
                "}" +
                "node.colour6 {" +
                "   fill-color: orange;" +
                "}" +
                "node.colour7 {" +
                "   fill-color: blue;" +
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