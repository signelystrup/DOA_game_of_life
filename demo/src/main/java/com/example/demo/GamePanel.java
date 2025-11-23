package com.example.demo;

import com.example.demo.entities.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable{

    private Thread gameThread;
    private final int FPS = 60;

    //dirt sprite
    private BufferedImage backgroundSprite;

    private Grid grid;
    private GrassManager grassManager;
    private List<Bunny> bunnies = new ArrayList<>();
    private List<Wolf> wolves = new ArrayList<>();
    private List <FenceManager> fenceManagers = new ArrayList<>();

    private List<Heart> hearts = new ArrayList<>();
    private Random random = new Random();

    // Performance metrics tracking - accumulate over entire game session
    private VisionMetrics totalBunnyMetrics = new VisionMetrics();
    private VisionMetrics totalWolfMetrics = new VisionMetrics();
    private long gameStartTime = 0;
    private int totalFrames = 0;

    public GamePanel(){
        //screen settings
        this.setPreferredSize(new Dimension(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT));
        this.setBackground(Color.PINK);
        this.setDoubleBuffered(true); //improve rendering performance.

        loadSprite();
    }

    public void setUpGame(int bunnyCount, int wolfCount, int grassCount, int fenceCount){
        //init grid and entities here.
        // Cell size = max(vision ranges) to ensure findNearby() works for all species
        grid = new Grid(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, GameConfig.getGridCellSize());
        grassManager = new GrassManager(grid);

        //fences: create random fences with random lengths;
        for (int i = 0; i < fenceCount; i++) {
            int randomLength = random.nextInt(5, 15);  // Random length between 5-14 segments
            fenceManagers.add(new FenceManager(randomLength));

            for (int j = 0; j < randomLength; j++){
                Fence segment = fenceManagers.get(i).getSegments()[j];
                grid.insert(segment, segment.getStartX(), segment.getStartY());
            }//inner loop
        }//outer loop.

        //bunnies: random placement
        for (int i = 0 ; i < bunnyCount ; i ++){
            int randomX = random.nextInt(GameConfig.WORLD_WIDTH);
            int randomY = random.nextInt(GameConfig.WORLD_HEIGHT);
            Bunny bunny = new Bunny(randomX, randomY);
            bunnies.add(bunny);
            grid.insert(bunnies.get(i), bunnies.get(i).getWorldX(), bunnies.get(i).getWorldY());
        }

        //wolves: random placement
        for (int i = 0 ; i < wolfCount ; i ++){
            int randomX = random.nextInt(GameConfig.WORLD_WIDTH);
            int randomY = random.nextInt(GameConfig.WORLD_HEIGHT);
            Wolf wolf = new Wolf(randomX, randomY);
            wolves.add(wolf);
            grid.insert(wolves.get(i), wolves.get(i).getWorldX(), wolves.get(i).getWorldY());
        }

        //grass: random placement
        for (int i = 0 ; i < grassCount ; i ++){
            int randomX = random.nextInt(GameConfig.WORLD_WIDTH);
            int randomY = random.nextInt(GameConfig.WORLD_HEIGHT);
            Grass grass = new Grass(randomX, randomY);
            grid.insert(grass, randomX, randomY);
        }
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start(); //calls run method.
        gameStartTime = System.currentTimeMillis();
        totalFrames = 0;
        totalBunnyMetrics.reset();
        totalWolfMetrics.reset();
        System.out.println("game start");
    }

    /**
     * Stop the game thread gracefully
     */
    public void stopGameThread() {
        Thread threadToStop = gameThread;
        gameThread = null;  // This will cause run() loop to exit

        // Wait for thread to finish
        if (threadToStop != null) {
            try {
                threadToStop.join(1000);  // Wait up to 1 second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void run(){
        double drawInterval = 1000000000 / FPS; //1/60 second.
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){
            currentTime = System.nanoTime(); //get current time
            delta += (currentTime - lastTime) / drawInterval; //time passed / 1/60 second.

            lastTime = currentTime; //set new start for interval.

            //if delta is bigger than 1, 1/60 second has passed.
            if (delta >= 1) {
                update();
                repaint();

                delta--; //timing
            }

        }//end of while loop.
    }

    public void update(){
        //game logic here
        if (grassManager != null) {
            grassManager.update();
        }

        // Update all bunnies: reset, move, accumulate (safe index-based loop)
        for(int i = 0; i < bunnies.size(); i++){
            Bunny bunny = bunnies.get(i);
            bunny.metrics.reset();
            moveEntity(bunny);
            totalBunnyMetrics.add(bunny.metrics);
        }

        // Update all wolves: reset, move, accumulate (safe index-based loop)
        for(int i = 0; i < wolves.size(); i++) {
            Wolf wolf = wolves.get(i);
            wolf.metrics.reset();
            moveEntity(wolf);
            totalWolfMetrics.add(wolf.metrics);
        }

        // Check for animal eating (generic for all animals)
        handleAnimalEating(bunnies);  // Bunnies eat grass
        handleAnimalEating(wolves);   // Wolves eat bunnies

        // Check for animal breeding (generic for all animals)
        handleAnimalBreeding(bunnies);
        handleAnimalBreeding(wolves);

        totalFrames++;
    }
    private void moveEntity(Animal animal) {
      // Remove from old grid position
      grid.remove(animal, animal.getWorldX(), animal.getWorldY());
      // Update movement
      animal.update(grid);
      // Insert into new grid position
      grid.insert(animal, animal.getWorldX(), animal.getWorldY());
    }

    /**
     * GENERIC: Check if animals can eat their prey
     * Works for any animal type - each animal defines what it can eat
     */
    private <T extends Animal> void handleAnimalEating(List<T> predators) {
        List<Object> preyToRemove = new ArrayList<>();

        // Check each predator against nearby entities
        for (T predator : predators) {
            if (predator.hasEaten()) continue; // Skip if already eaten

            // Find nearby entities
            int searchRadius = GameConfig.getSearchRadius(predator.getClass());
            List<Object> nearby = grid.findNearby(predator.getWorldX(), predator.getWorldY(), searchRadius);
            
            for (Object entity : nearby) {
                // Check if this predator can eat this entity
                if (predator.canEat(entity)) {
                    // Check collision (close enough to eat)
                    double dx = predator.getWorldX() - getEntityX(entity);
                    double dy = predator.getWorldY() - getEntityY(entity);
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance < predator.getEatingRange()) {
                        predator.eat(entity);
                        preyToRemove.add(entity);
                        break; // One prey per frame
                    }
                }
            }
        }

        // Remove eaten prey from grid and lists
        for (Object prey : preyToRemove) {
            if (prey instanceof Grass) {
                grid.remove(prey, ((Grass)prey).getWorldX(), ((Grass)prey).getWorldY());
            } else if (prey instanceof Bunny) {
                Bunny bunny = (Bunny) prey;
                grid.remove(bunny, bunny.getWorldX(), bunny.getWorldY());
                bunnies.remove(bunny);
            }
        }
    }

    /**
     * GENERIC: Check if animals can breed with each other
     * Works for any animal type - each animal defines breeding rules
     */
    private <T extends Animal> void handleAnimalBreeding(List<T> animals) {
        List<T> newAnimals = new ArrayList<>();

        // Check each pair of animals
        for (int i = 0; i < animals.size(); i++) {
            T animal1 = animals.get(i);
            if (!animal1.hasEaten()) continue;

            for (int j = i + 1; j < animals.size(); j++) {
                T animal2 = animals.get(j);

                // Check if they can breed
                if (animal1.canBreedWith(animal2)) {
                    // Create baby at midpoint between parents
                    int babyX = (animal1.getWorldX() + animal2.getWorldX()) / 2;
                    int babyY = (animal1.getWorldY() + animal2.getWorldY()) / 2;

                    // Create new instance of the same type
                    T baby = createAnimal(animal1.getClass(), babyX, babyY);
                    if (baby != null) {
                        newAnimals.add(baby);
                    }

                    Heart heart = new Heart (babyX, babyY); //display heart.
                    hearts.add(heart);

                    // Reset parents after breeding
                    animal1.resetAfterBreeding();
                    animal2.resetAfterBreeding();

                    break; // Each animal can only breed once per cycle
                }
            }
        }

        // Add new animals to the game
        for (T baby : newAnimals) {
            animals.add(baby);
            grid.insert(baby, baby.getWorldX(), baby.getWorldY());
        }
    }

    /**
     * Helper: Get X position of any entity
     */
    private int getEntityX(Object entity) {
        if (entity instanceof Animal) return ((Animal)entity).getWorldX();
        if (entity instanceof Grass) return ((Grass)entity).getWorldX();
        return 0;
    }

    /**
     * Helper: Get Y position of any entity
     */
    private int getEntityY(Object entity) {
        if (entity instanceof Animal) return ((Animal)entity).getWorldY();
        if (entity instanceof Grass) return ((Grass)entity).getWorldY();
        return 0;
    }

    /**
     * Helper: Create a new animal instance of the given type
     */
    @SuppressWarnings("unchecked")
    private <T extends Animal> T createAnimal(Class<?> animalClass, int x, int y) {
        try {
            if (animalClass == Bunny.class) {
                return (T) new Bunny(x, y);
            } else if (animalClass == Wolf.class) {
                return (T) new Wolf(x, y);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; //get graphics as Graphics2D

        //background
        drawBackground(g2);

        //fences: draw first (foreground layer)
        for(int i = 0; i < fenceManagers.size(); i ++){
            fenceManagers.get(i).draw(g2);
        }

        // Draw all entities from grid (only if grid is initialized)
        if (grid == null) {
            g2.dispose();
            return;
        }

        List<Object> allEntities = grid.getAllEntities();

        //grass, wolves, bunnies
        for (int i = 0; i < allEntities.size(); i++) {
            Object obj = allEntities.get(i);
            if (obj instanceof Grass) {
                ((Grass)obj).draw(g2);
            } else if (obj instanceof Bunny) {
                ((Bunny)obj).draw(g2);
            } else if (obj instanceof Wolf) {
                ((Wolf)obj).draw(g2);
            }
        }

        //draw hearts
        for (int i = hearts.size() -1 ; i >= 0 ; i --){
            if (hearts.get(i).getHeartTimer() == 0){
                hearts.remove(i);
            }else {
                hearts.get(i).draw(g2);
            }
        }

        g2.dispose(); //good practice, Saves memory. (program still works without this line)

    }

    /**
     * Add a single wolf at random empty position
     */
    public void addWolf() {
        if (grid == null) return;

        int maxAttempts = 20;
        for (int i = 0; i < maxAttempts; i++) {
            int randomX = random.nextInt(GameConfig.WORLD_WIDTH);
            int randomY = random.nextInt(GameConfig.WORLD_HEIGHT);

            if (grid.isEmpty(randomX, randomY)) {
                Wolf newWolf = new Wolf(randomX, randomY);
                wolves.add(newWolf);
                grid.insert(newWolf, newWolf.getWorldX(), newWolf.getWorldY());
                return;
            }
        }
    }

    /**
     * Add a single bunny at random empty position
     */
    public void addBunny() {
        if (grid == null) return;

        int maxAttempts = 20;
        for (int i = 0; i < maxAttempts; i++) {
            int randomX = random.nextInt(GameConfig.WORLD_WIDTH);
            int randomY = random.nextInt(GameConfig.WORLD_HEIGHT);

            if (grid.isEmpty(randomX, randomY)) {
                Bunny newBunny = new Bunny(randomX, randomY);
                bunnies.add(newBunny);
                grid.insert(newBunny, newBunny.getWorldX(), newBunny.getWorldY());
                return;
            }
        }
    }

    /**
     * Add a single grass at random empty position
     */
    public void addGrass() {
        if (grid == null) return;

        int maxAttempts = 20;
        for (int i = 0; i < maxAttempts; i++) {
            int randomX = random.nextInt(GameConfig.WORLD_WIDTH);
            int randomY = random.nextInt(GameConfig.WORLD_HEIGHT);

            if (grid.isEmpty(randomX, randomY)) {
                Grass newGrass = new Grass(randomX, randomY);
                grid.insert(newGrass, newGrass.getWorldX(), newGrass.getWorldY());
                return;
            }
        }
    }

    /**
     * Add a single fence with random length
     */
    public void addFence() {
        int randomLength = random.nextInt(5, 15);
        FenceManager newFenceManager = new FenceManager(randomLength);
        fenceManagers.add(newFenceManager);

        for (int i = 0; i < randomLength; i++){
            Fence segment = fenceManagers.get(fenceManagers.size() - 1).getSegments()[i];
            grid.insert(segment, segment.getStartX(), segment.getStartY());
        }

    }

    /**
     * Reset game to initial state
     * Prints final metrics before resetting
     */
    public void resetGame(int bunnyCount, int wolfCount, int grassCount, int fenceCount) {
        // Print metrics from previous session
        printFinalMetrics();

        // Clear and reset
        if (grid != null) {
            grid.clear();
        }
        bunnies.clear();
        wolves.clear();
        fenceManagers.clear();
        setUpGame(bunnyCount, wolfCount, grassCount, fenceCount);

        // Reset metrics for new session
        gameStartTime = System.currentTimeMillis();
        totalFrames = 0;
        totalBunnyMetrics.reset();
        totalWolfMetrics.reset();
    }

    /**
     * Check if game thread is running
     */
    public boolean isGameRunning() {
        return gameThread != null;
    }

    /**
     * Print final performance metrics for entire game session
     */
    public void printFinalMetrics() {
        if (gameStartTime == 0) {
            System.out.println("No metrics to display - game hasn't started yet.");
            return;
        }

        long duration = System.currentTimeMillis() - gameStartTime;
        double durationSeconds = duration / 1000.0;

        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println(  "║          FINAL VISION PERFORMANCE METRICS                      ║");
        System.out.println(  "╚════════════════════════════════════════════════════════════════╝");
        System.out.println("\nStrategy: " + GameConfig.STRATEGY);
        System.out.println("Description: " + GameConfig.getStrategyDescription());
        System.out.println("Cell Size: " + GameConfig.getGridCellSize() + "px");
        System.out.println("\nSession Duration: " + String.format("%.1f", durationSeconds) + " seconds");
        System.out.println("Total Frames: " + totalFrames);
        System.out.println("Average FPS: " + String.format("%.1f", totalFrames / durationSeconds));

        System.out.println("\n─────────────────────────────────────────────────────────────────");
        System.out.println("BUNNIES (count: " + bunnies.size() + ")");
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println(totalBunnyMetrics);
        System.out.println("Average per frame: " +
            String.format("%.1f fetched, %.1f in vision, %.1f wasted",
                totalBunnyMetrics.getEntitiesFetched() / (double)totalFrames,
                totalBunnyMetrics.getEntitiesInVision() / (double)totalFrames,
                totalBunnyMetrics.getWastedEntities() / (double)totalFrames));

        System.out.println("\n─────────────────────────────────────────────────────────────────");
        System.out.println("WOLVES (count: " + wolves.size() + ")");
        System.out.println("─────────────────────────────────────────────────────────────────");
        System.out.println(totalWolfMetrics);
        System.out.println("Average per frame: " +
            String.format("%.1f fetched, %.1f in vision, %.1f wasted",
                totalWolfMetrics.getEntitiesFetched() / (double)totalFrames,
                totalWolfMetrics.getEntitiesInVision() / (double)totalFrames,
                totalWolfMetrics.getWastedEntities() / (double)totalFrames));

        System.out.println("\n═════════════════════════════════════════════════════════════════\n");
    }

    /**
     * Save benchmark results to CSV file
     * Appends to existing file or creates new one with headers
     */
    public void saveResultsToCSV() {
        String filename = "benchmark_results.csv";
        File file = new File(filename);
        boolean fileExists = file.exists();

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            // Write headers if file is new
            if (!fileExists) {
                writer.println("Strategy,Cell_Size,Duration_Sec,Total_Frames,Avg_FPS," +
                        "Bunny_Count,Bunny_Fetched,Bunny_InVision,Bunny_Wasted,Bunny_Efficiency,Bunny_Checks," +
                        "Wolf_Count,Wolf_Fetched,Wolf_InVision,Wolf_Wasted,Wolf_Efficiency,Wolf_Checks");
            }

            // Calculate values
            long duration = System.currentTimeMillis() - gameStartTime;
            double durationSeconds = duration / 1000.0;
            double avgFPS = totalFrames / durationSeconds;

            // Write data row
            writer.printf("%s,%d,%.1f,%d,%.1f,%d,%d,%d,%d,%.1f,%d,%d,%d,%d,%d,%.1f,%d%n",
                    GameConfig.STRATEGY,
                    GameConfig.getGridCellSize(),
                    durationSeconds,
                    totalFrames,
                    avgFPS,
                    bunnies.size(),
                    totalBunnyMetrics.getEntitiesFetched(),
                    totalBunnyMetrics.getEntitiesInVision(),
                    totalBunnyMetrics.getWastedEntities(),
                    totalBunnyMetrics.getEfficiencyPercent(),
                    totalBunnyMetrics.getVisionChecks(),
                    wolves.size(),
                    totalWolfMetrics.getEntitiesFetched(),
                    totalWolfMetrics.getEntitiesInVision(),
                    totalWolfMetrics.getWastedEntities(),
                    totalWolfMetrics.getEfficiencyPercent(),
                    totalWolfMetrics.getVisionChecks());

            System.out.println("✓ Results saved to " + filename);
        } catch (IOException e) {
            System.err.println("✗ Error saving to CSV: " + e.getMessage());
        }
    }

    public void drawBackground(Graphics2D g2){
        int ratio = backgroundSprite.getWidth() / backgroundSprite.getHeight(); //keep aspect ratio.
        int height = GameConfig.WORLD_HEIGHT ;
        int width = GameConfig.WORLD_WIDTH * ratio;


        g2.drawImage(backgroundSprite, 0, 0, width, height , null);
    }

    public void loadSprite(){
        if (backgroundSprite == null) {
            try {
                backgroundSprite = ImageIO.read(getClass().getResourceAsStream("/static/sprites/dirt.jpg"));
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
