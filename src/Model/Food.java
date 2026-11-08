package model;

import java.io.Serializable;

public class Food implements Serializable {

    private static final long serialVersionUID = 1L;

    private String foodID;
    private String name;
    private String category;
    private int quantity;
    private double price;
    private String expiryDate; // yyyy-MM-dd

    public Food(String foodID, String name, String category,
                int quantity, double price, String expiryDate) {
        this.foodID = foodID;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.expiryDate = expiryDate;
    }

    public String getFoodID() {
        return foodID;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return foodID + " - " + name;
    }
}