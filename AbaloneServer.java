package abaloneserver;

import java.io.*;
import java.net.*;
import java.util.*;

public class AbaloneServer extends Thread {

    private static final int PORT = 8989;
    private static final int BACKLOG_NUMBER = 10;
    private int userCount = 1;
    private Map<String, UserThread> mapUserThread = new HashMap<>();
    private Queue<String> qmsg = new LinkedList();

    public void addMsgQueue(String msg) {
        qmsg.add(msg);
    }

    public boolean isMsgQueueEmpty() {
        return qmsg.isEmpty();
    }

    public String peekMsgQueue() {
        return qmsg.peek();
    }

    public String pollMsgQueue() {
        return qmsg.poll();
    }

    public void sendMessage(String userName, String msg) {
        if (mapUserThread.containsKey(userName)) {
            mapUserThread.get(userName).sendMessage(msg);
        }
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT, BACKLOG_NUMBER)) {
            System.out.println("Abalone Server is listening on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                String uniqueUserName = "USER_" + userCount;
                System.out.println("New user connected: " + uniqueUserName);
                UserThread newUser = new UserThread(socket, this, uniqueUserName);
                mapUserThread.put(uniqueUserName, newUser);
                userCount++;
                newUser.start();
                Thread.sleep(100);
            }
        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        } catch (InterruptedException ie) {
            System.out.println("Error in UserThread: " + ie.getMessage());
            ie.printStackTrace();
        }
    }

    public void stopClient(String uniqueName) {
        UserThread ut = mapUserThread.get(uniqueName);
        if (ut != null) {
            ut.setStop(true);
            mapUserThread.remove(ut);
        }
    }

    public void stopClients() {
        for (UserThread ut : mapUserThread.values()) {
            ut.sendMessage("STOP");
            ut.setStop(true);
        }
    }
}
