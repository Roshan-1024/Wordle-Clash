package networking;

import game.GameEngine;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client{
    String username;

    Socket socket;
    BufferedReader in;
    PrintWriter out;

    public Client(String host, int port) throws Exception{
        socket = new Socket(host, port);

        // Setup the readers and writers to Server
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void start(){
        try{
            Scanner sc = new Scanner(System.in);

            // Take username
            System.out.print("Enter username: ");
            username = sc.nextLine();
            out.println(username); // send server the username

            System.out.println("Connected as " + username);

            GameEngine engine = new GameEngine(username);
            int difficulty = engine.promptDifficuilty();
            out.println("Difficulty:" + difficulty);

            // Wait for the START signal from the Server
            String msg;
            while((msg = in.readLine()) != null){
                if(msg.startsWith("CORRECT_WORD:")){
                    engine.correctWord = msg.substring("CORRECT_WORD:".length()).trim();
                    engine.applyDifficulty(difficulty);
                    engine.load_data();
                }
                else if(msg.equals("START")){
                    System.out.println("Game starting...");
                    break;
                }
                else if(msg.equals("ERROR")){
                    throw new Exception("Server Shutdown. Stopping Clients...");
                }
            }

            // start listening to server in a separate thread
            new Thread(this::listenToServer).start();

            // Game starts...
            runGame(engine, sc);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void listenToServer(){
        try{
            String msg;

            while((msg = in.readLine()) != null){
                if(msg.startsWith("RESULT")){
                    System.out.println(msg);
                    System.out.println("Game Over.");
                    socket.close();
                    System.exit(0);
                }
                else if(msg.equals("TIME_UP")){
                    System.out.println("Time's up!");
                    socket.close();
                    System.exit(0);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Disconnected from server");
        }
    }

    private void runGame(GameEngine engine, Scanner sc){
        try{
            System.out.println("Start Guessing...");

            // Game Loop
            while(!engine.isGameOver()){
                System.out.print("Attempt-" + (engine.currentAttempt+1) + ": ");
                String guess = sc.next().toLowerCase();

                if(!engine.makeGuess(guess)){
                    continue;
                }
                engine.currentAttempt++;

                if(engine.isCorrectGuess){
                    System.out.println("Game completed.");
                    out.println("DONE"); // notify the server
                    return;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        try{
            Client player = new Client("localhost", 5000);
            player.start();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
