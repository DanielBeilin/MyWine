package com.example.mywine.model.Post;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.mywine.model.Converters;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
public class Post {

    final public static String COLLECTION_NAME = "posts";

    @PrimaryKey
    @NonNull
    String Uid = "";
    String title = "";
    String userId = "";
    String content = "";
    String photoUrl = "";
    Integer likeCount = 0;

    @TypeConverters(Converters.class)
    ArrayList<String> LikedBy = new ArrayList<String>();
    @TypeConverters(Converters.class)
    ArrayList<String> CommentList = new ArrayList<String>();
    Long updateDate = 0L;

    public Post() {
    }

    public Post(@NonNull String uid,String title, String userId, String content, String photoUrl, Integer likeCount, Long updateDate) {
        Uid = uid;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.photoUrl = photoUrl;
        this.likeCount = likeCount;
        this.updateDate = updateDate;
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

    public void addLike(String userID) {
        this.likeCount += 1;
        this.LikedBy.add(userID);
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
        json.put("id", Uid);
        json.put("userId",userId);
        json.put("title",title);
        json.put("content", content);
        json.put("likeCount", likeCount);
        json.put("updateDate", FieldValue.serverTimestamp());
        json.put("commentList", TextUtils.join(", ", CommentList));
        json.put("likedBy", TextUtils.join(", ", LikedBy));
        json.put("photoUrl", photoUrl);
        return json;
    }

    public static Post create(Map<String, Object> json) {
        String Uid = (String) json.get("id");
        String userId = (String) json.get("userId");
        String title = (String) json.get("title");
        String content = (String) json.get("content");
        Integer likeCount = (Integer) json.get("likeCount");
        Long updateDate = ((Timestamp) Objects.requireNonNull(json.get("updateDate"))).getSeconds();
        ArrayList<String> commentList = new ArrayList<String>
                (Arrays.asList(((String) Objects.requireNonNull(json.get("commentList"))).split(",")));
        ArrayList<String> likedBy = new ArrayList<String>
                (Arrays.asList(((String) Objects.requireNonNull(json.get("likedBy"))).split(",")));
        String photoUrl = (String) json.get("photoUrl");
        Post post = new Post(Objects.requireNonNull(Uid), title,userId, content,photoUrl,likeCount,updateDate);
        post.setCommentList(commentList);
        post.setLikeCount(likeCount);
        return post;
    }


}
