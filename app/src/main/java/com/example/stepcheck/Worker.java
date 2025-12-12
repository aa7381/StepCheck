package com.example.stepcheck;

import static com.example.stepcheck.FBRef.refAuth;

import com.google.firebase.auth.FirebaseUser;

public class Worker
{
    private String id  ;
    private String username ;
    private String job_rank ;
    //private boolean inShift;
   // private boolean canEditInventory ;
    //private ArrayList<Presences>presences ;
   // private boolean can_manage_shift ;

    public Worker(String id, String username, String job_rank)
    {
        this.id = id;
        this.username = username;
        this.job_rank = job_rank;
      //  this.inShift = inShift;
        //this.canEditInventory = canEditInventory;
       // this.can_manage_shift = can_manage_shift;
    }
    public String getJob_rank() {
        return job_rank;
    }

    public void setJob_rank(String job_rank) {
        this.job_rank = job_rank;
    }


    public String getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }


    public void setId(String id) {
        this.id =id;
    }
    public void setUsername(String username) {
        this.username =username;
    }

    public Worker()
    {
        this.id = "";
        this.username = "";
        this.job_rank = "";

    }









}
