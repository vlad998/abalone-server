package abaloneserver;

import java.io.*;
import java.net.*;

public class UserThread extends Thread {

    private Socket socket;
    private AbaloneServer server;
    private PrintWriter writer;
    private boolean stop;
    String uniqueUserName;

    public UserThread(Socket socket, AbaloneServer server, String uniqueUserName) {
        this.socket = socket;
        this.server = server;
        this.uniqueUserName = uniqueUserName;
        stop = true;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public void run() {
        stop = false;
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            String firstMessage = reader.readLine();
            if (firstMessage != null) {
                firstMessage = firstMessage.trim();
                System.out.println(uniqueUserName + ":" + firstMessage);
                server.addMsgQueue(uniqueUserName + ":" + firstMessage);
            }
            String clientMessage;
            while (!stop) {
                clientMessage = reader.readLine();
                if (clientMessage != null) {
                    clientMessage = clientMessage.trim();
                    if (clientMessage.length() > 0) {
                        server.addMsgQueue(uniqueUserName + ":" + clientMessage);
                        System.out.println(uniqueUserName + ":" + clientMessage);
                        if (clientMessage.equals("STOP")) {
                            break;
                        }
                    }
                }
                Thread.sleep(100);
            }
            stop = true;
            socket.close();
            System.out.println("User " + uniqueUserName + " has quitted.");
        } catch (IOException ioe) {
            System.out.println("Error in UserThread: " + ioe.getMessage());
            ioe.printStackTrace();
        } catch (InterruptedException ie) {
            System.out.println("Error in UserThread: " + ie.getMessage());
            ie.printStackTrace();
        }
    }

    void sendMessage(String message) {
        writer.println(message);
    }
}
