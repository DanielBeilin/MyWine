package com.example.mywine.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Comment {

    final public static String COLLECTION_NAME = "comments";

    @PrimaryKey
    @NonNull
    String Uid="";
    String content="";
    Long updateDate = new Long(0);
    String created_by="";


}
