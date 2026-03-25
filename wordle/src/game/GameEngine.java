package game;

import java.util.Scanner;
import java.util.Vector;
import java.util.Random;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Paths;

// NOTE:
// 1. Ask the user about the difficulty level (Easy: 4 letters; Medium: 5 letters; Hard: 6 letters)
// 2. Load the right csv file and pick a random word.
// 3. Game Starts. Timer (3min) starts asynchronously (probably a daemon).
// 4. User gets 6 attempts.
// 5. Check valid word entered or not. Valid length check for the cli version.
// 6. After a word is entered, check if string matches.
// Game ends.

// Instantiate to start a new instance of a Wordle Game
public class GameEngine{
    Vector<String> words; // stores all the words from the csv file
    Vector<String> guesses; // stores the user guesses
    HashMap<String, Integer> config; // configuration settings
    String words_file_path = "";
    String username;
    String correctWord;
    boolean isCorrectGuess; // After a makeGuess() this is updated
    int currentAttempt;

    Scanner sc;

    GameEngine(String username){
        this.username = username;

        this.config = new HashMap<>();
        this.config.put("word_length", null);
        this.config.put("timer", null); // 3 minutes
        this.config.put("maxAttempts", 6);

        this.sc = new Scanner(System.in);
        this.guesses = new Vector<>();
        this.isCorrectGuess = false;
        this.currentAttempt = 0;

        System.out.println("Welcome to Wordle Clash, " + this.username);
    }

    void menu(){
        System.out.println("Choose difficulty: ");
        System.out.println("1. Easy: 4 words");
        System.out.println("2. Medium: 5 words");
        System.out.println("3. Hard: 6 words");

        int choice = -1;
        choice = this.sc.nextInt();

        switch(choice){
            case 1:
                this.config.put("word_length", 4);
                this.config.put("timer", 3);
                this.config.put("maxAttempts", 6);
                this.words_file_path = "../../resources/4_letter_words.csv";
                break;

            case 2:
                this.config.put("word_length", 5);
                this.config.put("timer", 4);
                this.config.put("maxAttempts", 7);
                this.words_file_path = "../../resources/5_letter_words.csv";
                break;

            case 3:
                this.config.put("word_length", 6);
                this.config.put("timer", 5);
                this.config.put("maxAttempts", 8);
                this.words_file_path = "../../resources/6_letter_words.csv";
                break;

            default:
                System.out.println("Invalid option selected");
                return;
        }

    }

    // Loads the words csv file into a vector and picks a random word
    void load_data() throws Exception{
        this.words = new Vector<>(
            java.util.Arrays.stream(
                Files.readString(Paths.get(this.words_file_path)).split(",")
            )
            .map(String::trim)
            .toList()
        );

        Random rand = new Random();
        int n = rand.nextInt(this.words.size());
        this.correctWord = this.words.get(n);
    }

    boolean isCorrect(String guess){
        return guess.equals(this.correctWord);
    }

    boolean makeGuess(String guess){
        // invalid guesss word length
        if(guess.length() != this.config.get("word_length")){
            System.out.println("Invalid length! Try again.");
            return false;
        }
        // TODO: Also, check if valid word is entered.
        guesses.add(guess);

        // check correct letters
        System.out.println("Correct letters: ");
        for(int j = 0; j < this.config.get("word_length"); j++){
            if(guess.charAt(j) == this.correctWord.charAt(j)){
                System.out.print(guess.charAt(j));
            }
            else{
                System.out.print("#");
            }
        }
        System.out.println();
        // TODO: Print correct letters, but incorrect positions

        if(isCorrect(guess)){
            this.isCorrectGuess = true;
        }
        return true;
    }
}
