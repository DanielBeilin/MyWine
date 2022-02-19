package com.example.mywine.model.User;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mywine.model.User.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM User")
    List<User> getAll();

    @Query("SELECT * FROM User WHERE email = :email")
    User selectUserByEmail(String email);

    @Query("SELECT * FROM User WHERE Uid = :user_id")
    User selectUserById(String user_id);

    @Query("SELECT fullName FROM User where Uid = :user_id")
    String selectUserNameById(String user_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(User... users);

    @Delete
    void delete(User user);


}
