import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InitialDialog extends JDialog {

    // Removed the single top imageLabel

    // Components for the first option
    private JLabel headingLabel1;
    private JLabel imageLabel1; // Label for the first image
    private JLabel descriptionLabel1;
    private JButton button1;

    // Components for the second option
    private JLabel headingLabel2;
    private JLabel imageLabel2; // Label for the second image
    private JLabel descriptionLabel2;
    private JButton button2;

    private JPanel contentPane;
    // Field to store the user's choice
    private boolean optionCRSelected = false; // Default to false (Option 2 or closed)

    // Define a width for the description text wrapping
    private static final int DESCRIPTION_WIDTH = 200; // pixels
    private static final float HEADING_FONT_SIZE_INCREASE = 4f; // Increase heading font size by 4 points
    private static final int MAX_IMAGE_HEIGHT = 250;
    // Removed IMAGE_SIZE constant

    public InitialDialog(JFrame parent) {
        super(parent, "Welcome", true);
        initComponents();
        layoutComponents();
        addEventHandlers();

        // Set a maximum size for the dialog
        // Consider adjusting max height based on your image sizes
        setMaximumSize(new Dimension(600, 600));
    }

    private void initComponents() {
        contentPane = new JPanel();
        contentPane.setBackground(Color.decode("#7DA2C1")); // Added background color
        // --- Image Initialization ---
        // ** IMPORTANT: Replace these placeholder paths with the actual paths to your images! **
        String imagePath1 = "ImagePebbleGame.png"; // Placeholder path 1
        String imagePath2 = "ImageColourRefine.png"; // Placeholder path 2

        ImageIcon icon1 = createImageIcon(imagePath1, "Icon for Option 1");
        ImageIcon icon2 = createImageIcon(imagePath2, "Icon for Option 2");

        // Create JLabels for the images
        imageLabel1 = new JLabel(icon1);
        // Set a preferred size if your images are large and you want to scale them
        // imageLabel1.setPreferredSize(new Dimension(100, 100)); // Example size
        if (icon1 == null) {
            imageLabel1.setText("Image 1 not found"); // Placeholder text if image fails
        }


        imageLabel2 = new JLabel(icon2);
        // imageLabel2.setPreferredSize(new Dimension(100, 100)); // Example size
         if (icon2 == null) {
            imageLabel2.setText("Image 2 not found"); // Placeholder text if image fails
        }
        // --- End Image Initialization ---


        // Initialize components for the first option
        headingLabel1 = new JLabel("Pebble Game");
        Font baseFont1 = headingLabel1.getFont();
        headingLabel1.setFont(baseFont1.deriveFont(Font.BOLD, baseFont1.getSize() + HEADING_FONT_SIZE_INCREASE));
        headingLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        headingLabel1.setOpaque(false);

        String desc1Text = "Play the k-Pebble Game to test graph partial-isomorphism. This implementation uses colour refinement  variant to help guide the Duplicator's moves for placing pebbles.";
        descriptionLabel1 = new JLabel(String.format("<html><body style='width: %dpx'>%s</body></html>", DESCRIPTION_WIDTH, desc1Text));
        descriptionLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        descriptionLabel1.setOpaque(false);
        button1 = new JButton("Play Pebble Game");

        // Initialize components for the second option
        headingLabel2 = new JLabel("Colour Refinement");
        Font baseFont2 = headingLabel2.getFont();
        headingLabel2.setFont(baseFont2.deriveFont(Font.BOLD, baseFont2.getSize() + HEADING_FONT_SIZE_INCREASE));
        headingLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        headingLabel2.setOpaque(false);

        String desc2Text = "See the Colour Refinement algorithm in action. This technique iteratively distinguishes graph vertices based on neighbour colours.";
        descriptionLabel2 = new JLabel(String.format("<html><body style='width: %dpx'>%s</body></html>", DESCRIPTION_WIDTH, desc2Text));
        descriptionLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        descriptionLabel2.setOpaque(false);
        button2 = new JButton("Start Visualization");

    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    // Helper method to load images and handle potential errors
    protected ImageIcon createImageIcon(String path, String description) {
        ImageIcon originalIcon = null;
        java.io.File imgFile = new java.io.File(path);

        // Try loading from file system or classpath
        if (imgFile.exists()) {
             originalIcon = new ImageIcon(path, description);
        } else {
             java.net.URL imgURL = getClass().getResource(path);
             if (imgURL != null) {
                 originalIcon = new ImageIcon(imgURL, description);
             } else {
                 System.err.println("Couldn't find file: " + path);
                 return null; // Image not found
             }
        }

        // Check if scaling is needed
        if (originalIcon != null && originalIcon.getIconHeight() > MAX_IMAGE_HEIGHT) {
            Image originalImage = originalIcon.getImage();
            int originalHeight = originalIcon.getIconHeight();
            int originalWidth = originalIcon.getIconWidth();

            // Calculate new width to maintain aspect ratio
            double scaleRatio = (double) MAX_IMAGE_HEIGHT / (double) originalHeight;
            int newWidth = (int) (originalWidth * scaleRatio);

            // Create scaled image instance (use SCALE_SMOOTH for better quality)
            Image scaledImage = originalImage.getScaledInstance(newWidth, MAX_IMAGE_HEIGHT, Image.SCALE_SMOOTH);

            // Return a new ImageIcon based on the scaled image
            return new ImageIcon(scaledImage, description);
        } else {
            // Return the original icon if no scaling is needed or if it failed to load
            return originalIcon;
        }
    }


    private void layoutComponents() {
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        // General padding - adjust as needed
        gbc.insets = new Insets(5, 15, 5, 15);
        gbc.weightx = 0.5; // Give columns equal horizontal weight by default


        // --- Column 1: Option 1 ---

        // Row 0: Heading 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1; // Reset gridwidth
        gbc.weighty = 0;   // Reset weighty
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(headingLabel1, gbc);

        // Row 1: Image 1
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER; // Center the image
        gbc.fill = GridBagConstraints.NONE;     // Don't resize image component
        contentPane.add(imageLabel1, gbc);

        // Row 2: Description 1
        gbc.gridy = 2;
        gbc.weighty = 0.5; // Give description vertical weight
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill description horizontally
        contentPane.add(descriptionLabel1, gbc);

        // Row 3: Button 1
        gbc.gridy = 3;
        gbc.weighty = 0; // Reset weighty for button
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 15, 10, 15); // Bottom padding for button row
        contentPane.add(button1, gbc);


        // --- Column 2: Option 2 ---
        gbc.insets = new Insets(5, 15, 5, 15); // Reset insets for column 2

        // Row 0: Heading 2
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(headingLabel2, gbc);

        // Row 1: Image 2
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER; // Center the image
        gbc.fill = GridBagConstraints.NONE;     // Don't resize image component
        contentPane.add(imageLabel2, gbc);

        // Row 2: Description 2
        gbc.gridy = 2;
        gbc.weighty = 0.5; // Give description vertical weight
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill description horizontally
        contentPane.add(descriptionLabel2, gbc);

        // Row 3: Button 2
        gbc.gridy = 3;
        gbc.weighty = 0; // Reset weighty for button
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 15, 10, 15); // Bottom padding for button row
        contentPane.add(button2, gbc);


        // Set the content pane
        setContentPane(contentPane);

        // Pack the dialog
        pack();

        setLocationRelativeTo(getParent()); // Center the dialog
    }

    private void addEventHandlers() {
        // Action listeners remain the same
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionCRSelected = false; 
                dispose(); // Close the dialog
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionCRSelected = true;
                dispose();
            }
        });
    }

    /**
     * Call this method *after* the dialog has been closed (i.e., after setVisible(true) returns)
     * to check which option the user selected.
     *
     * @return true if Option 1 was selected, false otherwise (Option 2 or closed).
     */
    public boolean isOptionCRSelected() {
        return optionCRSelected;
    }
}
