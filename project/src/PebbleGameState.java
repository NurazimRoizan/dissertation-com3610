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

    public void addPebble(Node newNode, Graph currentGraph, String currentMode) {

        if (currentMode.equals("spoiler")) {
            // Spoiler just placed a pebble
            currentSpoilerPebble = newNode;
            currentSpoilerGraph = currentGraph;
            System.out.println("Spoiler placed pebble on " + newNode.getId() + " in graph " + currentGraph.getId());
        } else { // Duplicator is responding
            System.out.println("Duplicator attempting to respond with " + newNode.getId() + " in graph " + currentGraph.getId());

            Node pebbleForG1;
            Node pebbleForG2;
            boolean checkResult;

            // Determine which node corresponds to G1 and G2 for this round's pair
            if (currentSpoilerGraph.equals(graph1)) {
                // Spoiler previously chose in graph1, Duplicator responds with newNode (in graph2)
                pebbleForG1 = currentSpoilerPebble;
                pebbleForG2 = newNode;
            } else { // Spoiler previously chose in graph2 (currentSpoilerGraph must be graph2)
                // Duplicator responds with newNode (in graph1)
                pebbleForG1 = newNode;
                pebbleForG2 = currentSpoilerPebble;
            }

            // Check if this move maintains the partial isomorphism
            checkResult = checkPartialIso(pebbleForG1, pebbleForG2);


            if (!checkResult) {
                // Duplicator's move fails the check - Spoiler wins
                System.out.println("-------------------------");
                System.out.println("Partial isomorphism check failed!");
                System.out.println("SPOILER WINS ! ! !");
                System.out.println("-------------------------");
                pebbleGameEnded = true;
            } else {
                // Duplicator's move is valid, record the pebble pair
                pebblesG1.add(pebbleForG1);
                pebblesG2.add(pebbleForG2);
                System.out.println("Duplicator successfully placed pair: (" + pebbleForG1.getId() + ", " + pebbleForG2.getId() + ")");

                // Reset for the next round (Spoiler's turn)
                currentSpoilerPebble = null;
                currentSpoilerGraph = null;
            }
        }
    }

    /**
     * Checks if adding the new pair (newNodeG1, newNodeG2) to the existing
     * partial mapping (pebblesG1, pebblesG2) maintains the partial isomorphism property.
     *
     * @param newNodeG1 The newly proposed pebble in G1.
     * @param newNodeG2 The newly proposed pebble in G2.
     * @return true if the partial isomorphism property holds, false otherwise.
     */
    public boolean checkPartialIso(Node newNodeG1, Node newNodeG2) {
        System.out.println("pebbleG1 has :");
        System.out.println(this.pebblesG1);
        System.out.println("pebbleG2 has :");
        System.out.println(this.pebblesG2);


        for (int i = 0; i < pebblesG1.size(); i++) {
            Node existingG1 = this.pebblesG1.get(i);
            Node existingG2 = this.pebblesG2.get(i);

            // Check adjacency between the new node and existing nodes
            // Assumes undirected graphs via hasEdgeBetween. Modify if directed needed.
            boolean adjacentInG1 = newNodeG1.hasEdgeBetween(existingG1);
            boolean adjacentInG2 = newNodeG2.hasEdgeBetween(existingG2);
            System.out.println("graph 1 = "+ adjacentInG1 + "==============================");
            System.out.println(newNodeG1 + " vs " + existingG1);
            System.out.println("graph 2 = "+ adjacentInG2+ "==============================");
            System.out.println(newNodeG2 + " vs " + existingG2);



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
