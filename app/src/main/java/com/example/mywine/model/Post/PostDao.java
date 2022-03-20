package com.example.mywine.model.Post;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PostDao {

    @Query("SELECT * FROM Post WHERE NOT isDeleted")
    List<Post> getAll();

    @Query("SELECT * FROM Post WHERE userId= :userId AND NOT isDeleted")
    List<Post> getAllByUser(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Post... posts);

    @Query("UPDATE Post SET isDeleted=:post_status WHERE Uid=:post_id")
    void deletePost(Boolean post_status,String post_id);

    @Delete
    void delete(Post post);
}
