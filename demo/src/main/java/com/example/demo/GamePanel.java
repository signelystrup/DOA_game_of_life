package com.example.demo;

import com.example.demo.entities.Bunny;
import com.example.demo.entities.Grass;
import com.example.demo.entities.Wolf;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{

    private Thread gameThread;
    private final int FPS = 60;

    private Bunny[] bunnies = new Bunny[10];
    private Wolf wolf = new Wolf(350, 350);
    private Grass grass = new Grass(170,170);

    public GamePanel(){
        //screen settings
        this.setPreferredSize(new Dimension(500, 500));
        this.setBackground(Color.PINK);
        this.setDoubleBuffered(true); //improve rending performance.
    }

    public void setUpGame(){
        //init grid and entities here.

        //bunnies:
        for (int i = 0 ; i < 5 ; i ++){
            bunnies[i] = new Bunny(32 * i, 32 * i);
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
        //bunny.update();
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; //get graphics as Graphics2D

        grass.draw(g2);
        
        for (int i = 0; i < bunnies.length; i ++){
            if (bunnies[i] != null) {
                bunnies[i].draw(g2);
            }
        }

        wolf.draw(g2);

        g2.dispose(); //good practice, Saves memory. (program still works without this line)

    }

}
