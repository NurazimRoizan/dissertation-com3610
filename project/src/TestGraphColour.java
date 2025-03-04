import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

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
    protected boolean exploreGraph2 = false;
    protected int colourIndex = 0;
    protected int round = 1;
    protected Map<String, Integer> colourTable = new HashMap<String, Integer>();

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

        JButton myButton4 = new JButton("Text button");
        myButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Exploring2 . . .");
                exploreGraph2 = true;
            }
        });
        JButton myButton5 = new JButton("Increase Round");
        myButton5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                round += 1;
                System.out.println("increased round to "+ round);
            }
        });

        buttonPanel.add(myButton3);
        buttonPanel.add(myButton4);
        buttonPanel.add(myButton5);

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

        while (loop) {
            fromViewer.pump();
            if (exploreGraph) {
                explore(computeCentroid(graph));
            };
            if (exploreGraph2) {
                nextExplore(computeCentroid(graph));
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


    float colourTemp =  0;
    public void buttonPushed(String id) {
		System.out.println("Button pushed on node "+id);
        Node clickedNode = currentGraph.getNode(id);
        String currentClass = (String)clickedNode.getAttribute("ui.class");

        //check neighbour
        // Boolean hehe = clickedNode.hasEdgeToward(currentGraph.getNode("1"));
        int hehe = clickedNode.getDegree();
        System.out.println("number of neightbour="+ hehe);

        checkNode(clickedNode);

        // Check if the clicked node ID exists in graph1
        if (!currentClass.equals("unmarked")) {
            colourTemp = (float)clickedNode.getAttribute("ui.color");
            clickedNode.setAttribute("ui.class", "unmarked");
            
        } else {
            clickedNode.setAttribute("ui.class", "colour");
            clickedNode.setAttribute("ui.color", colourTemp);
        }
	}
    int currentCentroid = 0;
    public Node computeCentroid(Graph graph){
        APSP apsp = new APSP();
 		apsp.init(graph);
 		apsp.compute();
 
 		Centroid centroid = new Centroid();
 		centroid.init(graph);
 		centroid.compute();
		graph.nodes().forEach( n -> {
 			Boolean in = (Boolean) n.getAttribute("centroid");
 			System.out.printf("%s is%s in the centroid.\n", n.getId(), in ? "" : " not");
            if (in){
                currentCentroid = Integer.parseInt(n.getId());
            }
 		});
        return graph.getNode(currentCentroid);
    }

    public void explore(Node source) {
        Iterator<? extends Node> k = source.getBreadthFirstIterator();
        Iterator<? extends Node> j = source.getBreadthFirstIterator();
        int currentColourIndex = -1;
        int degree = source.getDegree();

        // Assign each node with colour attribute
        // - First node
        if (degree >= 0){
            if (colourTable.containsKey(String.valueOf(degree))){
                source.setAttribute("ui.class", "colour");
            }else {
                colourTable.put(String.valueOf(degree), colourIndex);
                source.setAttribute("ui.class", "colour");
                colourIndex += 1;
            }
        } else {source.setAttribute("ui.class", "colour0"); }


        // - rest of the node
        while (k.hasNext()) {
            Node next = k.next();
            degree = next.getDegree();
            
            if (degree >= 0){
                if (colourTable.containsKey(String.valueOf(degree))){
                    next.setAttribute("ui.class", "colour");
                }else {
                    colourTable.put(String.valueOf(degree), colourIndex);
                    next.setAttribute("ui.class", "colour");
                    colourIndex += 1;
                    
                }
            } else {next.setAttribute("ui.class", "colour0"); }
        }
        System.out.println("Assigned colour attribute to the nodes");
        
        // Colour the nodes
        while(j.hasNext()){
            Node next = j.next();
            degree = next.getDegree();
            float div = 1/(float)(colourTable.size()-1);
            if (colourTable.containsKey(String.valueOf(degree))){
                currentColourIndex = colourTable.get(String.valueOf(degree)); 
                next.setAttribute("ui.color", div*(currentColourIndex));
                //String addString = String.valueOf(next.getAttribute("signature"));
                next.setAttribute("signature1", String.valueOf(degree));
            }
            System.out.println("id="+next.getId() + "with colourIndex =" + currentColourIndex + "att =" + div*(currentColourIndex) );
            sleep();
        }

        exploreGraph = false;
        System.out.println("End of Exploration");
    }

    public void nextExplore(Node source) {
        Iterator<? extends Node> k = source.getBreadthFirstIterator();
        Iterator<? extends Node> j = source.getBreadthFirstIterator();
        Iterator<? extends Node> i = source.getBreadthFirstIterator();
        colourTable.clear();
        colourIndex = 0;
        int currentColourIndex = -1;

        //Stream<Node> neighbourNodes = source.neighborNodes();

        // Assign each node with signature attribute
        // - First node
        System.out.println("source . . . . . . . . . . . ");
        checkNode(source);
        while (i.hasNext()){
            Node next = i.next();
            System.out.println(next + " . . . . . . . . . . . ");
            checkNode(next);
            sleep();
        }

        String currentSignature = String.valueOf(source.getAttribute("signature"+(round+1)));

        if (currentSignature != null){
            if (colourTable.containsKey(currentSignature)){
                //do nothing
            }else {
                colourTable.put(currentSignature, colourIndex);
                colourIndex += 1;
            }
        }
        // - rest of the node
        while (k.hasNext()) {
            Node next = k.next();
            currentSignature = String.valueOf(next.getAttribute("signature"+(round+1)));
            
            if (currentSignature != null){
                if (colourTable.containsKey(currentSignature)){
                    //do nothing
                }else {
                    colourTable.put(currentSignature, colourIndex);
                    colourIndex += 1;
                }
            }
        }
        System.out.println("Assigned signature attribute to the nodes");
        
        // Colour the nodes
        
        while(j.hasNext()){
            Node next = j.next();
            currentSignature = String.valueOf(next.getAttribute("signature"+(round+1)));
            float div = 1/(float)(colourTable.size()-1);
            currentColourIndex = colourTable.get(currentSignature);     
            next.setAttribute("ui.color", div*(currentColourIndex));
            System.out.println("id="+next.getId() + "with colourIndex =" + currentColourIndex + "att =" + div*(currentColourIndex) );
            sleep();
        }

        exploreGraph2 = false;
        System.out.println("End of Exploration");
    }

    public void checkNode(Node source){
        System.out.println(source);
        Stream<Node> neighbourNodes = source.neighborNodes();
        String currentSignature = String.valueOf(source.getAttribute("signature"+round));
        String currentDegree = currentSignature.substring(0,1);
        StringBuilder neighbourSignature = new StringBuilder();
        neighbourNodes.forEach(neighbourNode -> {
            neighbourSignature.append(String.valueOf(neighbourNode.getAttribute("signature"+round)));
            System.out.println("Neighbor color signature: " + neighbourSignature);
            System.out.println("Current Signature: " + String.valueOf(currentDegree) + neighbourSignature);
        });
        String sortedNeighbourSignature = createSortedSignature(neighbourSignature.toString());
        neighbourSignature.setLength(0);
        source.setAttribute("signature"+(round+1), currentDegree + sortedNeighbourSignature);
        System.out.println("Done set for node= "+ source);
    }

    protected void sleep() {
        try { Thread.sleep(100); } catch (Exception e) {}
    }

    // Old method. Delete after trial
    public static boolean isSameSignature(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }

        char[] aChars = a.toCharArray();
        char[] bChars = b.toCharArray();

        Arrays.sort(aChars);
        Arrays.sort(bChars);

        return Arrays.equals(aChars, bChars);
    }

    // New Method 
    public static String createSortedSignature(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}
