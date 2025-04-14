import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
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
    protected Graph graph, graph2, currentGraph, previousPickedGraph;
    protected String colourMode = "marked";
    protected boolean exploreGraph = false; //old
    protected ColourRefinementAlgorithm cRefineGraph;
    protected boolean cRefinementGoing = false;
    protected JLabel currentLabel, graphLabel1, graphLabel2, nodeInfoLabel, speedLabel, kPebbleLabel, nPebbleLabel;
    protected JFrame frame;
    protected int sleepTime = 0;
    protected int maxNode = 5;
    protected ViewerPipe fromViewer; 
    protected boolean pebbleStarted = false;
    protected Node firstPebble;
    protected JToggleButton spoilerMark = new JToggleButton("Spoiler Move");
    protected JToggleButton duplicatorMark = new JToggleButton("Duplicator Move");
    protected PebbleGameState pebbleGame;
    protected JButton startPebbleButton, crButton, backButton, nextButton, welcomeButton, pebbleRuleDialogButton;
    protected Map<String, Integer> finalColors;
    protected InitialDialog dialog;
    protected JSpinner speedSpinner, pebbleKSpinner, pebbleNSpinner;
    protected int pebbleK = 2; protected int pebbleN = 5;
    public static final String FINAL_COLOR_ATTR = "color_refinement.final_color";


    public static void main(String args[]) {
        System.setProperty("org.graphstream.ui", "swing");
        new App();
    }

    public App() {
        Generator gen = new BarabasiAlbertGenerator(1);
        Generator gen2 = new BarabasiAlbertGenerator(1);
        //Generator gen2 = new DorogovtsevMendesGenerator();
        //graph = TestGraphManager.createGraph("Graph A", gen);
        //graph2 = TestGraphManager.createGraph("Graph B", gen2);
        graph = TestGraphManager.createExampleGraph1();
        graph2 = TestGraphManager.createExampleGraph2();
        currentGraph = graph;

        // graph.setAutoCreate(true);
        // graph.setStrict(false);
        SwingViewer viewer = new SwingViewer(graph, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        SwingViewer viewer2 = new SwingViewer(graph2, SwingViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        viewer2.enableAutoLayout();
        ViewPanel view = (ViewPanel) viewer.addDefaultView(false);
        ViewPanel view2 = (ViewPanel) viewer2.addDefaultView(false);

        frame = new JFrame("Main Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dialog = new InitialDialog(frame); // Pass the frame as parent
        dialog.setVisible(true);

        JPanel headerPanel = new JPanel(new GridLayout(1, 2));
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        speedLabel = new JLabel("Animation Delay: "); // Create the label
        graphLabel1 = new JLabel("Graph A", SwingConstants.CENTER);
        graphLabel2 = new JLabel("Graph B", SwingConstants.CENTER);
        nodeInfoLabel = new JLabel("Click a node to get detailed attributes . . .", SwingConstants.CENTER);

        javax.swing.border.Border border = BorderFactory.createLineBorder(Color.ORANGE, 3); 


        headerPanel.setBackground(Color.LIGHT_GRAY);
        bottomPanel.setBackground(Color.LIGHT_GRAY);
        headerPanel.add(graphLabel1);
        headerPanel.add(graphLabel2);
        centerPanel.add((Component) view);
        centerPanel.add((Component) view2);

        spoilerMark.setVisible(false);
        spoilerMark.setSelected(true);
        duplicatorMark.setSelected(true);
        duplicatorMark.setVisible(false);
        
        Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        spoilerMark.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (spoilerMark.isSelected()){
                    spoilerMark.setText("Spoiler move");
                    System.out.println("Spoiler now can mark the node");

                    colourMode = "spoiler";
                }else{
                    spoilerMark.setText("Resume Pebble Game");
                    colourMode = "marked";
                }
            }
        });
    
        duplicatorMark.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (duplicatorMark.isSelected()){
                    duplicatorMark.setText("Spoiler move");

                    System.out.println("Duplicator now can mark the node");
                    nodeInfoLabel.setText("Duplicator Turn");
                    colourMode = "duplicator";
                }else{
                    duplicatorMark.setText("Resume Pebble Game");
                    colourMode = "marked";
                }
                
            }
        });
        welcomeButton = new JButton("<<");
        welcomeButton.setToolTipText("Go back to welcome window");
        welcomeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog = new InitialDialog(frame);
                dialog.setTitle("Pick other mode");
                dialog.setVisible(true);
                newGraphGenerator(3, graph);
                newGraphGenerator(2, graph2);
                resetEverything();
            }
        });

        startPebbleButton = new JButton("Start Pebble Game");
        startPebbleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startPebbleButton.setVisible(false);
                spoilerMark.setVisible(true);
                nodeInfoLabel.setText("Round 1 : Spoiler Turn");
                colourMode = "spoiler";
                pebbleStarted = true;
                pebbleGame = new PebbleGameState(graph, graph2, pebbleK, pebbleN);
            }
        });

        crButton = new JButton("Colour Refinement");
        crButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.YELLOW, Color.ORANGE, Color.RED, Color.BLUE), emptyBorder));

        crButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Starting Algorithm . . .");
                cRefineGraph = new ColourRefinementAlgorithm(graphLabel1, graph, graphLabel2, graph2 ,sleepTime);
                cRefineGraph.setCRefinementGoing(true);
                backButton.setVisible(true);
                nextButton.setVisible(true);
                //finalColors = ColorRefinement.refine(graph, graph2, true);
            }
        });
        backButton = new JButton("Back Iteration");
        backButton.setVisible(false);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Moving back in time . . .");
                int previousRound = Integer.parseInt(currentLabel.getText().substring(currentLabel.getText().length()-1))-1;
                if (previousRound > 0){
                    cRefineGraph.setIteration(previousRound);
                    cRefineGraph.setIteration(previousRound);
                    System.out.println("setting on round " + previousRound);
                    System.out.println("signature"+(previousRound));
                    System.out.println("======================== ");
                    graphLabel1.setText("Round " + previousRound);
                    graphLabel2.setText("Round " + previousRound);
                }
            }
        });

        nextButton = new JButton("Next Iteration");
        nextButton.setVisible(false);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Going forward in time . . .");
                int nextRound = Integer.parseInt(currentLabel.getText().substring(currentLabel.getText().length()-1))+1;
                if (cRefineGraph.getStableRound()>nextRound){
                    cRefineGraph.setIteration(nextRound);
                    cRefineGraph.setIteration(nextRound);
                System.out.println("setting on round " + nextRound);
                System.out.println("======================== ");
                    graphLabel1.setText("Round " + nextRound);
                    graphLabel2.setText("Round " + nextRound);
                }
            }
        });
        JButton resetOption = new JButton("Reset graph");
        resetOption.setVisible(true);
        resetOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Showing custom reset dialog");
                showResetDialog(frame);
            }
        });

        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 100, 10);
        speedSpinner = new JSpinner(model);

        speedSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int speed = (int) speedSpinner.getValue();
                System.out.println("Speed: " + speed);
                sleepTime = speed;

            }
        });
        pebbleRuleDialogButton = new JButton("Game Rules");
        pebbleRuleDialogButton.setVisible(false);
        pebbleRuleDialogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PebbleGameRulesDialog gameRuleDialog = new PebbleGameRulesDialog(frame);
                gameRuleDialog.setVisible(true);
            }
        });
        kPebbleLabel = new JLabel("k-Pebble :");
        SpinnerNumberModel modelKPebble = new SpinnerNumberModel(2, 1, 20, 1);
        pebbleKSpinner = new JSpinner(modelKPebble);
        pebbleKSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int k = (int) pebbleKSpinner.getValue();
                System.out.println("Updated k-Pebble: " + k);
                pebbleK = k;
                if (pebbleStarted) {
                    pebbleGame.updateKPebble(k);
                }
            }
        });
        nPebbleLabel = new JLabel("n-Round :");
        SpinnerNumberModel modelNPebble = new SpinnerNumberModel(5, 1, 20, 1);
        pebbleNSpinner = new JSpinner(modelNPebble);
        pebbleNSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int n = (int) pebbleNSpinner.getValue();
                System.out.println("Updated Game Round: " + n);
                pebbleN = n;
                if (pebbleStarted) {
                    pebbleGame.updateNPebble(n);
                }
            }
        });

        panelVisibility(dialog.isOptionCRSelected());
        buttonPanel.add(welcomeButton);
        buttonPanel.add(startPebbleButton);
        buttonPanel.add(spoilerMark);
        buttonPanel.add(duplicatorMark);
        buttonPanel.add(crButton);
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(resetOption);
        buttonPanel.add(speedLabel);
        buttonPanel.add(speedSpinner);
        buttonPanel.add(kPebbleLabel);
        buttonPanel.add(pebbleKSpinner);
        buttonPanel.add(nPebbleLabel);
        buttonPanel.add(pebbleNSpinner);
        buttonPanel.add(pebbleRuleDialogButton);

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
                    cRefineGraph.cRefinement(graph, graph2);
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

        if (!"marked".equals(colourMode)) { // Marking Mode
            if (currentClass.equals("colour") && !currentClass.equals(colourMode)) { // Pebble Marking (after color refienment)
                if (!currentGraph.hasAttribute("pebbleStarted")){
                    currentGraph.setAttribute("pebbleStarted", clickedNode.getId());
                }
                if (pebbleStarted) {
                    if (pebbleGame.checkValidMove(currentGraph, colourMode)){
                        if (!pebbleGame.availablePebble()){
                            JOptionPane.showMessageDialog(null, "No more pebble can be placed. Try picking up other placed pebbles instead", "Max pebbles reached", JOptionPane.WARNING_MESSAGE);
                            nodeInfoLabel.setText(" k number of pebbles reached");
                        }else{
                            clickedNode.setAttribute("ui.class", colourMode, "colour");
                            clickedNode.setAttribute("mark", colourMode);
                            pebbleGame.addPebble(clickedNode, currentGraph, colourMode);
                            swapColourMode();
                        }
                    }else{
                        JOptionPane.showMessageDialog(null, "Spoiler already choose a node from this graph !", "Choose a different graph", JOptionPane.WARNING_MESSAGE);
                        nodeInfoLabel.setText("! ! ! Duplicator should not choose node on the same graph as spoiler ! ! !");
                    }
                }
            } else if (currentClass.equals("unmarked")) { // Pebble marking (before color refienment)
                if (!currentGraph.hasAttribute("pebbleStarted")){
                    currentGraph.setAttribute("pebbleStarted", clickedNode.getId());
                }
                if (pebbleStarted) {
                    playerMovePebble(clickedNode);
                }
            } else if (currentClass.equals(colourMode)) {   //Remove Pebble Marking
                playerMovePebble(clickedNode);
            } else { // add colour or remove colour when clicked appropriately
                playerMovePebble(clickedNode);
            }
        }else { // Default mode (No marking)
            if (clickedNode.hasAttribute("ui.color")) { //Remove or add colour 
                System.out.println(currentClass);
                clickedNode.setAttribute("ui.class", currentClass.equals("colour") ? "unmarked" : "colour");
            } else {
                clickedNode.setAttribute("ui.class", currentClass.equals("marked") ? "unmarked" : "marked");
                System.out.println("attempt 3");
            }
        }
        if (!pebbleStarted) {
            nodeInfoLabel.setText(getNodeInformation(clickedNode)); 
        }
        if (pebbleStarted && pebbleGame.isGameEnded()){
            stopPebble();
            displayWinner();
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

    public void newGraphGenerator(int choosenType, Graph choosenGraph){
        Graph newGraph;
        switch (choosenType) {
            case 3: // Barabasi
                //newGraph = TestGraphManager.createGraph("Graph", new BarabasiAlbertGenerator(1), maxNode);
                newGraph = TestGraphManager.createExampleGraph1();
                break;
            case 2: // Dorogo
                //newGraph = TestGraphManager.createGraph("Graph", new DorogovtsevMendesGenerator(), maxNode);
                newGraph = TestGraphManager.createExampleGraph2();
                break;
            case 0: //Simple stub
                //newGraph = TestGraphManager.createGraph("Graph",new WattsStrogatzGenerator(maxNode,2,0.5), maxNode);
                newGraph = TestGraphManager.createPartialIso_G1_K4Handle();
                break;
            case 1: //  Simple 2 Stub
                //newGraph = TestGraphManager.createGraph("Graph", new WattsStrogatzGenerator(maxNode,2,0.5), maxNode);
                newGraph = TestGraphManager.createPartialIso_G2_K4Stubs();
                break;
            case 4: //  Simple
                newGraph = TestGraphManager.createGraph("Graph", new WattsStrogatzGenerator(maxNode,2,0.5), maxNode);
                break;
            default:
                newGraph = TestGraphManager.createGraph("Graph", new WattsStrogatzGenerator(10,2,0.5), maxNode);
                //newGraph = TestGraphManager.createPathP4("NonIso_P4");

        }
        System.out.println("Getting new graph");
        choosenGraph.clear();
        //Graph newGraph = TestGraphManager.createGraph("Graph A", new DorogovtsevMendesGenerator());

        newGraph.attributeKeys().forEach((key) -> {
            choosenGraph.setAttribute(key, new Object[]{newGraph.getAttribute(key)});
            });

        Stream<Node> nodeStream = StreamSupport.stream(newGraph.nodes().spliterator(), false);
        nodeStream.forEach(node -> {
            choosenGraph.addNode(node.getId());
            node.attributeKeys().forEach(key -> choosenGraph.getNode(node.getId()).setAttribute(key, node.getAttribute(key)));
        });

        // Convert Iterable to Stream for edges 
        Stream<Edge> edgeStream = StreamSupport.stream(newGraph.edges().spliterator(), false);
        edgeStream.forEach(edge -> {
            choosenGraph.addEdge(edge.getId(), edge.getSourceNode().getId(), edge.getTargetNode().getId(), edge.isDirected());
            edge.attributeKeys().forEach(key -> choosenGraph.getEdge(edge.getId()).setAttribute(key, edge.getAttribute(key)));
        });
    }

    public void resetEverything(){
        stopPebble();
        sleepTime = 0;
        maxNode = 5;
        nodeInfoLabel.setText("Click a node to get detailed attributes . . .");
        startPebbleButton.setText("Start Pebble Game");
        startPebbleButton.setEnabled(true);
        graphLabel1.setText("Graph A");
        graphLabel2.setText("Graph B");
        panelVisibility(dialog.isOptionCRSelected());

    }

    public void stopPebble(){
        firstPebble = null;
        colourMode= "marked";
        pebbleStarted= false;
        spoilerMark.setVisible(false);
        duplicatorMark.setVisible(false);
        startPebbleButton.setVisible(true);
        startPebbleButton.setEnabled(false);
        startPebbleButton.setText("Pebble Game Ended");
    }

    public void displayWinner(){
        if (pebbleGame.maxRoundReached()) {
            nodeInfoLabel.setText("Duplicator won up to "+ (pebbleGame.getCurrentRound()-1) + " round");
            JOptionPane.showMessageDialog(null, "Duplicator maintains the win !", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }else{
            nodeInfoLabel.setText("Spoiler Won in "+ (pebbleGame.getCurrentRound()) + " Round");
            JOptionPane.showMessageDialog(null, "Spoiler Wins !", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void swapColourMode(){
        if (colourMode.equals("duplicator")) {
            //cRefineGraph = new NonCountingColourRefinementAlgorithm(graphLabel1, graph, graphLabel2, graph2 ,sleepTime);
            //cRefineGraph.setCRefinementGoing(true);
            // NonCountingColourRefinementAlgorithm refineNCRalgo = new NonCountingColourRefinementAlgorithm();
            // refineNCRalgo.initializeNodeColorsByDegree();
            // refineNCRalgo.runAllIteration();
            // refineNCRalgo.updateGraphAppearance();
            refineColourPebble();
        }
        spoilerMark.setVisible(colourMode.equals("duplicator") ? true : false);
        duplicatorMark.setVisible(colourMode.equals("spoiler") ? true : false);
        colourMode = (colourMode.equals("duplicator") ? "spoiler" : "duplicator") ;
        nodeInfoLabel.setText("Round " + (pebbleGame.getCurrentRound()) + " : " +colourMode.substring(0, 1).toUpperCase() + colourMode.substring(1) + " turn");
    }

    public void playerMovePebble(Node clickedNode){
        if (clickedNode.hasAttribute("mark")) {
            if(colourMode.equals("spoiler")){
                pebbleGame.removePebble(clickedNode);
                refineColourPebble();
                nodeInfoLabel.setText(colourMode+ " picked up the pebble");
            }else{
                JOptionPane.showMessageDialog(null, "Duplicator can not pick up pebble !!", "Choose a different node", JOptionPane.WARNING_MESSAGE);
                nodeInfoLabel.setText("! ! ! Duplicator should not choose placed pebble ! ! !");
            }
        }else{
            if (pebbleGame.availablePebble()){
                if (pebbleGame.checkValidMove(currentGraph, colourMode)){
                    clickedNode.setAttribute("ui.class", colourMode);
                    clickedNode.setAttribute("mark", colourMode);
                    pebbleGame.addPebble(clickedNode, currentGraph, colourMode);
                    swapColourMode();
                }else{
                    JOptionPane.showMessageDialog(null, "Spoiler already choose a node from this graph !", "Choose a different graph", JOptionPane.WARNING_MESSAGE);
                    nodeInfoLabel.setText("! ! ! Duplicator should not choose node on the same graph as spoiler ! ! !");
                }
            } else{
                JOptionPane.showMessageDialog(null, "No more pebble can be placed. Try picking up other placed pebbles instead", "Max pebbles reached", JOptionPane.WARNING_MESSAGE);
                nodeInfoLabel.setText("k number of pebbles reached");
            }
            
        }
    }

    private void showResetDialog(JFrame parentFrame) {
        JDialog resetDialog = new JDialog(parentFrame, "Reset Options", true); // true for modal
        resetDialog.setLayout(new BorderLayout());

        JLabel graphTypeLabel = new JLabel("Choose graph type:", SwingConstants.CENTER);
        String[] graphOptions = {"Simple Stub", "Simple Stub 2", "Simple Triangle", "Simple Star", "Complex Random"};

        JCheckBox clearBothGraphCheckBox = new JCheckBox("CLEAR BOTH GRAPH");
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        clearBothGraphCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
        checkboxPanel.add(clearBothGraphCheckBox);
        clearBothGraphCheckBox.setSelected(false); // Set initial state if needed

        JPanel buttonPanel = new JPanel(new FlowLayout());
        ActionListener graphButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedType = e.getActionCommand();
                boolean clearBoth = clearBothGraphCheckBox.isSelected();

                System.out.println("Selected type: " + selectedType);
                System.out.println("Clear data: " + clearBoth);

                int choice = -1;
                for (int i = 0; i < graphOptions.length; i++) {
                    if (graphOptions[i].equals(selectedType)) {
                        choice = i;
                        break;
                    }
                }
                if (clearBoth) {
                newGraphGenerator(choice, graph);
                newGraphGenerator(choice, graph2);
                }else{
                    newGraphGenerator(choice, currentGraph);
                }
                resetEverything();
                resetDialog.dispose(); // Close the dialog
            }
        };

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetDialog.dispose(); // Close the dialog
            }
        });

        for (String option : graphOptions) {
            JButton optionButton = new JButton(option);
            optionButton.setActionCommand(option); // Set the action command to the graph type
            optionButton.addActionListener(graphButtonListener);
            buttonPanel.add(optionButton);
        }
        buttonPanel.add(cancelButton);

        JLabel maxNodeLabel = new JLabel("Maximum Node per Graph:");
        SpinnerNumberModel model = new SpinnerNumberModel(5, 1, 100, 5);
        JSpinner maxNumberNodeSpinner = new JSpinner(model);
        maxNumberNodeSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int maxNumber = (int) maxNumberNodeSpinner.getValue();
                System.out.println("Max Node set: " + maxNumber);
                maxNode = maxNumber;
            }
        });

        checkboxPanel.add(maxNodeLabel);
        checkboxPanel.add(maxNumberNodeSpinner);
        
        resetDialog.add(checkboxPanel, BorderLayout.NORTH);
        resetDialog.add(graphTypeLabel, BorderLayout.CENTER);
        resetDialog.add(buttonPanel, BorderLayout.SOUTH);

        resetDialog.pack(); // Adjust dialog size to fit components
        resetDialog.setLocationRelativeTo(parentFrame); // Center relative to the parent
        resetDialog.setVisible(true);
    }

    public void refineColourPebble(){
        GraphColorRefiner refiner = new GraphColorRefiner(graph);
        GraphColorRefiner refiner2 = new GraphColorRefiner(graph2);
        refiner.initializeByDegree();
        refiner.refineUntilStable();
        refiner2.initializeByDegree();
        refiner2.refineUntilStable();
        GraphAppearanceUpdater.updateNodeAppearance(graph);
        GraphAppearanceUpdater.updateNodeAppearance(graph2);
    }

    public void panelVisibility(boolean flag){
        backButton.setVisible(false);
        nextButton.setVisible(false);
        if (dialog.isOptionCRSelected()){
            startPebbleButton.setVisible(false);
            crButton.setVisible(true);
            speedSpinner.setVisible(true);
            speedLabel.setVisible(true);
            frame.setTitle("Colour Refinement");
            pebbleRuleDialogButton.setVisible(false);
            pebbleKSpinner.setVisible(false);
            pebbleNSpinner.setVisible(false);
            kPebbleLabel.setVisible(false);
            nPebbleLabel.setVisible(false);
        }else{
            startPebbleButton.setVisible(true);
            crButton.setVisible(false);
            speedSpinner.setVisible(false);
            speedLabel.setVisible(false);
            frame.setTitle("Pebble Game");
            pebbleRuleDialogButton.setVisible(true);
            pebbleKSpinner.setVisible(true);
            pebbleNSpinner.setVisible(true);
            kPebbleLabel.setVisible(true);
            nPebbleLabel.setVisible(true);
        }
    }

}