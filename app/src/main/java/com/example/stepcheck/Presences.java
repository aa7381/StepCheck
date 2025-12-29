package com.example.stepcheck;

/**
 * Represents a worker's presence record for a single shift.
 * This class holds the worker's ID and the start and end times of their shift.
 */
public class Presences
{

    private String worker_id ; //id of worker who presences  == id of worker
    private String start_your_Shift;//start time of worker Shift
    private String end_your_Shift ;//end time of worker Shift

    private String time ;

    /**
     * Constructs a new Presences object with specified details.
     *
     * @param worker_id The ID of the worker.
     * @param start_your_Shift The time the worker started their shift.
     * @param end_your_Shift The time the worker ended their shift.
     */
    public Presences(String worker_id, String start_your_Shift, String end_your_Shift, String time)
    {
        this.worker_id = worker_id;
        this.start_your_Shift = start_your_Shift;
        this.end_your_Shift = end_your_Shift;
        this.time = time;
    }

    /**
     * Default constructor for Firebase serialization.
     */
    public Presences()
    {
        this.worker_id = "";
        this.start_your_Shift = "";
        this.end_your_Shift = "";
        this.time = "";

    }

    /**
     * Gets the worker's ID.
     * @return The worker ID.
     */

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWorker_id() {
        return worker_id;
    }

    /**
     * Gets the shift start time.
     * @return The start time.
     */
    public String getStart_your_Shift() {
        return start_your_Shift;
    }

    /**
     * Gets the shift end time.
     * @return The end time.
     */
    public String getEnd_your_Shift() {
        return end_your_Shift;
    }

    /**
     * Sets the worker's ID.
     * @param worker_id The new worker ID.
     */
    public void setWorker_id(String worker_id) {
        this.worker_id = worker_id;
    }

    /**
     * Sets the shift start time.
     * @param start_your_Shift The new start time.
     */
    public void setStart_your_Shift(String start_your_Shift) {
        this.start_your_Shift = start_your_Shift;
    }

    /**
     * Sets the shift end time.
     * @param end_your_Shift The new end time.
     */
    public void setEnd_your_Shift(String end_your_Shift) {
        this.end_your_Shift = end_your_Shift;
    }

}
