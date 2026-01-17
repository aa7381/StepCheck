package com.example.stepcheck;

/**
 * A data model class that represents a product.
 * This class holds information about the product's ID and name.
 */
public class Product {
    private String id;
    private String name;

    /**
     * Constructs a new Product object with the given ID and name.
     * @param id The ID of the product.
     * @param name The name of the product.
     */
    public Product(String id, String name) {
        this.id = id;
        this.name = name;
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
}
