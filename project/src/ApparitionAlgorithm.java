import java.util.HashMap;

import org.graphstream.algorithm.DynamicAlgorithm;
import org.graphstream.graph.Graph;
import org.graphstream.stream.SinkAdapter;

public class ApparitionAlgorithm extends SinkAdapter implements
 		DynamicAlgorithm {
 
    Graph theGraph;
    HashMap<String, Integer> apparitions;
    double avg;
  
    public void init(Graph graph) {
        theGraph = graph;
        avg = 0;
        graph.addSink(this);
    }
  
    public void compute() {
        avg = 0;
  
        for (int a : apparitions.values())
            avg += a;
            avg /= apparitions.size();
        }
  
    public void terminate() {
        theGraph.removeSink(this);
    }
  
    public double getAverageApparitions() {
        return avg;
    }
  
    public int getApparitions(String nodeId) {
        return apparitions.get(nodeId);
    }
  
    public void nodeAdded(String sourceId, long timeId, String nodeId) {
        int a = 0;
  
        if (apparitions.containsKey(nodeId))
            a = apparitions.get(nodeId);
  
        apparitions.put(nodeId, a + 1);
    }
  
    public void stepBegins(String sourceId, long timeId, double step) {
        compute();
    }
  }