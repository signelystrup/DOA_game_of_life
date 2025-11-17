package com.example.demo;

import com.example.demo.entities.Bunny;
import com.example.demo.entities.Grass;
import com.example.demo.entities.Wolf;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable{

    private Thread gameThread;
    private final int FPS = 60;
    
    // World size
    private final int WORLD_WIDTH = 500;
    private final int WORLD_HEIGHT = 500;
    private final int CELL_SIZE = 50;  // Grid cell size

    // Grid for spatial partitioning
    private Grid grid;
    
    // Entities
    private List<Bunny> bunnies = new ArrayList<>();
    private List<Wolf> wolves = new ArrayList<>();
    private List<Grass> grassList = new ArrayList<>();

    public GamePanel(){
        //screen settings
        this.setPreferredSize(new Dimension(WORLD_WIDTH, WORLD_HEIGHT));
        this.setBackground(Color.PINK);
        this.setDoubleBuffered(true); //improve rendering performance.
    }

    public void setUpGame(){
        // Initialize grid
        grid = new Grid(WORLD_WIDTH, WORLD_HEIGHT, CELL_SIZE);
        
        // Create bunnies
        for (int i = 0; i < 10; i++){
            int x = (int)(Math.random() * WORLD_WIDTH);
            int y = (int)(Math.random() * WORLD_HEIGHT);
            bunnies.add(new Bunny(x, y));
        }
        
        // Create wolves
        for (int i = 0; i < 3; i++){
            int x = (int)(Math.random() * WORLD_WIDTH);
            int y = (int)(Math.random() * WORLD_HEIGHT);
            wolves.add(new Wolf(x, y));
        }
        
        // Create grass patches
        for (int i = 0; i < 20; i++){
            int x = (int)(Math.random() * WORLD_WIDTH);
            int y = (int)(Math.random() * WORLD_HEIGHT);
            grassList.add(new Grass(x, y));
        }
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start(); //calls run method.
        System.out.println("game start");
    }

    @Override
    public void run(){
        double drawInterval = 1000000000.0 / FPS; //1/60 second.
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
        // Clear and rebuild grid each frame
        grid.clear();
        
        // Insert all entities into grid using worldX and worldY
        for (Bunny bunny : bunnies) {
            grid.insert(bunny, bunny.getWorldX(), bunny.getWorldY());
        }
        for (Wolf wolf : wolves) {
            grid.insert(wolf, wolf.getWorldX(), wolf.getWorldY());
        }
        for (Grass grass : grassList) {
            grid.insert(grass, grass.getWorldX(), grass.getWorldY());
        }
        
        // Update all bunnies with flocking
        for (Bunny bunny : bunnies) {
            bunny.update(grid);
        }
        
        // Update all wolves with flocking
        for (Wolf wolf : wolves) {
            wolf.update(grid);
        }
        
        // TODO: Check for eating grass, collisions, etc.
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; //get graphics as Graphics2D

        // Draw grass first (background layer)
        for (Grass grass : grassList) {
            grass.draw(g2);
        }
        
        // Draw bunnies
        for (Bunny bunny : bunnies) {
            bunny.draw(g2);
        }
        
        // Draw wolves
        for (Wolf wolf : wolves) {
            wolf.draw(g2);
        }

        g2.dispose(); //good practice, Saves memory.
    }
}
