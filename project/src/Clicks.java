import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.algorithm.generator.BananaTreeGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

public class Clicks implements ViewerListener {
	protected boolean loop = true;

	public static void main(String args[]) {
		System.setProperty("org.graphstream.ui", "swing");
		new Clicks();
	}
	public Clicks() {
		// We do as usual to display a graph. This
		// connect the graph outputs to the viewer.
		// The viewer is a sink of the graph.
        Generator gen = new BananaTreeGenerator();
		Graph graph = new SingleGraph("Clicks");
        gen.addSink(graph);
        gen.begin();
        for(int i = 0; i < 10; i++)
            gen.nextEvents();
        gen.end();
		Viewer viewer = graph.display();

		// The default action when closing the view is to quit
		// the program.
		viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

		// We connect back the viewer to the graph,
		// the graph becomes a sink for the viewer.
		// We also install us as a viewer listener to
		// intercept the graphic events.
		ViewerPipe fromViewer = viewer.newViewerPipe();
		fromViewer.addViewerListener(this);
		fromViewer.addSink(graph);

		// Then we need a loop to do our work and to wait for events.
		// In this loop we will need to call the
		// pump() method before each use of the graph to copy back events
		// that have already occurred in the viewer thread inside
		// our thread.

		while(loop) {
			fromViewer.pump(); // or fromViewer.blockingPump(); in the nightly builds

			// here your simulation code.

			// You do not necessarily need to use a loop, this is only an example.
			// as long as you call pump() before using the graph. pump() is non
			// blocking.  If you only use the loop to look at event, use blockingPump()
			// to avoid 100% CPU usage. The blockingPump() method is only available from
			// the nightly builds.
		}
	}

	public void viewClosed(String id) {
		loop = false;
	}

	public void buttonPushed(String id) {
		System.out.println("Button pushed on node "+id);
	}

	public void buttonReleased(String id) {
		System.out.println("Button released on node "+id);
	}

	public void mouseOver(String id) {
		System.out.println("Need the Mouse Options to be activated");
	}

	public void mouseLeft(String id) {
		System.out.println("Need the Mouse Options to be activated");
	}
}