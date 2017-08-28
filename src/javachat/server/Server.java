package javachat.server;

import java.util.List;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import javachat.UniqueIdentifier;

public class Server implements Runnable {
    
    private List<ServerClient> clients = new ArrayList<>();
    private List<Integer> clientResponse = new ArrayList<>();
    
    private Date date = new Date();
    private DatagramSocket socket;
    private Thread run, manage, send, receive;
    
    private int port;
    private boolean running = false;
    private boolean raw = false;
    
    private final int MAX_ATTEMPTS = 5;

    public Server(int port) {
        this.port = port;
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }
        run = new Thread(this, "Server");
        run.start();
    }

    @Override
    public void run() {
        running = true;
        System.out.println("Server started on port " + port + " on " + date.toString());
        manageClients();
        receive();
        Scanner input = new Scanner(System.in);
        while (running) {            
            String text = input.nextLine();
            if (!text.startsWith("/")) {
                sendToAll("/m/\n[Server: " + text + "]\n");
                continue;
            }
            text.substring(1);
            if (text.equals("/raw")) {
                raw = !raw;
            } else if (text.equals("/clients")) {
                System.out.println("Clients: ");
                System.out.println("==========");
                for (int i = 0; i < clients.size(); i++) {
                    ServerClient c = clients.get(i);
                    System.out.println(c.name.trim() + "(" + c.getID() + "): " + c.address.toString() + ":" + c.port);
                }
            }
        }
    }
   
//Managing     
    private void manageClients() {
        manage = new Thread("Manage") {
            public void run() {
                while (running) {
                    sendToAll("/i/server");
                    sendStatus();
                    /*try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    for (int i = 0; i < clients.size(); i++) {
                        ServerClient c = clients.get(i);
                        if (!clientResponse.contains(c.getID())) {
                            if (c.attempt >= MAX_ATTEMPTS) {
                                disconnect(c.getID(), false);
                            } else {
                                c.attempt++;
                            }
                        } else {
                            clientResponse.remove(new Integer(c.getID()));
                            c.attempt = 0;
                        }
                    }
                }
            }
        };
        manage.start();
    }
    
    private void sendStatus() {
        if (clients.size() <= 0) return;
        String users = "/u/";
        for (int i = 0; i < clients.size() - 1; i++) {
            users += clients.get(i).name + "/n/";
        }
        users += clients.get(clients.size() - 1).name;
        sendToAll(users);
    }
    
//Receiving  
    private void receive() {
        receive = new Thread("Receive") {
            @Override
            public void run() {
                while (running) {
                    byte[] data = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    try {
                        socket.receive(packet); //Waits unitl it receives data
                    } catch (IOException e) {
                    }
                    process(packet);
                    //System.out.println(clients.get(0).address.toString() + ":" + clients.get(0).port);
                }
            }
        };
        receive.start();
    }
    
    private void sendToAll(String message) {
        if (message.startsWith("/m/")) {
            String text = message.substring(3);
            System.out.println(text);
        }
        for (int i = 0; i < clients.size(); i++) {
            ServerClient client = clients.get(i);
            send(message.getBytes(), client.address, client.port);
        }
    }
    
    private void send(byte[] data, final InetAddress address, final int port) {
        send = new Thread("Send") {
            public void run() {
                DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                }
            }
        };
        send.start();
    }
    /*
    private void send(String message, InetAddress address, int port) {
        message += "/e/";
        send(message.getBytes(), address, port);
    }
    */
    private void process(DatagramPacket packet) {
        String string = new String(packet.getData());
        string = string.trim();
        if (raw) System.out.println(string);
        if (string.startsWith("/c/")) {
            //UUID id = UUID.randomUUID();
            int id = UniqueIdentifier.getIdentifier();
            clients.add(new ServerClient(string.substring(3, string.length()), packet.getAddress(), packet.getPort(), id));
            System.out.println(string.substring(3, string.length()) + " (ID: " + id + ") logged in.");
            String ID = "/c/" + id;
            send(ID.getBytes(), packet.getAddress(), packet.getPort());
        } else if (string.startsWith("/m/")) {
            sendToAll(string);
        } else if (string.startsWith("/d/")) {
            String id = string.split("/d/")[1];
            disconnect(Integer.parseInt(id), true);
        } else if (string.startsWith("/i/")) {
            clientResponse.add(Integer.parseInt(string.split("/i/")[1]));
            
        } else {
            System.out.println(string);
        }
    }
    
    private void disconnect(int id, boolean status) {
        ServerClient c = null;
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getID() == id) {
                c = clients.get(i);
                clients.remove(i);
                break;
            }
        }
        String message = "";
        if (status) {
            message = "Client " + c.name + " (" + c.getID() + ") @ " + c.address.toString() + ":" + c.port + " disconnected.";
        } else {
            message = "Client " + c.name + " (" + c.getID() + ") @ " + c.address.toString() + ":" + c.port + " timed out.";
        }
        System.out.println(message);
    }
    
}
