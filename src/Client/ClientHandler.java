package Client;

import Message.*;

import java.io.*;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {
    private static final ConcurrentHashMap<String, ObjectOutputStream> clientMap = new ConcurrentHashMap<>();
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String role;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            role = (String) in.readObject(); // read role like "Manager", "DonationOfficer"
            clientMap.put(role, out);

            Object obj;
            while ((obj = in.readObject()) != null) {
                if (obj instanceof BaseMessage) {
                    BaseMessage msg = (BaseMessage) obj;
                    String to = msg.getReceiverRole();

                    if (clientMap.containsKey(to)) {
                        clientMap.get(to).writeObject(msg);
                        clientMap.get(to).flush();
                    } else {
                        System.out.println("Receiver " + to + " is not connected.");
                    }
                }
            }


        } catch (Exception e) {
            System.out.println("Connection closed for: " + role);
        } finally {
            try {
                //clientMap.remove(role);
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
