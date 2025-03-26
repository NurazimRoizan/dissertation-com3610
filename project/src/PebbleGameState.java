import java.util.ArrayList;
import java.util.List;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class PebbleGameState {
    private List<Node> pebblesG1;
    private List<Node> pebblesG2;
    private Graph graph1;
    private Graph graph2;
    private Node currentSpoilerPebble;
    private Graph currentSpoilerGraph;
    private boolean pebbleGameEnded;


    public PebbleGameState(Graph graph1, Graph graph2){
        this.graph1= graph1;
        this.graph2= graph2;
        this.pebblesG1 = new ArrayList<>();
        this.pebblesG2 = new ArrayList<>();
    }

    public void addPebble(Node newNode, Graph currentGraph, String currentMode){
        if(currentMode.equals("spoiler")){
            currentSpoilerPebble = newNode;
            currentSpoilerGraph = currentGraph;
        }else{
            if (checkPartialIso(currentSpoilerPebble, newNode)){
                if (currentSpoilerGraph.equals(graph1)){
                    pebblesG1.add(currentSpoilerPebble);
                    pebblesG2.add(newNode);
                }else{
                    pebblesG1.add(newNode);
                    pebblesG2.add(currentSpoilerPebble);
                }
            }else{
                System.out.println("SPOILER WINS ! ! !");
                pebbleGameEnded = true;
            }
        }
    }

    /**
     * Checks if adding the new pair (newNodeG1, newNodeG2) to the existing
     * partial mapping (pebblesG1, pebblesG2) maintains the partial isomorphism property.
     *
     * @param pebblesG1 Existing pebbled nodes in G1.
     * @param pebblesG2 Existing corresponding pebbled nodes in G2.
     * @param newNodeG1 The newly proposed pebble in G1.
     * @param newNodeG2 The newly proposed pebble in G2.
     * @return true if the partial isomorphism property holds, false otherwise.
     */
    public boolean checkPartialIso(Node newNodeG1, Node newNodeG2) {

        for (int i = 0; i < pebblesG1.size(); i++) {
            Node existingG1 = pebblesG1.get(i);
            Node existingG2 = pebblesG2.get(i);

            // Check adjacency between the new node and existing nodes
            // Assumes undirected graphs via hasEdgeBetween. Modify if directed needed.
            boolean adjacentInG1 = newNodeG1.hasEdgeBetween(existingG1);
            boolean adjacentInG2 = newNodeG2.hasEdgeBetween(existingG2);

            if (adjacentInG1 != adjacentInG2) {
                return false; // Adjacency mismatch
            }
            
        }
        return true; // All checks passed
    }

    public boolean checkValidMove(Graph currentGraph, String currentMode){
        return !(currentMode.equals("duplicator") && currentSpoilerGraph.equals(currentGraph) );
    }

    public boolean isGameEnded(){
        return pebbleGameEnded;
    }
}
