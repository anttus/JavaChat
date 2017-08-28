package javachat;

import javax.swing.JList;

public class OnlineUsers extends javax.swing.JFrame {
    
    private JList list;
    private ClientGUI clientGUI;
    
    public OnlineUsers() {
        setType(Type.UTILITY);
        setSize(200, 320);
        setLocationRelativeTo(clientGUI);
        initComponents();
        list = new JList();
    }
    
    public void update(String[] users) {
        list.setListData(users);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        UsersOnlineScroll = new javax.swing.JScrollPane();
        UserList = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Users online");

        UsersOnlineScroll.setViewportView(UserList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(UsersOnlineScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(UsersOnlineScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> UserList;
    private javax.swing.JScrollPane UsersOnlineScroll;
    // End of variables declaration//GEN-END:variables
}
