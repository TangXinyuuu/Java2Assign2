package application;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

  static ArrayList<String> waiting_name_list = new ArrayList<>();
  Queue<ClientHandler> waiting_list = new LinkedList<>();

  static HashMap<String, ClientHandler> hashMap = new HashMap<>();
  ServerSocket serverSocket = null;


  public Server() throws IOException {
    serverSocket = new ServerSocket(9996);
    String file_path = "./src/application/waiting.txt";
    File file = new File(file_path);
    String content = "";
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    fileOutputStream.write(content.getBytes("gbk"));
    fileOutputStream.flush();
    fileOutputStream.close();
  }

  public void removeClientInQueue(ClientHandler clientHandler) throws IOException {
    waiting_list.remove(clientHandler);
    infoListChange();
  }

  public void waitConn() throws IOException {
    while (true) {
      Socket s = serverSocket.accept();
      System.out.println("has accepted");
      ClientHandler clientHandler = new ClientHandler(this, s);
      Thread thread = new Thread(clientHandler);
      thread.start();
      waiting_list.add(clientHandler);
      infoListChange();
      System.out.println("add to wait");

    }
  }

  public synchronized void match(ClientHandler clientHandler, String opName) throws IOException {
    System.out.println("start match");
    System.out.println(clientHandler.idName);
    System.out.println(opName);

    ClientHandler opHandler = hashMap.get(opName);
    System.out.println(opName + " : " + opHandler.idName);

    PlayingConnection playingConnection = new PlayingConnection(clientHandler, opHandler);
    clientHandler.playingConnection = playingConnection;
    opHandler.playingConnection = playingConnection;

    waiting_list.remove(opHandler);
    waiting_list.remove(clientHandler);

    String file_path = "./src/application/waiting.txt";
    File file = new File(file_path);

    BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
    String line_content = null;
    StringBuffer buffer = new StringBuffer();
    while ((line_content = br.readLine()) != null) {
      if (line_content.equals(opHandler.idName) || line_content.equals(clientHandler.idName)) {
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

    clientHandler.sendConnMessage();
    opHandler.sendConnMessage();

    infoListChange();
    System.out.println("finish match");

  }

  public synchronized void infoListChange() throws IOException {
    for (ClientHandler clientHandler : waiting_list) {
      clientHandler.infoChange();
    }
  }


  public static void main(String[] args) throws IOException {
    System.out.println("ppppp");
    Server server = new Server();
    server.waitConn();
  }

}
