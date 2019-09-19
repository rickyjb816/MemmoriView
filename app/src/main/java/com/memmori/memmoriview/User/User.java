package com.memmori.memmoriview.User;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class User implements Parcelable {

    private String mUserName;
    private String mEmail;
    private String mUserID;
    private String mUserType; //Need to change to enum or something later
    private ArrayList<String> mUserLocations = new ArrayList<>();

    public User(QueryDocumentSnapshot document) {
        mUserName = String.valueOf(document.get("username"));
        mEmail = String.valueOf(document.get("email"));
        mUserID = String.valueOf(document.get("user_id"));
        mUserType = String.valueOf(document.get("user_type"));
        //createLocations(document);
    }

    protected User(Parcel in) {
        mUserName = in.readString();
        mEmail = in.readString();
        mUserID = in.readString();
        mUserType = in.readString();
        in.readStringList(mUserLocations);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mUserName);
        parcel.writeString(mEmail);
        parcel.writeString(mUserID);
        parcel.writeString(mUserType);
        parcel.writeStringList(mUserLocations);
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

    public ArrayList<String> getmUserLocations() {
        return mUserLocations;
    }

    public void setmUserLocations(ArrayList<String> mUserLocations) {
        this.mUserLocations = mUserLocations;
    }

    public void addNewLocation(String location)
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

    private void createLocations(QueryDocumentSnapshot doc)
    {

        boolean check = true;
        int i = 1;
        while(check)
        {
            if(String.valueOf(doc.get("locations.location"+i)).isEmpty())
            {
                check =  false;
            }
            else
            {
                mUserLocations.add(String.valueOf(doc.get("locations.location"+i)));
                i++;
            }
        }
    }
}
