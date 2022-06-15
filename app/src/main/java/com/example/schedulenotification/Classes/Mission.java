package com.example.schedulenotification.Classes;

import java.util.ArrayList;
/**
 * @author		Tahel Hazan <th8887@bs.amalnet.k12.il>
 * @version	beta
 * @since		1/10/2021
 * A class for mission the user creates. In order to upload the mission each user creates to firebase
 */
public class Mission {
    private String title;
    private boolean active; //true- completed. false- yet complete.
    private int importance;//0- דחוף, 1-רגיל, 2- לא דחוף.
    private String description;
    private String openDate; //dd MM yyyy-hh:mm
    private String dueDate;//dd MM yyyy-hh:mm
    private int category;//the index of the chosen category from the list
    private ArrayList<String> imagesLinks;//links of the images.
    private ArrayList<String> imagesNames;// The names of each image.
    private String color;//color chosen for the mission

    public Mission(String title, int importance,
                   String description, String openDate,
                   String dueDate, int category, String color){
        this.title=title;
        this.importance=importance;
        this.active= false;
        this.description = description;
        this.openDate = openDate;
        this.dueDate = dueDate;
        this.category = category;
        this.imagesLinks = new ArrayList<String>();
        this.imagesNames = new ArrayList<String>();
        imagesLinks.add("links->");
        imagesNames.add("images->");
        this.color = color;
        this.active= false;
    }


    public Mission(){ }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setimagesLinks(ArrayList<String> images) {
        this.imagesLinks = images;
    }

    public void setImagesNames(ArrayList<String> imagesNames) { this.imagesNames = imagesNames; }

    public String getColor() { return color; }

    public void setColor(String color) { this.color = color; }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public boolean isActive() {
        return active;
    }

    public int getImportance() {
        return importance;
    }

    public String getDescription() {
        return description;
    }

    public String getOpenDate() {
        return openDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public int getCategory() {
        return category;
    }

    public ArrayList<String> getImagesLinks(){return imagesLinks;}

    public ArrayList<String> getImagesNames() { return imagesNames; }
    @Override
    public String toString() {
        return "Mission{" +
                "title='" + title + '\'' +
                ", active=" + active +
                ", importance=" + importance +
                ", description='" + description + '\'' +
                ", openDate='" + openDate + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", category=" + category +
                ", images=" + imagesLinks +
                ",color=" + color +
                '}';
    }
}

