package com.example.mywine.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Post {

    final public static String COLLECTION_NAME = "posts";

    @PrimaryKey
    @NonNull
    String Uid="";
    String content="";
    String PhotoUrl="";
    Integer likeCount=0;
    List<String> LikedBy = new ArrayList<String>();
    List<String> CommentList = new ArrayList<String>();
    Long updateDate = new Long(0);



}
