package com.example.stepcheck;

import java.util.ArrayList;

/**
 * Represents a worker in the application.
 * This class holds all information related to a worker, including their personal details,
 * job rank, current shift status, and specific permissions for application features.
 */
public class Worker
{
    private String id;
    private String username;
    private String job_rank;
    private boolean inShift;
    private boolean canEditInventory;
    /**
     * A list of the worker's presences.
     */
    private ArrayList<Presences> presences;
    private boolean can_manage_shift;

    /**
     * Constructs a new Worker object with specified details.
     *
     * @param id The unique identifier for the worker.
     * @param username The worker's username.
     * @param job_rank The worker's job title or rank.
     * @param inShift A boolean indicating if the worker is currently in a shift.
     * @param canEditInventory A boolean indicating if the worker has permission to edit inventory.
     * @param can_manage_shift A boolean indicating if the worker has permission to manage shifts.
     */
    public Worker(String id, String username, String job_rank, boolean inShift, boolean canEditInventory, boolean can_manage_shift)
    {
        this.id = id;
        this.username = username;
        this.job_rank = job_rank;
        this.inShift = inShift;
        this.canEditInventory = canEditInventory;
        this.can_manage_shift = can_manage_shift;
        this.presences = new ArrayList<>();
    }

    /**
     * Default constructor for Firebase serialization.
     */
    public Worker()
    {
        this.id = "";
        this.username = "";
        this.job_rank = "";
        this.presences = new ArrayList<>();
    }

    /**
     * Gets the worker's job rank.
     * @return The job rank.
     */
    public String getJob_rank() {
        return job_rank;
    }

    /**
     * Sets the worker's job rank.
     * @param job_rank The new job rank.
     */
    public void setJob_rank(String job_rank) {
        this.job_rank = job_rank;
    }

    /**
     * Gets the worker's unique ID.
     * @return The worker ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the worker's unique ID.
     * @param id The new ID.
     */
    public void setId(String id) {
        this.id =id;
    }

    /**
     * Gets the worker's username.
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the worker's username.
     * @param username The new username.
     */
    public void setUsername(String username) {
        this.username =username;
    }

    /**
     * Checks if the worker is currently in a shift.
     * @return true if in a shift, false otherwise.
     */
    public boolean getInShift() {
        return inShift;
    }

    /**
     * Sets the worker's shift status.
     * @param inShift The new shift status.
     */
    public void setInShift(boolean inShift) {
        this.inShift = inShift;
    }

    /**
     * Checks if the worker can edit the inventory.
     * @return true if the worker has permission, false otherwise.
     */
    public boolean getCanEditInventory() {
        return canEditInventory;
    }

    /**
     * Sets the worker's permission to edit inventory.
     * @param canEditInventory The new permission status.
     */
    public void setCanEditInventory(boolean canEditInventory) {
        this.canEditInventory = canEditInventory;
    }

    /**
     * Gets the list of worker's presences.
     * @return The list of presences.
     */
    public ArrayList<Presences> getPresences() {
        return presences;
    }

    /**
     * Sets the list of worker's presences.
     * @param presences The new list of presences.
     */
    public void setPresences(ArrayList<Presences> presences) {
        this.presences = presences;
    }


    /**
     * Checks if the worker can manage shifts.
     * @return true if the worker has permission, false otherwise.
     */
    public boolean getCan_manage_shift() {
        return can_manage_shift;
    }

    /**
     * Sets the worker's permission to manage shifts.
     * @param can_manage_shift The new permission status.
     */
    public void setCan_manage_shift(boolean can_manage_shift) {
        this.can_manage_shift = can_manage_shift;
    }
}
