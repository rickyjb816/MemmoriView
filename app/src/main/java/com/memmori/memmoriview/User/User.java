package com.memmori.memmoriview.User;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.memmori.memmoriview.Location.Location;

import java.util.ArrayList;
import java.util.Map;

public class User implements Parcelable {

    private String mUserName;
    private String mEmail;
    private String mUserID;
    private String mUserType; //Need to change to enum or something later
    private ArrayList<Location> mUserLocations;

    public User(QueryDocumentSnapshot document) {
        mUserName = String.valueOf(document.get("username"));
        mEmail = String.valueOf(document.get("email"));
        mUserID = String.valueOf(document.get("user_id"));
        mUserType = String.valueOf(document.get("user_type"));
        //Map<String, Object> map = document.getData();

    }

    protected User(Parcel in) {
        mUserName = in.readString();
        mEmail = in.readString();
        mUserID = in.readString();
        mUserType = in.readString();
        mUserLocations = in.createTypedArrayList(Location.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mUserName);
        parcel.writeString(mEmail);
        parcel.writeString(mUserID);
        parcel.writeString(mUserType);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmUserID() {
        return mUserID;
    }

    public void setmUserID(String mUserID) {
        this.mUserID = mUserID;
    }

    public String getmUserType() {
        return mUserType;
    }

    public void setmUserType(String mUserType) {
        this.mUserType = mUserType;
    }

    public ArrayList<Location> getmUserLocations() {
        return mUserLocations;
    }

    public void setmUserLocations(ArrayList<Location> mUserLocations) {
        this.mUserLocations = mUserLocations;
    }

    public void addNewLocation(Location location)
    {
        //Add to Database
        mUserLocations.add(location);
    }

    private void SetUpUserLocation(DocumentSnapshot document) {

    }

    @Override
    public int describeContents() {
        return 0;
    }


}
