package my.company.my.safarigame.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for loading safari game grid data from text files.
 * <p>
 * The GridLoader handles parsing and interpreting map data from either resource files
 * or local file system files. It creates and populates a Grid object with cells
 * based on character representations in the file, and adds corresponding landscape
 * objects to the provided SafariMap.
 * </p>
 * <p>
 * The loader recognizes various cell types represented by different characters in the map file:
 * </p>
 * <ul>
 *   <li>'w', 'a', 's', 'l', 'g', 't' - Different types of walls and gates</li>
 *   <li>'b' - Bush plant</li>
 *   <li>'p' - Generic plant</li>
 *   <li>'h' - Shrub plant</li>
 *   <li>'|' - Vertical road</li>
 *   <li>'r' - Horizontal road</li>
 *   <li>'1', '2', '3', '4' - Curved road sections (different orientations)</li>
 *   <li>'P' - Water area/pond</li>
 *   <li>'c' - Cow (Herbivore)</li>
 *   <li>'d' - Deer (Herbivore)</li>
 *   <li>'y' - Lion (Carnivore)</li>
 *   <li>'z' - Wolf (Carnivore)</li>
 *   <li>'R' - Ranger</li>
 *   <li>'j' - Jeep</li>
 *   <li>'-' - Normal grass (default)</li>
 * </ul>
 */
public class GridLoader {

