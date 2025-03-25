import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.algorithm.generator.*;

public class App implements ViewerListener {
    protected boolean loop = true;
    protected Graph graph, graph2, currentGraph;
    protected String colourMode = "marked";
    protected boolean exploreGraph = false; //old
    protected ColourRefinementAlgorithm cRefineGraph;
    protected boolean cRefinementGoing = false;
    protected JLabel currentLabel, graphLabel1, graphLabel2, nodeInfoLabel;
    protected int sleepTime = 0;
    //protected Generator gen = new BarabasiAlbertGenerator(1);
    protected ViewerPipe fromViewer; 


    public static void main(String args[]) {
        System.setProperty("org.graphstream.ui", "swing");
        new App();
    }

    public App() {
        Generator gen = new BarabasiAlbertGenerator(1);
        Generator gen2 = new BarabasiAlbertGenerator(1);
        //Generator gen2 = new DorogovtsevMendesGenerator();
        graph = TestGraphManager.createGraph("Graph A", gen);
        graph2 = TestGraphManager.createGraph("Graph B", gen2);
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
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        JLabel graphLabel1 = new JLabel("Graph A", SwingConstants.CENTER);
        JLabel graphLabel2 = new JLabel("Graph B", SwingConstants.CENTER);
        nodeInfoLabel = new JLabel("Click a node to get detailed attributes . . .", SwingConstants.CENTER);

        javax.swing.border.Border border = BorderFactory.createLineBorder(Color.ORANGE, 3); 


        headerPanel.setBackground(Color.LIGHT_GRAY);
        bottomPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.add(graphLabel1);
        headerPanel.add(graphLabel2);
        centerPanel.add((Component) view);
        centerPanel.add((Component) view2);

        JToggleButton spoilerMark = new JToggleButton("Spoiler Move");
        JToggleButton duplicatorMark = new JToggleButton("Duplicator Move");
        
        Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        spoilerMark.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (spoilerMark.isSelected()){
                    duplicatorMark.setSelected(false);
                    System.out.println("Spoiler now can mark the node");
                    nodeInfoLabel.setText("Spoiler Turn");

                    colourMode = "spoiler";
                }else{
                    colourMode = "marked";
                }
            }
        });
    
        duplicatorMark.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (duplicatorMark.isSelected()){
                    spoilerMark.setSelected(false);
                    System.out.println("Duplicator now can mark the node");
                    nodeInfoLabel.setText("Duplicator Turn");
                    colourMode = "duplicator";
                }else{
                    colourMode = "marked";
                }
                
            }
        });

        JButton myButton3 = new JButton("Colour Refinement");
        myButton3.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.YELLOW, Color.ORANGE, Color.RED, Color.BLUE), emptyBorder));

        myButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Starting Algorithm . . .");
                cRefineGraph = new ColourRefinementAlgorithm(currentLabel, sleepTime);
                cRefineGraph.setCRefinementGoing(true);
            }
        });
        JButton myButton5 = new JButton("Back Iteration");
        //myButton5.setVisible(false);
        myButton5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Moving back in time . . .");
                int previousRound = Integer.parseInt(currentLabel.getText().substring(currentLabel.getText().length()-1))-1;
                if (previousRound > 0){
                    cRefineGraph.setIteration(currentGraph, previousRound);
                    System.out.println("======================== ");
                    currentLabel.setText("Round " + previousRound);
                }
            }
        });

        JButton myButton4 = new JButton("Next Iteration");
        //myButton4.setVisible(false);
        myButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Going forward in time . . .");
                int nextRound = Integer.parseInt(currentLabel.getText().substring(currentLabel.getText().length()-1))+1;
                if (cRefineGraph.getStableRound()>nextRound){
                    cRefineGraph.setIteration(currentGraph, nextRound);
                    System.out.println("======================== ");
                    currentLabel.setText("Round " + nextRound);
                }
            }
        });
        JButton resetOption = new JButton("Reset graph");
        resetOption.setVisible(true);
        resetOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Popping a window");

                Object[] options = {"Simple", "Barabasi", "Dorogovt", "Random"};
                int choice = JOptionPane.showOptionDialog(null, "Choose graph type", "Generators", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                newGraphGenerator(choice);
            }
        });

        JButton resetButton = new JButton("Reset Graph");
        resetButton.setVisible(true);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Getting new graph");
                currentGraph.clear();
                //Graph newGraph = TestGraphManager.createGraph("Graph A", new DorogovtsevMendesGenerator());
                Graph newGraph = TestGraphManager.createGraph("Graph A", new BarabasiAlbertGenerator(1));

                newGraph.attributeKeys().forEach((key) -> {
                    currentGraph.setAttribute(key, new Object[]{newGraph.getAttribute(key)});
                 });

                Stream<Node> nodeStream = StreamSupport.stream(newGraph.nodes().spliterator(), false);
                nodeStream.forEach(node -> {
                    currentGraph.addNode(node.getId());
                    node.attributeKeys().forEach(key -> currentGraph.getNode(node.getId()).setAttribute(key, node.getAttribute(key)));
                });

                // Convert Iterable to Stream for edges 
                Stream<Edge> edgeStream = StreamSupport.stream(newGraph.edges().spliterator(), false);
                edgeStream.forEach(edge -> {
                    currentGraph.addEdge(edge.getId(), edge.getSourceNode().getId(), edge.getTargetNode().getId(), edge.isDirected());
                    edge.attributeKeys().forEach(key -> currentGraph.getEdge(edge.getId()).setAttribute(key, edge.getAttribute(key)));
                });
            }
        });

        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 100, 10);
        JSpinner speedSpinner = new JSpinner(model);

        speedSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int speed = (int) speedSpinner.getValue();
                System.out.println("Speed: " + speed);
                sleepTime = speed;

            }
        });

        buttonPanel.add(spoilerMark);
        buttonPanel.add(duplicatorMark);
        buttonPanel.add(myButton3);
        buttonPanel.add(myButton5);
        buttonPanel.add(myButton4);
        buttonPanel.add(resetOption);
        JLabel speedLabel = new JLabel("Animation Delay: "); // Create the label
        //buttonPanel.add(speedLabel);
        //buttonPanel.add(speedSpinner);
        buttonPanel.add(resetButton);

        bottomPanel.add(nodeInfoLabel);
        bottomPanel.add(buttonPanel);


        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
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
                view2.setBorder(null);
                view.setBorder(border);
            }

            // ... other MouseListener methods ...
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                currentGraph = graph;
                currentLabel = graphLabel1;
                view2.setBorder(null);
                view.setBorder(border);
            }
        });

        view2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                System.out.println("Clicked on graph2");
                currentGraph = graph2;
                currentLabel = graphLabel2;
                view.setBorder(null);
                view2.setBorder(border);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                currentGraph = graph2;
                currentLabel = graphLabel2;
                view.setBorder(null);
                view2.setBorder(border);
            }
        });

        fromViewer = viewer.newViewerPipe();
        //ViewerPipe fromViewer = viewer.newViewerPipe();
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
                clickedNode.setAttribute("mark", colourMode);
            } else if (currentClass.equals("unmarked")) {
                clickedNode.setAttribute("ui.class", colourMode);
                clickedNode.setAttribute("mark", colourMode);
            } else if (currentClass.equals(colourMode)) {
                clickedNode.setAttribute("ui.class", "unmarked");
                clickedNode.removeAttribute("mark");
            } else if (currentClass.equals(colourMode)) {
                clickedNode.setAttribute("ui.class", "unmarked");
                clickedNode.removeAttribute("mark");
            } else {
                clickedNode.setAttribute("ui.class", clickedNode.hasAttribute("ui.color") ? "colour" : "unmarked");
                clickedNode.removeAttribute("mark");
            }
        }else {
            if (clickedNode.hasAttribute("ui.color")) {
                System.out.println(currentClass);
                clickedNode.setAttribute("ui.class", currentClass.equals("colour") ? "unmarked" : "colour");
            } else {
                clickedNode.setAttribute("ui.class", currentClass.equals("marked") ? "unmarked" : "marked");
            }
        }
        nodeInfoLabel.setText(getNodeInformation(clickedNode));
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

    public String getNodeInformation(Node source) {
        StringBuilder result = new StringBuilder();
        result.append("Clicked node '").append(source.toString());
        result.append("' has ").append(String.valueOf(source.getDegree())).append(" neighbours. ");
        if (source.hasAttribute("ui.color")){
            String currentRound = currentLabel.getText().substring(currentLabel.getText().length() - 1);
            String currentSignature = String.valueOf(source.getAttribute("signature" + currentRound));
            float currentColor = (float) source.getAttribute("ui.color");
            currentSignature = currentSignature.length() > 10 ? currentSignature.substring(0, 10) : currentSignature;
            result.append(" Color code = ").append(currentColor).append(". ");
            result.append(" Current signature = ").append(currentSignature).append(". ");
        }
        result.append("Current class = ").append(String.valueOf(colourMode));
        return result.toString();
    }

    protected void sleep() {
        try { Thread.sleep(100); } catch (Exception e) {}
    }
    public void newGraphGenerator(int choosenType){
        Graph newGraph;
        switch (choosenType) {
            case 1:
                newGraph = TestGraphManager.createGraph("Graph", new BarabasiAlbertGenerator(1));
                break;
            case 2:
                newGraph = TestGraphManager.createGraph("Graph", new DorogovtsevMendesGenerator());
                break;
            case 0: //Simple
                newGraph = TestGraphManager.createGraph("Graph",new WattsStrogatzGenerator(10,2,0.5));
                break;
            case 3:
                newGraph = TestGraphManager.createGraph("Graph", new WattsStrogatzGenerator(10,2,0.5));
                break;
            default:
                newGraph = TestGraphManager.createGraph("Graph", new WattsStrogatzGenerator(10,2,0.5));
        }
        System.out.println("Getting new graph");
        currentGraph.clear();
        //Graph newGraph = TestGraphManager.createGraph("Graph A", new DorogovtsevMendesGenerator());

        newGraph.attributeKeys().forEach((key) -> {
            currentGraph.setAttribute(key, new Object[]{newGraph.getAttribute(key)});
            });

        Stream<Node> nodeStream = StreamSupport.stream(newGraph.nodes().spliterator(), false);
        nodeStream.forEach(node -> {
            currentGraph.addNode(node.getId());
            node.attributeKeys().forEach(key -> currentGraph.getNode(node.getId()).setAttribute(key, node.getAttribute(key)));
        });

        // Convert Iterable to Stream for edges 
        Stream<Edge> edgeStream = StreamSupport.stream(newGraph.edges().spliterator(), false);
        edgeStream.forEach(edge -> {
            currentGraph.addEdge(edge.getId(), edge.getSourceNode().getId(), edge.getTargetNode().getId(), edge.isDirected());
            edge.attributeKeys().forEach(key -> currentGraph.getEdge(edge.getId()).setAttribute(key, edge.getAttribute(key)));
        });
    }
}