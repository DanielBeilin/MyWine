package com.example.mywine;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mywine.model.PicturePickDialog;
import com.example.mywine.model.Post.Post;
import com.example.mywine.model.PostModelStorageFunctions;
import com.example.mywine.model.User.User;
import com.example.mywine.model.UserModelStorageFunctions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class AddPostFragment extends PicturePickDialog {

    public AddPostFragment() {
        // Required empty public constructor
    }

    TextInputLayout contentInputLayout;
    TextInputEditText contentEditText;
    ImageView imageToUpload;
    MaterialButton uploadButton;
    RatingBar ratingBar;
    AppCompatTextView username;
    ShapeableImageView profilePic;
    ProgressDialog pd;
    String userId;
    User currentUser;
    NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        init(view);
        setListeners();

        return view;
    }

    public void init(View view) {
        contentEditText = view.findViewById(R.id.contentEditText);
        contentInputLayout = view.findViewById(R.id.contentInputLayout);
        username = view.findViewById(R.id.userName);
        profilePic = view.findViewById(R.id.userImg);
        imageToUpload = view.findViewById(R.id.imageToUpload);
        imageToUpload.setImageResource(R.drawable.empty_image);
        uploadButton = view.findViewById(R.id.uploadButton);
        ratingBar = view.findViewById(R.id.ratingBar);

        pd = new ProgressDialog(getContext());
        pd.setCanceledOnTouchOutside(false);

        userId = AddPostFragmentArgs.fromBundle(getArguments()).getUserId();
        setUser(userId);
        navController = NavHostFragment.findNavController(this);
    }

    public void setUser(String id) {
        UserModelStorageFunctions.instance.getUserById(id, new UserModelStorageFunctions.GetUserById() {
            @Override
            public void onComplete(User user) {
                if (user != null) {
                    currentUser = user;
                    initUserDetails();
                }
            }
        });
    }

    private void initUserDetails() {
        username.setText(currentUser.getFullName());
        profilePic.setImageResource(R.drawable.ic_profile);
        String imageUrl = currentUser.getProfilePhoto();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .into(profilePic);
        }
    }

    public void setListeners() {
        onImageClick();
        onUploadButtonClick();
    }

    public void onUploadButtonClick() {
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = contentEditText.getText().toString().trim();

                if (TextUtils.isEmpty(description)) {
                    contentInputLayout.setError("Post Can't be empty");
                } else if (imageToUpload.getDrawable() != null) {
                    uploadPost(description, ratingBar.getRating());
                } else {
                    Toast.makeText(getContext(), "Select an Image", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void uploadPost(String content, Float rating) {
        pd.setMessage("Publishing Post");
        pd.show();

        Post p = new Post(content, rating);
        p.setUserId(userId);
        Bitmap postImage = ((BitmapDrawable)imageToUpload.getDrawable()).getBitmap();
        if (postImage == null) {
            PostModelStorageFunctions.instance.addPost(p, () -> {
                pd.dismiss();
                navController.navigate(R.id.feedFragment);
            });
        } else {
            PostModelStorageFunctions.instance.uploadPostImage(postImage, UUID.randomUUID().toString() + ".jpg", (url) -> {
                p.setPhotoUrl(url);
                PostModelStorageFunctions.instance.addPost(p, () -> {
                    pd.dismiss();
                    navController.navigate(R.id.feedFragment);
                });
            });
        }
    }

    public void onImageClick() {
            imageToUpload.setOnClickListener(this::showDialog);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    imageToUpload.setImageURI(selectedImageUri);
                }
            }
            if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageToUpload.setImageBitmap(imageBitmap);
            }
        }
    }
}