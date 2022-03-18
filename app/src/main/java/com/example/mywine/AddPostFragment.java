package com.example.mywine;

import static com.example.mywine.model.PicturePickDialog.IMAGE_PICK_CAMERA_REQUEST_CODE;
import static com.example.mywine.model.PicturePickDialog.IMAGE_PICK_GALLERY_REQUEST_CODE;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.mywine.model.ModelFirebase;
import com.example.mywine.model.PicturePickDialog;
import com.example.mywine.model.Post.Post;
import com.example.mywine.model.PostModelStorageFunctions;
import com.example.mywine.model.User.User;
import com.example.mywine.model.UserModelStorageFunctions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
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
        uploadButton = view.findViewById(R.id.uploadButton);
        ratingBar = view.findViewById(R.id.ratingBar);

        pd = new ProgressDialog(getContext());
        pd.setCanceledOnTouchOutside(false);

        userId = UserModelStorageFunctions.instance.getLoggedInUser().getUid();
        setUser(userId);
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

        Post p = new Post(contentEditText.getText().toString());
        p.setUserId(userId);
        Bitmap postImage = ((BitmapDrawable)imageToUpload.getDrawable()).getBitmap();
        if (postImage == null) {
            PostModelStorageFunctions.instance.addPost(p, () -> {
                navController.navigate(R.id.feedFragment);
            });
        } else {
            PostModelStorageFunctions.instance.uploadPostImage(postImage, UUID.randomUUID().toString() + ".jpg", (url) -> {
                p.setPhotoUrl(url);
                PostModelStorageFunctions.instance.addPost(p, () -> {
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