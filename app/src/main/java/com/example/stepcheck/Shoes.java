package com.example.stepcheck;

/**
 * Represents a single shoe item in the inventory.
 * This class stores details about a shoe, such as its ID (QR code), name, color, type, price,
 * and manufacturing company.
 */
public class Shoes
{
    private String id; //id of shoe equal to qr code on the shoe
    private String shoe_name; // the name of shoe
    private String color ; // the color of shoe
    private String type ; //צרה או רחבה
    private double price ; // the price of shoe
    private String manufacturing_company ; // the company name


    /**
     * Constructs a new Shoes object with specified details.
     *
     * @param id The unique ID of the shoe (QR code).
     * @param shoe_name The name of the shoe.
     * @param color The color of the shoe.
     * @param type The type of the shoe (e.g., wide, narrow).
     * @param price The price of the shoe.
     * @param manufacturing_company The company that manufactured the shoe.
     */
    public Shoes(String id, String shoe_name, String color, String type, double price, String manufacturing_company) {
        this.id = id;
        this.shoe_name = shoe_name;
        this.color = color;
        this.type = type;
        this.price = price;
        this.manufacturing_company = manufacturing_company;

    }

    /**
     * Default constructor for Firebase serialization.
     */
    public Shoes(String qr_code_data, String shoeName, String shoeType, String price, String manufacturing_company) {
        this.id = "";
        this.shoe_name = "";
        this.color = "";
        this.type = "";
        this.price = 0;
        this.manufacturing_company = "";

    }

    /**
     * Gets the shoe's unique ID.
     * @return The shoe ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the name of the shoe.
     * @return The shoe name.
     */
    public String getShoe_name() {
        return shoe_name;
    }

    /**
     * Gets the color of the shoe.
     * @return The shoe color.
     */
    public String getColor() {
        return color;
    }

    /**
     * Gets the type of the shoe.
     * @return The shoe type.
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the price of the shoe.
     * @return The shoe price.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Gets the manufacturing company of the shoe.
     * @return The manufacturing company name.
     */
    public String getManufacturing_company() {
        return manufacturing_company;
    }

    /**
     * Sets the shoe's unique ID.
     * @param id The new ID for the shoe.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the name of the shoe.
     * @param shoe_name The new name for the shoe.
     */
    public void setShoe_name(String shoe_name) {
        this.shoe_name = shoe_name;
    }

    /**
     * Sets the color of the shoe.
     * @param color The new color for the shoe.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Sets the type of the shoe.
     * @param type The new type for the shoe.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the price of the shoe.
     * @param price The new price for the shoe.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Sets the manufacturing company of the shoe.
     * @param manufacturing_company The new manufacturing company name.
     */
    public void setManufacturing_company(String manufacturing_company) {
        this.manufacturing_company = manufacturing_company;
    }
}


