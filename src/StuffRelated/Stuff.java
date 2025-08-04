package StuffRelated;

import java.io.Serializable;

public class Stuff implements Serializable {
    private String name;
    private String id;
    private String phone;
    private String pin;
    private boolean isLoggedIn;
    private String role = "Undefined";
    private static final long serialVersionUID = 1L;

    // Constructor
    public Stuff(String name, String id, String phone, String pin) {
        this.name = name;
        this.id = id;
        this.phone = phone;
        this.pin = pin;
        this.isLoggedIn = false;
    }

    // Getters
    public String getName() { return name; }
    public String getId() { return id; }
    public String getPhone() { return phone; }
    public String getPin() { return pin; }
    public boolean isLoggedIn() { return isLoggedIn; }
    public String getRole() { return role; }

    // Setters
    public void setRole(String role) {
        this.role = role;
    }

    // Login/Logout
    public void login(String pin) {
        if(pin.equals(this.pin)) {
            this.isLoggedIn = true;
            System.out.println(name + " logged in.");
        }
    }

    public void logout() {
        this.isLoggedIn = false;
        System.out.println(name + " logged out.");
    }

    // Profile display
    public void showInfo() {
        System.out.println("Name: " + name);
        System.out.println("ID: " + id);
        System.out.println("Phone: " + phone);
        System.out.println("Role: " + role);
        System.out.println("Login Status: " + (isLoggedIn ? "Logged In" : "Logged Out"));
    }

    @Override
    public String toString() {
        return name + " (" + id + "), Role: " + role + ", Status: " + (isLoggedIn ? "In" : "Out");
    }
}
