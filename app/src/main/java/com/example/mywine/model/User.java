package com.example.mywine.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class User {
    final public static String COLLECTION_NAME = "users";

    @PrimaryKey
    @NonNull
    String Uid="";
    String Fullname="", ProfilePhoto="", email="";
    Long updateDate = new Long(0);


}
