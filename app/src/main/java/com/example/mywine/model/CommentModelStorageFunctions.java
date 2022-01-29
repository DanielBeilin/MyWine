package com.example.mywine.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mywine.MyApplication;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CommentModelStorageFunctions {
    public static final CommentModelStorageFunctions instance = new CommentModelStorageFunctions();
    public Executor executor = Executors.newFixedThreadPool(1);
    public Handler mainThread = HandlerCompat.createAsync(Looper.getMainLooper());

    public enum CommentListLoadingState {
        loading,
        loaded
    }

    MutableLiveData<CommentListLoadingState> commentListLoadingState = new MutableLiveData<CommentListLoadingState>();

    public LiveData<CommentListLoadingState> getCommentLoadingState() { return commentListLoadingState; }

    ModelFirebase modelFirebase = new ModelFirebase();

    private CommentModelStorageFunctions() {
        commentListLoadingState.setValue(CommentListLoadingState.loaded);
    }

    MutableLiveData<List<Comment>> commentList = new MutableLiveData<List<Comment>>();

    public LiveData<List<Comment>> getAllComments() {
        if (commentList.getValue() == null) {
            refreshCommentList();
        }

        return commentList;
    }

    public interface addCommentListener {
        void onComplete();
    }

    public void addComment(Comment comment, CommentModelStorageFunctions.addCommentListener listener ){
        modelFirebase.addComment(comment, () -> {
            listener.onComplete();
            refreshCommentList();
        });
    }

    public interface  GetCommentById {
        void onComplete(Comment comment);
    }

    public interface GetCommentsByPostId{
        void onComplete(List<Comment> commentList);
    }

    public List<Comment> getCommentsByPostId(String postId, GetCommentsByPostId listener) {
        modelFirebase.getCommentsByPostId(postId, listener);
        return null;
    }

    public Comment getCommentById(String commentId, GetCommentById listener) {
        modelFirebase.getCommentById(commentId,listener);
        return null;
    }


    public void refreshCommentList() {
        commentListLoadingState.setValue(CommentListLoadingState.loading);

        Long lastUpdateDate = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getLong("CommentLastUpdateDate", 0);

        executor.execute(() -> {
            List<Comment> cmtList = AppLocalDB.db.CommentDao().getAll();
            commentList.postValue(cmtList);
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
                        List<Comment> cmtList = AppLocalDB.db.CommentDao().getAll();
                        commentList.postValue(cmtList);
                        commentListLoadingState.postValue(CommentModelStorageFunctions.CommentListLoadingState.loaded);
                    }
                });
            }

        });
    }

}
