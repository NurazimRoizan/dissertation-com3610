import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Graph;

public class DegreesAlgorithm implements Algorithm {
    Graph theGraph;
    int minDegree, maxDegree, avgDegree;
  
    public void init(Graph graph) {
        theGraph = graph;
    }
  
    public void compute() {
        avgDegree = 0;
        minDegree = Integer.MAX_VALUE;
        maxDegree = 0;
  
        theGraph.nodes().forEach( n -> {
 		    int deg = n.getDegree();
  
            minDegree = Math.min(minDegree, deg);
            maxDegree = Math.max(maxDegree, deg);
            avgDegree += deg;
        });
  
        avgDegree /= theGraph.getNodeCount();
    }
  
    public int getMaxDegree() {
        return maxDegree;
    }
  
    public int getMinDegree() {
        return minDegree;
    }
  
    public int getAverageDegree() {
        return avgDegree;
    }


}