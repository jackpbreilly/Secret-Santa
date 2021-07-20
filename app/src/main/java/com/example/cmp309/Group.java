package com.example.cmp309;
// data to create a new group
class Group{
    private String Name, Time, Date, Theme, Limit, creator;
    private String  Lat;
    private String  Lon;

    public Group(String Name_, String Time_, String Date_, String Theme_, String Limit_, String lat, String lon, String Creator){
        Name = Name_; Time=Time_; Date = Date_; Theme = Theme_; Limit = Limit_;
        Lat = lat;
        Lon = lon;
        creator = Creator;
    }

    public String getLat() {
        return Lat;
    }
    public String getCreator() {
        return creator;
    }

    public String getLon() {
        return Lon;
    }

    public String getName() {
        return Name;
    }

    public String getTime() {
        return Time;
    }

    public String getDate() {
        return Date;
    }

    public String getTheme() {
        return Theme;
    }

    public String getLimit() {
        return Limit;
    }


}