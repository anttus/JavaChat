package javachat;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import javax.swing.UIManager;

public class ClientGUI extends javax.swing.JFrame implements Runnable {
    
    private Client client;
    private Thread listen, run;
    private OnlineUsers users;
    
    private boolean running = false;
    
    ClientGUI(String name, String address, int port) {
        client = new Client(name, address, port);
        boolean connect = client.openConnection(address);
        if (!connect) {
            System.err.println("Connection failed!");
            console("Connection failed!");
        }
        initComponents();
        createWindow();
        console("[Attempting a connection to " + address + ":" + port + ", user: " + name);
        String connection = "/c/" + name;
        client.send(connection.getBytes());
        users = new OnlineUsers();
        running = true;
        run = new Thread(this, "Running");
        run.start();
    }
    
    public void run() {
        listen();
    }
    
    private void createWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        } 
        setSize(800, 560);
        setLocationRelativeTo(null);
        textArea.requestFocusInWindow();
        
        setVisible(true);
    }
    
    public void console(String message) {
        textHistory.append(message + "\n\r");
    }
    
    public void send(String message, boolean text) {
        if (message.equals("")) return;
        if (text) {
            message = client.getName() + ": " + message;
            message = "/m/" + message;
        }
        client.send(message.getBytes());
        textArea.setText("");
        textArea.requestFocusInWindow();
        //textHistory.selectAll();
    }
    
    public void listen() {
        listen = new Thread("Listen") {
            public void run() {
                while (running) {
                    String message = client.receive();
                    if (message.startsWith("/c/")) {
                        client.setID(Integer.parseInt(message.split("/c/")[1].trim()));
                        console("Succesfully connected to server! ID: " + client.getID());
                    } else if (message.startsWith("/m/")) {
                        String text = message.substring(3);
                        //String text = message.split("/m/")[1];
                        console(text);
                        textHistory.selectAll();
                    } else if (message.startsWith("/i/")) {
                        String text = "/i/" + client.getID();
                        send(text, false);
                    } else if (message.startsWith("/u/")) {
                        String[] u = message.split("/u/|/n/");
                        users.update(Arrays.copyOfRange(u, 1, u.length - 1));
                    }
                }
            }
        };
        listen.start();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sendButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        textHistory = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        MenuBar = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        OnlineUsers = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Client - JavaChat");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        sendButton.setText("Send");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        textHistory.setColumns(20);
        textHistory.setRows(5);
        textHistory.setToolTipText("");
        textHistory.setFocusable(false);
        textHistory.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane1.setViewportView(textHistory);

        textArea.setColumns(20);
        textArea.setRows(5);
        textArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textAreaKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(textArea);

        FileMenu.setText("File");

        OnlineUsers.setText("Online users");
        OnlineUsers.setToolTipText("Show the users online");
        OnlineUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OnlineUsersActionPerformed(evt);
            }
        });
        FileMenu.add(OnlineUsers);

        MenuBar.add(FileMenu);

        setJMenuBar(MenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        send(textArea.getText(), true);
    }//GEN-LAST:event_sendButtonActionPerformed

    private void textAreaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textAreaKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            send(textArea.getText(), true);
            evt.consume();
        }
    }//GEN-LAST:event_textAreaKeyPressed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        String disconnect = "/d/" + client.getID();
        send(disconnect, false);
        String disconnectMessage = "[User " + client.getName() + " disconnected.]";
        send(disconnectMessage,true);
        running = false;
        client.close();
    }//GEN-LAST:event_formWindowClosing

    private void OnlineUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OnlineUsersActionPerformed
        users.setVisible(true);
    }//GEN-LAST:event_OnlineUsersActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenuBar MenuBar;
    private javax.swing.JMenuItem OnlineUsers;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JButton sendButton;
    public javax.swing.JTextArea textArea;
    public javax.swing.JTextArea textHistory;
    // End of variables declaration//GEN-END:variables
}
