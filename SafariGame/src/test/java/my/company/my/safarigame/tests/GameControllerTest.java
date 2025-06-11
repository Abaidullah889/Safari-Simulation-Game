package my.company.my.safarigame.tests;

import my.company.my.safarigame.model.SafariGameModel;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.swing.JFrame;
import my.company.my.safarigame.controller.GameController;

public class GameControllerTest {

    private GameController controller;
    private boolean isHeadless;

    @Before
    public void setUp() {
        isHeadless = java.awt.GraphicsEnvironment.isHeadless();
        if (isHeadless) {
            System.out.println("Running in headless environment...");
            // Skip controller initialization or use a mock/special constructor for headless mode
            controller = createHeadlessController();
        } else {
            controller = new GameController(new SafariGameModel());
        }
    }

    private GameController createHeadlessController() {
        // Create a special version of the controller that doesn't initialize GUI components
        // This is a simple example - you may need to adjust based on your GameController implementation
        SafariGameModel model = new SafariGameModel();
        try {
            // Assuming there's a way to create the controller without GUI initialization
            // If there's no such way, you might need to modify your GameController class
            return new GameController(model);
        } catch (Exception e) {
            // If still fails, return a mock or partially initialized controller
            System.out.println("Using minimal controller for headless tests");
            return new MinimalGameController(model);
        }
    }

    // This is a minimal implementation that can be used for headless testing
    // You might need to implement this class in your project
    private static class MinimalGameController extends GameController {
        public MinimalGameController(SafariGameModel model) {
            super(model);
            // Override any GUI initialization that might happen in the parent constructor
        }
        
        // Override methods that interact with GUI if needed
    }

    @After
    public void tearDown() {
        controller = null;
    }

    @Test
    public void testGetModel() {
        System.out.println("Running testGetModel...");
        SafariGameModel model = controller.getModel();
        assertNotNull("Model should not be null", model);
    }

    @Test
    public void testSetAndGetMainFrame() {
        if (isHeadless) {
            System.out.println("Skipping testSetAndGetMainFrame in headless environment...");
            return; // Skip the test
        }
        
        System.out.println("Running testSetAndGetMainFrame...");
        JFrame frame = new JFrame();
        controller.setMainFrame(frame);
        assertEquals("Main frame should match", frame, controller.getMainFrame());
    }

    @Test
    public void testUpdateSettings() {
        System.out.println("Running testUpdateSettings...");
        // Skip or modify the test if it requires GUI components
        if (isHeadless && requiresGuiComponents()) {
            System.out.println("Skipping GUI-dependent test in headless environment...");
            return;
        }
        
        controller.updateSettings(50, 3);
        // No exception = Pass ✅
    }
    
    private boolean requiresGuiComponents() {
        // Return true if the updateSettings method interacts with GUI components
        // This is a placeholder - you need to implement based on your actual code
        return false;
    }
}