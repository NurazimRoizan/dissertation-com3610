import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.util.Iterator;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class PebbleGameSet implements ViewerListener {
    protected boolean loop = true;
    protected Graph graph, graph2, currentGraph;
    protected String colourMode = "marked";
    protected boolean exploreGraph = false;

    public static void main(String args[]) {
        System.setProperty("org.graphstream.ui", "swing");
        new PebbleGameSet();
    }

    public PebbleGameSet() {
        graph = GraphManager.createGraph("Graph A");
        graph2 = GraphManager.createGraph("Graph B");
        currentGraph = graph;

        graph.setAutoCreate(true);
        graph.setStrict(false);

        SwingViewer viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        ViewPanel view = (ViewPanel) viewer.addDefaultView(false);
        SwingViewer viewer2 = new SwingViewer(graph2, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer2.enableAutoLayout();
        ViewPanel viewPanel2 = (ViewPanel) viewer2.addDefaultView(false);

        JFrame frame = new JFrame("Pebble Game Set");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.add((Component) view);
        centerPanel.add((Component) viewPanel2);

        JButton myButton = new JButton("Spoiler");
        myButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Spoiler now can mark the node");
                colourMode = "marked3";
            }
        });
        JButton myButton2 = new JButton("Duplicator");
        myButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Duplicator now can mark the node");
                colourMode = "marked2";
            }
        });
        JButton myButton3 = new JButton("Explore");
        myButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Exploring . . .");
                exploreGraph = true;
            }
        });

        buttonPanel.add(myButton);
        buttonPanel.add(myButton2);
        buttonPanel.add(myButton3);

        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setPreferredSize(new Dimension(800, 600));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                System.out.println("Clicked on graph1");
                currentGraph = graph;
            }

            // ... other MouseListener methods ...
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                currentGraph = graph;
            }
        });

        viewPanel2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                System.out.println("Clicked on graph2");
                currentGraph = graph2;
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                currentGraph = graph2;
            }
        });

        ViewerPipe fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer.addSink(graph);
        ViewerPipe fromViewer2 = viewer2.newViewerPipe();
        fromViewer2.addViewerListener(this);
        fromViewer2.addSink(graph2);

        while (loop) {
            fromViewer.pump();
            fromViewer2.pump();
            if (exploreGraph) {
                explore(currentGraph.getNode("1"));
            };
        }

        
    }

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

    public void explore(Node source) {
        Iterator<? extends Node> k = source.getBreadthFirstIterator();
    
        while (k.hasNext()) {
            Node next = k.next();
            next.setAttribute("ui.class", "marked");
            sleep();
        }
    }

    protected void sleep() {
        try { Thread.sleep(100); } catch (Exception e) {}
    }
}