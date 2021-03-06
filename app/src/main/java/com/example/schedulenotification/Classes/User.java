package com.example.schedulenotification.Classes;

import java.util.ArrayList;
/**
 * @author		Tahel Hazan <th8887@bs.amalnet.k12.il>
 * @version	beta
 * @since		1/10/2021
 * A class for a user in the app, to upload the user to firebase.
 */
public class User {
    private String name;
    private String phone;
    private String uID;
    private String email;
    /**
     * true - connected to the app
     * false - not connected to the app
     */
    private boolean active;
    private ArrayList<String> category;
    private int complete;
    private int all;
    private String profile;

    public User(String name, String e,
                String phone, String uID,String profile, boolean active) {
        this.name=name;
        this.email= e;
        this.phone = phone;
        this.uID= uID;
        this.profile = profile;
        this.active= active;
        this.category= new ArrayList<>();
        category.add("Category");
        this.all = 0;
        this.complete = 0;
    }

    public User(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ArrayList<String> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<String> category) {
        this.category = category;
    }

    public int getComplete() { return complete; }

    public void setComplete(int complete) { this.complete = complete; }

    public int getAll() { return all; }

    public void setAll(int all) { this.all = all; }

    public String getProfile() { return profile; }

    public void setProfile(String profile) { this.profile = profile; }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", uID='" + uID + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +'\'' +
                ", category=" + category +'\'' +
                ",complete=" + complete + '\'' +
                ",all=" + all +
                '}';
    }
}
