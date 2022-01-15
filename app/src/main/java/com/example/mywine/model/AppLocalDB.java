package com.example.mywine.model;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mywine.MyApplication;

@Database(entities = {User.class,Comment.class,Post.class}, version = 4)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract UserDao UserDao();
    public abstract PostDao PostDao();
    public abstract CommentDao CommentDao();
}

public class AppLocalDB {
    static public AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.getContext(),
                    AppLocalDbRepository.class,
                    "dbFileName.db")
                    .fallbackToDestructiveMigration()
                    .build();

}
