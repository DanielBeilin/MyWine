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

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mywine.model.PicturePickDialog;
import com.example.mywine.model.Post.Post;
import com.example.mywine.model.PostModelStorageFunctions;
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

public class ProfileFragment extends PicturePickDialog {

    ImageView avatarImage;
    TextView nameTextView, emailTextView;
    MaterialButton editButton;
    ProgressDialog pd;
    Bitmap imageBitmap;
    String userId;
    User currentUser;
    NavController navController;

    SwipeRefreshLayout swipeRefresh;
    ProfileListRvViewModel profileViewModel;
    ProfileAdapter profileAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        profileViewModel = new ViewModelProvider(this).get(ProfileListRvViewModel.class);
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
        onAvatarImageClick();
    }

    private void onAvatarImageClick() {
        avatarImage.setOnClickListener(this::showDialog);
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

        RecyclerView list = view.findViewById(R.id.postlist_rv);
        list.setHasFixedSize(true);

        list.setLayoutManager(new LinearLayoutManager(getContext()));
        profileAdapter = new ProfileAdapter();
        list.setAdapter(profileAdapter);
        profileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v,int position) {
                Log.d("TAG",profileViewModel.getData().getValue().toString());
                String stId = profileViewModel.getData().getValue().get(position).getUid();
                Log.d("TAG",String.format("%s",stId));

                //Navigation.findNavController(v).navigate(StudentListRvFragmentDirections.actionStudentListRvFragmentToStudentDetailsFragment(stId));
            }
        });

        profileViewModel.getData().observe(getViewLifecycleOwner(), list1 -> refresh());
        swipeRefresh.setRefreshing(PostModelStorageFunctions.instance.getUserPostListLoadingState().getValue() == PostModelStorageFunctions.PostListLoadingState.loading);
        PostModelStorageFunctions.instance.getUserPostListLoadingState().observe(getViewLifecycleOwner(),postListLoadingState  -> {
            if (postListLoadingState == PostModelStorageFunctions.PostListLoadingState.loading){
                swipeRefresh.setRefreshing(true);
            }else{
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void initUserDetails() {
        nameTextView.setText(currentUser.getFullName());
        emailTextView.setText(currentUser.getEmail());
        avatarImage.setImageResource(R.drawable.ic_profile);
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
                    UserModelStorageFunctions.instance.updateUser(currentUser, () -> {
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
        Bitmap profileImage = ((BitmapDrawable) avatarImage.getDrawable()).getBitmap();
        UserModelStorageFunctions.instance.uploadUserPhoto(profileImage, currentUser.getUid() + ".jpg", (url) -> {
            currentUser.setProfilePhoto(url);
            UserModelStorageFunctions.instance.updateUser(currentUser, () -> {
                pd.dismiss();
                navController.navigate(R.id.profileFragment);
            });
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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


    private void refresh() {
        profileAdapter.notifyDataSetChanged();
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView postImv;
        TextView contentTv;
        TextView likeCountTv;
        TextView commentCountTv;
        Button likeBtn;

        public ProfileViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            postImv = itemView.findViewById(R.id.post_row_post_imgv);
            contentTv = itemView.findViewById(R.id.post_row_content_tv);
            likeCountTv = itemView.findViewById(R.id.post_row_like_count_tv);
            commentCountTv = itemView.findViewById(R.id.post_comments_count_tv);
            likeBtn = itemView.findViewById(R.id.post_row_like_btn);

        }


        void bind(Post post) {
            contentTv.setText(post.getContent());
            Integer likeNum = post.getLikeCount();
            likeCountTv.setText(likeNum.toString());
            commentCountTv.setText(String.valueOf(post.getCommentList().size()));
            postImv.setImageResource(R.drawable.avatar);
            if (post.getPhotoUrl() != null) {
                Picasso.get()
                        .load(post.getPhotoUrl())
                        .into(postImv);
            }

            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    post.addLike(currentUser.getUid());
                    likeCountTv.setText(String.valueOf(post.getLikeCount()));
                }
            });
        }
    }

    interface OnItemClickListener{
        void onItemClick(View v,int position);
    }

    class ProfileAdapter extends RecyclerView.Adapter<ProfileViewHolder> {
        OnItemClickListener listener;
        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.post_list_profile_row,parent,false);
            return new ProfileViewHolder(view,listener);
        }

        @Override
        public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
            Post post = profileViewModel.getData().getValue().get(position);
            holder.bind(post);
        }

        @Override
        public int getItemCount() {
            if(profileViewModel.getData().getValue() == null) {
                return 0;
            }
            return profileViewModel.getData().getValue().size();
        }

    }
}