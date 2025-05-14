import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class PebbleGameState {
    private List<Node> pebblesG1;
    private List<Node> pebblesG2;
    private Graph graph1, graph2, currentSpoilerGraph;
    private Node currentSpoilerPebble;
    private boolean pebbleGameEnded;
    private int maxPebble, maxRound, currentRound;


    public PebbleGameState(Graph graph1, Graph graph2, int k, int n){
        this.graph1= graph1;
        this.graph2= graph2;
        this.pebblesG1 = new ArrayList<>();
        this.pebblesG2 = new ArrayList<>();
        this.maxPebble = k;
        this.maxRound = n;
        this.currentRound = 1;
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
                currentRound ++;
            }
            // Check for maxRound
            if (maxRoundReached()) {
                pebbleGameEnded = true;
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

        for (int i = 0; i < pebblesG1.size(); i++) {
            Node existingG1 = this.pebblesG1.get(i);
            Node existingG2 = this.pebblesG2.get(i);

            // Check adjacency between the new node and existing nodes
            boolean adjacentInG1 = newNodeG1.hasEdgeBetween(existingG1);
            boolean adjacentInG2 = newNodeG2.hasEdgeBetween(existingG2);

            if (adjacentInG1 != adjacentInG2) {
                return false; // Adjacency mismatch
            }
            
        }
        return true; // All checks passed
    }

    /**
     * Removes the pebble pair associated with the clickedNode.
     * If the clickedNode is part of a pair (present in either pebblesG1 or pebblesG2),
     * the corresponding pair is removed from both lists.
     * This represents Spoiler picking up a pebble pair to reuse the pebble index.
     *
     * @param clickedNode The node instance (in graph1 or graph2) where the pebble to be removed lies.
     * @return true if a pebble pair was successfully removed, false otherwise (e.g., node had no pebble).
     */
    public boolean removePebble(Node clickedNode){
        if (clickedNode == null) {
             System.out.println("Error: Cannot remove pebble from a null node.");
             return false;
        }

        int indexToRemove = -1; // Initialize to an invalid index

        // Find the index of the pebble pair corresponding to the clicked node
        for (int i = 0; i < pebblesG1.size(); i++) {
            if (Objects.equals(clickedNode, pebblesG1.get(i)) || Objects.equals(clickedNode, pebblesG2.get(i))) {
                indexToRemove = i;
                break; // Found the pair, exit the loop
            }
        }
        System.out.println("========================================================");
        // If a matching pair was found, remove it from both lists
        if (indexToRemove != -1) {
            Node removedG1 = pebblesG1.remove(indexToRemove);
            Node removedG2 = pebblesG2.remove(indexToRemove);
            System.out.println("Removed pebble pair: (" + removedG1.getId() + ", " + removedG2.getId() + ")");
            removedG1.removeAttribute("mark");
            removedG2.removeAttribute("mark");
            removedG1.setAttribute("ui.class", clickedNode.hasAttribute("ui.color") ? "colour" : "unmarked");
            removedG2.setAttribute("ui.class", clickedNode.hasAttribute("ui.color") ? "colour" : "unmarked");

            return true; // Indicate success
        } else {
            System.out.println("Node " + clickedNode.getId() + " does not have an active pebble to remove.");
            return false; // Indicate pebble not found
        }
    }

    public boolean checkValidMove(Graph currentGraph, String currentMode){return !(currentMode.equals("duplicator") && currentSpoilerGraph.equals(currentGraph) );}

    public boolean availablePebble(){return (pebblesG1.size() < maxPebble);}

    public int getCurrentRound() {return this.currentRound;}

    public boolean maxRoundReached(){return (currentRound > maxRound);}

    public boolean isGameEnded(){
        return pebbleGameEnded;
    }

    /**
     * Checks the current winner of the pebble game state
     *
     * @return "spoiler" if spoiler wins or "duplicator" if duplicator wins.
     */
    public String checkWinner(){
        return ("duplicator");
    }

    public List<Node> getPebblesG1() {
        return pebblesG1;
    }

    public List<Node> getPebblesG2() {
        return pebblesG2;
    }

    public void updateKPebble(int k){
        maxPebble = k;
    }

    public void updateNPebble(int n){
        maxRound = n;
    }
}
