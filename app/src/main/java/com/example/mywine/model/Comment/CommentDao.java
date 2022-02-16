package com.example.mywine.model.Comment;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CommentDao {

    @Query("SELECT * FROM Comment")
    List<Comment> getAll();

    @Query("SELECT * FROM Comment WHERE userId = :user_id")
    List<Comment> getCommentsByUser(String user_id);

    @Query("SELECT * FROM Comment WHERE Uid = :comment_id")
    Comment getCommentById(String comment_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Comment... Comment);

    @Delete
    void delete(Comment Comment);
}
