package com.example.mywine.model.Post;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.mywine.model.Converters;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Post {

    final public static String COLLECTION_NAME = "Posts";

    @PrimaryKey
    @NonNull
    String Uid = "";
    String title = "";
    String userId = "";
    String content = "";
    String photoUrl = "";
    Integer likeCount = 0;
    Boolean isDeleted = false;

    @TypeConverters(Converters.class)
    ArrayList<String> LikedBy = new ArrayList<String>();
    @TypeConverters(Converters.class)
    ArrayList<String> CommentList = new ArrayList<String>();
    Long updateDate = 0L;

    public Post() {
    }

    @Ignore
    public Post(String content) {
        this.content = content;
        this.photoUrl = null;
    }

    public Post(String title,
                String userId,
                String content,
                String photoUrl,
                Integer likeCount,
                Boolean isDeleted) {
        this.Uid = UUID.randomUUID().toString();
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.photoUrl = photoUrl;
        this.likeCount = likeCount;
        this.isDeleted = isDeleted;
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public ArrayList<String> getLikedBy() {
        return LikedBy;
    }

    public void setLikedBy(ArrayList<String> likedBy) {
        LikedBy = likedBy;
    }

    public ArrayList<String> getCommentList() {
        return CommentList;
    }

    public void setCommentList(ArrayList<String> commentList) {
        CommentList = commentList;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public void addLike(String userID) {
        if(LikedBy.contains(userID)) {
            this.likeCount -= 1;
            this.LikedBy.remove(userID);
        } else {
            this.likeCount += 1;
            this.LikedBy.add(userID);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void addComment(String commentID) {
        this.CommentList.add(commentID);
    }


    public Map<String, Object> toJson() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("Uid", Uid);
        json.put("userId",userId);
        json.put("title",title);
        json.put("content", content);
        json.put("likeCount", likeCount);
        json.put("updateDate", FieldValue.serverTimestamp());
        json.put("commentList", TextUtils.join(", ", CommentList));
        json.put("likedBy", TextUtils.join(", ", LikedBy));
        json.put("photoUrl", photoUrl);
        json.put("isDeleted",isDeleted);
        return json;
    }

    public static Post create(Map<String, Object> json) {
        String userId = (String) json.get("userId");
        String title = (String) json.get("title");
        String content = (String) json.get("content");
        Integer likeCount =  Integer.parseInt((String) json.get("likeCount"));
        Timestamp ts = (Timestamp) json.get("updateDate");
        Long updateDate = ts.getSeconds();
        ArrayList<String> commentList = new ArrayList<String>
                (Arrays.asList(((String) Objects.requireNonNull(json.get("commentList"))).split(",")));
        ArrayList<String> likedBy = new ArrayList<String>
                (Arrays.asList(((String) Objects.requireNonNull(json.get("likedBy"))).split(",")));
        String photoUrl = (String) json.get("photoUrl");
        Boolean isDeleted = (Boolean) json.get("isDeleted");
        Post post = new Post(title,userId, content,photoUrl,likeCount,isDeleted);
        post.setUid((String) json.get("Uid"));
        post.setUpdateDate(updateDate);
        post.setCommentList(commentList);
        post.setLikedBy(likedBy);
        post.setLikeCount(likeCount);
        return post;
    }


}
