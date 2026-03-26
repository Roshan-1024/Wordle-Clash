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
    public String correctWord;
    public boolean isCorrectGuess; // After a makeGuess() this is updated
    public int currentAttempt;

    Scanner sc;

    public GameEngine(String username){
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

    public int promptDifficuilty(){
        System.out.println("Choose difficulty: ");
        System.out.println("1. Easy: 4 words");
        System.out.println("2. Medium: 5 words");
        System.out.println("3. Hard: 6 words");
        System.out.print("Choice: ");

        int choice = -1;
        choice = this.sc.nextInt();

        return choice;
    }

    public void applyDifficulty(int choice){
        switch(choice){
            case 1:
                this.config.put("word_length", 4);
                this.config.put("timer", 3);
                this.config.put("maxAttempts", 6);
                this.words_file_path = "/4_letter_words.csv";
                break;

            case 2:
                this.config.put("word_length", 5);
                this.config.put("timer", 4);
                this.config.put("maxAttempts", 7);
                this.words_file_path = "/5_letter_words.csv";
                break;

            case 3:
                this.config.put("word_length", 6);
                this.config.put("timer", 5);
                this.config.put("maxAttempts", 8);
                this.words_file_path = "/6_letter_words.csv";
                break;

            default:
                System.out.println("Invalid option selected");
                return;
        }
    }

    public void decideWord(){
        Random rand = new Random();
        int n = rand.nextInt(this.words.size());
        this.correctWord = this.words.get(n);
    }

    // Loads the words csv file into a vector
    public void load_data() throws Exception{
        java.io.InputStream is = getClass().getResourceAsStream(this.words_file_path);

        if(is == null){
            throw new java.io.FileNotFoundException("Could not find file in classpath: " + this.words_file_path);
        }

        String content = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);

        this.words = new Vector<>(
            java.util.Arrays.stream(content.split(","))
                .map(String::trim)
                .toList()
        );

        is.close(); // Clean up the stream
    }

    boolean isCorrect(String guess){
        return guess.equals(this.correctWord);
    }

    public boolean isGameOver(){
        return this.isCorrectGuess ||
               this.currentAttempt >= this.config.get("maxAttempts");
    }

    public boolean makeGuess(String guess){
        // invalid guesss word length
        if(guess.length() != this.config.get("word_length")){
            System.out.println("Invalid length! Try again.");
            return false;
        }

        // Check if word is valid
        boolean validWord = false;
        for(String word : words){
            if(word.equalsIgnoreCase(guess)){
                guesses.add(guess);
                validWord = true;
                break;
            }
        }
        if(!validWord){
            System.out.println("Invalid word! Try again.");
            return false;
        }

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

        // TODO: Check if same word guessed again.
        System.out.println();
        // TODO: Print correct letters, but incorrect positions

        if(isCorrect(guess)){
            this.isCorrectGuess = true;
        }
        return true;
    }
}
