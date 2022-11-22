import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Controller implements Initializable {
    private static final int PLAY_1 = 1;
    private static final int PLAY_2 = -1;
    private static final int EMPTY = 0;
    private static final int BOUND = 90;
    private static final int OFFSET = 15;
    private static int PLAYER;
    private static int OPPONENT;

    @FXML
    private Pane baseSquare;

    @FXML
    private Rectangle gamePanel;

    private boolean turn;
    private boolean finish;

    private static final int[][] chessBoard = new int[3][3];
    private static final boolean[][] flag = new boolean[3][3];

    private Socket socket;

    private BufferedReader br;
    private PrintWriter pw;

    private int count;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket("127.0.0.1", 8080);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
            String info = br.readLine();
            count = Integer.parseInt(info.split(" ")[1]);
            finish = false;
            if (info.split(" ")[0].equals("1")) {
                System.out.println("You are player 1, waiting for game " + count + " to match.");
                turn = true;
                PLAYER = PLAY_1;
                OPPONENT = PLAY_2;
            } else {
                System.out.println("You are player 2, game " + count + " start.");
                turn = false;
                PLAYER = PLAY_2;
                OPPONENT = PLAY_1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void gamePanelOnMouseMoved() {
        try {
            if (!turn) {
                String info = br.readLine();
                if (info != null) {
                    String[] sp = info.split(" ");
                    int x = Integer.parseInt(sp[0]);
                    int y = Integer.parseInt(sp[1]);
                    refreshBoard(x, y);
                    turn = true;
                }
                if (getWinner() == EMPTY) {
                    System.out.println("Game draw!");
                    pw.println("0");
                    gamePanel.setDisable(true);
                    finish = true;
                }
                if (getWinner() == PLAYER) {
                    System.out.println("You Win!");
                    gamePanel.setDisable(true);
                    finish = true;
                }
                if (getWinner() == OPPONENT) {
                    System.out.println("You lose!");
                    gamePanel.setDisable(true);
                    finish = true;
                }
                if (getWinner() == PLAY_1) {
                    pw.println("1");
                }
                if (getWinner() == PLAY_2) {
                    pw.println("-1");
                }
                pw.flush();
            }
        } catch (Exception e) {
            if (e.getMessage().equals("Connection reset")) {
                System.out.println("Server disconnected, game exit.");
                System.exit(-1);
            } else {
                e.printStackTrace();
            }
        }
        if (finish) {
            System.exit(0);
        }
    }

    @FXML
    public void gamePanelOnMouseClicked(javafx.scene.input.MouseEvent mouseEvent) {
        if (turn) {
            int x = (int) (mouseEvent.getX() / BOUND);
            int y = (int) (mouseEvent.getY() / BOUND);
            if (refreshBoard(x, y)) {
                turn = !turn;
                String info = x + " " + y;
                pw.println(info);
                pw.flush();
            }
        }
    }

    private boolean refreshBoard(int x, int y) {
        if (chessBoard[x][y] == EMPTY) {
            chessBoard[x][y] = turn ? PLAYER : OPPONENT;
            drawChess();
            return true;
        }
        return false;
    }

    private void drawChess() {
        for (int i = 0; i < chessBoard.length; i++) {
            for (int j = 0; j < chessBoard[0].length; j++) {
                if (flag[i][j]) {
                    // This square has been drawing, ignore.
                    continue;
                }
                switch (chessBoard[i][j]) {
                    case PLAY_1:
                        drawCircle(i, j);
                        break;
                    case PLAY_2:
                        drawLine(i, j);
                        break;
                    case EMPTY:
                        // do nothing
                        break;
                    default:
                        System.err.println("Invalid value!");
                }
            }
        }
    }

    private void drawCircle(int i, int j) {
        Circle circle = new Circle();
        baseSquare.getChildren().add(circle);
        circle.setCenterX(i * BOUND + BOUND / 2.0 + OFFSET);
        circle.setCenterY(j * BOUND + BOUND / 2.0 + OFFSET);
        circle.setRadius(BOUND / 2.0 - OFFSET / 2.0);
        circle.setStroke(Color.RED);
        circle.setFill(Color.TRANSPARENT);
        flag[i][j] = true;
    }

    private void drawLine(int i, int j) {
        Line lineA = new Line();
        Line lineB = new Line();
        baseSquare.getChildren().add(lineA);
        baseSquare.getChildren().add(lineB);
        lineA.setStartX(i * BOUND + OFFSET * 1.5);
        lineA.setStartY(j * BOUND + OFFSET * 1.5);
        lineA.setEndX((i + 1) * BOUND + OFFSET * 0.5);
        lineA.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        lineA.setStroke(Color.BLUE);

        lineB.setStartX((i + 1) * BOUND + OFFSET * 0.5);
        lineB.setStartY(j * BOUND + OFFSET * 1.5);
        lineB.setEndX(i * BOUND + OFFSET * 1.5);
        lineB.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        lineB.setStroke(Color.BLUE);
        flag[i][j] = true;
    }

    private int getWinner() {
        if (chessBoard[0][0] + chessBoard[0][1] + chessBoard[0][2] == 3
                || chessBoard[1][0] + chessBoard[1][1] + chessBoard[1][2] == 3
                || chessBoard[2][0] + chessBoard[2][1] + chessBoard[2][2] == 3
                || chessBoard[0][0] + chessBoard[1][0] + chessBoard[2][0] == 3
                || chessBoard[0][1] + chessBoard[1][1] + chessBoard[2][1] == 3
                || chessBoard[0][2] + chessBoard[1][2] + chessBoard[2][2] == 3
                || chessBoard[0][0] + chessBoard[1][1] + chessBoard[2][2] == 3
                || chessBoard[0][2] + chessBoard[1][1] + chessBoard[2][0] == 3) {
            return PLAY_1;
        }
        if (chessBoard[0][0] + chessBoard[0][1] + chessBoard[0][2] == -3
                || chessBoard[1][0] + chessBoard[1][1] + chessBoard[1][2] == -3
                || chessBoard[2][0] + chessBoard[2][1] + chessBoard[2][2] == -3
                || chessBoard[0][0] + chessBoard[1][0] + chessBoard[2][0] == -3
                || chessBoard[0][1] + chessBoard[1][1] + chessBoard[2][1] == -3
                || chessBoard[0][2] + chessBoard[1][2] + chessBoard[2][2] == -3
                || chessBoard[0][0] + chessBoard[1][1] + chessBoard[2][2] == -3
                || chessBoard[0][2] + chessBoard[1][1] + chessBoard[2][0] == -3) {
            return PLAY_2;
        }
        if (isFull()) {
            return EMPTY;
        }
        return 2;
    }

    private boolean isFull() {
        for (int i = 0; i < chessBoard.length; i++) {
            for (int j = 0; j < chessBoard[i].length; j++) {
                if (chessBoard[i][j] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }
}
