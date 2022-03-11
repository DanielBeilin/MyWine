package com.example.mywine;

import static com.example.mywine.model.PicturePickDialog.IMAGE_PICK_CAMERA_REQUEST_CODE;
import static com.example.mywine.model.PicturePickDialog.IMAGE_PICK_GALLERY_REQUEST_CODE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mywine.model.PicturePickDialog;
import com.example.mywine.model.User.User;
import com.example.mywine.model.UserModelStorageFunctions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.util.HashMap;

public class ProfileFragment extends Fragment implements PicturePickDialog.NoticeDialogListener {

    ImageView avatarImage;
    TextView nameTextView, emailTextView;
    MaterialButton editButton;
    ProgressDialog pd;
    Bitmap imageBitmap;
    String userId;
    User currentUser;
    NavController navController;
    SwipeRefreshLayout swipeRefresh;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setHasOptionsMenu(true);

        init(view);
        setListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        UserModelStorageFunctions.instance.refreshUserList();
    }

    private void setListeners() {
        onFabClick();
    }

    private void onFabClick() {
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });
    }

    private void init(View view) {
        avatarImage = view.findViewById(R.id.profilePhoto);
        nameTextView = view.findViewById(R.id.name);
        emailTextView = view.findViewById(R.id.email);
        editButton = view.findViewById(R.id.editButton);
        swipeRefresh = view.findViewById(R.id.profile_fragment_swipe_refresh);
        navController = NavHostFragment.findNavController(this);
        pd = new ProgressDialog(getActivity());
        userId = UserModelStorageFunctions.instance.getLoggedInUser().getUid();
        setUser(userId);
    }

    private void initUserDetails() {
        nameTextView.setText(currentUser.getFullName());
        emailTextView.setText(currentUser.getEmail());
        avatarImage.setImageResource(R.drawable.ic_add_photo);
        String imageUrl = currentUser.getProfilePhoto();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .into(avatarImage);
        }
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

    private void showEditProfileDialog() {
        String options[] = {"edit name", "edit profile photo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // edit name
                    pd.setMessage("updating name");
                    showFieldUpdateDialog("name");
                } else if (which == 1) {
                    // edit profile photo
                    pd.setMessage("updating profile picture");
                    showPicturePickDialog();
                }
            }
        });
        builder.create().show();
    }

    public void showPicturePickDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment PicturePickDialog = new PicturePickDialog();
        PicturePickDialog.show(getActivity().getSupportFragmentManager(), "PicturePickDialogFragment");
    }

    private void showFieldUpdateDialog(String field) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update " + field);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);
        EditText editText = new EditText(getActivity());
        editText.setHint("Enter " + field);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        // update
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)) {
                    pd.show();
                    currentUser.setFullName(value);
                    UserModelStorageFunctions.instance.updateUser(currentUser,() -> {
                        pd.dismiss();
                        navController.navigate(R.id.profileFragment);
                    });
                } else {
                    Toast.makeText(getActivity(), "Please enter " + field, Toast.LENGTH_SHORT).show();
                }
            }
        });
        // cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void updateProfilePic() {
        pd.show();
        Bitmap profileImage = ((BitmapDrawable)avatarImage.getDrawable()).getBitmap();
        UserModelStorageFunctions.instance.uploadUserPhoto(profileImage, currentUser.getUid() + ".jpg", (url) -> {
            currentUser.setProfilePhoto(url);
            UserModelStorageFunctions.instance.updateUser(currentUser,() -> {
                pd.dismiss();
                navController.navigate(R.id.profileFragment);
            });
        });
    }

    @Override
    public void onDialogPickCompleted(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    avatarImage.setImageURI(selectedImageUri);
                }
            }
            if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                avatarImage.setImageBitmap(imageBitmap);
            }
            updateProfilePic();
        } else {
            Toast.makeText(getActivity(), "error picking photo", Toast.LENGTH_SHORT).show();
        }
    }
}