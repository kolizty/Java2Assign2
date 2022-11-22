import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private static final int PLAY_1 = 1;
    private static final int PLAY_2 = -1;
    private static final int EMPTY = 0;
    private static final int BOUND = 90;
    private static final int OFFSET = 15;

    private static int PLAYER, OPPONENT;

    @FXML
    private Pane base_square;

    @FXML
    private Rectangle game_panel;

    private boolean TURN;

    private static final int[][] chessBoard = new int[3][3];
    private static final boolean[][] flag = new boolean[3][3];

    private Socket socket;

    BufferedReader br;
    PrintWriter pw;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket("127.0.0.1", 8080);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
            String info = br.readLine();
            if (info.equals("1")) {
                System.out.println("You are player 1, waiting for match.");
                TURN = true;
                PLAYER = PLAY_1;
                OPPONENT = PLAY_2;
            } else {
                System.out.println("You are player 2, match start.");
                TURN = false;
                PLAYER = PLAY_2;
                OPPONENT = PLAY_1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void gamePanelOnMouseMoved() throws Exception {
        if (!TURN) {
            String info;
            if ((info = br.readLine()) != null) {
                String[] sp = info.split(" ");
                int x = Integer.parseInt(sp[0]), y = Integer.parseInt(sp[1]);
                refreshBoard(x, y);
                TURN = true;
            }
        }
        if (getWinner() == EMPTY){
            System.out.println("Game draw!");
            pw.write("0");
        }
        if (getWinner() == PLAYER){
            System.out.println("You Win!");
            pw.write("1");
        }
        if (getWinner() == OPPONENT){
            System.out.println("You lose!");
            pw.write("-1");
        }
        pw.flush();
    }

    @FXML
    public void gamePanelOnMouseClicked(javafx.scene.input.MouseEvent mouseEvent) throws Exception {
        if (TURN) {
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            int x = (int) (mouseEvent.getX() / BOUND);
            int y = (int) (mouseEvent.getY() / BOUND);
            if (refreshBoard(x, y)) {
                TURN = !TURN;
                String info = x + " " + y;
                pw.write(info);
                pw.flush();
            }
        }
    }

    private boolean refreshBoard(int x, int y) {
        if (chessBoard[x][y] == EMPTY) {
            chessBoard[x][y] = PLAYER;
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
        base_square.getChildren().add(circle);
        circle.setCenterX(i * BOUND + BOUND / 2.0 + OFFSET);
        circle.setCenterY(j * BOUND + BOUND / 2.0 + OFFSET);
        circle.setRadius(BOUND / 2.0 - OFFSET / 2.0);
        circle.setStroke(Color.RED);
        circle.setFill(Color.TRANSPARENT);
        flag[i][j] = true;
    }

    private void drawLine(int i, int j) {
        Line line_a = new Line();
        Line line_b = new Line();
        base_square.getChildren().add(line_a);
        base_square.getChildren().add(line_b);
        line_a.setStartX(i * BOUND + OFFSET * 1.5);
        line_a.setStartY(j * BOUND + OFFSET * 1.5);
        line_a.setEndX((i + 1) * BOUND + OFFSET * 0.5);
        line_a.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        line_a.setStroke(Color.BLUE);

        line_b.setStartX((i + 1) * BOUND + OFFSET * 0.5);
        line_b.setStartY(j * BOUND + OFFSET * 1.5);
        line_b.setEndX(i * BOUND + OFFSET * 1.5);
        line_b.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        line_b.setStroke(Color.BLUE);
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
                || chessBoard[0][2] + chessBoard[1][1] + chessBoard[2][0] == 3)
            return PLAY_1;
        if (chessBoard[0][0] + chessBoard[0][1] + chessBoard[0][2] == -3
                || chessBoard[1][0] + chessBoard[1][1] + chessBoard[1][2] == -3
                || chessBoard[2][0] + chessBoard[2][1] + chessBoard[2][2] == -3
                || chessBoard[0][0] + chessBoard[1][0] + chessBoard[2][0] == -3
                || chessBoard[0][1] + chessBoard[1][1] + chessBoard[2][1] == -3
                || chessBoard[0][2] + chessBoard[1][2] + chessBoard[2][2] == -3
                || chessBoard[0][0] + chessBoard[1][1] + chessBoard[2][2] == -3
                || chessBoard[0][2] + chessBoard[1][1] + chessBoard[2][0] == -3)
            return PLAY_2;
        if (isFull())
            return EMPTY;
        return 2;
    }

    private boolean isFull() {
        for (int i = 0; i < chessBoard.length; i++)
            for (int j = 0; j < chessBoard[i].length; j++)
                if (chessBoard[i][j] == EMPTY)
                    return false;
        return true;
    }
}
