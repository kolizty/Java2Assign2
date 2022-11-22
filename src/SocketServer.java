import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    public static void main(String[] args) {
        int port = 8080;
        try {
            ServerSocket server = new ServerSocket(port);
            int count = 1;
            while (true) {
                System.out.println("Waiting for client to connect...");
                Socket socket1 = server.accept();
                PrintWriter pw1 = new PrintWriter(socket1.getOutputStream());
                pw1.println("1 " + count);
                pw1.flush();
                System.out.println("First client connected, waiting for second client.");
                Socket socket2 = server.accept();
                PrintWriter pw2 = new PrintWriter(socket2.getOutputStream());
                pw2.println("2 " + count);
                pw2.flush();
                System.out.println("Second client connected.");
                ServerThread thread = new ServerThread(socket1, socket2, count);
                System.out.println("Game " + count + " start.");
                thread.start();
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
