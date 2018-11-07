package com.example.minimous.servocontrol.Models;

import java.sql.Date;

/**
 * Created by minimous on 08/05/2016.
 */
public class History {
    private String username;
    private String date;
    private String action;

    public History(String username, String action, String date) {
        this.username = username;
        this.date = date;
        this.action = action;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
