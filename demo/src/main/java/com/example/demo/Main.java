package com.example.demo;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> {

            // Get initial population counts from user
            int bunnyCount = getIntInput("How many bunnies?", 5);
            int wolfCount = getIntInput("How many wolves?", 1);
            int grassCount = getIntInput("How many grass patches?", 10);

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
            gamePanel.setUpGame(bunnyCount, wolfCount, grassCount);
            gamePanel.startGameThread();
        });//end of invoke later
    }

    /**
     * Get integer input from user via dialog
     * @param prompt The question to ask the user
     * @param defaultValue Default value if user cancels or enters invalid input
     * @return The user's input or default value
     */
    private static int getIntInput(String prompt, int defaultValue) {
        String input = JOptionPane.showInputDialog(null, prompt, defaultValue);
        if (input == null) {
            return defaultValue; // User cancelled
        }
        try {
            int value = Integer.parseInt(input);
            return value >= 0 ? value : defaultValue; // Ensure non-negative
        } catch (NumberFormatException e) {
            return defaultValue; // Invalid input, use default
        }
    }
}
