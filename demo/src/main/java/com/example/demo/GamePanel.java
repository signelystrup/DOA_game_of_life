package com.example.demo;

import com.example.demo.entities.Bunny;
import com.example.demo.entities.Fence;
import com.example.demo.entities.Grass;
import com.example.demo.entities.Wolf;
import com.example.demo.entities.Animal;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GamePanel extends JPanel implements Runnable{

    private Thread gameThread;
    private final int FPS = 60;

    private Grid grid;
    private GrassManager grassManager;
    private List<Bunny> bunnies = new ArrayList<>();
    private List<Wolf> wolves = new ArrayList<>();
    private Fence[] fences = new Fence[0];
    private Random random = new Random();

    public GamePanel(){
        //screen settings
        this.setPreferredSize(new Dimension(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT));
        this.setBackground(Color.PINK);
        this.setDoubleBuffered(true); //improve rending performance.
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
        for (Bunny bunny : bunnies) {
            moveEntity(bunny);
        }

        // Update all wolves
        for (Wolf wolf : wolves) {
            moveEntity(wolf);
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

        //fences: draw first (background layer)
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

        for (Object obj : allEntities) {
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

}
