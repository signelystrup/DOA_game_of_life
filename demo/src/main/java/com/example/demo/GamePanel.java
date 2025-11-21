package com.example.demo;

import com.example.demo.entities.Bunny;
import com.example.demo.entities.Fence;
import com.example.demo.entities.Grass;
import com.example.demo.entities.Wolf;
import com.example.demo.entities.Animal;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
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
    private Fence[] fences = new Fence[0];
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
        grid = new Grid(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, GameConfig.GRID_CELL_SIZE);
        grassManager = new GrassManager(grid);

        //fences: create random fences with random lengths
        fences = new Fence[fenceCount];
        for (int i = 0; i < fenceCount; i++) {
            int randomLength = random.nextInt(5, 15);  // Random length between 5-14 segments
            fences[i] = new Fence(randomLength);
        }

        //bunnies: random placement
        //bunnies = new Bunny[bunnyCount];
        for (int i = 0 ; i < bunnyCount ; i ++){
            int randomX = random.nextInt(GameConfig.WORLD_WIDTH);
            int randomY = random.nextInt(GameConfig.WORLD_HEIGHT);
            Bunny bunny = new Bunny(randomX, randomY);
            bunnies.add(bunny);
            grid.insert(bunnies.get(i), bunnies.get(i).getWorldX(), bunnies.get(i).getWorldY());
        }

        //wolves: random placement
        //wolves = new Wolf[wolfCount];
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


    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; //get graphics as Graphics2D

        //background
        drawBackground(g2);

        //fences: draw first (foreground layer)
        for(int i = 0; i < fences.length; i ++){
            if (fences[i] != null){
                fences[i].draw(g2);
            }
        }

        // Draw all entities from grid (only if grid is initialized)
        if (grid == null) {
            g2.dispose();
            return;
        }

        List<Object> allEntities = grid.getAllEntities();

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
        Fence newFence = new Fence(randomLength);

        // Expand fences array
        Fence[] newFences = new Fence[fences.length + 1];
        System.arraycopy(fences, 0, newFences, 0, fences.length);
        newFences[fences.length] = newFence;
        fences = newFences;
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
        System.out.println("║          FINAL VISION PERFORMANCE METRICS                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println("\nStrategy: " + GameConfig.STRATEGY);
        System.out.println("Description: " + GameConfig.getStrategyDescription());
        System.out.println("Cell Size: " + GameConfig.GRID_CELL_SIZE + "px");
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

    public void drawBackground(Graphics2D g2){
        g2.drawImage(backgroundSprite, 0, 0, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, null);
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
