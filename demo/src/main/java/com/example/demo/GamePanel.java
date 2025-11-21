package com.example.demo;

import com.example.demo.entities.*;

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
    private List <FenceManager> fenceManagers = new ArrayList<>();

    private Random random = new Random();

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

        //fences: create random fences with random lengths;
        for (int i = 0; i < fenceCount; i++) {
            int randomLength = random.nextInt(5, 15);  // Random length between 5-14 segments
            fenceManagers.add (new FenceManager(randomLength));

            for (int j = 0; j < randomLength; j++){
                Fence segment = fenceManagers.get(i).getSegments()[j];
                grid.insert(segment, segment.getStartX(), segment.getStartY());
                System.out.println("added fence to grid"); //debug. remove
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
        //bunny.update();
         // Update all bunnies
        for(int i = 0; i < bunnies.size(); i++){
        //for (Bunny bunny : bunnies) {
            moveEntity(bunnies.get(i));
        }

        // Update all wolves
        for(int i = 0; i < wolves.size(); i++) {
            moveEntity(wolves.get(i));
        }


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
        for(int i = 0; i < fenceManagers.size(); i ++){
            fenceManagers.get(i).draw(g2);
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
        FenceManager newFenceManager = new FenceManager(randomLength);
        fenceManagers.add(newFenceManager);

        for (int i = 0; i < randomLength; i++){
            Fence segment = fenceManagers.get(i).getSegments()[i];
            grid.insert(segment, segment.getStartX(), segment.getStartY());
        }//inner loop
    }

    /**
     * Reset game to initial state
     */
    public void resetGame(int bunnyCount, int wolfCount, int grassCount, int fenceCount) {
        if (grid != null) {
            grid.clear();
        }
        bunnies.clear();
        wolves.clear();
        setUpGame(bunnyCount, wolfCount, grassCount, fenceCount);
    }

    /**
     * Check if game thread is running
     */
    public boolean isGameRunning() {
        return gameThread != null;
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
