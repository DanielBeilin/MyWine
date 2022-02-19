package com.example.mywine.model.Post;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PostDao {

    @Query("SELECT * FROM Post")
    List<Post> getAll();

    @Query("SELECT CommentList FROM Post WHERE Uid = :comment_id")
    List<String> getAllCommentsByPost(String comment_id);

    @Query("SELECT LikedBy FROM Post WHERE Uid = :comment_id")
    List<String> getAllLikesByPost(String comment_id);

    @Query("SELECT * FROM Post WHERE Uid = :comment_id")
    Post selectPostById(String comment_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Post... posts);

    @Delete
    void delete(Post post);
}
