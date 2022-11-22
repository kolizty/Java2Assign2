import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket socket1 = null;
    private Socket socket2 = null;
    private final int count;

    public ServerThread(Socket socket1, Socket socket2, int count) {
        this.socket1 = socket1;
        this.socket2 = socket2;
        this.count = count;
    }

    public void run() {
        try {
            while (true) {
                BufferedReader br1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
                PrintWriter pw2 = new PrintWriter(socket2.getOutputStream());
                String info1 = br1.readLine();
                if (gameEnd(info1)) {
                    break;
                }
                System.out.println("Game " + count + ": Player 1 puts " + info1);
                pw2.println(info1);
                pw2.flush();
                BufferedReader br2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
                PrintWriter pw1 = new PrintWriter(socket1.getOutputStream());
                String info2 = br2.readLine();
                if (gameEnd(info2)) {
                    break;
                }
                System.out.println("Game " + count + ": Player 2 puts " + info2);
                pw1.println(info2);
                pw1.flush();
            }
            socket1.close();
            socket2.close();
        } catch (Exception e) {
            if (e.getMessage().equals("Connection reset")) {
                System.out.println("Client disconnected, game " + count + " exit.");
                System.exit(-1);
            }
            e.printStackTrace();
        }
    }

    private boolean gameEnd(String info) {
        if (info.equals("1")) {
            System.out.println("Game " + count + " finished, Player 1 win, Player 2 lose.");
            return true;
        }
        if (info.equals("-1")) {
            System.out.println("Game " + count + " finished, Player 1 lose, Player 2 win.");
            return true;
        }
        if (info.equals("0")) {
            System.out.println("Game " + count + " finished, Player 1 and Player 2 draw.");
            return true;
        }
        return false;
    }
}
