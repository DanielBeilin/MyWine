package com.example.mywine.model.Comment;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.io.File;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Comment {

    final public static String COLLECTION_NAME = "comments";

    String content="";
    Long updateDate = 0L;
    String userId ="";
    String postId ="";
    @PrimaryKey
    @NonNull
    String Uid = "";


    public Comment(String content, String userId, String postId) {
        this.content = content;
        this.userId = userId;
        this.postId = postId;
        this.Uid = UUID.randomUUID().toString();
    }

    public String getUid() {return this.Uid;}


    public void setUid(@NonNull String uid) {
        Uid = uid;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Comment() {
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("commentId", Uid);
        json.put("content", content);
        json.put("userId", userId);
        json.put("postId", postId);
        json.put("updateDate", FieldValue.serverTimestamp());
        return json;
    }

    public static Comment create(Map<String, Object> json) {
        String uuid = (String) json.get("Uid");
        String content = (String) json.get("content");
        Timestamp ts = (Timestamp) json.get("updateDate");
        Long updateDate = ts.getSeconds();
        String userId = (String) json.get("userId");
        String postId = (String) json.get("postId");
        Comment comment = new Comment(content,userId,postId);
        comment.setUid(uuid);
        comment.setUpdateDate(updateDate);
        return comment;
    }

}
