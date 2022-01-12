package com.example.mywine;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mywine.model.User;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        String userID = ProfileFragmentArgs.fromBundle(getArguments()).getUserId();
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}