package abaloneserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;

class User {

    String uniqueName;
    String name;
    int maxTurns;
    int maxTime;
    int mode;

    User(String unm, String nm) {
        uniqueName = unm;
        name = nm;
        maxTurns = -1;
        maxTime = -1;
        mode = -1;
    }
}

public class ServerMainForm extends javax.swing.JFrame {

    private ServerSocket serverSocket = null;
    private AbaloneServer abaloneServer = null;
    private Timer tmr = null;

    private List<User> waitingListBlue = new ArrayList();
    private List<User> waitingListRed = new ArrayList();
    private List<User> waitingListGreen = new ArrayList();
    private List<User> waitingListOrange = new ArrayList();

    private Map<String, Set<User>> mapBlueToAll = new HashMap<>();
    private Map<String, Set<User>> mapRedToAll = new HashMap<>();
    private Map<String, Set<User>> mapGreenToAll = new HashMap<>();
    private Map<String, Set<User>> mapOrangeToAll = new HashMap<>();

    public ServerMainForm() {
        initComponents();
        abaloneServer = new AbaloneServer();
        abaloneServer.start();

        tmr = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (!abaloneServer.isMsgQueueEmpty()) {
                    String msg = abaloneServer.pollMsgQueue();
                    if (msg != null) {
                        txtClientMessage.setText(msg);
                        interpretMessage(msg);
                    }
                    User userBlueDel = null;
                    for (User userBlue : waitingListBlue) {
                        if (userBlue.maxTime > 0 && userBlue.maxTurns > 0 && userBlue.mode >= 2) {
                            Set<User> sUsers = null;
                            User userRed = null;
                            User userGreen = null;
                            User userOrange = null;
                            if (userBlue.mode == 2) {
                                for (User usr : waitingListRed) {
                                    if (usr.mode == 2) {
                                        userRed = usr;
                                        break;
                                    }
                                }
                                if (userRed != null) {
                                    userBlueDel = userBlue;
                                    waitingListRed.remove(userRed);
                                    sUsers = new HashSet();
                                    sUsers.add(userRed);
                                    mapBlueToAll.put(userBlue.uniqueName, sUsers);
                                    sUsers = new HashSet();
                                    sUsers.add(userBlue);
                                    mapRedToAll.put(userRed.uniqueName, sUsers);
                                    abaloneServer.sendMessage(userRed.uniqueName, "MAXTURNS:" + userBlue.maxTurns);
                                    abaloneServer.sendMessage(userRed.uniqueName, "MAXTIME:" + userBlue.maxTime);
                                    abaloneServer.sendMessage(userRed.uniqueName, "USER:" + userBlue.name);
                                    abaloneServer.sendMessage(userBlue.uniqueName, "USER:" + userRed.name);
                                    abaloneServer.sendMessage(userRed.uniqueName, "START");
                                    abaloneServer.sendMessage(userBlue.uniqueName, "START");
                                    break;
                                }
                            } else if (userBlue.mode == 3) {
                                for (User usr : waitingListRed) {
                                    if (usr.mode == 3) {
                                        userRed = usr;
                                        break;
                                    }
                                }
                                if (userRed != null) {
                                    for (User usr : waitingListGreen) {
                                        if (usr.mode == 3) {
                                            userGreen = usr;
                                            break;
                                        }
                                    }
                                    if (userGreen != null) {
                                        userBlueDel = userBlue;
                                        waitingListRed.remove(userRed);
                                        waitingListGreen.remove(userGreen);
                                        sUsers = new HashSet();
                                        sUsers.add(userRed);
                                        sUsers.add(userGreen);
                                        mapBlueToAll.put(userBlue.uniqueName, sUsers);
                                        sUsers = new HashSet();
                                        sUsers.add(userBlue);
                                        sUsers.add(userGreen);
                                        mapRedToAll.put(userRed.uniqueName, sUsers);
                                        sUsers = new HashSet();
                                        sUsers.add(userBlue);
                                        sUsers.add(userRed);
                                        mapRedToAll.put(userGreen.uniqueName, sUsers);
                                        Set<String> sUnique = new HashSet();
                                        sUnique.add(userBlue.uniqueName);
                                        sUnique.add(userRed.uniqueName);
                                        sUnique.add(userGreen.uniqueName);
                                        for (String str : sUnique) {
                                            if (!str.equals(userBlue.uniqueName)) {
                                                abaloneServer.sendMessage(str, "MAXTURNS:" + userBlue.maxTurns);
                                                abaloneServer.sendMessage(str, "MAXTIME:" + userBlue.maxTime);
                                            }
                                            String umsg = "";
                                            if (!str.equals(userBlue.uniqueName)) {
                                                umsg += userBlue.name + " ";
                                            }
                                            if (!str.equals(userRed.uniqueName)) {
                                                umsg += userRed.name + " ";
                                            }
                                            if (!str.equals(userGreen.uniqueName)) {
                                                umsg += userGreen.name + " ";
                                            }
                                            abaloneServer.sendMessage(str, "USER:" + umsg);
                                            abaloneServer.sendMessage(str, "START");
                                        }
                                        break;
                                    }
                                }
                            } else if (userBlue.mode == 4) {
                                for (User usr : waitingListRed) {
                                    if (usr.mode == 4) {
                                        userRed = usr;
                                        break;
                                    }
                                }
                                if (userRed != null) {
                                    for (User usr : waitingListGreen) {
                                        if (usr.mode == 4) {
                                            userGreen = usr;
                                            break;
                                        }
                                    }
                                    if (userGreen != null) {
                                        for (User usr : waitingListOrange) {
                                            if (usr.mode == 4) {
                                                userOrange = usr;
                                                break;
                                            }
                                        }
                                        if (userOrange != null) {
                                            userBlueDel = userBlue;
                                            waitingListRed.remove(userRed);
                                            waitingListGreen.remove(userGreen);
                                            waitingListOrange.remove(userOrange);

                                            sUsers = new HashSet();
                                            sUsers.add(userRed);
                                            sUsers.add(userGreen);
                                            sUsers.add(userOrange);
                                            mapBlueToAll.put(userBlue.uniqueName, sUsers);
                                            sUsers = new HashSet();
                                            sUsers.add(userBlue);
                                            sUsers.add(userGreen);
                                            sUsers.add(userOrange);
                                            mapRedToAll.put(userRed.uniqueName, sUsers);
                                            sUsers = new HashSet();
                                            sUsers.add(userBlue);
                                            sUsers.add(userRed);
                                            sUsers.add(userOrange);
                                            mapRedToAll.put(userGreen.uniqueName, sUsers);
                                            sUsers = new HashSet();
                                            sUsers.add(userBlue);
                                            sUsers.add(userRed);
                                            sUsers.add(userGreen);
                                            mapRedToAll.put(userOrange.uniqueName, sUsers);

                                            Set<String> sUnique = new HashSet();
                                            sUnique.add(userBlue.uniqueName);
                                            sUnique.add(userRed.uniqueName);
                                            sUnique.add(userGreen.uniqueName);
                                            sUnique.add(userOrange.uniqueName);
                                            for (String str : sUnique) {
                                                if (!str.equals(userBlue.uniqueName)) {
                                                    abaloneServer.sendMessage(str, "MAXTURNS:" + userBlue.maxTurns);
                                                    abaloneServer.sendMessage(str, "MAXTIME:" + userBlue.maxTime);
                                                }
                                                String umsg = "";
                                                if (!str.equals(userBlue.uniqueName)) {
                                                    umsg += userBlue.name + " ";
                                                }
                                                if (!str.equals(userRed.uniqueName)) {
                                                    umsg += userRed.name + " ";
                                                }
                                                if (!str.equals(userGreen.uniqueName)) {
                                                    umsg += userGreen.name + " ";
                                                }
                                                if (!str.equals(userOrange.uniqueName)) {
                                                    umsg += userOrange.name + " ";
                                                }
                                                abaloneServer.sendMessage(str, "USER:" + umsg);
                                                abaloneServer.sendMessage(str, "START");
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (userBlueDel != null) {
                        waitingListBlue.remove(userBlueDel);
                    }
                }
            }
        });

        tmr.start();
    }

    private void interpretMessage(String msg) {
        String[] arrs = msg.split(":");
        String uniqueName = arrs[0];
        String command = arrs[1];
        String name = "";
        if (command.equals("BLUE")) {
            name = arrs[2];
            User usr = new User(uniqueName, name);
            waitingListBlue.add(usr);
        } else if (command.equals("RED")) {
            name = arrs[2];
            User usr = new User(uniqueName, name);
            waitingListRed.add(usr);
        } else if (command.equals("GREEN")) {
            name = arrs[2];
            User usr = new User(uniqueName, name);
            waitingListGreen.add(usr);
        } else if (command.equals("ORANGE")) {
            name = arrs[2];
            User usr = new User(uniqueName, name);
            waitingListOrange.add(usr);
        } else if (command.equals("MODE")) {
            String sMode = arrs[2];
            int mode = Integer.parseInt(sMode);
            for (User usr : waitingListBlue) {
                if (usr.uniqueName.equals(uniqueName)) {
                    usr.mode = mode;
                    break;
                }
            }
            for (User usr : waitingListRed) {
                if (usr.uniqueName.equals(uniqueName)) {
                    usr.mode = mode;
                    break;
                }
            }
            for (User usr : waitingListGreen) {
                if (usr.uniqueName.equals(uniqueName)) {
                    usr.mode = mode;
                    break;
                }
            }
            for (User usr : waitingListOrange) {
                if (usr.uniqueName.equals(uniqueName)) {
                    usr.mode = mode;
                    break;
                }
            }
        } else if (command.equals("MOVE")) {
            String move = arrs[2];
            Set<User> susr = mapBlueToAll.get(uniqueName);
            if (susr == null) {
                susr = mapRedToAll.get(uniqueName);
                if (susr == null) {
                    susr = mapGreenToAll.get(uniqueName);
                    if (susr == null) {
                        susr = mapOrangeToAll.get(uniqueName);
                    }
                }
            }
            if (susr != null) {
                Set<String> sUnique = new HashSet();
                for (User usr : susr) {
                    sUnique.add(usr.uniqueName);
                }
                sUnique.remove(uniqueName);
                for (String str : sUnique) {
                    abaloneServer.sendMessage(str, "MOVE:" + move);
                }
            }
        } else if (command.equals("MAXTURNS")) {
            String sMaxTurns = arrs[2];
            int maxTurns = Integer.parseInt(sMaxTurns);
            for (User usr : waitingListBlue) {
                if (usr.uniqueName.equals(uniqueName)) {
                    usr.maxTurns = maxTurns;
                    break;
                }
            }
        } else if (command.equals("MAXTIME")) {
            String sMaxTime = arrs[2];
            int maxTime = Integer.parseInt(sMaxTime);
            for (User usr : waitingListBlue) {
                if (usr.uniqueName.equals(uniqueName)) {
                    usr.maxTime = maxTime;
                    break;
                }
            }
        } else if (command.equals("STOP")) {
            Set<User> susr = mapBlueToAll.get(uniqueName);
            if (susr == null) {
                susr = mapRedToAll.get(uniqueName);
                if (susr == null) {
                    susr = mapGreenToAll.get(uniqueName);
                    if (susr == null) {
                        susr = mapOrangeToAll.get(uniqueName);
                    }
                }
            }
            if (susr != null) {
                abaloneServer.stopClient(uniqueName);
                if (mapBlueToAll.containsKey(uniqueName)) {
                    mapBlueToAll.remove(uniqueName);
                } else if (mapRedToAll.containsKey(uniqueName)) {
                    mapRedToAll.remove(uniqueName);
                } else if (mapGreenToAll.containsKey(uniqueName)) {
                    mapGreenToAll.remove(uniqueName);
                } else if (mapOrangeToAll.containsKey(uniqueName)) {
                    mapOrangeToAll.remove(uniqueName);
                }
                Set<String> sUnique = new HashSet();
                for (User usr : susr) {
                    sUnique.add(usr.uniqueName);
                }
                sUnique.remove(uniqueName);
                for (String str : sUnique) {
                    abaloneServer.sendMessage(str, "STOP");
                    abaloneServer.stopClient(str);
                    if (mapBlueToAll.containsKey(str)) {
                        mapBlueToAll.remove(str);
                    } else if (mapRedToAll.containsKey(str)) {
                        mapRedToAll.remove(str);
                    } else if (mapGreenToAll.containsKey(str)) {
                        mapGreenToAll.remove(str);
                    } else if (mapOrangeToAll.containsKey(str)) {
                        mapOrangeToAll.remove(str);
                    }
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblRecMsg = new javax.swing.JLabel();
        txtClientMessage = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Abalone Server");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        lblRecMsg.setText("Received Message");

        txtClientMessage.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRecMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClientMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(49, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblRecMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtClientMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }//GEN-LAST:event_formWindowOpened

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int answer = JOptionPane.showConfirmDialog(this, "Are you sure?");
        if (answer == JOptionPane.YES_OPTION) {
            abaloneServer.stopClients();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                System.out.println("Error in ServerMainForm: " + ie.getMessage());
                ie.printStackTrace();
            }
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ServerMainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServerMainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServerMainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServerMainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServerMainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblRecMsg;
    private javax.swing.JTextField txtClientMessage;
    // End of variables declaration//GEN-END:variables
}
