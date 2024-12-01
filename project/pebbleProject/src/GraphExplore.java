import java.util.Iterator;

import org.graphstream.ui.spriteManager.*;

import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomEuclideanGenerator;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class GraphExplore {
public static void main(String args[]) {
    new GraphExplore();
}

public GraphExplore() {
    Generator gen = new RandomEuclideanGenerator();
    Graph graph = new SingleGraph("Explore test");
    SpriteManager sman = new SpriteManager(graph);
    Sprite s = sman.addSprite("S1");
    gen.addSink(graph);
    gen.begin();
    for(int i = 0; i < 50; i++)
        gen.nextEvents();
    gen.end();

    graph.setAttribute("ui.stylesheet", styleSheet);
    
    graph.setAutoCreate(true);
    graph.setStrict(false);
    System.setProperty("org.graphstream.ui", "swing"); 
    graph.display();

    //show label on the nodes
    for (Node node : graph) {
        node.setAttribute("ui.label", node.getId());
    }

    explore(graph.getNode("1"));
    // graph.nodes().forEach(n -> {
    //     n.removeAttribute("ui.class");;
    // });
    // explore(graph.getNode("root"));
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
    // "	shape: box;" +
    "	stroke-mode: plain;" +
    "   size: 10px, 15px;" +
    "	stroke-color: yellow;" +
    "   size: 20px; fill-color: rgb(100,255,100), rgba(50,50,50,0); fill-mode: gradient-radial;" +
    // "   shadow-mode: gradient-radial; shadow-width: 5px; shadow-color: #EEF, #000; shadow-offset: 2px;" +
    "}" +
    "node.marked {" +
    "	fill-color: red;" +
    "}" +
    "node:clicked {" + 
        "fill-color: green;" + 
    "}" +
    "edge {" + 
        "fill-color: brown;" + 
        // "shape: cubic-curve;" +
    "}" +
    "graph {" + 
        "fill-color: #001329, #1C3353, red;" +
        "fill-mode: gradient-vertical;" +
    "}";
}