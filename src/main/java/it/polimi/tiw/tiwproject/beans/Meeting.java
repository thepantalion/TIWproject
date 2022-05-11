package it.polimi.tiw.tiwproject.beans;

import java.sql.Time;
import java.util.Date;

public class Meeting {
    private int id;
    private int idCreator;
    private String creator;
    private String title;
    private Date date;
    private Time time;
    private int duration;
    private int numberOfParticipants;

    public Meeting(String creator, String title, Date date, Time time, int duration, int numberOfParticipants) {
        this.creator = creator;
        this.title = title;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.numberOfParticipants = numberOfParticipants;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCreator() {
        return idCreator;
    }

    public void setIdCreator(int idCreator) {
        this.idCreator = idCreator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
