import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.FlowLayout;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.graphstream.algorithm.APSP;
import org.graphstream.algorithm.Centroid;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class TestGraphColour implements ViewerListener {
    protected Graph graph, currentGraph;
    protected boolean loop = true;
    protected String colourMode = "marked";
    protected boolean exploreGraph = false;

    public static void main(String args[]){
        System.setProperty("org.graphstream.ui", "swing");
        new TestGraphColour();
    }

    public TestGraphColour() {
        graph = TestGraphManager.createGraph("Graph Test");
        currentGraph = graph;
        graph.setAutoCreate(true);
        graph.setStrict(false);

        SwingViewer viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        ViewPanel view = (ViewPanel) viewer.addDefaultView(false);

        JFrame frame = new JFrame("Test Ground");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.add((Component) view);

        JButton myButton3 = new JButton("Explore");
        myButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Exploring . . .");
                exploreGraph = true;
            }
        });

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


        ViewerPipe fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer.addSink(graph);

        computeCentroid(graph);

        while (loop) {
            fromViewer.pump();
            if (exploreGraph) {
                explore(currentGraph.getNode("1"));
            };
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

    public void viewClosed(String id) {
		loop = false;
	}
    public void buttonPushed(String id) {
		System.out.println("Button pushed on node "+id);
        // Toggle the color between green and the original color
        Node clickedNode = currentGraph.getNode(id);
        //check neighbour
        // Boolean hehe = clickedNode.hasEdgeToward(currentGraph.getNode("1"));
        int hehe = clickedNode.getDegree();
        System.out.println(hehe);
        // Check if the clicked node ID exists in graph1
        String currentClass = (String)clickedNode.getAttribute("ui.class");
        if (currentClass.equals(colourMode)) {
            clickedNode.setAttribute("ui.class", "unmarked");
        } else {
            clickedNode.setAttribute("ui.class", colourMode);
        }
	}

    public void computeCentroid(Graph graph){
        APSP apsp = new APSP();
 		apsp.init(graph);
 		apsp.compute();
 
 		Centroid centroid = new Centroid();
 		centroid.init(graph);
 		centroid.compute();
		graph.nodes().forEach( n -> {
 			Boolean in = (Boolean) n.getAttribute("centroid");
 
 			System.out.printf("%s is%s in the centroid.\n", n.getId(), in ? ""
 					: " not");
 		});
    }

    public void explore(Node source) {
        Iterator<? extends Node> k = source.getBreadthFirstIterator();
        int degree;
    
        while (k.hasNext()) {
            Node next = k.next();
            degree = next.getDegree();
            if (degree > 9){
                next.setAttribute("ui.class", "colour");
            }
            else if (degree== 1){
                next.setAttribute("ui.class", "interpo");
                next.setAttribute("ui.color", (float)(0.8));
            } else {next.setAttribute("ui.class", "colour"+degree); }
            System.out.println("colour"+degree);
            sleep();
        }
        exploreGraph = false;
        System.out.println("End of Exploration");
    }

    protected void sleep() {
        try { Thread.sleep(300); } catch (Exception e) {}
    }
}
