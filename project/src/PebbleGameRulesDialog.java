import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A simple modal dialog window to display the rules of the k-Pebble Game,
 * using JTextPane to allow styled text (e.g., bolding).
 */
public class PebbleGameRulesDialog extends JDialog {

    // Changed from JTextArea to JTextPane to support styled text
    private JTextPane rulesTextPane;
    private JScrollPane scrollPane;
    private JButton okButton;

    // Placeholder text for the k-Pebble Game rules, now using HTML for formatting.
    // ** Replace/modify this with your actual rules and desired formatting! **
    // Added <html><body> tags, <br> for newlines, and <b> for bold text.
    // Set a body width to help with initial wrapping.
    private static final String K_PEBBLE_GAME_RULES_HTML =
            "<html><body style='width: 350px'>" + // Set a body width for wrapping
            "The <b>k-Pebble Game</b> determines structural equivalence between two graphs (G and H) up to k moves.<br><br>" +
            "<b>Players:</b><br>" +
            "- <b>Spoiler</b>: Tries to show the graphs are different.<br>" +
            "- <b>Duplicator</b>: Tries to show the graphs are similar.<br><br>" +
            "<b>Setup:</b><br>" +
            "- k pairs of pebbles available.<br><br>" +
            "<b>Gameplay (Rounds):</b><br>" +
            "1. <b>Spoiler</b> places a pebble from an unused pair onto a vertex in either G or H.<br>" +
            "2. <b>Duplicator</b> must place the corresponding pebble from the same pair onto a vertex in the <i>other</i> graph.<br>" + // Italicized 'other'
            "3. If Spoiler placed pebble 'i' on vertex 'u' in G, Duplicator places pebble 'i' on vertex 'v' in H.<br><br>" +
            "<b>Winning Condition (Duplicator):</b><br>" +
            "- <b>Duplicator</b> wins if, after each move, the partial mapping defined by the pebbled vertices (e.g., (u1, v1), (u2, v2), ...) is a <b>partial isomorphism</b>. This means the local neighbourhoods around corresponding pebbled vertices look the same (considering previously pebbled neighbours).<br><br>" +
            "<b>Winning Condition (Spoiler):</b><br>" +
            "- <b>Spoiler</b> wins if <b>Duplicator</b> cannot place a pebble such that the <b>partial isomorphism</b> is maintained.<br>" +
            "- <b>Spoiler</b> also wins if they manage to pebble two distinct vertices u, u' in one graph, but <b>Duplicator</b> is forced to pebble the <i>same</i> vertex v=v' in the other graph.<br><br>" + // Italicized 'same'
            "<b>Goal:</b> If <b>Duplicator</b> has a winning strategy for k pebbles over a certain number of rounds, the graphs are considered <b>k-equivalent</b>." +
            "</body></html>";


    /**
     * Creates a new Pebble Game Rules dialog.
     * @param owner The Frame or Dialog from which the dialog is displayed.
     */
    public PebbleGameRulesDialog(Window owner) {
        super(owner, "k-Pebble Game Rules", ModalityType.APPLICATION_MODAL); // Modal dialog
        initComponents();
        layoutComponents();
        addEventHandlers();

        pack(); // Size the dialog based on its components
        setResizable(false); // Optional: Prevent resizing
        setLocationRelativeTo(owner); // Center relative to the owner window
    }

    private void initComponents() {
        // Create the JTextPane for the rules
        rulesTextPane = new JTextPane();
        rulesTextPane.setContentType("text/html"); // Set content type to handle HTML
        rulesTextPane.setText(K_PEBBLE_GAME_RULES_HTML); // Set the HTML content
        rulesTextPane.setEditable(false); // User cannot edit the rules
        // Set background to match default panel background for Look&Feel consistency
        rulesTextPane.setBackground(UIManager.getColor("Panel.background"));
        // Margin is handled by HTML body style or CSS potentially, but can set here too
        rulesTextPane.setMargin(new Insets(10, 10, 10, 10));
        // Line wrapping is handled by HTML/CSS width

        // Put the text pane in a scroll pane
        scrollPane = new JScrollPane(rulesTextPane);
        // Set a preferred size for the scroll pane to guide the 'pack()' method
        scrollPane.setPreferredSize(new Dimension(450, 300));

        // Create the OK button
        okButton = new JButton("OK");
    }

    private void layoutComponents() {
        // Use BorderLayout for overall structure
        setLayout(new BorderLayout(10, 10)); // Add gaps between components

        // Add the scroll pane (containing the text pane) to the center
        add(scrollPane, BorderLayout.CENTER);

        // Create a small panel for the button to add padding and control its position
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center the button
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Add bottom padding
        buttonPanel.add(okButton);

        // Add the button panel to the bottom
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addEventHandlers() {
        // Add action listener to the OK button to close the dialog
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the dialog window
            }
        });
    }

    /*
     * Example Usage is the same as before.
     */
}
