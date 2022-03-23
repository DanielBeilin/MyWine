package com.example.mywine.model.User;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Entity
public class User {
    final public static String COLLECTION_NAME = "users";

    @PrimaryKey
    @NonNull
    String Uid = "";
    String fullName = "", ProfilePhoto = "", email = "";
    Long updateDate = 0L;

    public User() {
    }

    public User(@NonNull String uid, String fullName, String email) {
        Uid = uid;
        this.fullName = fullName;
        this.email = email;
    }

    public User(String fullName, String email) {
        Uid = UUID.randomUUID().toString();
        this.fullName = fullName;
        ProfilePhoto = "";
        this.email = email;
    }

    @NonNull
    public String getUid() {
        return Uid;
    }

    public void setUid(@NonNull String uid) {
        Uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePhoto() {
        return ProfilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        ProfilePhoto = profilePhoto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("Uid", Uid);
        json.put("fullName", fullName);
        json.put("ProfilePhoto", ProfilePhoto);
        json.put("updateDate", FieldValue.serverTimestamp());
        json.put("email", email);
        return json;
    }

    public static User create(Map<String, Object> json) {
        String Uid = (String) json.get("Uid");
        String fullName = (String) json.get("fullName");
        String profilePhoto = (String) json.get("ProfilePhoto");
        Timestamp ts = (Timestamp) json.get("updateDate");
        Long updateDate = ts.getSeconds();
        String email = (String) json.get("email");
        User user = new User(fullName, email);
        user.setUid(Uid);
        user.setUpdateDate(updateDate);
        user.setProfilePhoto(profilePhoto);
        return user;
    }
}
