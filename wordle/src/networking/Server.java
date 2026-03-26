package networking;

import game.GameEngine;

import java.io.*;
import java.net.*;

public class Server{
    static String winner = null;
    static PrintWriter out1, out2;

    ServerSocket serverSocket;
    int port;

    Server() throws Exception{
        this.port = 5000;
        serverSocket = new ServerSocket(this.port);
    }

    public static void main(String[] args) throws Exception{
        Server server = new Server();

        System.out.println("Server started... Waiting for 2 clients");

        Socket player1 = server.serverSocket.accept();
        System.out.println("Client 1 connected");

        Socket player2 = server.serverSocket.accept();
        System.out.println("Client 2 connected");

        // First take their usernames
        BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
        BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));

        String player1_username = in1.readLine();
        String player2_username = in2.readLine();

        out1 = new PrintWriter(player1.getOutputStream(), true);
        out2 = new PrintWriter(player2.getOutputStream(), true);


        // NOTE: when game starts, clients respond with the difficulty choice
        // Then the server checks compatibility of the players
        // if not, game stops. Else game on.

        int diff1 = Integer.parseInt(
                in1.readLine()
                .substring("Difficulty:".length())
        );
        int diff2 = Integer.parseInt(
                in2.readLine()
                .substring("Difficulty:".length())
        );

        System.out.println("Player 1 chose difficulty: " + diff1);
        System.out.println("Player 2 chose difficulty: " + diff2);

        if(diff1 != diff2){
            System.out.println("ERROR: Incompatible players. Keep your difficulty level same");
            out1.println("ERROR");
            out2.println("ERROR");

            player1.close();
            player2.close();
            return;
        }


        // server has the difficulty.
        // server choses the word

        // Server sends a common correct word.
        GameEngine engine = new GameEngine("Server");
        engine.applyDifficulty(diff1); // both are equal
        engine.load_data();
        engine.decideWord();
        out1.println("CORRECT_WORD:" + engine.correctWord);
        out2.println("CORRECT_WORD:" + engine.correctWord);

        // Send START first
        out1.println("START");
        out2.println("START");


        // Then start threads
        new Thread(() -> handleClient(player1, player1_username)).start();
        new Thread(() -> handleClient(player2, player2_username)).start();
        // game ends after this.
    }

    public static synchronized void declareWinner(String playerName){
        if(winner == null){
            winner = playerName;

            out1.println("RESULT: " + playerName + " WON");
            out2.println("RESULT: " + playerName + " WON");

            System.out.println(playerName + " wins!");
        }
    }

    public static void handleClient(Socket player, String username){
        try{
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(player.getInputStream()));

            String msg;
            while((msg = in.readLine()) != null){
                if("DONE".equals(msg)){
                    declareWinner(username);
                    break;
                }
            }

            player.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
