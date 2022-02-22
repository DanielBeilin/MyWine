package com.example.mywine.model;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.mywine.MyApplication;
import com.example.mywine.model.Comment.Comment;
import com.example.mywine.model.Comment.CommentDao;
import com.example.mywine.model.Post.Post;
import com.example.mywine.model.Post.PostDao;
import com.example.mywine.model.User.User;
import com.example.mywine.model.User.UserDao;

@Database(entities = {User.class,  Post.class}, version = 4)
@TypeConverters(Converters.class)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract UserDao UserDao();
    public abstract PostDao PostDao();
}

public class AppLocalDB {
    static public AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.getContext(),
                    AppLocalDbRepository.class,
                    "dbFileName.db")
                    .fallbackToDestructiveMigration()
                    .build();

}