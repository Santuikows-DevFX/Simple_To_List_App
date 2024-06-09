package com.example.todolistnamin;

public class TodoModel {

    int id, entryId, entryTitleId;
    String entry, title, date, time, status;

    public TodoModel(int id, int entryId, int entryTitleId,String entry, String title, String date, String time, String status) {
        this.id = id;
        this.entryId = entryId;
        this.entryTitleId = entryTitleId;
        this.entry = entry;
        this.title = title;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public TodoModel(int id, String title, String date) {
        this.id = id;
        this.title = title;
        this.date = date;
    }

    public TodoModel(){

    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
