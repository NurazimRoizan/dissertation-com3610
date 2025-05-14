import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InitialDialog extends JDialog {

    // Components for the first option
    private JLabel headingLabel1;
    private JLabel imageLabel1; 
    private JLabel descriptionLabel1;
    private JButton button1;

    // Components for the second option
    private JLabel headingLabel2;
    private JLabel imageLabel2; 
    private JLabel descriptionLabel2;
    private JButton button2;

    private JPanel contentPane;
    // Field to store the user's choice
    private boolean optionCRSelected = false; // Default to false

    // Define a width for the description text wrapping
    private static final int DESCRIPTION_WIDTH = 200; 
    private static final float HEADING_FONT_SIZE_INCREASE = 4f; 
    private static final int MAX_IMAGE_HEIGHT = 250;

    public InitialDialog(JFrame parent) {
        super(parent, "Welcome", true);
        initComponents();
        layoutComponents();
        addEventHandlers();

        setMaximumSize(new Dimension(1000, 1000));
    }

    private void initComponents() {
        contentPane = new JPanel();
        contentPane.setBackground(Color.decode("#7DA2C1"));
        // --- Image Initialization ---
        String imagePath1 = "ImagePebbleGame.png"; 
        String imagePath2 = "ImageColourRefine.png"; 

        ImageIcon icon1 = createImageIcon(imagePath1, "Icon for Option 1");
        ImageIcon icon2 = createImageIcon(imagePath2, "Icon for Option 2");

        imageLabel1 = new JLabel(icon1);
        if (icon1 == null) {
            imageLabel1.setText("Image 1 not found"); // Placeholder text if image fails
        }


        imageLabel2 = new JLabel(icon2);
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

            // Create scaled image instance
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
        gbc.insets = new Insets(5, 15, 5, 15);
        gbc.weightx = 0.5;

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
        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.fill = GridBagConstraints.NONE;   
        contentPane.add(imageLabel1, gbc);

        // Row 2: Description 1
        gbc.gridy = 2;
        gbc.weighty = 0.5; 
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        contentPane.add(descriptionLabel1, gbc);

        // Row 3: Button 1
        gbc.gridy = 3;
        gbc.weighty = 0; 
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 15, 10, 15); 
        contentPane.add(button1, gbc);


        // --- Column 2: Option 2 ---
        gbc.insets = new Insets(5, 15, 5, 15); 

        // Row 0: Heading 2
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(headingLabel2, gbc);

        // Row 1: Image 2
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.fill = GridBagConstraints.NONE;     
        contentPane.add(imageLabel2, gbc);

        // Row 2: Description 2
        gbc.gridy = 2;
        gbc.weighty = 0.5; 
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        contentPane.add(descriptionLabel2, gbc);

        // Row 3: Button 2
        gbc.gridy = 3;
        gbc.weighty = 0; 
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 15, 10, 15); 
        contentPane.add(button2, gbc);

        setContentPane(contentPane);
        pack();
        setLocationRelativeTo(getParent()); 
    }

    private void addEventHandlers() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionCRSelected = false; 
                dispose(); 
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
     * Call this method *after* the dialog has been closed
     * to check which option the user selected.
     *
     * @return true if Option 1 was selected, false otherwise (Option 2 or closed).
     */
    public boolean isOptionCRSelected() {
        return optionCRSelected;
    }
}
