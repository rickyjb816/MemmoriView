package com.memmori.memmoriview;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Location implements Parcelable {

    private String name;
    private GeoPoint location;
    private String owner;
    private String photographer;
    private Timestamp dateTaken;
    private Timestamp dateAdded;
    private int picture;
    private String streetName;
    private String buildingName;
    private String description;

    public Location(String name, GeoPoint location, String owner, String photographer, Timestamp dateTaken, Timestamp dateAdded, int picture, String streetName, String buildingName, String description) {
        this.name = name;
        this.location = location;
        this.owner = owner;
        this.photographer = photographer;
        this.dateTaken = dateTaken;
        this.dateAdded = dateAdded;
        this.picture = picture;
        this.streetName = streetName;
        this.buildingName = buildingName;
        this.description = description;
    }

    public Location(QueryDocumentSnapshot location) {
        this.name = location.get("name").toString();
        this.location = (GeoPoint) location.get("location");
        this.owner = location.get("owner").toString();
        this.photographer = location.get("photographer").toString();
        this.dateTaken = (Timestamp) location.get("date_Taken");
        this.dateAdded = (Timestamp) location.get("date_Added");
        this.picture = Integer.valueOf((String) location.get("picture"));
        this.streetName = location.get("street_name").toString();
        this.buildingName = location.get("building_name").toString();
        this.description = location.get("description").toString();
    }

    protected Location(Parcel in) {
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        this.photographer = photographer;
    }

    public Timestamp getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Timestamp dateTaken) {
        this.dateTaken = dateTaken;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
