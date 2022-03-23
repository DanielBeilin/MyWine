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
import com.example.mywine.model.Post.Post;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PostModelStorageFunctions {
    public static final PostModelStorageFunctions instance = new PostModelStorageFunctions();
    public Executor executor = Executors.newFixedThreadPool(1);
    public Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());

    public enum PostListLoadingState {
        loading,
        loaded
    }

    MutableLiveData<PostListLoadingState> postListLoadingState = new MutableLiveData<PostListLoadingState>();
    MutableLiveData<PostListLoadingState> userPostListLoadingState = new MutableLiveData<PostListLoadingState>();

    public LiveData<PostListLoadingState> getPostListLoadingState() {
        return postListLoadingState;
    }
    public LiveData<PostListLoadingState> getUserPostListLoadingState() {
        return userPostListLoadingState;
    }

    ModelFirebase modelFirebase = new ModelFirebase();

    private PostModelStorageFunctions() {
        postListLoadingState.setValue(PostListLoadingState.loaded);
        userPostListLoadingState.setValue(PostListLoadingState.loaded);
    }

    MutableLiveData<List<Post>> postList = new MutableLiveData<List<Post>>();
    MutableLiveData<List<Post>> userPostList = new MutableLiveData<List<Post>>();

    public LiveData<List<Post>> getAllPosts() {
        if (postList.getValue() == null) {
            refreshPostList();
        }

        return postList;
    }

    public LiveData<List<Post>> getPostsByUserID() {
        if (userPostList.getValue() == null ){
            refreshUserPostList();
        }
        return userPostList;
    }

    public interface addPostListener {
        void onComplete();
    }

    public interface updatePostListener {
        void onComplete();
    }

    public void updatePost(Post post, updatePostListener listener) {
        modelFirebase.updatePost(post, () -> {
            listener.onComplete();
            refreshPostList();
            refreshUserPostList();
        });
    }

    public void refreshPostList() {
        postListLoadingState.setValue(PostListLoadingState.loading);

        Long lastUpdateDate = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getLong("PostLastUpdateDate", 0);

        executor.execute(() -> {
            List<Post> ptList = AppLocalDB.db.PostDao().getAll();
            postList.postValue(ptList);
        });

        modelFirebase.getAllPosts(lastUpdateDate, new ModelFirebase.getAllPostsListener() {
            @Override
            public void onComplete(List<Post> list) {
                // add all records to the local db
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long lastUpdateDate = new Long(0);
                        Log.d("TAG", "fb returned " + list.size());
                        for (Post post : list) {
                            AppLocalDB.db.PostDao().insertAll(post);
                            if (lastUpdateDate < post.getUpdateDate()) {
                                lastUpdateDate = post.getUpdateDate();
                            }
                        }
                        // update last local update date
                        MyApplication.getContext()
                                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                                .edit()
                                .putLong("PostLastUpdateDate", lastUpdateDate)
                                .commit();

                        //return all data to caller
                        List<Post> ptList = AppLocalDB.db.PostDao().getAll();
                        postList.postValue(ptList);
                        postListLoadingState.postValue(PostListLoadingState.loaded);
                    }
                });
            }
        });
    }

    public void refreshUserPostList() {
        userPostListLoadingState.setValue(PostListLoadingState.loading);
        String currentUserId = UserModelStorageFunctions.instance.getLoggedInUser().getUid();

        Long lastUpdateDate = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getLong("PostLastUpdateDate", 0);

        executor.execute(() -> {
            List<Post> ptList = AppLocalDB.db.PostDao().getAllByUser(currentUserId);
            userPostList.postValue(ptList);
        });

        modelFirebase.getPostsByUserId(currentUserId,lastUpdateDate, new ModelFirebase.getPostsByUserIDListener() {
            @Override
            public void onComplete(List<Post> list) {
                // add all records to the local db
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Long lastUpdateDate = new Long(0);
                        Log.d("TAG", "fb returned " + list.size());
                        for (Post post : list) {
                            AppLocalDB.db.PostDao().insertAll(post);
                            if (lastUpdateDate < post.getUpdateDate()) {
                                lastUpdateDate = post.getUpdateDate();
                            }
                        }
                        // update last local update date
                        MyApplication.getContext()
                                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                                .edit()
                                .putLong("PostLastUpdateDate", lastUpdateDate)
                                .commit();

                        //return all data to caller
                        List<Post> ptList = AppLocalDB.db.PostDao().getAllByUser(currentUserId);
                        userPostList.postValue(ptList);
                        userPostListLoadingState.postValue(PostListLoadingState.loaded);
                    }
                });
            }
        });
    }

    public interface getAllPostsListener{
        void onComplete(List<Post> list);
    }

    public interface  GetPostById {
        void onComplete(Post post);
    }

    public interface deletePostListener {
        void onComplete();
    }

    public void deletePost(Post post, deletePostListener listener){
        executor.execute(()->{
            AppLocalDB.db.PostDao().deletePost(true,post.getUid());
        });
        modelFirebase.deletePost(post, () ->{
            listener.onComplete();
            refreshPostList();
            refreshUserPostList();
        });
    }

    public interface getPostsByUserID {
        void onComplete(List<Post> postList);
    }


    public Post getPostById(String postId, GetPostById listener) {
        modelFirebase.getPostById(postId,listener);
        return null;
    }


    public interface addPostImageListener {
        void onComplete(String url);
    }

    public void addPost(Post post, addPostListener listener ){
        modelFirebase.addPost(post, () -> {
            listener.onComplete();
            refreshPostList();
            refreshUserPostList();
        });
    }

    public void uploadPostImage(Bitmap imageBmp, String name, PostModelStorageFunctions.addPostImageListener listener) {
        modelFirebase.uploadPostImage(imageBmp, name, listener);
    }

    public interface savePostImageListener{
        void onComplete(String url);
    }

//    public void savePostImage(Bitmap imageBitmap, String imageName, PostModelStorageFunctions.savePostImageListener listener ) {
//        modelFirebase.savePostImage(imageBitmap,imageName,listener);
//    }

}
