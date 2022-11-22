package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.sql.Time;

public class PlayingConnection {
    ClientHandler player1;
    ClientHandler player2;

    int turn = 0;

    int[][] board = new int[3][3];

    int[] dx = new int[]{0, 0, 1, -1, -1, 1, 1, -1};

    int[] dy = new int[]{1, -1, 0, 0, 1, -1, 1, -1};

    public PlayingConnection(ClientHandler clientHandler1, ClientHandler clientHandler2) {
        this.player1 = clientHandler1;
        clientHandler1.id = 1;
        this.player2 = clientHandler2;
        clientHandler2.id = -1;
    }

    public ClientHandler getPlayer(int id) {
        if (id == 1) {
            return player1;
        } else return player2;
    }

    public void clickPos(int id, int x, int y) throws IOException, InterruptedException {
        if (turn == 0 || turn == id) {
            System.out.println("It's my turn");
            player1.sendClickMessage(x, y);
            player2.sendClickMessage(x, y);
            System.out.println("turn: " + turn + " id: " + id + " position: " + x + " " + y);
            board[x][y] = id;
            if (turn == 0) {
                turn = -id;
            } else {
                turn = -turn;
            }
            checkEnd(x, y);
        }
    }

    public void win(ClientHandler clientHandler) throws IOException, InterruptedException {
        clientHandler.sendWinMessage();
        System.out.println(clientHandler.id + " win");
    }

    public void lose(ClientHandler clientHandler) throws IOException, InterruptedException {
        clientHandler.sendLoseMessage();
        System.out.println(clientHandler.id + " lose");
    }

    public void draw() throws IOException, InterruptedException {
        player1.sendDrawMessage();
        player2.sendDrawMessage();
        System.out.println("draw");
    }

    public void changeInfoWinLose(ClientHandler winner, ClientHandler loser) throws IOException, InterruptedException {
        String file_path = "./src/application/info.txt";
        File file = new File(file_path);

        BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
        String s = null;
        StringBuffer buffer = new StringBuffer();
        int win_time = 0;
        int lose_time = 0;
        int draw_time = 0;
        while ((s = br.readLine()) != null) { //使用readLine方法，一次读一行
//            System.out.println("!!!!!!" + s);
            String[] parts = s.split(" ");
            if (parts[0].equals(winner.idName)) {
                win_time = Integer.parseInt(parts[2]) + 1;
                lose_time = Integer.parseInt(parts[3]);
                draw_time = Integer.parseInt(parts[4]);
                String new_line = parts[0] + " " + parts[1] + " " + win_time + " " + lose_time + " " + draw_time + "\n";
                buffer.append(new_line);
            } else if (parts[0].equals(loser.idName)){
                win_time = Integer.parseInt(parts[2]);
                lose_time = Integer.parseInt(parts[3]) + 1;
                draw_time = Integer.parseInt(parts[4]);
                String new_line = parts[0] + " " + parts[1] + " " + win_time + " " + lose_time + " " + draw_time + "\n";
                buffer.append(new_line);
            }else {
                buffer.append(s + "\n");
            }
        }
        String content = buffer.toString();
//        System.out.print(content);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(content.getBytes("gbk"));
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public void changeInfoDraw() throws IOException, InterruptedException {
        String file_path = "./src/application/info.txt";
        File file = new File(file_path);

        BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
        String s = null;
        StringBuffer buffer = new StringBuffer();
        int win_time = 0;
        int lose_time = 0;
        int draw_time = 0;
        while ((s = br.readLine()) != null) { //使用readLine方法，一次读一行
            String[] parts = s.split(" ");
            if (parts[0].equals(player1.idName) || parts[0].equals(player2.idName)) {
                win_time = Integer.parseInt(parts[2]);
                lose_time = Integer.parseInt(parts[3]);
                draw_time = Integer.parseInt(parts[4]) + 1;
                String new_line = parts[0] + " " + parts[1] + " " + win_time + " " + lose_time + " " + draw_time + "\n";
                buffer.append(new_line);
            }else {
                buffer.append(s + "\n");
            }
        }
        String content = buffer.toString();
//        System.out.print(content);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(content.getBytes("gbk"));
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public void playerExit(int id) throws IOException {
        ClientHandler rest_player = getPlayer(-id);
        rest_player.opClosed();
    }

    public boolean checkEnd(int x, int y) throws IOException, InterruptedException {
        for (int m = 0; m < 3; m++) {
            for (int n = 0; n < 3; n++) {
                if (board[m][n] != 0) {
                    for (int i = 0; i < 8; i++) {

                        int times = 0;
                        int tmp_x = m;
                        int tmp_y = n;
                        while (tmp_x >= 0 && tmp_x < 3 && tmp_y >= 0 && tmp_y < 3 && board[tmp_x][tmp_y] == board[m][n]) {
                            tmp_x = tmp_x + dx[i];
                            tmp_y = tmp_y + dy[i];
                            times = times + 1;
                        }
                        if (times == 3) {
//                            System.out.println("iiiiiiiiiiiiiiii");
                            ClientHandler winner = getPlayer(board[m][n]);
                            ClientHandler loser = getPlayer(-board[m][n]);
                            this.changeInfoWinLose(winner, loser);
                            this.win(winner);
                            this.lose(loser);
                            return true;
                        }
                    }
                }
            }
        }
        for (int m = 0; m < 3; m++) {
            for (int n = 0; n < 3; n++) {
                if (board[m][n] == 0) {
                    System.out.println("Continue");
                    return false;
                }
            }
        }
        changeInfoDraw();
        draw();
        return true;
    }


}
