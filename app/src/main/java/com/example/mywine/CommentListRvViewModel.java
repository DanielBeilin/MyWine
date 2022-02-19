package com.example.mywine;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mywine.model.Comment.Comment;
import com.example.mywine.model.CommentModelStorageFunctions;

import java.util.List;

public class CommentListRvViewModel extends ViewModel {
    LiveData<List<Comment>> data;

    public CommentListRvViewModel() { data = CommentModelStorageFunctions.instance.getAllComments();}
    public LiveData<List<Comment>> getData() { return data; }
}
