// This is a JFrame style tester view. Not used in the GUI implementation. Only for bug and feature testing
// Requires finishing the remainder of the tester driver for your purposes in order to use. Just a starting point if needed

package StegoDesktop;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DisplayPanel {

    private JFrame imgFrame;
    private JLabel imgDisp;
    private JScrollPane imgViewer;

    public DisplayPanel(BufferedImage imgToDisplay, String titleToUse) {            // Creates a general purpose JFrame to view images
        imgFrame = new JFrame();                                                    // When testing. All images are loaded into the same panel type
        imgDisp = new JLabel(new ImageIcon(imgToDisplay));                          // shown here. The current behavior is to exit the program when closed
        imgViewer = new JScrollPane(imgDisp);                                       // for convenience, but this can easily be changed
        imgFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                    // on this line to another built in functionality
        imgViewer.setPreferredSize(new Dimension(1000,600));            			// The size and location of the window can be
        imgFrame.setLocation(100,100);                                          	// edited on these two lines
        imgFrame.getContentPane().add(imgViewer);
        imgFrame.setTitle(titleToUse);
        imgFrame.pack();
        imgFrame.setVisible(true);
    }

    public void closePanel() { imgFrame.dispose(); }
}

