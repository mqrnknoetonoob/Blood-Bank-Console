/* Set-Location "C:\Users\win11\OneDrive\Documents\BUET program\project java\javaProject"
 Remove-Item -Recurse -Force .\out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path ".\out\production\javaProject"
 $files = Get-ChildItem -Recurse -Filter *.java .\src | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d ".\out\production\javaProject" $files


 cd "C:\Users\win11\OneDrive\Documents\BUET program\project java\javaProject"
java -cp "out/production/javaProject" Server.Server

Step-05: cd "C:\Users\win11\OneDrive\Documents\BUET program\project java\javaProject\out\production\javaProject"
java Client.ClientMain

*/


// Server.java
package Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import Message.*;
import StuffRelated.*;
import BloodRelated.*;
import KitRelated.*;

public class Server {
    private static final int PORT = 12345;
    private static int x = 0, y = 0;
    private static final String XFILE = "C:\\Users\\win11\\OneDrive\\Documents\\BUET program\\project java\\javaProject\\data\\x.csv";
    private static final String YFILE = "C:\\Users\\win11\\OneDrive\\Documents\\BUET program\\project java\\javaProject\\data\\y.csv";
    private static Map<String, ObjectOutputStream> clientOutputStreams = new ConcurrentHashMap<>();
    private static StuffList staffList = new StuffList();
    private static BloodUnitList bloodUnitList = new BloodUnitList();
    private static KitManagement kit = new KitManagement();
    static {
        x = loadCounter(XFILE);
        y = loadCounter(YFILE);
    }

    private static int loadCounter(String path){
        try(BufferedReader br = new BufferedReader(new FileReader(path))){
            String ln = br.readLine();
            return (ln==null || ln.isBlank()) ? 0 : Integer.parseInt(ln.trim());
        }catch(Exception e){ return 0; }
    }
    private static synchronized void saveCounter(String path, int val){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(path,false))){
            bw.write(String.valueOf(val));
        }catch(IOException ignore){}
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        staffList.loadFromFile("data/stuff.csv");
        bloodUnitList.loadAllFromFiles("data");
        kit.loadKitData();

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket);
            new ClientHandler(clientSocket).start();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private Stuff loggedInUser;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    Object obj = in.readObject();

                    if (obj instanceof String && obj.equals("LOGIN_REQUEST")) {
                        int roleIndex = (int) in.readObject();
                        String pin = (String) in.readObject();
                        String role = getRoleFromIndex(roleIndex);

                        Stuff user = staffList.getStuffByRole(role);
                        if (user != null && pin.equals(user.getPin())) {
                            user.login(pin);
                            user.setRole(role);
                            loggedInUser = user;
                            clientOutputStreams.put(role, out);
                            out.writeObject(user); // Send Stuff object to confirm login
                        } else {
                            out.writeObject("Invalid role or PIN");
                        }
                        out.flush();
                    } else if (obj instanceof String && obj.equals("LOGOUT")) {
                        if (loggedInUser != null) {
                            clientOutputStreams.remove(loggedInUser.getRole());
                            loggedInUser.logout();
                            out.writeObject("Logged out successfully.");
                            out.flush();
                            break;
                        }

                    } else if (obj instanceof BaseMessage) {
                        BaseMessage msg = (BaseMessage) obj;
                        String target = msg.getReceiverRole();

                         if (target.equals("Server")) {
                            // Handle server-specific requests from Manager
                            if(msg.getSenderRole().equals("Delivery Tester"))
                            {
                                y++;
                                saveCounter(YFILE, y);
                            }
                            else if(msg.getSenderRole().equals("Preservation Officer"))
                            {
                                x++;
                                saveCounter(XFILE, x);
                            }
                            else if (msg.getSenderRole().equals("Manager")) {
                                if (msg instanceof CustomerRequestMessage) {
                                    // Forward customer request to PreservationOfficer
                                    ObjectOutputStream targetStream = clientOutputStreams.get("PreservationOfficer");
                                    if (targetStream != null) {
                                        targetStream.writeObject(msg);
                                        targetStream.flush();
                                    } else {
                                        StockAlertMessage alert = new StockAlertMessage(((CustomerRequestMessage) msg).getBloodGroup());
                                        out.writeObject(alert);
                                        out.flush();
                                    }
                                } else {
                                    // Handle custom Manager requests
                                    String msgStr = msg.toString();
                                    if (msgStr.contains("StaffInfo")) {
                                        out.writeObject(staffList);
                                    } else if (msgStr.contains("KitQuantity")) {
                                        out.writeObject(kit);
                                    } else if (msgStr.contains("BloodUnitDetails")) {
                                        out.writeObject(bloodUnitList);
                                    }
                                    out.flush();
                                }
                            }
                        } else {
                            // Forward message to the target role
                            ObjectOutputStream targetStream = clientOutputStreams.get(target);
                            if (targetStream != null) {
                                targetStream.writeObject(msg);
                                targetStream.flush();
                            } else {
                                System.out.println("Target role offline or not registered: " + target);
                            }
                        }

                        // Check KitManagement and BloodUnitList after relevant operations
                        checkAndSendAlerts();
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client disconnected or error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (loggedInUser != null) {
                    clientOutputStreams.remove(loggedInUser.getRole());
                    loggedInUser.logout();
                }
            }
        }

        private String getRoleFromIndex(int roleIndex) {
            switch (roleIndex) {
                case 1: return "Manager";
                case 2: return "Registration Tester";
                case 3: return "Donation Officer";
                case 4: return "Preservation Officer";
                case 5: return "Delivery Tester";
                default: return "Undefined";
            }
        }

        private void checkAndSendAlerts() {
            try {
                ObjectOutputStream managerStream = clientOutputStreams.get("Manager");
                if (managerStream == null) {
                    return; // Manager not connected
                }

                // Check KitManagement for zero quantities
                if (kit.getNumberOfBag() == 0 || kit.getNumberOfNeedle() == 0 ||
                        kit.getNumberOfPipe() == 0 || kit.getNumberOfTestingKit() == 0) {
                    KitMessage kitAlert = new KitMessage(kit);
                    managerStream.writeObject(kitAlert);
                    managerStream.flush();
                }

                // Check BloodUnitList for empty blood groups
                String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
                for (String group : bloodGroups) {
                    ArrayList<BloodUnit> list = bloodUnitList.fetchBlood(group);
                    if (list != null && list.isEmpty()) {
                        StockAlertMessage alert = new StockAlertMessage(group);
                        managerStream.writeObject(alert);
                        managerStream.flush();
                    }
                }
            } catch (IOException e) {
                System.out.println("Error sending alerts to Manager: " + e.getMessage());
            }
        }
    }
}
