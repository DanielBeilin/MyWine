package com.example.mywine.model;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.mywine.MyApplication;
import com.example.mywine.model.Post.Post;

public class PostModelStorageFunctions {
    public static final PostModelStorageFunctions instance = new PostModelStorageFunctions();
    public Executor executor = Executors.newFixedThreadPool(1);
    public Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());

    public enum PostListLoadingState {
        loading,
        loaded
    }

    MutableLiveData<PostListLoadingState> postListLoadingState = new MutableLiveData<PostListLoadingState>();

    public LiveData<PostListLoadingState> getPostListLoadingState() {
        return postListLoadingState;
    }

    ModelFirebase modelFirebase = new ModelFirebase();

    private PostModelStorageFunctions() {
        postListLoadingState.setValue(PostListLoadingState.loaded);
    }

    MutableLiveData<List<Post>> postList = new MutableLiveData<List<Post>>();

    public LiveData<List<Post>> getAllPosts() {
        if (postList.getValue() == null) {
            refreshPostList();
        }

        return postList;
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

    public interface getAllPostsListener{
        void onComplete(List<Post> list);
    }

    public interface  GetPostById {
        void onComplete(Post post);
    }

    public interface getPostsByUserID {
        void onComplete(List<Post> postList);
    }

    public List<Post> getPostsByUserID(String userId, getPostsByUserID listener) {
        modelFirebase.getPostsByUserId(userId,listener);
        return null;
    }

    public Post getPostById(String postId, GetPostById listener) {
        modelFirebase.getPostById(postId,listener);
        return null;
    }

    public interface addPostListener {
        void onComplete();
    }

    public void addPost(Post post, addPostListener listener ){
        modelFirebase.addPost(post, () -> {
            listener.onComplete();
            refreshPostList();
        });
    }
}
