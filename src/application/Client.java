package application;

import javafx.application.Platform;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client implements Runnable {

  Socket socket;
  Main mainPage;

  String idName;

  OutputStream osClient;
  InputStreamReader inClient;

  BufferedReader bufferedReader;


  public Client(String idName) throws IOException {
    this.idName = idName;
    this.socket = new Socket(InetAddress.getLocalHost(), 9996);
    System.out.println("try to connect..");
    this.osClient = socket.getOutputStream();
    this.inClient = new InputStreamReader(socket.getInputStream());
    this.bufferedReader = new BufferedReader(inClient);

//        String s = "id "+ idName + "\n";
//        byte[] msg = s.getBytes();
//        osClient.write(msg);
//        System.out.println("send my id to handler");
  }

  public void clickPos(int x, int y) throws IOException {
    String s = "pos" + " " + Integer.toString(x) + " " + Integer.toString(y) + "\n";
    byte[] msg = s.getBytes();
    osClient.write(msg);
    System.out.println("Position msg has sent: " + s.replace('\n', ' '));
  }

  public void chooseOppo(String opName) throws IOException {
    String s = "wantOp " + opName + "\n";
    byte[] msg = s.getBytes();
    osClient.write(msg);
    System.out.println("wantOp msg has sent: " + s.replace('\n', ' '));
  }


  public void toClose() throws IOException {
    String s = "CloseMe\n";
    byte[] msg = s.getBytes();
    osClient.write(msg);
    System.out.println("I want to close myself");
  }

  public void sendIdName() throws IOException {
    String s = "id " + idName + "\n";
    byte[] msg = s.getBytes();
    osClient.write(msg);
    System.out.println("send my id to handler");
  }

  public void windowClosed() throws IOException {
    String s = "WindowClosed\n";
    byte[] msg = s.getBytes();
    osClient.write(msg);
    System.out.println("Window closed");
  }

  public void selfExited() throws IOException {
    String s = "selfExited\n";
    byte[] msg = s.getBytes();
    osClient.write(msg);
    System.out.println("self Exited");
  }


  public boolean isServerClosed() {
    try {
      socket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
      return false;
    } catch (Exception se) {
      return true;
    }
  }


  @Override
  public void run() {
    while (true) {
      if (isServerClosed()) {
        Platform.runLater(() -> {
          mainPage.serverClosed();
        });
        break;
      }
      String message = null;
      try {
        System.out.println("trying to receive");
        message = bufferedReader.readLine();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      if (message == null) {
        Platform.runLater(() -> {
          mainPage.serverClosed();
        });
        break;
      }
      String[] parts = message.replace('\n', ' ').split(" ");
      System.out.println("receive " + message);
      if (message.equals("Connected")) {
        Platform.runLater(() -> {
          mainPage.isConnected();
        });
        System.out.println("Connected Successfully");
      } else if (message.equals("Win")) {
        Platform.runLater(() -> {
          mainPage.isWin();
        });
        System.out.println("Win!");
        break;
      } else if (message.equals("Lose")) {
        Platform.runLater(() -> {
          mainPage.isLose();
        });
        System.out.println("Lose!");
        break;
      } else if (message.equals("Draw")) {
        Platform.runLater(() -> {
          mainPage.isDraw();
        });
        System.out.println("Draw!");
        break;
      } else if (message.equals("opClosed")) {
        Platform.runLater(() -> {
          mainPage.opClosed();
        });
        System.out.println("You op exited");
        break;
      } else if (message.equals("infoChange")) {
        Platform.runLater(() -> {
          try {
            mainPage.showWaiting();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
        System.out.println("waiting list change");
      }
//            else if (message.equals("wantIdName")) {
//                try {
//                    sendIdName();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
      else if (parts[0].equals("repos")) {
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        System.out.println("Client receive repos " + x + " " + y);
        Platform.runLater(() -> {
          mainPage.refreshBoard(x, y);
        });
      } else if (message.equals("Close")) {
        break;
      }
    }
  }
}