    /**
     * Loads a grid from a file path that could be either a resource or local file.
     * <p>
     * This method attempts to read the specified file as either a local file system file
     * or as a resource file. It then parses the file contents to create a Grid object
     * and populates it with cells of appropriate types based on the characters in the file.
     * It also creates and adds corresponding landscape objects to the provided SafariMap.
     * </p>
     * <p>
     * The method expects the grid file to contain a 50x50 character grid, where each
     * character represents a specific cell type. If the grid dimensions do not match
     * 50x50, an IOException is thrown.
     * </p>
     *
     * @param filePath The path to the grid file (can be a local file system path or a resource path)
     * @param map The SafariMap instance to which landscape objects will be added
     * @return A fully initialized Grid object with cells based on the file contents
     * @throws IOException If the file cannot be read, is not found, is empty, or has incorrect dimensions
     */
    public static Grid loadGridFromFile(String filePath, SafariMap map) throws IOException {
        List<String> lines = new ArrayList<>();
        
        // Check if this is a local file path
        File localFile = new File(filePath);
        if (localFile.exists() && localFile.isFile()) {
            // Read from local file system
            try (BufferedReader reader = new BufferedReader(new FileReader(localFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
            System.out.println("Loaded grid from local file: " + filePath);
        } else {
            // Try to read as a resource
            try (InputStream is = GridLoader.class.getResourceAsStream(filePath); 
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
            System.out.println("Loaded grid from resource: " + filePath);
        }

        // If we couldn't read any lines, the file couldn't be found
        if (lines.isEmpty()) {
            throw new IOException("Grid file not found or empty: " + filePath);
        }

        // Calculate dimensions
        int rows = lines.size();
        int cols = lines.get(0).length();

        // Verify the grid is exactly 50x50
        if (rows != 50 || cols != 50) {
            throw new IOException("Grid must be exactly 50x50, but found " + rows + "x" + cols);
        }

        int cellSize = 48; // Use the same cell size as in MapView

        // Create grid
        Grid grid = new Grid(rows, cols, cellSize);

        // Fill grid with cell data
        for (int r = 0; r < rows; r++) {
            String line = lines.get(r);
            for (int c = 0; c < cols; c++) {
                if (c < line.length()) {
                    char cellType = line.charAt(c);
                    Cell cell = grid.getCell(r, c);

                    // Set cell properties based on character
                    switch (cellType) {
                        case 'w': // Regular wall
                        case 'a': // Corner wall (top)
                        case 's': // Corner wall (bottom)
                        case 'l': // Side wall
                        case 'g': // gate top
                        case 't': // gate bottom
                            // Mark as wall/occupied
                            cell.setOccupied(true);
                            cell.setCellType(cellType); // Preserve the actual character
                            break;
                        case 'b': // Bush
                            // Add a plant at this location
                            Plant bush = new Plant(
                                    new Coordinate(r, c),
                                    "bush", // You can customize plant species
                                    10.0 // Default price
                            );
                            map.addLandscapeObject(bush);
                            cell.setOccupied(true);
                            cell.setCellType(cellType);
                            break;
                        case 'p': // Plant
                            // Add a plant at this location
                            Plant plant = new Plant(
                                    new Coordinate(r, c),
                                    "plant", // You can customize plant species
                                    20.0 // Default price
                            );
                            map.addLandscapeObject(plant);
                            cell.setOccupied(true);
                            cell.setCellType(cellType);
                            break;
                        case 'h': // Shrub
                            // Add a plant at this location
                            Plant shrub = new Plant(
                                    new Coordinate(r, c),
                                    "shrub", // You can customize plant species
                                    30.0 // Default price
                            );
                            map.addLandscapeObject(shrub);
                            cell.setOccupied(true);
                            cell.setCellType(cellType);
                            break;
                        case '|': // Vertical Road
                            // Add a vertical road at this location
                            Road verticalRoad = new Road(
                                    new Coordinate(r, c),
                                    1.0, // Default length
                                    50.0, // Default price
                                    "vertical"
                            );
                            map.addLandscapeObject(verticalRoad);
                            cell.setOccupied(true);
                            cell.setCellType(cellType);
                            break;
                        case 'r': // Horizontal Road
                            // Add a horizontal road at this location
                            Road horizontalRoad = new Road(
                                    new Coordinate(r, c),
                                    1.0, // Default length
                                    50.0, // Default price
                                    "horizontal"
                            );
                            map.addLandscapeObject(horizontalRoad);
                            cell.setOccupied(true);
                            cell.setCellType(cellType);
                            break;
                        case '1': // Right-down curve road
                            Road rightDown = new Road(
                                    new Coordinate(r, c),
                                    1.0, // Default length
                                    50.0, // Default price
                                    "rightDown"
                            );
                            map.addLandscapeObject(rightDown);
                            cell.setOccupied(true);
                            cell.setCellType(cellType);
                            break;
                        case '2': // Right-up curve road
                            Road rightUp = new Road(
                                    new Coordinate(r, c),
                                    1.0, // Default length
                                    50.0, // Default price
                                    "rightUp"
                            );
                            map.addLandscapeObject(rightUp);
                            cell.setOccupied(true);
                            cell.setCellType(cellType);
                            break;
                        case '3': // Left-up curve road
                            Road leftUp = new Road(
                                    new Coordinate(r, c),
                                    1.0, // Default length
                                    50.0, // Default price
                                    "leftUp"
                            );
                            map.addLandscapeObject(leftUp);
                            cell.setOccupied(true);
                            cell.setCellType(cellType);
                            break;
                        case '4': // Left-down curve road
                            Road leftDown = new Road(
                                    new Coordinate(r, c),
                                    1.0, // Default length
                                    50.0, // Default price
                                    "leftDown"
                            );
                            map.addLandscapeObject(leftDown);
                            cell.setOccupied(true);
                            cell.setCellType(cellType);
                            break;
                        case 'P': // Water area/pond
                            WaterArea pond = new WaterArea(
                                new Coordinate(r, c), 100.0 // Use proper coordinates
                            );
                            map.addLandscapeObject(pond);
                            cell.setOccupied(true);
                            cell.setCellType(cellType);
                            break;
                        case 'c': // Cow (Herbivore)
                            Herbivore cow = new Herbivore(
                                new Coordinate(r, c),
                                "cow"
                            );
                            map.addLandscapeObject(cow);
                            cell.setOccupied(true);
                            cell.setCellType(cellType);
                            break;
                        case 'd': // Deer (Herbivore)
                            Herbivore deer = new Herbivore(
                                new Coordinate(r, c),
                                "deer"
                            );
                            map.addLandscapeObject(deer);
                            cell.setOccupied(true);
                            cell.setCellType(cellType);
                            break;
                        case 'y': // Lion (Carnivore)
                            Carnivore lion = new Carnivore(
                                new Coordinate(r, c),
                                "lion"
                            );
                            map.addLandscapeObject(lion);
                            cell.setOccupied(true);
                            cell.setCellType(cellType);
                            break;
                        case 'z': // Wolf (Carnivore)
                            Carnivore wolf = new Carnivore(
                                new Coordinate(r, c),
                                "wolf"
                            );
                            map.addLandscapeObject(wolf);
                            cell.setOccupied(true);
                            cell.setCellType(cellType);
                            break;
                        case 'R': // Ranger
                            Ranger ranger = new Ranger(
                                new Coordinate(r, c)
                            );
                            map.addRanger(ranger);
                            //map.addLandscapeObject(ranger);
                            cell.setOccupied(true);
                            cell.setCellType(cellType);
                            break;
                        case 'j': // Jeep
                            Jeep jeep = new Jeep(
                                new Coordinate(r, c)
                            );
                            map.addJeep(jeep);
                            //map.addLandscapeObject(jeep);
                            cell.setOccupied(true);
                            cell.setCellType(cellType);
                            break;
                        case '-': // Normal grass
                        default:
                            // Not occupied
                            cell.setOccupied(false);

                            // If it's a valid character we know about, keep it
                            if (cellType == '-') {
                                cell.setCellType('-');
                            } else {
                                // For any unknown character, we'll still store it for rendering
                                // but not mark as occupied
                                cell.setCellType(cellType);
                                System.out.println("Found unknown cell type: " + cellType + " at " + r + "," + c);
                            }
                            break;
                    }
                }
            }
        }

        return grid;
    }
}