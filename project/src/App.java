import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Iterator;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class App implements ViewerListener {
    protected boolean loop = true;
    protected Graph graph, graph2, currentGraph;
    protected String colourMode = "marked";
    protected boolean exploreGraph = false; //old
    protected ColourRefinementAlgorithm cRefineGraph;
    protected boolean cRefinementGoing = false;

    public static void main(String args[]) {
        System.setProperty("org.graphstream.ui", "swing");
        new App();
    }

    public App() {
        graph = TestGraphManager.createGraph("Graph A");
        graph2 = TestGraphManager.createGraph("Graph B");
        currentGraph = graph;

        graph.setAutoCreate(true);
        graph.setStrict(false);

        SwingViewer viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        SwingViewer viewer2 = new SwingViewer(graph2, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        viewer2.enableAutoLayout();
        ViewPanel view = (ViewPanel) viewer.addDefaultView(false);
        ViewPanel view2 = (ViewPanel) viewer2.addDefaultView(false);

        JFrame frame = new JFrame("Main Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel headerPanel = new JPanel(new GridLayout(1, 2));
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        JLabel graphLabel1 = new JLabel("Graph A", SwingConstants.CENTER);
        JLabel graphLabel2 = new JLabel("Graph B", SwingConstants.CENTER);

        headerPanel.setBackground(Color.GRAY);
        headerPanel.add(graphLabel1);
        headerPanel.add(graphLabel2);
        centerPanel.add((Component) view);
        centerPanel.add((Component) view2);

        JButton spoilerMark = new JButton("Spoiler Move");
        spoilerMark.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Spoiler now can mark the node");
                colourMode = "marked3";
            }
        });
        JButton duplicatorMark = new JButton("Duplicator Move");
        duplicatorMark.addActionListener(new ActionListener() {
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
                cRefineGraph = new ColourRefinementAlgorithm(graphLabel1, currentGraph);
                cRefineGraph.setCRefinementGoing(true);
            }
        });

        buttonPanel.add(spoilerMark);
        buttonPanel.add(duplicatorMark);
        buttonPanel.add(myButton3);

        frame.add(headerPanel, BorderLayout.NORTH);
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

        view2.addMouseListener(new MouseAdapter() {
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
        ViewerPipe fromViewer2 = viewer2.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer2.addViewerListener(this);
        fromViewer.addSink(graph);
        fromViewer2.addSink(graph2);

        while (loop) {
            fromViewer.pump();
            fromViewer2.pump();
            if (cRefineGraph != null){
                if (cRefineGraph.getCRefinementGoing()){
                    System.out.println("you should run crefinement now");
                    cRefineGraph.cRefinement(currentGraph);
                }
            }
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