import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    public static void main(String[] args){
        int port = 8080;
        try{
            ServerSocket server = new ServerSocket(port);
            int count = 0;
            while (true){
                System.out.println("Waiting for client to connect...");
                Socket socket1 = server.accept();
                PrintWriter pw1 = new PrintWriter(socket1.getOutputStream());
                pw1.write("1");
                pw1.flush();
                pw1.close();
                System.out.println("First client connected, waiting for second client.");
                Socket socket2 = server.accept();
                PrintWriter pw2 = new PrintWriter(socket2.getOutputStream());
                pw2.write("2");
                pw2.flush();
                pw2.close();
                System.out.println("Second client connected.");
                count++;
                ServerThread thread = new ServerThread(socket1, socket2, count);
                System.out.println("Game " + count + " start.");
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
