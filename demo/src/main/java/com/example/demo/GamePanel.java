package com.example.demo;

import com.example.demo.entities.Bunny;
import com.example.demo.entities.Grass;
import com.example.demo.entities.Wolf;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable{

    private Thread gameThread;
    private final int FPS = 60;

    private Grid grid;
    private GrassManager grassManager;
    private Bunny[] bunnies = new Bunny[10];
    private Wolf wolf;
    private Random random = new Random();

    public GamePanel(){
        //screen settings
        this.setPreferredSize(new Dimension(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT));
        this.setBackground(Color.PINK);
        this.setDoubleBuffered(true); //improve rending performance.
    }

    public void setUpGame(){
        //init grid and entities here.
        // Cell size = max(vision ranges) to ensure findNearby() works for all species
        grid = new Grid(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, GameConfig.GRID_CELL_SIZE);
        grassManager = new GrassManager(grid);

        //bunnies: random placement
        for (int i = 0 ; i < 5 ; i ++){
            int randomX = random.nextInt(GameConfig.WORLD_WIDTH);
            int randomY = random.nextInt(GameConfig.WORLD_HEIGHT);
            bunnies[i] = new Bunny(randomX, randomY);
            grid.insert(bunnies[i], bunnies[i].x, bunnies[i].y);
        }

        //wolf: random placement
        int randomX = random.nextInt(GameConfig.WORLD_WIDTH);
        int randomY = random.nextInt(GameConfig.WORLD_HEIGHT);
        wolf = new Wolf(randomX, randomY);
        grid.insert(wolf, wolf.x, wolf.y);
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
        grassManager.update();
        //bunny.update();
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; //get graphics as Graphics2D

        // Draw all entities from grid
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

        // Old drawing code (commented out)
        //for (int i = 0; i < bunnies.length; i ++){
        //    if (bunnies[i] != null) {
        //        bunnies[i].draw(g2);
        //    }
        //}
        //
        //wolf.draw(g2);

        g2.dispose(); //good practice, Saves memory. (program still works without this line)

    }

}
