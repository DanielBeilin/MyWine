package com.example.mywine.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class User {
    final public static String COLLECTION_NAME = "users";

    @PrimaryKey
    @NonNull
    String id="", name="", ProfilePhoto="", posts="";
    Date updateDate = new Date();


}
