package com.example.stepcheck.models;

/**
 * A data model class that represents a product.
 * This class holds information about the product's ID, name, and price.
 */
public class Product {
    private String id;
    private String name;
    private double price;

    /**
     * Constructs a new Product object with the given ID, name, and price.
     * @param id The ID of the product.
     * @param name The name of the product.
     * @param price The price of the product.
     */
    public Product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    /**
     * Returns the ID of the product.
     * @return The product ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of the product.
     * @return The product name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the price of the product.
     * @return The product price.
     */
    public double getPrice() {
        return price;
    }
}
