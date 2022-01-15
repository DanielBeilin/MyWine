package com.example.mywine.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Comment {

    final public static String COLLECTION_NAME = "comments";

    @PrimaryKey
    @NonNull
    String Uid="";
    String content="";
    Long updateDate = new Long(0);
    String createdBy ="";

    public Comment() {
    }

    public Comment(@NonNull String uid, String content, Long updateDate, String created_by) {
        Uid = uid;
        this.content = content;
        this.updateDate = updateDate;
        this.createdBy = created_by;
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

    public String getCreated_by() {
        return createdBy;
    }

    public void setCreated_by(String created_by) {
        this.createdBy = created_by;
    }

    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("id", Uid);
        json.put("content", content);
        json.put("createdBy", createdBy);
        json.put("updateDate", FieldValue.serverTimestamp());
        return json;
    }

    public static Comment create(Map<String, Object> json) {
        String Uid = (String) json.get("id");
        String content = (String) json.get("content");
        Long updateDate = ((Timestamp) json.get("updateDate")).getSeconds();
        String createdBy = (String) json.get("createdBy");

        Comment comment = new Comment(Uid, content,updateDate,createdBy);
        return comment;
    }

}
