package com.example.mywine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mywine.model.PicturePickDialog;
import com.example.mywine.model.Post.Post;
import com.example.mywine.model.PostModelStorageFunctions;
import com.example.mywine.model.User.User;
import com.example.mywine.model.UserModelStorageFunctions;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends PicturePickDialog {

    ImageView avatarImage;
    TextView nameTextView, emailTextView;
    MaterialButton editButton;
    ProgressDialog pd;
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
        onAvatarImageClick();
        onEditButtonClick();
    }

    private void onEditButtonClick() {
        editButton.setOnClickListener(view -> {
            showFieldUpdateDialog("name");
        });
    }

    private void onAvatarImageClick() {
        avatarImage.setOnClickListener(this::showDialog);
    }

    private void init(View view) {
        avatarImage = view.findViewById(R.id.profilePhoto);
        nameTextView = view.findViewById(R.id.name);
        emailTextView = view.findViewById(R.id.email);
        editButton = view.findViewById(R.id.editButton);
        swipeRefresh = view.findViewById(R.id.profile_fragment_swipe_refresh);
        navController = NavHostFragment.findNavController(this);
        pd = new ProgressDialog(getActivity());
        userId = ProfileFragmentArgs.fromBundle(getArguments()).getUserId();
        setUser(userId);

        swipeRefresh.setOnRefreshListener(PostModelStorageFunctions.instance::refreshPostList);

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
            }
        });

        profileViewModel.getData().observe(getViewLifecycleOwner(), list1 -> refresh());
        swipeRefresh.setRefreshing(PostModelStorageFunctions.instance.getPostListLoadingState().getValue() == PostModelStorageFunctions.PostListLoadingState.loading);
        PostModelStorageFunctions.instance.getPostListLoadingState().observe(getViewLifecycleOwner(),postListLoadingState  -> {
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
        avatarImage.setImageResource(R.drawable.ic_baseline_person_24);
        String imageUrl = currentUser.getProfilePhoto();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .into(avatarImage);
        }
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
                        navController.navigateUp();
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
                navController.navigateUp();
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
        Button likeBtn;
        Button deleteBtn;
        Button editBtn;
        RatingBar ratingBar;

        public ProfileViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            postImv = itemView.findViewById(R.id.post_row_post_imgv);
            contentTv = itemView.findViewById(R.id.post_row_content_tv);
            likeCountTv = itemView.findViewById(R.id.post_row_like_count_tv);
            likeBtn = itemView.findViewById(R.id.post_row_like_btn);
            deleteBtn = itemView.findViewById(R.id.post_delete_btn);
            editBtn = itemView.findViewById(R.id.post_edit_btn);
            deleteBtn.setVisibility(View.VISIBLE);
            editBtn.setVisibility(View.VISIBLE);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }


        void bind(Post post) {
            if (!userId.equals(post.getUserId())) {
                deleteBtn.setVisibility(View.GONE);
                editBtn.setVisibility(View.GONE);
            }

            contentTv.setText(post.getContent());
            Integer likeNum = post.getLikeCount();
            likeCountTv.setText(likeNum.toString());
            postImv.setImageResource(R.drawable.avatar);
            ratingBar.setRating(post.getRating());
            if (post.getPhotoUrl() != null) {
                Picasso.get()
                        .load(post.getPhotoUrl())
                        .into(postImv);
            }

            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    post.addLike(userId);
                    likeCountTv.setText(String.valueOf(post.getLikeCount()));
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostModelStorageFunctions.instance.deletePost(post, () -> {
                    });
                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String postId = post.getUid();
                    navController.navigate(ProfileFragmentDirections.actionProfileFragmentToEditPostFragment(postId));
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