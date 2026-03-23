import java.io.*;
import java.net.*;

public class Client{
    String username;

    Socket socket;
    BufferedReader in;
    PrintWriter out;

    Client(){
        socket = new Socket("localhost", 5000);

        // Setup the readers and writers to Server
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }
    public static void main(String[] args) throws Exception {
        Client player = new Client();

        Scanner sc = new Scanner(System.in);
        this.username = sc.next();

        out.println(this.username);
        System.out.println("Client: " + this.username + "connected");

        // Wait for the Server to say START
        String msg = "";
        while(!("START".equals(msg))){
            msg = in.readLine();
        }

        // Game logic goes here...


        // Send the server DONE to indicate game end.
        out.println("DONE");
        System.out.println("Sent DONE");

        // Server writes who won.
        String response = in.readLine();
        System.out.println("Server says: " + response);

        socket.close();
    }
}
