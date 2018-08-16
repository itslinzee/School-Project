package game;
  
import java.util.*;
import java.util.stream.*;
import java.util.Random;

import static java.util.stream.Collectors.*;

public class Scrambler {

    private final SpellChecker spellChecker;

    public Scrambler(SpellChecker spellCheck) {
        spellChecker = spellCheck;
    }

    public int score(String guess, String realWord) {
      if(!spellChecker.isSpellingCorrect(guess)) return 0;
      
      String guessLowerCase = guess.toLowerCase();

      Map<String, Long> frequencyOfLettersInGuess = Stream.of(guessLowerCase.split(""))
              .collect(groupingBy(letter -> letter, counting()));

      Map<String, Long> frequencyOfLettersWord = Stream.of(realWord.split(""))
              .collect(groupingBy(letter -> letter, counting()));

      if(frequencyOfLettersInGuess.keySet().stream()
              .filter(letter -> frequencyOfLettersInGuess.get(letter) > frequencyOfLettersWord.computeIfAbsent(letter, key -> 0L))
              .count() > 0) return 0;

      List<String> VOWELS = List.of("a", "e", "i", "o", "u");

      return Stream.of(guessLowerCase.split(""))
              .mapToInt(letter -> VOWELS.contains(letter) ? 1 : 2)
              .sum();
    }

    public String scramble(String word, long seed) {
        List<String> letters = Stream.of(word.split("")).collect(toList());
        Collections.shuffle(letters, new Random(seed));

        return String.join("", letters);
    }

    public String getRandomWord(List<String> wordList, long seed) {
        Random random = new Random(seed);

        return wordList.get(random.nextInt(wordList.size()));
    }
}