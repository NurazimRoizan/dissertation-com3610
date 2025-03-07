import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
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
    protected JLabel currentLabel, graphLabel1, graphLabel2;

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
                colourMode = "spoiler";
            }
        });
        JButton duplicatorMark = new JButton("Duplicator Move");
        duplicatorMark.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Duplicator now can mark the node");
                colourMode = "duplicator";
            }
        });

        JButton myButton3 = new JButton("Explore");
        myButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Exploring . . .");
                cRefineGraph = new ColourRefinementAlgorithm(currentLabel);
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
                currentLabel = graphLabel1;
            }

            // ... other MouseListener methods ...
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                currentGraph = graph;
                currentLabel = graphLabel1;
            }
        });

        view2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                System.out.println("Clicked on graph2");
                currentGraph = graph2;
                currentLabel = graphLabel2;
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                currentGraph = graph2;
                currentLabel = graphLabel2;
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
                    cRefineGraph.cRefinement(currentGraph);
                }
            }
        }

        
    }

    public void viewClosed(String id) {
		loop = false;
	}

    float colourTemp =  -1;
    Object classTemp;
	public void buttonPushed(String id) {
		System.out.println("Button pushed on node "+id);
        Node clickedNode = currentGraph.getNode(id);
        String currentClass = String.valueOf(clickedNode.getAttribute("ui.class"));

        if (!"marked".equals(colourMode)) { 
            if (currentClass.equals("colour") && !currentClass.equals(colourMode)) {
                clickedNode.setAttribute("ui.class", colourMode, "colour");
            } else if (currentClass.equals("unmarked")) {
                clickedNode.setAttribute("ui.class", colourMode);
            } else if (currentClass.equals(colourMode)) {
                clickedNode.setAttribute("ui.class", "unmarked");
            } else {
                clickedNode.setAttribute("ui.class", clickedNode.hasAttribute("ui.color") ? "colour" : "unmarked");
            }
        }else {
            if (clickedNode.hasAttribute("ui.color")) {
                getNodeInformation(clickedNode); // Only called when ui.color exists
                clickedNode.setAttribute("ui.class", currentClass.equals("colour") ? "unmarked" : "colour");
            } else {
                clickedNode.setAttribute("ui.class", currentClass.equals("marked") ? "unmarked" : "marked");
            }
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

    public void getNodeInformation(Node source){
        System.out.println(source);
        String currentRound = currentLabel.getText().substring(currentLabel.getText().length()-1);
        System.out.println(currentRound);
        String currentSignature = String.valueOf(source.getAttribute("signature"+currentRound));
        float currentColor = (float)source.getAttribute("ui.color");
        System.out.println("Current node has "+ String.valueOf(source.getDegree()) +"neighbours");
        System.out.println(source + " current color is "+ currentColor);
        System.out.println(source + " current signature is "+ currentSignature);
    }

    protected void sleep() {
        try { Thread.sleep(100); } catch (Exception e) {}
    }
}