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
            out.println(username);

            System.out.println("Connected as " + username);

            // Wait for the START signal from the Server
            String msg;
            while((msg = in.readLine()) != null){
                if(msg.equals("START")){
                    System.out.println("Game starting...");
                    break;
                }
            }

            // start listening to server in a separate thread
            new Thread(this::listenToServer).start();

            // Game starts...
            runGame(sc);
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

    private void runGame(Scanner sc){
        try{
            GameEngine engine = new GameEngine(username);

            engine.menu();
            engine.load_data(); // can cause exceptions

            System.out.println("Start Guessing...");


            while(!engine.isGameOver()){
                System.out.print("Enter guess: ");
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
            if(!engine.isCorrectGuess){
                System.out.println("You failed to guess the word.");
                out.println("DONE");
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
