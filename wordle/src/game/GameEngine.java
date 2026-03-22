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
// 4. User gets 6 chances.
// 5. Check valid word entered or not. Valid length check for the cli version.
// 6. After a word is entered, check if string matches.
// Game ends.

// Instantiate to start a new instance of a Wordle Game
public class GameEngine{
    Vector<String> words;
    Vector<String> guesses;
    HashMap<String, Integer> config;
    String words_file_path = "";
    String username;
    String correctWord;

    Scanner sc;
    // TODO: Add makeGuess(), isGameOver()

    GameEngine(String username){
        this.username = username;

        this.config = new HashMap<>();
        this.config.put("word_length", null);
        this.config.put("timer", null); // 3 minutes
        this.config.put("chances", 6);

        this.sc = new Scanner(System.in);
        this.guesses = new Vector<>();

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
                this.config.put("chances", 6);
                this.words_file_path = "../../../4_letter_words.csv";
                break;

            case 2:
                this.config.put("word_length", 5);
                this.config.put("timer", 4);
                this.config.put("chances", 7);
                this.words_file_path = "../../../5_letter_words.csv";
                break;

            case 3:
                this.config.put("word_length", 6);
                this.config.put("timer", 5);
                this.config.put("chances", 8);
                this.words_file_path = "../../../6_letter_words.csv";
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

    void start(){
        // TODO: Build the async. timer
        System.out.println("Start Guessing...");
        for(int i = 0; i < this.config.get("chances"); i++){
            System.out.print("Chance-"+(i+1)+": ");
            String guess = this.sc.next().toLowerCase();

            // invalid guesss word length
            if (guess.length() != this.config.get("word_length")) {
                System.out.println("Invalid length! Try again.");
                i--;
                continue;
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
                System.out.println("Congrats you won!!");
                return;
            }
        }

        System.out.println("Correct word was: "+ this.correctWord);
    }

    public static void main(String[] args){
        GameEngine engine = new GameEngine("Roshan");
        engine.menu();
        try{
            engine.load_data();
        }
        catch (Exception e) {
            System.out.println("Error loading file!");
            e.printStackTrace();
            return;
        }
        engine.start();

        System.out.println("Game ends");
    }
}
