package com.example.mywine;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mywine.model.Post.Post;
import com.example.mywine.model.PostModelStorageFunctions;

import java.util.List;

public class ProfileListRvViewModel extends ViewModel {
    LiveData<List<Post>> data;

    public ProfileListRvViewModel() { data = PostModelStorageFunctions.instance.getPostsByUserID();}
    public LiveData<List<Post>> getData() { return data; }
}
