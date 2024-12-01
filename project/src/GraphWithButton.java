import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.graphstream.ui.graphicGraph.stylesheet.Color;
import org.graphstream.ui.spriteManager.*;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.w3c.dom.events.MouseEvent;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomEuclideanGenerator;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;

public class GraphWithButton implements ViewerListener {
    protected boolean loop = true;
    protected Graph graph, graph2, currentGraph;
    protected String colourMode = "marked";
public static void main(String args[]) {
    //System.setProperty("org.graphstream.ui", "swing"); 
    new GraphWithButton();
}

public GraphWithButton() {
    //Generator gen = new RandomEuclideanGenerator();
    Generator gen = new DorogovtsevMendesGenerator();
    graph = new SingleGraph("Explore test");
    gen.addSink(graph);
    gen.begin();
    for(int i = 0; i < 20; i++)
        gen.nextEvents();
    gen.end();

    Generator gen2 = new DorogovtsevMendesGenerator();
    graph2 = new SingleGraph("Explore test");
    gen2.addSink(graph2);
    gen2.begin();
    for(int i = 0; i < 20; i++)
        gen2.nextEvents();
    gen2.end();

    graph.setAttribute("ui.stylesheet", styleSheet);
    graph2.setAttribute("ui.stylesheet", styleSheet);
    currentGraph = graph;
    
    graph.setAutoCreate(true);
    graph.setStrict(false);
    System.setProperty("org.graphstream.ui", "swing"); 
    //graph.display();

    // ------------------------------------
    // Create the viewer and get the ViewPanel
        // Viewer viewer = graph.display();
        // View view = viewer.getDefaultView();
        SwingViewer viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        ViewPanel view = (ViewPanel) viewer.addDefaultView(false); 
        SwingViewer viewer2 = new SwingViewer(graph2, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer2.enableAutoLayout();
        ViewPanel viewPanel2 = (ViewPanel) viewer2.addDefaultView(false);
        //SwingViewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        //viewer.enableAutoLayout();
        //View view = viewer.getDefaultView();
        //View viewPanel= viewer.addDefaultView(false);
        //SwingViewer viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        //Viewer viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        //viewer.getDefaultView().enableMouseOptions(); // -------------------ENABLE THIS FOR HOVER OPTIONS ----!!!!!!
        //View viewPanel = viewer.addDefaultView(false);
        //viewPanel.getCamera().setViewPercent(1); // set 0.5 to zoom

        // Create a JFrame to hold the components
        JFrame frame = new JFrame("Pebble Game Set");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the button
        JPanel buttonPanel = new JPanel();
        JPanel centerPanel = new JPanel(new GridLayout(1, 2)); // 1 row, 2 columns
        centerPanel.add((Component) view);
        centerPanel.add((Component) viewPanel2);
        JButton myButton = new JButton("Spoiler");
        myButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Spoiler now can mark the node");
                colourMode = "marked3";
                //explore(graph.getNode("34"), "marked3");
            }
        });
        JButton myButton2 = new JButton("Duplicator");
        myButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Duplicator now can mark the node");
                colourMode = "marked2";
                //explore(graph.getNode("25"), "marked2");
            }
        });

        // Add the button to the panel
        buttonPanel.add(myButton);
        buttonPanel.add(myButton2);

        // Add the ViewPanel and button panel to the frame
        //frame.add((Component)viewPanel, BorderLayout.CENTER);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setPreferredSize(new Dimension(800, 600)); 
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    // ----------------------------------

    //show label on the nodes
    // for (Node node : graph) {
    //     node.setAttribute("ui.label", node.getId());
    // }

    for (Node node : graph) {
        node.setAttribute("ui.label", node.getId());
        node.setAttribute("ui.class", "unmarked"); // Add a default class
    }
    for (Node node : graph2) {
        node.setAttribute("ui.label", node.getId());
        node.setAttribute("ui.class", "unmarked"); // Add a default class
    }


    //explore(graph.getNode("1"), "marked");
    
view.addMouseListener(new MouseListener() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        System.out.println("Clicked on graph1");
        currentGraph = graph;
        // ... your logic for graph1 click ... 
    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub
        currentGraph = graph;
    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub
    }
});

// Add mouse listener to the second view
viewPanel2.addMouseListener(new MouseListener() {

    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub
        System.out.println("Clicked on graph2");
        currentGraph = graph2;
    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub
        currentGraph = graph2;
    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {
        // TODO Auto-generated method stub
    }
});
    ViewerPipe fromViewer = viewer.newViewerPipe();
	fromViewer.addViewerListener(this);
	fromViewer.addSink(graph);
    ViewerPipe fromViewer2 = viewer2.newViewerPipe();
	fromViewer2.addViewerListener(this);
	fromViewer2.addSink(graph2);
    while(loop) {
        fromViewer.pump(); // or fromViewer.blockingPump(); in the nightly builds
        fromViewer2.pump();
        // here your simulation code.
    }
}

public void explore(Node source, String mark) {
    Iterator<? extends Node> k = source.getBreadthFirstIterator();

    while (k.hasNext()) {
        Node next = k.next();
        next.setAttribute("ui.class", mark);
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
    "node.marked2 {" +
    "	fill-color: blue;" +
    "}" +
    "node.marked3 {" +
    "	fill-color: yellow;" +
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

    public void viewClosed(String id) {
		loop = false;
	}

	public void buttonPushed(String id) {
		System.out.println("Button pushed on node "+id);
        // Toggle the color between green and the original color
        Node clickedNode = currentGraph.getNode(id);
        // Check if the clicked node ID exists in graph1
        String currentClass = (String)clickedNode.getAttribute("ui.class");
        if (currentClass.equals(colourMode)) {
            clickedNode.setAttribute("ui.class", "unmarked");
        } else {
            clickedNode.setAttribute("ui.class", colourMode);
        }
	}

	public void buttonReleased(String id) {
		//System.out.println("Button released on node "+id);
	}

	public void mouseOver(String id) {
		System.out.println("Need the Mouse Options to be activated");
	}

	public void mouseLeft(String id) {
		System.out.println("Need the Mouse Options to be activated");
	}
}