package application;

import application.controller.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Main extends Application {

  Pane root = new Pane();
  Stage stage;
  Controller controller;

  boolean isConnected = false;

  Client client;

  //    public Main() throws IOException {
//        Client client = new Client();
//        client.mainPage = this;
//    }
  public void isWin() {
    try {
      Text text = new Text(100, 100, "Win!");
      root.getChildren().add(text);
//            Scene scene = new Scene(root, 600, 400);
//            stage.setScene(scene);
//            stage.setTitle("Tic Tac Toe");
//            stage.setResizable(false);

      String info = getInfo();
      Text text1 = new Text(100, 250, info);
      root.getChildren().add(text1);

      stage.show();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getInfo() throws IOException {
    String file_path = "./src/application/info.txt";
    File file = new File(file_path);

    BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
    String s = null;
    int win_time = 0;
    int lose_time = 0;
    int draw_time = 0;
    while ((s = br.readLine()) != null) { //使用readLine方法，一次读一行
      String[] parts = s.split(" ");
      if (parts[0].equals(client.idName)) {
        win_time = Integer.parseInt(parts[2]);
        lose_time = Integer.parseInt(parts[3]);
        draw_time = Integer.parseInt(parts[4]);
      }
    }

    String info =
        client.idName + ": \n" + "Win: " + win_time + "\n" + "Lose: " + lose_time + "\n" + "Draw: "
            + draw_time + "\n";
    return info;
  }

  public void isDraw() {
    try {
      Text text = new Text(100, 100, "Draw!");
      root.getChildren().add(text);
//            Scene scene = new Scene(root, 600, 400);
//            stage.setScene(scene);
//            stage.setTitle("Tic Tac Toe");
//            stage.setResizable(false);
      String info = getInfo();
      Text text1 = new Text(100, 250, info);
      root.getChildren().add(text1);
      stage.show();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void opClosed() {
    try {
      root = new Pane();
      Text text = new Text(100, 100, "Sorry! You opponent has exited");
      root.getChildren().add(text);
      Scene scene = new Scene(root, 600, 400);
      stage.setScene(scene);
      stage.setTitle("Tic Tac Toe");
      stage.setResizable(false);
      stage.show();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void isLose() {
    try {
      Text text = new Text(100, 100, "Lose!");
      root.getChildren().add(text);
//            Scene scene = new Scene(root, 600, 400);
//            stage.setScene(scene);
//            stage.setTitle("Tic Tac Toe");
//            stage.setResizable(false);
      String info = getInfo();
      Text text1 = new Text(100, 250, info);
      root.getChildren().add(text1);
      stage.show();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void serverClosed() {
    try {
      root = new Pane();
      Text text = new Text(100, 100, "Sorry! Server is closed!");
      root.getChildren().add(text);
      Scene scene = new Scene(root, 600, 400);
      stage.setScene(scene);
      stage.setTitle("Tic Tac Toe");
      stage.setResizable(false);
      stage.show();
      client.toClose();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      stage = primaryStage;
      GridPane grid = new GridPane();
      grid.setAlignment(Pos.CENTER);
      grid.setHgap(10);
      grid.setVgap(10);
      grid.setPadding(new Insets(25, 25, 25, 25));

      Text title = new Text("login/register:");
      Label Account = new Label("id: ");
      TextField userAccount = new TextField();
      Label pwd = new Label("password: ");
      PasswordField userPwd = new PasswordField();

      Button confirm = new Button("confirm");

      grid.add(title, 0, 0, 2, 1);
      grid.add(Account, 0, 1);
      grid.add(userAccount, 1, 1);
      grid.add(pwd, 0, 2);
      grid.add(userPwd, 1, 2);

      HBox panel = new HBox(40);
      panel.setAlignment(Pos.BOTTOM_RIGHT);
      panel.getChildren().add(confirm);
      grid.add(panel, 1, 4);

      stage.setScene(new Scene(grid, 350, 300));

      stage.show();

      confirm.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent arg0) {
          int reply = 0;
          String username = userAccount.getText();
          String password = userPwd.getText();
          try {
            reply = login(username, password);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          if (reply == 1) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("login");
            alert.setHeaderText(null);
            alert.setContentText("log in successfully");
            alert.showAndWait();
            System.out.println("log in successfully");
            try {
              isWaiting(username);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          } else if (reply == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("error");
            alert.setHeaderText(null);
            alert.setContentText("The user has already registered, wrong password");
            alert.showAndWait();
            userAccount.clear();
            userPwd.clear();
            System.out.println("The user has already registered, wrong password");
          } else if (reply == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("login");
            alert.setHeaderText(null);
            alert.setContentText("Never register before, Auto register and log in successfully");
            alert.showAndWait();
            System.out.println("Never register before, Auto register and log in successfully");
            try {
              isWaiting(username);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
        }

      });

      stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
          try {
            if (client != null) {
              if (isConnected) {
                client.windowClosed();
              } else {
                client.selfExited();
                Platform.exit();
                System.exit(0);
              }
            } else {
              Platform.exit();
            }
          } catch (IOException e) {
            Platform.exit();
          }
        }
      });

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static int login(String username, String password) throws IOException {
    int state = 0;
    String file_path = "./src/application/info.txt";
    File file = new File(file_path);
    String user0 = username + " " + password + " 0 0 0\n";
    if (!file.exists()) {
      file.createNewFile();
      try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
          new FileOutputStream(file_path, true))) {
        outputStreamWriter.write(user0);
      } catch (Exception ignored) {

      }
      return 0;
    }

    try {
      BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
      String s = null;
      while ((s = br.readLine()) != null) { //使用readLine方法，一次读一行
        String[] parts = s.split(" ");
        if (username.equals(parts[0])) {
          if (password.equals(parts[1])) {
            return 1;
          } else {
            return -1;
          }
        }
      }
      br.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
        new FileOutputStream(file_path, true))) {
      outputStreamWriter.write(user0);
    } catch (Exception ignored) {

    }
    return state;
  }

  public void isWaiting(String idName) throws IOException {
    client = new Client(idName);
    client.sendIdName();
//        Server.waiting_name_list.add(idName);
    addWaitClient(idName);
    System.out.println("my id name: " + idName);
    client.mainPage = this;
    Thread thread = new Thread(client);
    thread.start();
    showWaiting();
  }

  public void showWaiting() throws IOException {
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(25, 25, 25, 25));

    Text text = new Text(40, 60, "choose an opponent/wait to be chosen...");

//        StringBuffer buffer = new StringBuffer();
//        buffer.append("waiting list:\n");
//        for (String name: Server.waiting_name_list){
//            buffer.append(name + "\n");
//            System.out.println("clientHandler.idName " + name);
//        }
    String file_path = "./src/application/waiting.txt";
    File file = new File(file_path);

    BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
    String line_content = null;
    StringBuffer buffer = new StringBuffer();
    buffer.append("waiting list:\n");
    while ((line_content = br.readLine()) != null) {
      buffer.append(line_content + "\n");
    }
    String content = buffer.toString();
    Text wait_text = new Text(40, 60, content);
    Text account = new Text("Account: " + client.idName + "\n");

    Label chooseHint = new Label("choose my opponent: ");
    TextField chooseField = new TextField();

    Button confirm = new Button("confirm");

    grid.add(account, 0, 0);
    grid.add(wait_text, 0, 1);
    grid.add(chooseHint, 3, 3);
    grid.add(chooseField, 4, 3);
    grid.add(text, 3, 6, 2, 1);

    HBox panel = new HBox(40);
    panel.setAlignment(Pos.BOTTOM_RIGHT);
    panel.getChildren().add(confirm);
    grid.add(panel, 5, 5);

    stage.setScene(new Scene(grid, 600, 400));
    stage.setTitle("Wait");
    stage.setResizable(false);
    stage.show();

    confirm.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        String username = chooseField.getText();
        try {
          client.chooseOppo(username);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

    });
  }


  public void isConnected() {
    try {
//            Server.waiting_name_list.remove(client.idName);
      System.out.println(client.idName + " start to connect");
//            deleteWaiting(client.idName);
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getClassLoader().getResource("mainUI.fxml"));
      root = fxmlLoader.load();
      controller = fxmlLoader.getController();
      controller.setClient(client);

      isConnected = true;
      stage.setTitle("Tic Tac Toe");
      stage.setScene(new Scene(root));
      stage.setResizable(false);
      Text text = new Text(50, 50, client.idName);
      root.getChildren().add(text);
      stage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void refreshBoard(int x, int y) {
    try {
      controller.refreshMove(x, y);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void addWaitClient(String s) throws IOException {
    String file_path = "./src/application/waiting.txt";
    File file = new File(file_path);

    if (!file.exists()) {
      file.createNewFile();
      try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
          new FileOutputStream(file_path, true))) {
        outputStreamWriter.write(s + "\n");
      } catch (Exception ignored) {

      }
      return;
    }

    try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
        new FileOutputStream(file_path, true))) {
      outputStreamWriter.write(s + "\n");
    } catch (Exception ignored) {

    }

  }

  public void deleteWaiting(String s) throws IOException {
    String file_path = "./src/application/waiting.txt";
    File file = new File(file_path);

    BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
    String line_content = null;
    StringBuffer buffer = new StringBuffer();
    while ((line_content = br.readLine()) != null) {
      if (s.equals(line_content)) {
        continue;
      }
      buffer.append(line_content + "\n");
    }
    String content = buffer.toString();
    System.out.print(content);
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    fileOutputStream.write(content.getBytes("gbk"));
    fileOutputStream.flush();
    fileOutputStream.close();
  }

  public static void main(String[] args) throws IOException {
    System.out.println("kkkkk");
    launch(args);
  }

}
