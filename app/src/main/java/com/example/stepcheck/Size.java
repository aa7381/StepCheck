package com.example.stepcheck;

/**
 * Represents the available sizes for a particular shoe model.
 * This class holds arrays of sizes for men, women, and children. Each inner array likely
 * represents a specific size and the quantity available for that size.
 */
public class Size {
    private double[][] sizes_Man; //מידות גברים
    private double[][] sizes_Women; //מידות נשים
    private double[][] sizes_Child; //מידות ילדים
    private double[][] Size_Bigkids;
    private double[][] Size_youngkids;
    private double[][] Size_baby;



    /**
     * Constructs a new Size object with specified size arrays.
     *
     * @param sizes_Man A 2D array representing men's sizes and quantities.
     * @param sizes_Women A 2D array representing women's sizes and quantities.
     * @param sizes_Child A 2D array representing children's sizes and quantities.
     */
    public Size(double[][] sizes_Man, double[][] sizes_Women, double[][] sizes_Child, double[][] Size_Bigkids, double[][] Size_youngkids, double[][] Size_baby) {
        this.sizes_Man = sizes_Man;
        this.sizes_Women = sizes_Women;
        this.sizes_Child = sizes_Child;
        this.Size_Bigkids = Size_Bigkids;
        this.Size_youngkids = Size_youngkids;
        this.Size_baby = Size_baby;
    }

    /**
     * Default constructor for Firebase serialization.
     */
    public Size() {
        this.sizes_Man  = new double[0][0];
        this.sizes_Women = new double[0][0];
        this.sizes_Child = new double[0][0];
        this.Size_Bigkids = new double[0][0];
        this.Size_youngkids = new double[0][0];
        this.Size_baby = new double[0][0];
    }

    /**
     * Gets the array of men's sizes.
     * @return A 2D double array of men's sizes.
     */
    public double[][] getSizes_Man() {
        return sizes_Man;
    }

    /**
     * Sets the array of men's sizes.
     * @param sizes_Man The new 2D array for men's sizes.
     */
    public void setSizes_Man(double[][] sizes_Man) {
        this.sizes_Man = sizes_Man;
    }

    /**
     * Gets the array of women's sizes.
     * @return A 2D double array of women's sizes.
     */
    public double[][] getSizes_Women() {
        return sizes_Women;
    }

    /**
     * Sets the array of women's sizes.
     * @param sizes_Women The new 2D array for women's sizes.
     */
    public void setSizes_Women(double[][] sizes_Women) {
        this.sizes_Women = sizes_Women;
    }

    /**
     * Gets the array of children's sizes.
     * @return A 2D double array of children's sizes.
     */
    public double[][] getSizes_Child() {
        return sizes_Child;
    }

    /**
     * Sets the array of children's sizes.
     * @param sizes_Child The new 2D array for children's sizes.
     */
    public void setSizes_Child(double[][] sizes_Child) {
        this.sizes_Child = sizes_Child;
    }


    public double[][] getSize_Bigkids() {
        return Size_Bigkids;
    }

    public void setSize_Bigkids(double[][] Size_Bigkids) {
        this.Size_Bigkids = Size_Bigkids;
    }
    public double[][] getSize_youngkids() {
        return Size_youngkids;

    }
    public void setSize_youngkids(double[][] Size_youngkids) {
        this.Size_youngkids = Size_youngkids;
    }
    public double[][] getSize_baby() {
        return Size_baby;
    }
    public void setSize_baby(double[][] Size_baby) {
        this.Size_baby = Size_baby;
    }

}
