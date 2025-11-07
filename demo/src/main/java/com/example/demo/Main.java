package com.example.demo;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> {

            //Set up window (jFrame):
            JFrame jFrame = new JFrame();
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setResizable(false);
            jFrame.setTitle("Game of Life");
            //jFrame.setLocationRelativeTo(null);
            jFrame.setVisible(true);
            //System.out.println("frame focus?: " + jFrame.hasFocus());

            //Jpanel:
            GamePanel gamePanel = new GamePanel();
            jFrame.add(gamePanel); //add jPanel to frame.
            jFrame.pack(); //makes jFrame fit it's component's (the gamepanel's) preferred size.

            //start game:
            gamePanel.setUpGame();
            gamePanel.startGameThread();
        });//end of invoke later
    }
}
