package com.memmori.memmoriview.Location;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;

public class Location implements Parcelable {

    private GeoPoint location;
    private String owner;
    private String photographer;
    private Timestamp dateTaken;
    private Timestamp dateAdded;
    private String picture;
    private String streetName;
    private String buildingName;
    private String description;
    private String filter;

    public Location(GeoPoint location, String owner, String photographer, Timestamp dateTaken, Timestamp dateAdded, String picture, String streetName, String buildingName, String description, String Test) {
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
        this.location = (GeoPoint) location.get("location");
        this.owner = location.get("owner").toString();
        this.photographer = location.get("photographer").toString();
        this.dateTaken = (Timestamp) location.get("date_taken");
        this.dateAdded = (Timestamp) location.get("date_added");
        this.picture = location.get("picture").toString();
        this.streetName = location.get("street_name").toString();
        this.buildingName = location.get("building_name").toString();
        this.description = location.get("description").toString();
        this.filter = location.get("filter").toString();
    }

    protected Location(Parcel in) {
        location = new GeoPoint(in.readDouble(), in.readDouble());
        owner = in.readString();
        photographer = in.readString();
        dateTaken = in.readParcelable(Timestamp.class.getClassLoader());
        dateAdded = in.readParcelable(Timestamp.class.getClassLoader());
        picture = in.readString();
        streetName = in.readString();
        buildingName = in.readString();
        description = in.readString();
        filter = in.readString();
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

    public Location() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(location.getLatitude());
        parcel.writeDouble(location.getLongitude());
        parcel.writeString(owner);
        parcel.writeString(photographer);
        parcel.writeParcelable(dateTaken, 1);
        parcel.writeParcelable(dateAdded, 1);
        parcel.writeString(picture);
        parcel.writeString(streetName);
        parcel.writeString(buildingName);
        parcel.writeString(description);
        parcel.writeString(filter);
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

    public String getDateTakenString()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        String dateStr = simpleDateFormat.format(dateTaken.toDate());
        return dateStr;
    }

    public void setDateTaken(Timestamp dateTaken) {
        this.dateTaken = dateTaken;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public String getDateAddedString()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        String dateStr = simpleDateFormat.format(dateAdded.toDate());
        return dateStr;
    }

    public void setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
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

    public String getFilter()
    {
        return filter;
    }

    public void setFilter(String filter)
    {
        this.filter = filter;
    }
}
