import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread{
    private Socket socket1 = null;
    private Socket socket2 = null;
    private final int count;

    public ServerThread(Socket socket1, Socket socket2, int count){
        this.socket1 = socket1;
        this.socket2 = socket2;
        this.count = count;
    }

    public void run(){
        try{
            while (true){
                BufferedReader br1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
                BufferedReader br2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
                PrintWriter pw1 = new PrintWriter(socket1.getOutputStream());
                PrintWriter pw2 = new PrintWriter(socket2.getOutputStream());
                String info1 = br1.readLine();
                if (info1.equals("1")){
                    System.out.println("Game " + count + " finished, Player 1 win, Player 2 lose.");
                    break;
                }
                if (info1.equals("-1")){
                    System.out.println("Game " + count + " finished, Player 1 lose, Player 2 win.");
                    break;
                }
                if (info1.equals("0")){
                    System.out.println("Game " + count + " finished, Player 1 and Player 2 draw.");
                    break;
                }
                System.out.println("Game " + count + ": Player 1 puts " + info1);
                pw2.println(info1);
                pw2.flush();
                String info2 = br2.readLine();
                if (info2.equals("1")){
                    System.out.println("Game " + count + " finished, Player 1 win, Player 2 lose.");
                    break;
                }
                if (info2.equals("-1")){
                    System.out.println("Game " + count + " finished, Player 1 lose, Player 2 win.");
                    break;
                }
                if (info2.equals("0")){
                    System.out.println("Game " + count + " finished, Player 1 and Player 2 draw.");
                    break;
                }
                System.out.println("Game " + count + ": Player 2 puts " + info2);
                pw1.println(info2);
                pw1.flush();
            }
            socket1.close();
            socket2.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
