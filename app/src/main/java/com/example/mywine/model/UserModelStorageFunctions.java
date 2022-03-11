package com.example.mywine.model;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Map;
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

    public interface GetUserById {
        void onComplete(User user);
    }

    public interface GetNameByUserId {
        void onComplete(String userName);
    }

    public String getNameByUserId(String userId, UserModelStorageFunctions.GetNameByUserId listener) {
        modelFirebase.getUserNameById(userId,listener);
        return null;
    }
    public User getUserById(String userId,UserModelStorageFunctions.GetUserById listener) {
        modelFirebase.getUserById(userId,listener);
        return null;
    }


    public interface addUserListener {
        void onComplete();
    }

    public void addUser(User user, String password,UserModelStorageFunctions.addUserListener listener ){
        modelFirebase.addUser(user,password,() -> {
            listener.onComplete();
            refreshUserList();
        });
    }

    public void updateUser(User user, UserModelStorageFunctions.addUserListener listener) {
        modelFirebase.updateUser(user, () -> {
            listener.onComplete();
            refreshUserList();
        });
    }

    public interface UploadUserPhotoListener {
        void onComplete(String url);
    }

    public void uploadUserPhoto(Bitmap bitmap, String uid, UploadUserPhotoListener listener) {
        modelFirebase.uploadUserPhoto(bitmap, uid, listener);
    }

    public void signIn(String email, String password, ModelFirebase.SignInOnSuccessListener onSuccessListener, ModelFirebase.SignInOnFailureListener onFailureListener) {
        modelFirebase.signIn(email, password, onSuccessListener, onFailureListener);
    }

    public interface saveUserImageListener{
        void onComplete(String url);
    }

    public void saveUserImage(Bitmap imageBitmap, String uid,String imageName, UserModelStorageFunctions.saveUserImageListener listener ) {
        modelFirebase.saveUserImage(imageBitmap,uid,imageName,listener);
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

        modelFirebase.getAllUsers(lastUpdateDate, new ModelFirebase.getAllUsersListener() {
            @Override
            public void onComplete(List<User> list) {
                // add all records to the local db
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long lastUpdateDate = new Long(0);
                        Log.d("TAG", "fb returned " + list.size());
                        for (User user : list) {
                            AppLocalDB.db.UserDao().insertAll(user);
                            if (lastUpdateDate < user.getUpdateDate()) {
                                lastUpdateDate = user.getUpdateDate();
                            }
                        }
                        // update last local update date
                        MyApplication.getContext()
                                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                                .edit()
                                .putLong("UserLastUpdateDate", lastUpdateDate)
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


    public boolean isSignedIn() {
        return modelFirebase.isSignedIn();
    }

    public FirebaseUser getLoggedInUser() {
        return (modelFirebase.getLoggedInUser());
    }
    public void signUserOut() { modelFirebase.signUserOut();}
}
