package com.example.mywine;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mywine.model.Post.Post;
import com.example.mywine.model.PostModelStorageFunctions;

import java.util.List;

public class PostListRvViewModel extends ViewModel {
    LiveData<List<Post>> data;

    public PostListRvViewModel() { data = PostModelStorageFunctions.instance.getAllPosts();}
    public LiveData<List<Post>> getData() { return data; }
}
