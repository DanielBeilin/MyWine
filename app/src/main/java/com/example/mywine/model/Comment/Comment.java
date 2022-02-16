package com.example.mywine.model.Comment;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
public class Comment {

    final public static String COLLECTION_NAME = "comments";

    @PrimaryKey
    @NonNull
    String Uid="";
    String content="";
    Long updateDate = 0L;
    String userId ="";


    public Comment(@NonNull String uid, String content, Long updateDate, String userId, String postId) {
        Uid = uid;
        this.content = content;
        this.updateDate = updateDate;
        this.userId = userId;
        this.postId = postId;
    }

    @NonNull
    public String getUid() {
        return Uid;
    }

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

    String postId = "";
    public Comment() {
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("id", Uid);
        json.put("content", content);
        json.put("userId", userId);
        json.put("postId", userId);
        json.put("updateDate", FieldValue.serverTimestamp());
        return json;
    }

    public static Comment create(Map<String, Object> json) {
        String Uid = (String) json.get("id");
        String content = (String) json.get("content");
        Long updateDate = ((Timestamp) Objects.requireNonNull(json.get("updateDate"))).getSeconds();
        String userId = (String) json.get("userId");
        String postId = (String) json.get("postId");

        return new Comment(Uid, content,updateDate,userId,postId);
    }

}
