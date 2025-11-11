package com.example.demo;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable{

    private Thread gameThread;
    private final int FPS = 60;

    private Grid grid;
    private GrassManager grassManager;

    public GamePanel(){
        //screen settings
        this.setPreferredSize(new Dimension(500, 500));
        this.setBackground(Color.PINK);
        this.setDoubleBuffered(true); //improve rending performance.
    }

    public void setUpGame(){
        //init grid and entities here.
        grid = new Grid(500, 500, 50);
        grassManager = new GrassManager(grid);
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
        //grassManager.update();
        //bunny.update();
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; //get graphics as Graphics2D


        //bunny.draw(g2);
        g2.setColor(Color.BLACK);
        g2.fillRect(100,100,100,100);

        g2.dispose(); //good practice, Saves memory. (program still works without this line)

    }

}
