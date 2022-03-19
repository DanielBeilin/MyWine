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

    @Query("SELECT * FROM Post WHERE userId= :userId")
    List<Post> getAllByUser(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Post... posts);

    @Delete
    void delete(Post post);
}
