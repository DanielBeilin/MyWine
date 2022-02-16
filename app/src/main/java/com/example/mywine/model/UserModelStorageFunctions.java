package com.example.mywine.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mywine.MyApplication;
import com.example.mywine.model.Comment.Comment;
import com.example.mywine.model.Post.Post;
import com.example.mywine.model.User.User;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserModelStorageFunctions {

    public static final UserModelStorageFunctions instance = new UserModelStorageFunctions();
    public Executor executor = Executors.newFixedThreadPool(1);
    public Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());

    public enum UserListLoadingState {
        loading,
        loaded
    }

    MutableLiveData<UserModelStorageFunctions.UserListLoadingState> userListLoadingState =
            new MutableLiveData<UserModelStorageFunctions.UserListLoadingState>();

    public LiveData<UserModelStorageFunctions.UserListLoadingState> getUserListLoadingState() {
        return userListLoadingState;
    }

    ModelFirebase modelFirebase = new ModelFirebase();

    private UserModelStorageFunctions() {
        userListLoadingState.setValue(UserModelStorageFunctions.UserListLoadingState.loaded);
    }

    MutableLiveData<List<User>> userList = new MutableLiveData<List<User>>();

    public LiveData<List<User>> getAllPosts() {
        if (userList.getValue() == null) {
            refreshUserList();
        }

        return userList;
    }

    public interface  GetUserById {
        void onComplete(User user);
    }

    public Post getPostById(String userId, UserModelStorageFunctions.GetUserById listener) {
        modelFirebase.getUserById(userId,listener);
        return null;
    }


    public void refreshUserList() {
        userListLoadingState.setValue(UserListLoadingState.loading);

        Long lastUpdateDate = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getLong("CommentLastUpdateDate", 0);

        executor.execute(() -> {
            List<User> usrList = AppLocalDB.db.UserDao().getAll();
            userList.postValue(usrList);
        });

        modelFirebase.getAllComments(lastUpdateDate, new ModelFirebase.getAllCommentsListener() {
            @Override
            public void onComplete(List<Comment> list) {
                // add all records to the local db
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long lastUpdateDate = new Long(0);
                        Log.d("TAG", "fb returned " + list.size());
                        for (Comment comment : list) {
                            AppLocalDB.db.CommentDao().insertAll(comment);
                            if (lastUpdateDate < comment.getUpdateDate()) {
                                lastUpdateDate = comment.getUpdateDate();
                            }
                        }
                        // update last local update date
                        MyApplication.getContext()
                                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                                .edit()
                                .putLong("CommentLastUpdateDate", lastUpdateDate)
                                .commit();

                        //return all data to caller
                        List<User> cmtList = AppLocalDB.db.UserDao().getAll();
                        userList.postValue(cmtList);
                        userListLoadingState.postValue(UserListLoadingState.loaded);
                    }
                });
            }

        });
    }

}
