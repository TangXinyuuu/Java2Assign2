package application;

import javafx.application.Platform;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    Server server = null;
    Socket clientSocket;

    int id = 0;

    String idName;

    OutputStream osServer;
    InputStreamReader inServer;

    BufferedReader bufferedReader;

    PlayingConnection playingConnection;

    public ClientHandler(Server server, Socket clientSocket) throws IOException {
        this.server = server;
        this.clientSocket = clientSocket;
        osServer = clientSocket.getOutputStream();
        inServer = new InputStreamReader(clientSocket.getInputStream());
        bufferedReader = new BufferedReader(inServer);
    }

    public void sendConnMessage() throws IOException {
        byte[] msg = "Connected\n".getBytes();
        osServer.write(msg);
        System.out.println("Connecting msg has sent");
    }

//    public void getIdName() throws IOException {
//        byte[] msg = "wantIdName\n".getBytes();
//        osServer.write(msg);
//        System.out.println("I want to know my id name");
//    }

    public void sendWinMessage() throws IOException {
        byte[] msg = "Win\n".getBytes();
        osServer.write(msg);
        System.out.println("Winning msg has sent");
//        close();
//        System.out.println("ClientHandler closed");
    }

    public void sendDrawMessage() throws IOException {
        byte[] msg = "Draw\n".getBytes();
        osServer.write(msg);
        System.out.println("Draw msg has sent");
//        close();
//        System.out.println("ClientHandler closed");
    }

    public void sendLoseMessage() throws IOException {
        byte[] msg = "Lose\n".getBytes();
        osServer.write(msg);
        System.out.println("Losing msg has sent");
//        close();
//        System.out.println("ClientHandler closed");
    }

    public void sendClickMessage(int x, int y) throws IOException {
        String s = "repos" + " " + Integer.toString(x) + " " + Integer.toString(y) + "\n";
        byte[] msg = s.getBytes();
        osServer.write(msg);
        System.out.println("Return position msg has sent：" + s.replace('\n', ' '));
    }


    public void toClose() throws IOException {
        byte[] msg = "Close\n".getBytes();
        osServer.write(msg);
        System.out.println("Close msg has sent");
    }


    public boolean isClientClosed(){
        try{
            clientSocket.sendUrgentData(0xFF);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        }catch(Exception se){
            return true;
        }
    }

    public void opClosed() throws IOException {
        byte[] msg = "opClosed\n".getBytes();
        osServer.write(msg);
        System.out.println("opClosed msg has sent");
    }

    public void infoChange() throws IOException {
        byte[] msg = "infoChange\n".getBytes();
        osServer.write(msg);
        System.out.println("infoChange msg has sent");
    }

    @Override
    public void run() {
        int readLen = 0;
        while (true) {
            if (isClientClosed()){
                try {
                    if (playingConnection != null) {
                        playingConnection.playerExit(id);
                        toClose();
                    }else {
                        continue;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            String message = null;
            try {
                message = bufferedReader.readLine();
            } catch (IOException e) {
               break;
            }
            if(message == null){
                try {
                    if (playingConnection != null) {
                        playingConnection.playerExit(id);
                        toClose();
                    }else {
                        continue;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            String[] parts = message.replace('\n', ' ').split(" ");
            if (message.equals("WindowClosed")) {
                try {
                    playingConnection.playerExit(id);
                    toClose();
                } catch (IOException ignored) {

                }
                break;
            } else if(message.equals("selfExited")){
                try {
                    server.removeClientInQueue(this);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if(parts[0].equals("wantOp")){
                try {
                    server.match(this, parts[1]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if (parts[0].equals("pos")) {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                System.out.println("ClientHandler receive pos " + x + " " + y);
                try {
                    playingConnection.clickPos(id, x, y);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            else if (parts[0].equals("id")) {
                String id = parts[1];
                this.idName = id;
                Server.hashMap.put(id, this);
                System.out.println("ClientHandler receive idName " + id);
            }
            else if (message.equals("CloseMe")) {
                try {
                    toClose();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

