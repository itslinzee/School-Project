package game.gui;

import game.MasterMind;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Map;

public class MasterMindFrame extends JFrame{
    private static final int SIZE = 10;
    private static final int COLORS_TO_GUESS = 6;
    private static final int MAX_NUM_OF_TRIES = 20;

    private static MasterMind mastermind = new MasterMind();

    private static Color [] guessToSubmit = new Color [COLORS_TO_GUESS];
    private static int guessIndex = 0;
    private static int currentGuess = 0;
    private static Color guessColor;

    private static Color [][] triesColorsToDisplay = new Color [20][COLORS_TO_GUESS];
    private static Color [][] resultsColorsToDisplay = new Color [20][COLORS_TO_GUESS];

    public static void main(String[] args) {
        JFrame masterMindFrame = new JFrame("MasterMind");
        masterMindFrame.setSize(400, 600);
        masterMindFrame.setLocationRelativeTo(null);
        masterMindFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        masterMindFrame.add(new MasterMindDisplay());
        masterMindFrame.setResizable(false);
        masterMindFrame.setVisible(true);
    }

    private static class MasterMindDisplay extends JPanel {
        public MasterMindDisplay(){
            setLayout(null);

            // Add buttons to select color
            int buttonStartX = 50;
            int buttonStartY = 480;
            int buttonWidth = 30;
            int buttonHeight = 20;
            for(int i = 0; i < SIZE; i++) {
                JButton colorSelector = new JButton();
                colorSelector.setBackground(mastermind.getAvailableColors().get(i));
                colorSelector.setBounds(buttonStartX, buttonStartY, buttonWidth, buttonHeight);
                buttonStartX = buttonStartX + 30;

                colorSelector.addActionListener(new ColorSelectHandler());
                add(colorSelector);
            }

            // Add submit guess button
            JButton submit = new JButton("Submit");
            submit.setBounds(150, 520, 100, 20);
            submit.addActionListener(new SubmitSelectHandler());
            add(submit);

            // Add labels for each segment
            JLabel guessLabel = new JLabel("Previous tries:");
            guessLabel.setBounds(50, 10, 120, 20);
            add(guessLabel);

            JLabel resultLabel = new JLabel("Results:");
            resultLabel.setBounds(220, 10, 120, 20);
            add(resultLabel);

            JLabel guessPrompt = new JLabel("Select six of the following colors:");
            guessPrompt.setBounds(50, 460, 350, 20);
            add(guessPrompt);
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            setBackground(Color.WHITE);
            g.setColor(Color.BLACK);

            // Draw guess squares
            int guessStartX = 50;
            int guessStartY = 30;
            int guessWidth = 20;
            int guessHeight = 20;
            for(int row = 0; row < MAX_NUM_OF_TRIES; row++)
            {
                for(int col = 0; col < COLORS_TO_GUESS; col++)
                {
                    if(triesColorsToDisplay[row][col] == null)
                    {
                        g.drawRect(guessStartX, guessStartY, guessWidth, guessHeight);
                        guessStartX = guessStartX + guessWidth;
                    }
                    else
                    {
                        g.drawRect(guessStartX, guessStartY, guessWidth, guessHeight);
                        g.setColor(triesColorsToDisplay[row][col]);
                        g.fillRect(guessStartX, guessStartY, guessWidth, guessHeight);
                        g.setColor(Color.BLACK);
                        guessStartX = guessStartX + guessWidth;
                    }
                }
                guessStartY = guessStartY + guessHeight;
                guessStartX = 50;
            }

            // Draw result squares
            int resultStartX = 220;
            int resultStartY = 30;
            int resultWidth = 20;
            int resultHeight = 20;
            for(int row = 0; row < MAX_NUM_OF_TRIES; row++)
            {
                for(int col = 0; col < COLORS_TO_GUESS; col++)
                {
                    if(resultsColorsToDisplay[row][col] == null)
                    {
                        g.drawRect(resultStartX, resultStartY, resultWidth, resultHeight);
                        resultStartX = resultStartX + resultWidth;
                    }
                    else
                    {
                        g.drawRect(resultStartX, resultStartY, resultWidth, resultHeight);
                        g.setColor(resultsColorsToDisplay[row][col]);
                        g.fillRect(resultStartX, resultStartY, resultWidth, resultHeight);
                        g.setColor(Color.BLACK);
                        resultStartX = resultStartX + resultWidth;
                    }
                }
                resultStartY = resultStartY + resultHeight;
                resultStartX = 220;
            }
        }

        private class ColorSelectHandler implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JButton source = (JButton) actionEvent.getSource();
                guessColor = source.getBackground();
                if(guessIndex < COLORS_TO_GUESS)
                {
                    guessToSubmit[guessIndex] = guessColor;
                    triesColorsToDisplay[currentGuess][guessIndex] = guessColor;
                    guessIndex++;
                    repaint();
                }
            }
        }

        private class SubmitSelectHandler implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Map<MasterMind.Response, Long> response = mastermind.guess(Arrays.asList(guessToSubmit));
                int resultIndex = 0;

                for(int i = 0; i < response.get(MasterMind.Response.POSITIONAL_MATCH); i++)
                {
                    resultsColorsToDisplay[currentGuess][resultIndex] = Color.BLACK;
                    resultIndex++;
                }

                for(int i = 0; i < response.get(MasterMind.Response.MATCH); i++)
                {
                    resultsColorsToDisplay[currentGuess][resultIndex] = Color.LIGHT_GRAY;
                    resultIndex++;
                }

                resetGuessArray();
                currentGuess++;

                if(mastermind.getGameStatus() == MasterMind.GameStatus.WON)
                {
                    showWinningMessage();
                    repaint();
                    resetGame();
                }

                if(currentGuess >= MAX_NUM_OF_TRIES)
                {
                    if(mastermind.getGameStatus() == MasterMind.GameStatus.LOST)
                    {
                        JOptionPane.showMessageDialog(null, "Sorry, please try again.");
                        repaint();
                        resetGame();
                    }

                    if(mastermind.getGameStatus() == MasterMind.GameStatus.WON)
                    {
                        showWinningMessage();
                        repaint();
                        resetGame();
                    }
                }
                else
                {
                    guessIndex = 0;
                    repaint();
                }
            }
        }

        private void showWinningMessage() {
            JOptionPane.showMessageDialog(null, "Congratulations! You guessed the code!");
        }

        private void resetGame() {
            resetGuessArray();
            currentGuess = 0;
            guessIndex = 0;
            for(int row = 0; row < MAX_NUM_OF_TRIES; row++)
            {
                for(int col = 0; col < COLORS_TO_GUESS; col++)
                {
                    triesColorsToDisplay[row][col] = null;
                    resultsColorsToDisplay[row][col] = null;
                }
            }
        }

        private void resetGuessArray() {
            for(int i = 0; i < guessToSubmit.length; i++)
            {
                guessToSubmit[i] = null;
            }
        }
    }
}


