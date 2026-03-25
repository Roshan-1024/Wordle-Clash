import game.GameEngine;

import java.io.*;
import java.net.*;

public class Server {
    static String winner = null;
    ServerSocket serverSocket;
    int port;
    Server(){
        this.port = 5000;
        this.serverSocket = new ServerSocket(this.port);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Server started... Waiting for 2 clients");

        Socket player1 = this.serverSocket.accept();
        System.out.println("Client 1 connected");

        Socket player2 = this.serverSocket.accept();
        System.out.println("Client 2 connected");

        // First take their usernames
        BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
        BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
        String player1_username = in1.readLine();
        String player2_username = in2.readLine();

        PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
        PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);

        // Send START first
        out1.println("START");
        out2.println("START");

        // Then start threads
        new Thread(() -> handleClient(player1, player1_username)).start();
        new Thread(() -> handleClient(player2, player2_username)).start();
        // game ends after this.
    }

    public static synchronized void declareWinner(String playerName, PrintWriter out){
        if(winner == null){
            winner = playerName;
            out.println("RESULT: YOU WIN");
            System.out.println(playerName + " wins!");
        }
        else{
            out.println("RESULT: YOU LOSE");
        }
    }

    public static void handleClient(Socket socket, String username){
        try{
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            //
            // Game logic goes here...
            //
            GameEngine engine = new GameEngine(username);
            engine.menu();
            try{
                engine.load_data();
            }
            catch(Exception e){
                System.out.println("Error loading file!");
                e.printStackTrace();
                return;
            }
            System.out.println("Start guessing...");
            for(int i = 0; i < engine.config.get("chances"); i++){
                System.out.print("Chance-"+ (i+1)+": ");
                Scanner sc = new Scanner(System.in);
                String guess = sc.next().toLowerCase();
                if(!engine.makeGuess(guess)){ // if not made an appropriate guess
                    i--;
                    continue;
                }

                engine.currentAttempt++;
                if(engine.isCorrectGuess){
                    System.out.println("Congrats you won");
                }
            }


            // If game over
            String msg = in.readLine();

            if("DONE".equals(msg)){
                declareWinner(username, out);
            }
            socket.close();

        }
        catch(Exception e){
                e.printStackTrace();
        }
    }
}
