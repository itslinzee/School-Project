package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class ScramblerTest {
    Scrambler scrambler;

    @BeforeEach
    void init() {
        SpellChecker spellChecker = Mockito.mock(SpellChecker.class);
        when(spellChecker.isSpellingCorrect(anyString())).thenReturn(true);

        scrambler = new Scrambler(spellChecker);
    }

    @Test
    void canary () {
        assertTrue(true);
    }

    @Test
    void testValueOfVowels() {
        assertAll(
                () -> assertEquals(5, scrambler.score("aeiou", "aeiou")),
                () -> assertEquals(5, scrambler.score("AEIOU", "aeiou")));
    }

    @Test
    void checkScoreWhenUserGuessesWrongWord() {
        assertAll(
                () -> assertEquals(0, scrambler.score("oekmny", "pat")),
                () -> assertEquals(0, scrambler.score("moon", "monkey")),
                () -> assertEquals(0, scrambler.score("OI", "monkey")));
    }

    @Test
    void checkScoreWhenUserGuessesCorrectWordWithCorrectSpelling() {
        assertEquals(10, scrambler.score("monkey", "monkey"));
    }

    @Test
    void checkScoreWhenUserGuessesPartialWord() {
        assertAll(
                () -> assertEquals(7, scrambler.score("monk", "monkey")),
                () -> assertEquals(4, scrambler.score("my", "monkey")),
                () -> assertEquals(4, scrambler.score("My", "monkey")));
    }  
    
    @Test
    void checkSpellingWithIncorrectSpelling() {
        SpellChecker spellChecker = Mockito.mock(SpellChecker.class);
        when(spellChecker.isSpellingCorrect("whatever")).thenReturn(false);
        Scrambler scrambler = new Scrambler(spellChecker);

        assertEquals(0, scrambler.score("whatever", "monkey"));
    }

    @Test
    void checkSpellingWithCorrectSpelling(){
        SpellChecker spellChecker = Mockito.mock (SpellChecker.class);
        when(spellChecker.isSpellingCorrect("monkey")).thenReturn(true);
        when(spellChecker.isSpellingCorrect("Monkey")).thenReturn(true);
        Scrambler scrambler = new Scrambler(spellChecker);

        assertAll(
                () -> assertEquals(10, scrambler.score("monkey", "monkey")),
                () -> assertEquals(10, scrambler.score("Monkey", "monkey")));

    }

    @Test
    void checkSpellingWithNetworkError() {
        SpellChecker spellChecker = Mockito.mock(SpellChecker.class);
        when(spellChecker.isSpellingCorrect("whatever")).thenThrow(new RuntimeException("Network error"));
        Scrambler scrambler = new Scrambler(spellChecker);

        assertThrows(RuntimeException.class, () -> scrambler.score("whatever", "monkey"), "Network error");
    }

    @Test
    void checkScrambleReturnsSameWordWithSameSeed(){
        String scrambleWordWithSeed1 = scrambler.scramble("monkey", 1);
        String scrambleWordWithSeed2 = scrambler.scramble("monkey", 1);

        assertEquals(scrambleWordWithSeed1, scrambleWordWithSeed2);
    }
                                                          
    @Test
    void checkScrambleReturnsWordWithSameLengthAsOriginal() {
        assertEquals(6, scrambler.scramble("monkey", 1).length());
    }

    @Test
    void checkScrambleReturnsWordContainingLettersOfOriginal() {
        String testWord = "monkey";
        String scrambledWord = scrambler.scramble(testWord, 1000);

        assertTrue(scrambledWord.matches("[monkey]*"));
    }

    @Test
    void checkGetRandomWordReturnsRandomWordFromList(){
        List<String> wordsToGuessFrom = List.of("monkey", "fruit", "apple");

        assertAll(
                () -> assertEquals("fruit", scrambler.getRandomWord(wordsToGuessFrom, 1000)),
                () -> assertEquals("monkey", scrambler.getRandomWord(wordsToGuessFrom, 500)),
                () -> assertEquals("apple", scrambler.getRandomWord(wordsToGuessFrom, 14)));
    }

    @Test
    void checkGetRandomWordReturnsSameWordWithSameSeed() {
        List<String> wordsToGuessFrom = List.of("monkey", "fruit", "apple");

        String randomWordWithSeed1 = scrambler.getRandomWord(wordsToGuessFrom, 1000);
        String randomWordWithSeed2 = scrambler.getRandomWord(wordsToGuessFrom, 1000);

        assertEquals(randomWordWithSeed1, randomWordWithSeed2);
    }

    @Test
    void checkGetRandomWordReturnsDifferentWordWithDifferentSeed() {
        List<String> wordsToGuessFrom = List.of("monkey", "fruit", "apple");

        String randomWordWithSeed1 = scrambler.getRandomWord(wordsToGuessFrom, 1000);
        String randomWordWithSeed2 = scrambler.getRandomWord(wordsToGuessFrom, 500);

        assertNotEquals(randomWordWithSeed1, randomWordWithSeed2);
    }
}