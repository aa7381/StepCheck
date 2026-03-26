package com.example.stepcheck.models;

/**
 * Represents a worker's presence record for a single shift.
 * This class holds the worker's ID and the start and end times of their shift.
 */
public class Presences
{
    /**
     * Unique ID of the worker.
     */
    private String worker_id ; //id of worker who presences  == id of worker

    /**
     * The time the worker started their shift.
     */
    private String start_your_Shift;//start time of worker Shift
    
    /**
     * Flag indicating if the shift has started.
     */
    private Boolean is_startShift;
    
    /**
     * Flag indicating if the pause button is enabled.
     */
    private Boolean buttonPauseEnabled;
    
    /**
     * Flag indicating if the pause end button is enabled.
     */
    private Boolean buttonPauseEndEnabled;
    
    /**
     * Flag indicating if the shift end button is enabled.
     */
    private Boolean buttonEndEnabled;

    /**
     * The time the worker started their break.
     */
    private String pause_time ;//pause time of worker Shift
    
    /**
     * The time the worker ended their break.
     */
    private String pause_end_time ;//pause time of worker Shift

    /**
     * The time the worker ended their shift.
     */
    private String end_your_Shift ;//end time of worker Shift

    /**
     * The date of the presence record.
     */
    private String date ;

    /**
     * The latitude of the worker's location.
     */
    private double latitude;
    
    /**
     * The longitude of the worker's location.
     */
    private double longitude;

    /**
     * Constructs a new Presences object with specified details.
     *
     * @param worker_id The ID of the worker.
     * @param start_your_Shift The time the worker started their shift.
     * @param end_your_Shift The time the worker ended their shift.
     * @param pause_time The time the break started.
     * @param pause_end_time The time the break ended.
     * @param is_startShift Flag for shift start.
     * @param buttonPauseEnabled Flag for pause button.
     * @param buttonPauseEndEnabled Flag for pause end button.
     * @param buttonEndEnabled Flag for end button.
     * @param date The date of the record.
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
        this.latitude = 0;
        this.longitude = 0;

    }

    /**
     * Gets whether the end shift button is enabled.
     * @return true if enabled, false otherwise.
     */
    public Boolean getButtonEndEnabled() {
        return buttonEndEnabled;
    }
    
    /**
     * Sets whether the end shift button is enabled.
     * @param buttonEndEnabled New state.
     */
    public void setButtonEndEnabled(Boolean buttonEndEnabled) {
        this.buttonEndEnabled = buttonEndEnabled;
    }

    /**
     * Gets whether the pause button is enabled.
     * @return true if enabled, false otherwise.
     */
    public Boolean getButtonPauseEnabled() {
        return buttonPauseEnabled;
    }
    
    /**
     * Sets whether the pause button is enabled.
     * @param buttonPauseEnabled New state.
     */
    public void setButtonPauseEnabled(Boolean buttonPauseEnabled) {
        this.buttonPauseEnabled = buttonPauseEnabled;
    }

    /**
     * Gets whether the pause end button is enabled.
     * @return true if enabled, false otherwise.
     */
    public Boolean getButtonPauseEndEnabled() {
        return buttonPauseEndEnabled;
    }
    
    /**
     * Sets whether the pause end button is enabled.
     * @param buttonPauseEndEnabled New state.
     */
    public void setButtonPauseEndEnabled(Boolean buttonPauseEndEnabled) {
        this.buttonPauseEndEnabled = buttonPauseEndEnabled;
    }

    /**
     * Gets whether the shift has started.
     * @return true if started, false otherwise.
     */
    public Boolean getIs_startShift() {
        return is_startShift;
    }
    
    /**
     * Sets whether the shift has started.
     * @param is_startShift New state.
     */
    public void setIs_startShift(Boolean is_startShift) {
        this.is_startShift = is_startShift;
    }

    /**
     * Gets the pause end time.
     * @return The pause end time string.
     */
    public String getPause_end_time() {
        return pause_end_time;
    }
    
    /**
     * Sets the pause end time.
     * @param pause_end_time The new pause end time.
     */
    public void setPause_end_time(String pause_end_time) {
        this.pause_end_time = pause_end_time;
    }

    /**
     * Gets the pause start time.
     * @return The pause start time string.
     */
    public String getPause_time() {
        return pause_time;
    }

    /**
     * Sets the pause start time.
     * @param pause_time The new pause start time.
     */
    public void setPause_time(String pause_time) {
        this.pause_time = pause_time;
    }


    /**
     * Gets the date of the record.
     * @return The date string.
     */
    public String getTime() {
        return date;
    }

    /**
     * Sets the date of the record.
     * @param date The new date.
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
     * @return The start time string.
     */
    public String getStart_your_Shift() {
        return start_your_Shift;
    }

    /**
     * Gets the shift end time.
     * @return The end time string.
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

    /**
     * Gets the latitude.
     * @return The latitude.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude.
     * @param latitude The new latitude.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude.
     * @return The longitude.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude.
     * @param longitude The new longitude.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
