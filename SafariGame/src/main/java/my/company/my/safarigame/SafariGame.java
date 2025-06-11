package my.company.my.safarigame;

import my.company.my.safarigame.model.SafariGameModel;
import my.company.my.safarigame.controller.GameController;
import my.company.my.safarigame.audio.SoundManager;

import javax.swing.SwingUtilities;

/**
 * Main class for the Safari Game application
 * Sets up the MVC architecture and launches the game
 */
public class SafariGame {
    
    /**
     * Main method that launches the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Initialize the sound manager
        SoundManager.getInstance();
        
        // Use Swing's event dispatch thread for UI components
        SwingUtilities.invokeLater(() -> {
            try {
                // Create the model
                SafariGameModel model = new SafariGameModel();
                
                // Create the controller with the model
                GameController controller = new GameController(model);
                
                // The controller will create the view and set up the connections
                // between components following the MVC pattern
                
                System.out.println("Safari Game initialized successfully!");
                
            } catch (Exception e) {
                System.err.println("Error initializing Safari Game: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}