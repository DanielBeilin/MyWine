package com.example.mywine.model.User;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


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

    public User(@NonNull String uid, String fullName, String profilePhoto, String email, Long updateDate) {
        Uid = uid;
        this.fullName = fullName;
        ProfilePhoto = profilePhoto;
        this.email = email;
        this.updateDate = updateDate;
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
        json.put("id", Uid);
        json.put("name", fullName);
        json.put("profilePhoto", ProfilePhoto);
        json.put("updateDate", FieldValue.serverTimestamp());
        json.put("email", email);
        return json;
    }

    public static User create(Map<String, Object> json) {
        String Uid = (String) json.get("id");
        String fullName = (String) json.get("name");
        String profilePhoto = (String) json.get("profilePhoto");
        Long updateDate = ((Timestamp) Objects.requireNonNull(json.get("updateDate"))).getSeconds();
        String email = (String) json.get("email");

        return new User(Objects.requireNonNull(Uid), fullName, profilePhoto, email, updateDate);
    }
}
