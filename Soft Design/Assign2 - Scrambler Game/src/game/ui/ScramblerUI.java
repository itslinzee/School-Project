package game.ui;

import game.Scrambler;
import game.SpellCheckerService;

import java.io.*;
import java.util.*;

public class ScramblerUI {
    public static void main(String [] args) {

        SpellCheckerService spellChecker = new SpellCheckerService();
        Scrambler scrambler = new Scrambler(spellChecker);

        Random random = new Random(System.currentTimeMillis());
        Scanner userInput = new Scanner(System.in);

        System.out.print("Please enter the name of the file (including extension) containing the list of words: ");
        String fileName = userInput.nextLine();

        fileName = System.getProperty("user.dir").concat("\\src\\game\\ui\\" + fileName);
        File inputFile = new File(fileName);

        ArrayList<String> words = readInputFile(inputFile);

        String replayGame = "";

        do {
            String gameWord = scrambler.getRandomWord(words, random.nextLong());
            String scrambledWord = scrambler.scramble(gameWord, random.nextLong());

            int perfectScore = scrambler.score(gameWord, gameWord);
            int score = 0;

            System.out.println("\nCan you guess this scrambled word?");
            System.out.println(scrambledWord + "\n");

            while (score != perfectScore) {
                System.out.print("Please enter your guess: ");
                String guess = userInput.nextLine();

                score = scrambler.score(guess, gameWord);

                if (score == perfectScore) {
                    System.out.println("Congratulations! You guessed correctly!");
                    System.out.print("Would you like to play again? : ");
                    replayGame = userInput.nextLine();
                } else { System.out.println("Score for this guess : " + score + "/" + perfectScore + "\n"); }
            }
        }
        while (replayGame.charAt(0) == 'y' || replayGame.charAt(0) == 'Y');
    }

    private static ArrayList<String> readInputFile(File inputFile) {
        ArrayList<String> outputList = new ArrayList<>();

        try {
            Scanner inputScan = new Scanner(inputFile);

            while (inputScan.hasNextLine()) {
                String word = inputScan.nextLine();
                outputList.add(word);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Input file of words not found.");
            e.printStackTrace();
        }

        return outputList;
    }
}
