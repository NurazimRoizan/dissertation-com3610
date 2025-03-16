import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import javax.swing.JLabel;

import org.graphstream.algorithm.APSP;
import org.graphstream.algorithm.Centroid;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class ColourRefinementAlgorithm {
    private Map<String, Integer> colourTable;
    private int colourIndex;
    private boolean colorChanges;
    private boolean startIteration; // Or manage this externally
    private boolean cRefinementGoing;
    private JLabel roundTitle;
    private int round, stableRound;
    private int previousSize;
    private int sleep;

    public ColourRefinementAlgorithm(JLabel roundTitle, int sleep) {
        this.colourTable = new HashMap<>(); // Initialize the map
        this.colourIndex = 0;
        this.colorChanges = false; // Initialize
        this.startIteration = true; // or false
        this.cRefinementGoing=false;
        this.roundTitle = roundTitle;
        this.round = 1;
        this.previousSize = 0;
        this.sleep = sleep;
    }

    public void cRefinement(Graph graph){
        Node startNode = computeCentroid(graph);
        if (startIteration) {startup(startNode);}
        if (colorChanges){
            System.out.println("There is color changes. Starting new round . . .");
            round += 1;
            nextRound(startNode);
        } else {
            System.out.println("Algorithm now stable");
            cRefinementGoing = false;
            System.out.println("End of Colour Refinement");
            this.stableRound = round;
        }
    }

    public void startup(Node source) {
        //Iterator<? extends Node> k = source.getBreadthFirstIterator();
        Iterator<? extends Node> k = source.getDepthFirstIterator();
        Iterator<? extends Node> j = source.getDepthFirstIterator();
        //Iterator<? extends Node> j = source.getBreadthFirstIterator();
        int currentColourIndex = -1;
        int degree = source.getDegree();
    
        // Check Neighbour Nodes
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
        roundTitle.setText("Round " + round);
        System.out.println("End of 1st iteration");
    }

    public void nextRound(Node source) {
        //Iterator<? extends Node> k = source.getBreadthFirstIterator();
        //Iterator<? extends Node> j = source.getBreadthFirstIterator();
        //Iterator<? extends Node> i = source.getBreadthFirstIterator();
        Iterator<? extends Node> k = source.getDepthFirstIterator();
        Iterator<? extends Node> j = source.getDepthFirstIterator();
        Iterator<? extends Node> i = source.getDepthFirstIterator();

        colorChanges = false;
        colourTable.clear();
        colourIndex = 0;
        int currentColourIndex = -1;

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

        if (previousSize != colourTable.size()){
            colorChanges = true;
            previousSize = colourTable.size();
            roundTitle.setText("Round " + round);
        }

        System.out.println("End of Round "+ round);
    }

    public void updateSignature(Node source){
        Stream<Node> neighbourNodes = source.neighborNodes();
        String currentSignature = String.valueOf(source.getAttribute("signature"+(round-1)));
        String currentDegree = currentSignature.substring(0,1);
        StringBuilder neighbourSignature = new StringBuilder();
        neighbourNodes.forEach(neighbourNode -> {
            neighbourSignature.append(String.valueOf(neighbourNode.getAttribute("signature"+(round-1))));
            //System.out.println("Neighbor color signature: " + neighbourSignature);
            //System.out.println("Current Signature: " + String.valueOf(currentDegree) + neighbourSignature);
        });
        String sortedNeighbourSignature = createSortedSignature(neighbourSignature.toString());
        neighbourSignature.setLength(0);
        source.setAttribute("signature"+(round), currentDegree + sortedNeighbourSignature);
        //System.out.println("Done set for node= "+ source);
    }

    public static String createSortedSignature(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
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
}
