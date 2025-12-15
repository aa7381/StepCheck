package com.example.stepcheck;

import java.util.ArrayList;

/**
 * Represents a work shift in the application.
 * This class holds information about a specific shift, including its ID, the workers assigned to it,
 * and its start and end times.
 */
public class Shift
{
    private String Shift_Id ;
    private ArrayList<Worker> workers;
    private String Start_shift; // time of the Shift start
    private String end_shift; // time of the Shift ended

    /**
     * Constructs a new Shift object with specified details.
     *
     * @param shift_Id The unique identifier for the shift.
     * @param workers The list of workers assigned to this shift.
     * @param start_shift The start time of the shift.
     * @param end_shift The end time of the shift.
     */
    public Shift(String shift_Id, ArrayList<Worker> workers, String start_shift, String end_shift)
    {
        this.Shift_Id = shift_Id;
        this.workers = workers;
        this.Start_shift = start_shift;
        this.end_shift = end_shift;

    }

    /**
     * Default constructor for Firebase serialization.
     */
    public Shift()
    {
        this.Shift_Id = "";
        this.workers = new ArrayList<Worker>();
        this.Start_shift = "";
        this.end_shift = "";

    }

    /**
     * Gets the shift's unique ID.
     * @return The shift ID.
     */
    public String getShift_Id() {
        return Shift_Id;
    }

    /**
     * Gets the list of workers in the shift.
     * @return An ArrayList of Worker objects.
     */
    public ArrayList<Worker> getWorkers() {
        return workers;
    }

    /**
     * Gets the start time of the shift.
     * @return The shift's start time.
     */
    public String getStart_shift() {
        return Start_shift;
    }

    /**
     * Gets the end time of the shift.
     * @return The shift's end time.
     */
    public String getEnd_shift() {
        return end_shift;
    }

    /**
     * Sets the shift's unique ID.
     * @param shift_Id The new ID for the shift.
     */
    public void setShift_Id(String shift_Id) {
        Shift_Id = shift_Id;
    }

    /**
     * Sets the list of workers for the shift.
     * @param workers The new list of workers.
     */
    public void setWorkers(ArrayList<Worker> workers) {
        this.workers = workers;
    }

    /**
     * Sets the start time of the shift.
     * @param start_shift The new start time.
     */
    public void setStart_shift(String start_shift) {
        Start_shift = start_shift;
    }

    /**
     * Sets the end time of the shift.
     * @param end_shift The new end time.
     */
    public void setEnd_shift(String end_shift) {
        this.end_shift = end_shift;
    }

}
