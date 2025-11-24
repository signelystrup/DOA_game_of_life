package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> {

            //Set up window (jFrame):
            JFrame jFrame = new JFrame();
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setResizable(false);
            jFrame.setTitle("Game of Life");
            jFrame.setLayout(new BorderLayout());

            //Create game panel:
            GamePanel gamePanel = new GamePanel();

            //Create top panel with inputs and buttons:
            JPanel topPanel = new JPanel();
            topPanel.setBackground(Color.LIGHT_GRAY);
            topPanel.setLayout(new FlowLayout());

            //Input fields:
            JTextField bunnyField = new JTextField("5", 3);
            bunnyField.setForeground(Color.BLACK);
            JTextField wolfField = new JTextField("1", 3);
            wolfField.setForeground(Color.BLACK);
            JTextField grassField = new JTextField("10", 3);
            grassField.setForeground(Color.BLACK);
            JTextField fenceField = new JTextField("2", 3);
            fenceField.setForeground(Color.BLACK);

            //Strategy dropdown:
            JComboBox<String> strategyCombo = new JComboBox<>(new String[]{
                "Optimized for Wolves",
                "Optimized for Bunnies",
                "Optimized for Efficiency"
            });
            strategyCombo.setSelectedIndex(0); // Default: OPTIMIZED_FOR_WOLVES

            //Start button:
            JButton startButton = new JButton("Start Game");

            //Add entity buttons:
            JButton addWolfButton = new JButton("Add Wolf");
            JButton addBunnyButton = new JButton("Add Bunny");
            JButton addGrassButton = new JButton("Add Grass");
            JButton addFenceButton = new JButton("Add Fence");
            JButton resetButton = new JButton("Reset Game");

            //Add components to top panel:
            JLabel strategyLabel = new JLabel("Strategy:");
            strategyLabel.setForeground(Color.BLACK);
            topPanel.add(strategyLabel);
            topPanel.add(strategyCombo);

            JLabel separator0 = new JLabel("|");
            separator0.setForeground(Color.BLACK);
            topPanel.add(separator0);

            JLabel bunnyLabel = new JLabel("Bunnies:");
            bunnyLabel.setForeground(Color.BLACK);
            topPanel.add(bunnyLabel);
            topPanel.add(bunnyField);

            JLabel wolfLabel = new JLabel("Wolves:");
            wolfLabel.setForeground(Color.BLACK);
            topPanel.add(wolfLabel);
            topPanel.add(wolfField);

            JLabel grassLabel = new JLabel("Grass:");
            grassLabel.setForeground(Color.BLACK);
            topPanel.add(grassLabel);
            topPanel.add(grassField);

            JLabel fenceLabel = new JLabel("Fences:");
            fenceLabel.setForeground(Color.BLACK);
            topPanel.add(fenceLabel);
            topPanel.add(fenceField);
            topPanel.add(startButton);

            JLabel separator = new JLabel("|");
            separator.setForeground(Color.BLACK);
            topPanel.add(separator);
            topPanel.add(addBunnyButton);
            topPanel.add(addWolfButton);
            topPanel.add(addGrassButton);
            topPanel.add(addFenceButton);
            topPanel.add(resetButton);

            //Start button action:
            startButton.addActionListener(e -> {
                try {
                    int bunnyCount = Integer.parseInt(bunnyField.getText());
                    int wolfCount = Integer.parseInt(wolfField.getText());
                    int grassCount = Integer.parseInt(grassField.getText());
                    int fenceCount = Integer.parseInt(fenceField.getText());

                    // Set strategy based on dropdown selection
                    int selected = strategyCombo.getSelectedIndex();
                    if (selected == 0) {
                        GameConfig.STRATEGY = GridStrategy.OPTIMIZED_FOR_WOLVES;
                    } else if (selected == 1) {
                        GameConfig.STRATEGY = GridStrategy.OPTIMIZED_FOR_BUNNIES;
                    } else if (selected == 2) {
                        GameConfig.STRATEGY = GridStrategy.OPTIMIZED_FOR_EFFICIENCY;
                    }

                    gamePanel.resetGame(bunnyCount, wolfCount, grassCount, fenceCount);
                    if (!gamePanel.isGameRunning()) {
                        gamePanel.startGameThread();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(jFrame, "Please enter valid numbers!");
                }
            });

            //Add entity button actions:
            addWolfButton.addActionListener(e -> gamePanel.addWolf());
            addBunnyButton.addActionListener(e -> gamePanel.addBunny());
            addGrassButton.addActionListener(e -> gamePanel.addGrass());
            addFenceButton.addActionListener(e -> gamePanel.addFence());
            resetButton.addActionListener(e -> {
                try {
                    int bunnyCount = Integer.parseInt(bunnyField.getText());
                    int wolfCount = Integer.parseInt(wolfField.getText());
                    int grassCount = Integer.parseInt(grassField.getText());
                    int fenceCount = Integer.parseInt(fenceField.getText());

                    // Set strategy based on dropdown selection
                    int selected = strategyCombo.getSelectedIndex();
                    if (selected == 0) {
                        GameConfig.STRATEGY = GridStrategy.OPTIMIZED_FOR_WOLVES;
                    } else if (selected == 1) {
                        GameConfig.STRATEGY = GridStrategy.OPTIMIZED_FOR_BUNNIES;
                    } else if (selected == 2) {
                        GameConfig.STRATEGY = GridStrategy.OPTIMIZED_FOR_EFFICIENCY;
                    }

                    gamePanel.resetGame(bunnyCount, wolfCount, grassCount, fenceCount);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(jFrame, "Please enter valid numbers!");
                }
            });

            //Add window closing listener to print final metrics:
            jFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    gamePanel.printFinalMetrics();
                }
            });

            //Add panels to frame:
            jFrame.add(topPanel, BorderLayout.NORTH);
            jFrame.add(gamePanel, BorderLayout.CENTER);
            jFrame.pack();
            jFrame.setVisible(true);
        });//end of invoke later
    }
}
