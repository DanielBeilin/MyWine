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
import androidx.navigation.Navigation;
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

public class EditPostFragment extends PicturePickDialog {
    TextInputLayout contentInputLayout;
    TextInputEditText contentEditText;
    ImageView imageToUpload;
    MaterialButton uploadButton;
    MaterialButton cancelButton;
    RatingBar ratingBar;
    AppCompatTextView username;
    ShapeableImageView profilePic;
    ProgressDialog pd;
    String userId;
    String postId;
    User currentUser;
    Post currentPost;
    NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_post, container, false);
        init(view);
        setListeners();

        return view;

    }

    public void init(View view){
        contentEditText = view.findViewById(R.id.contentEditText);
        contentInputLayout = view.findViewById(R.id.contentInputLayout);
        username = view.findViewById(R.id.userName);
        profilePic = view.findViewById(R.id.userImg);
        imageToUpload = view.findViewById(R.id.imageToUpload);
        uploadButton = view.findViewById(R.id.saveBtn);
        ratingBar = view.findViewById(R.id.ratingBar);
        cancelButton = view.findViewById(R.id.cancelBtn);
        pd = new ProgressDialog(getActivity());

        userId = UserModelStorageFunctions.instance.getLoggedInUser().getUid();
        setUser(userId);

        postId = EditPostFragmentArgs.fromBundle(getArguments()).getPostId();
        setPost(postId);
        navController = NavHostFragment.findNavController(this);

    }

    public void setUser(String id) {
        UserModelStorageFunctions.instance.getUserById(userId, new UserModelStorageFunctions.GetUserById() {
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

    public void setPost(String id) {
        PostModelStorageFunctions.instance.getPostById(id, new PostModelStorageFunctions.GetPostById() {
            @Override
            public void onComplete(Post post) {
                if (post != null) {
                    currentPost = post;
                    initPostDetails();
                }
            }
        });
    }

    private void initPostDetails() {
        contentEditText.setText(currentPost.getContent());
        imageToUpload.setImageResource(R.drawable.ic_profile);
        ratingBar.setRating(currentPost.getRating());
        String imageUrl = currentPost.getPhotoUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .into(imageToUpload);
        }
    }
    public void setListeners() {
        onImageClick();
        onUploadButtonClick();
        onCancelButtonClick();
    }

    public void onCancelButtonClick(){
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });
    }

    public void onUploadButtonClick() {
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = contentEditText.getText().toString().trim();

                if (TextUtils.isEmpty(description)) {
                    contentInputLayout.setError("Post Can't be empty");
                } else if (imageToUpload.getDrawable() == null) {
                    Toast.makeText(getContext(), "Select an Image", Toast.LENGTH_LONG).show();
                } else {
                    uploadPost(description);
                }
            }
        });
    }

    private void uploadPost(String content) {
        pd.setMessage("Publishing Post");
        pd.show();
        currentPost.setContent(content);
        currentPost.setRating(ratingBar.getRating());
        Bitmap postImage = ((BitmapDrawable)imageToUpload.getDrawable()).getBitmap();
        if (postImage == null) {
            PostModelStorageFunctions.instance.addPost(currentPost, () -> {
                pd.dismiss();
                navController.navigate(R.id.feedFragment);
            });
        } else {
            PostModelStorageFunctions.instance.uploadPostImage(postImage, UUID.randomUUID().toString() + ".jpg", (url) -> {
                currentPost.setPhotoUrl(url);
                PostModelStorageFunctions.instance.updatePost(currentPost, () -> {
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