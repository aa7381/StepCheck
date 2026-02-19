package com.example.stepcheck;

/**
 * Represents a worker's presence record for a single shift.
 * This class holds the worker's ID and the start and end times of their shift.
 */
public class Presences
{

    private String worker_id ; //id of worker who presences  == id of worker


    private String start_your_Shift;//start time of worker Shift
    private Boolean is_startShift;
    private Boolean buttonPauseEnabled;
    private Boolean buttonPauseEndEnabled;
    private Boolean buttonEndEnabled;

    private String pause_time ;//pause time of worker Shift
    private String pause_end_time ;//pause time of worker Shift


    private String end_your_Shift ;//end time of worker Shift

    private String date ;

    /**
     * Constructs a new Presences object with specified details.
     *
     * @param worker_id The ID of the worker.
     * @param start_your_Shift The time the worker started their shift.
     * @param end_your_Shift The time the worker ended their shift.
     */
    public Presences(String worker_id, String start_your_Shift, String end_your_Shift,String pause_time,String pause_end_time,Boolean is_startShift,Boolean buttonPauseEnabled,Boolean buttonPauseEndEnabled,Boolean buttonEndEnabled, String date)
    {
        this.worker_id = worker_id;
        this.start_your_Shift = start_your_Shift;
        this.end_your_Shift = end_your_Shift;
        this.pause_time = pause_time;
        this.is_startShift = is_startShift;
        this.pause_end_time = pause_end_time;
        this.buttonPauseEnabled = buttonPauseEnabled;
        this.buttonPauseEndEnabled = buttonPauseEndEnabled;
        this.buttonEndEnabled = buttonEndEnabled;
        this.date = date;
    }

    /**
     * Default constructor for Firebase serialization.
     */
    public Presences()
    {
        this.buttonEndEnabled = false;
        this.buttonPauseEnabled = false;
        this.buttonPauseEndEnabled = false;
        this.worker_id = "";
        this.start_your_Shift = "";
        this.pause_time = "";
        this.pause_end_time = "";
        this.is_startShift = false;
        this.end_your_Shift = "";
        this.date = "";

    }

    public Boolean getButtonEndEnabled() {
        return buttonEndEnabled;
    }
    public void setButtonEndEnabled(Boolean buttonEndEnabled) {
        this.buttonEndEnabled = buttonEndEnabled;
    }

    public Boolean getButtonPauseEnabled() {
        return buttonPauseEnabled;
    }
    public void setButtonPauseEnabled(Boolean buttonPauseEnabled) {
        this.buttonPauseEnabled = buttonPauseEnabled;
    }

    public Boolean getButtonPauseEndEnabled() {
        return buttonPauseEndEnabled;
    }
    public void setButtonPauseEndEnabled(Boolean buttonPauseEndEnabled) {
        this.buttonPauseEndEnabled = buttonPauseEndEnabled;
    }

    public Boolean getIs_startShift() {
        return is_startShift;
    }
    public void setIs_startShift(Boolean is_startShift) {
        this.is_startShift = is_startShift;
    }

    public String getPause_end_time() {
        return pause_end_time;
    }
    public void setPause_end_time(String pause_end_time) {
        this.pause_end_time = pause_end_time;
    }

    public String getPause_time() {
        return pause_time;
    }

    public void setPause_time(String pause_time) {
        this.pause_time = pause_time;
    }


    /**
     * Gets the time the presence was recorded.
     * @return The time.
     */
    public String getTime() {
        return date;
    }

    /**
     * Sets the time the presence was recorded.
     */
    public void setTime(String date) {
        this.date = date;
    }

    /**
     * Gets the worker's ID.
     * @return The worker ID.
     */
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
