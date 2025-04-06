import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JLabel;

import org.graphstream.algorithm.APSP;
import org.graphstream.algorithm.Centroid;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class ColourRefinementAlgorithm {
    private Map<String, Integer> colourTable;
    private Graph graph1, graph2;
    private int colourIndex;
    private boolean colorChanges;
    private boolean startIteration; // Or manage this externally
    private boolean cRefinementGoing;
    private JLabel roundTitle1, roundTitle2;
    private int round, stableRound;
    private int previousSize;
    private int sleep;
    private int currentColourIndex = -1;

    public ColourRefinementAlgorithm(JLabel roundTitle1, Graph graph1,JLabel roundTitle2, Graph graph2, int sleep) {
        this.colourTable = new HashMap<>(); // Initialize the map
        this.colourIndex = 0;
        this.colorChanges = false; // Initialize
        this.startIteration = true; // or false
        this.cRefinementGoing=false;
        this.roundTitle1 = roundTitle1;
        this.roundTitle2 = roundTitle2;
        this.round = 1;
        this.previousSize = 0;
        this.sleep = sleep;
        this.graph1 = graph1;
        this.graph2 = graph2;
    }

    public void cRefinement(Graph graph, Node startNode){
        if (graph.equals(this.graph1)) {
            Node startNode2 = computeCentroid(graph2);
            cRefinement(graph1, startNode, graph2, startNode2);
        }else{
            Node startNode1 = computeCentroid(graph1);
            cRefinement(graph1, startNode1, graph2, startNode);
        }
    }

    public void cRefinement(Graph graph){
        Node startNode = computeCentroid(graph);
        cRefinement(graph, startNode);
    }

    public void cRefinement(Graph graph1, Graph graph2){
        Node startNode1 = computeCentroid(graph1);
        Node startNode2 = computeCentroid(graph2);
        cRefinement(graph1, startNode1, graph2, startNode2);
    }

    public void cRefinement(Graph graph1, Node nodeGraph1, Graph graph2, Node nodeGraph2){
        if (startIteration) {startup(nodeGraph1, nodeGraph2);}
        if (colorChanges){
            System.out.println("There is color changes. Starting new round . . .");
            round += 1;
            colourTable.clear();
            colourIndex = 0;
            //int currentColourIndex = -1;
            nextRound(nodeGraph1, nodeGraph2);
        } else {
            System.out.println("Algorithm now stable");
            cRefinementGoing = false;
            System.out.println("End of Colour Refinement");
            this.stableRound = round;
            startIteration = true;
            System.out.println(getColourTable());
        }
    }

    public void startup(Node source) {
        // Iterator<? extends Node> k = source.getDepthFirstIterator();
        // Iterator<? extends Node> j = source.getDepthFirstIterator();
        Iterator<? extends Node> k = source.getBreadthFirstIterator();
        Iterator<? extends Node> j = source.getBreadthFirstIterator();
        int degree = source.getDegree();
    
        // Check Neighbour Nodes
        while (k.hasNext()) {
            Node next = k.next();
            degree = next.getDegree();
            
            if (degree >= 0){
                if (colourTable.containsKey(String.valueOf(degree))){
                    if (next.hasAttribute("mark")){next.setAttribute("ui.class", "colour", next.getAttribute("mark"));
                    }else {
                        next.setAttribute("ui.class", "colour");
                    }
                }else {
                    colourTable.put(String.valueOf(degree), colourIndex);
                    if (next.hasAttribute("mark")){next.setAttribute("ui.class", "colour", next.getAttribute("mark"));
                    }else {
                        next.setAttribute("ui.class", "colour");
                    }
                    colourIndex += 1;
                    colorChanges = true;
                    
                }
            } else {next.setAttribute("ui.class", "colour0"); }
        }
        System.out.println("Assigned colour attribute to the nodes");
        
        // Colour the nodes
        while(j.hasNext()){
            Node next = j.next();
            degree = next.getDegree();
            double div = 1/(double)(colourTable.size()-1);
            if (colourTable.containsKey(String.valueOf(degree))){
                currentColourIndex = colourTable.get(String.valueOf(degree)); 
                next.setAttribute("ui.color", (float)(div*currentColourIndex));
                //String addString = String.valueOf(next.getAttribute("signature"));
                next.setAttribute("signature1", String.valueOf(degree));
            }
            //System.out.println("id="+next.getId() + "with colourIndex =" + currentColourIndex + "att =" + div*(currentColourIndex) );
            sleep(sleep);
        }
        startIteration = false;
        roundTitle1.setText("Round " + round);
        roundTitle2.setText("Round " + round);
        System.out.println("End of 1st iteration");
    }

    public void startup(Node source1, Node source2) {
        startup(source1);
        startup(source2);
    }

    public void nextRound(Node source) {
        Iterator<? extends Node> k = source.getBreadthFirstIterator();
        Iterator<? extends Node> j = source.getBreadthFirstIterator();
        Iterator<? extends Node> i = source.getBreadthFirstIterator();
        // Iterator<? extends Node> k = source.getDepthFirstIterator();
        // Iterator<? extends Node> j = source.getDepthFirstIterator();
        // Iterator<? extends Node> i = source.getDepthFirstIterator();

        //colorChanges = false;

        // Update each node with signature attribute
        System.out.println("Updating signature for round " + round);
        while (i.hasNext()){
            Node next = i.next();
            updateSignature(next);
        }

        // - rest of the node
        while (k.hasNext()) {
            Node next = k.next();
            String currentSignature = String.valueOf(next.getAttribute("signature"+(round)));
            
            if (currentSignature != null && !colourTable.containsKey(currentSignature)) {
                colourTable.put(currentSignature, colourIndex++);
            }
        }
        System.out.println("Assigned signature attribute to the nodes");
        
        // Colour the nodes
        System.out.println("Colouring the nodes");
        while(j.hasNext()){
            Node next = j.next();
            String currentSignature = String.valueOf(next.getAttribute("signature"+(round)));
            double div = 1/(double)(colourTable.size()-1);
            currentColourIndex = colourTable.get(currentSignature);     
            next.setAttribute("ui.color", (float)(div*currentColourIndex));
            //for debugging
            //System.out.println("id="+next.getId() + "with colourIndex =" + currentColourIndex + "att =" + div*(currentColourIndex) );
            sleep(sleep);
        }
        System.out.println("previousSize= "+ previousSize + "and currentSize= " + colourTable.size());

        if (previousSize < colourTable.size()){
            colorChanges = true;
            previousSize = colourTable.size();
            roundTitle1.setText("Round " + round);
            roundTitle2.setText("Round " + round);
        }

        System.out.println("End of Round "+ round);
    }

    public void nextRound(Node source1, Node source2) {
        colorChanges = false;
        nextRound(source1);
        nextRound(source2);
    }

    public void updateSignature(Node source){
        Stream<Node> neighbourNodes = source.neighborNodes();
        String currentSignature = String.valueOf(source.getAttribute("signature"+(round-1)));
        String currentDegree = currentSignature.substring(0,1);
        StringBuilder neighbourSignature = new StringBuilder();
        if (source.getAttribute("mark")=="spoiler"){
            neighbourSignature.append("s");
        }else if (source.getAttribute("mark")=="duplicator"){
            neighbourSignature.append("d");
        }
        neighbourNodes.forEach(neighbourNode -> {
            neighbourSignature.append(String.valueOf(neighbourNode.getAttribute("signature"+(round-1))));
            //System.out.println("Neighbor color signature: " + neighbourSignature);
            //System.out.println("Current Signature: " + String.valueOf(currentDegree) + neighbourSignature);
        });
        String sortedNeighbourSignature = createReverseSortedSignature(neighbourSignature.toString());
        neighbourSignature.setLength(0);
        
        source.setAttribute("signature"+(round), currentDegree + sortedNeighbourSignature);
        //System.out.println("Done set for node= "+ source);
    }

    public static String createSortedSignature(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    public static String createReverseSortedSignature(String s) {
        return s.chars()
                .mapToObj(c -> (char) c)
                .sorted(Comparator.reverseOrder())
                .map(String::valueOf)
                .collect(Collectors.joining());
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
 			//System.out.printf("%s is%s in the centroid.\n", n.getId(), in ? "" : " not");
            if (in){
                currentCentroid = Integer.parseInt(n.getId());
            }
 		});
        return graph.getNode(currentCentroid);
    }

    public void setIteration(Graph graph, int desiredRound){
        Node startNode = computeCentroid(graph);
        Iterator<? extends Node> j = startNode.getBreadthFirstIterator();
        Iterator<? extends Node> k = startNode.getBreadthFirstIterator();
        colourTable.clear();
        int colourIndex = 0;
        int currentColourIndex = -1;

        while (k.hasNext()) {
            Node next = k.next();
            String currentSignature = String.valueOf(next.getAttribute("signature"+(desiredRound)));
            
            if (currentSignature != null && !colourTable.containsKey(currentSignature)) {
                colourTable.put(currentSignature, colourIndex++);
            }
        }
        System.out.println("Assigned colorTable attribute to the nodes");

        // Colour the nodes
        System.out.println("Colouring the nodes");

        while(j.hasNext()){
            Node next = j.next();
            String currentSignature = String.valueOf(next.getAttribute("signature"+(desiredRound)));
            double div = 1/(double)(colourTable.size()-1);
            currentColourIndex = colourTable.get(currentSignature);     
            next.setAttribute("ui.color", (float)(div*currentColourIndex));
            //For debugging
            //System.out.println("id="+next.getId() + "with colourIndex =" + currentColourIndex + "att =" + div*(currentColourIndex) );
        }
        System.out.println("current table size is " + colourTable.size());
        System.out.println("Currently showing round number "+ desiredRound);
    }

    public void setCRefinementGoing(boolean onxon){
        this.cRefinementGoing=onxon;
    }
    public boolean getCRefinementGoing(){
        return this.cRefinementGoing;
    }
    public int getStableRound(){
        return stableRound;
    }

    protected void sleep(int milisec) {
        try { Thread.sleep(milisec); } catch (Exception e) {}
    }

    protected void resetState(){
        this.colorChanges = false; 
        this.startIteration = true; 
        this.cRefinementGoing=false;
    }

    public String getColourTable(){
        return colourTable.toString();
    }
}
