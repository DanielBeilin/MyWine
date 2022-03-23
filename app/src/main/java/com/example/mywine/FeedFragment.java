package com.example.mywine;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mywine.model.Post.Post;
import com.example.mywine.model.PostModelStorageFunctions;
import com.example.mywine.model.User.User;
import com.example.mywine.model.UserModelStorageFunctions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class FeedFragment extends Fragment {
    private PostListRvViewModel PostViewModel;
    private FeedAdapter feedAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private FirebaseUser user;
    private ProgressDialog pd;
    private FloatingActionButton addNewPostFAB;
    private ImageFilterView logout;
    private ShapeableImageView profileImage;
    private NavController navController;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        PostViewModel = new ViewModelProvider(this).get(PostListRvViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_fragment,container,false);

        init(view);
        setListeners();

        PostViewModel.getData().observe(getViewLifecycleOwner(), list1 -> refresh());
        swipeRefresh.setRefreshing(PostModelStorageFunctions.instance.getPostListLoadingState().getValue() == PostModelStorageFunctions.PostListLoadingState.loading);
        PostModelStorageFunctions.instance.getPostListLoadingState().observe(getViewLifecycleOwner(), postListLoadingState -> {
            if (postListLoadingState == PostModelStorageFunctions.PostListLoadingState.loading){
                swipeRefresh.setRefreshing(true);
            }else{
                swipeRefresh.setRefreshing(false);
            }

        });
        return view;
    }

    private void init(View view) {
        user = UserModelStorageFunctions.instance.getLoggedInUser();
        pd = new ProgressDialog(getActivity());
        navController = NavHostFragment.findNavController(this);
        addNewPostFAB = view.findViewById(R.id.add_post_fab);
        profileImage = view.findViewById(R.id.profile_image_icon);
        logout = view.findViewById(R.id.logout);
        setProfileImage();

        swipeRefresh = view.findViewById(R.id.feedFragment_swiperefresh);
        swipeRefresh.setOnRefreshListener(PostModelStorageFunctions.instance::refreshPostList);

        RecyclerView list = view.findViewById(R.id.postlist_rv);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        feedAdapter = new FeedAdapter();
        list.setAdapter(feedAdapter);
    }

    private void setProfileImage() {
        profileImage.setImageResource(R.drawable.ic_baseline_person_24);
        if (user != null && user.getPhotoUrl() != null) {
            Picasso.get()
                    .load(user.getPhotoUrl())
                    .into(profileImage);
        }
    }

    private void setListeners() {
        onFeedAdapterItemClick();
        onAddPostFabClick();
        setOnProfileImageClick();
        setOnLogoutClick();
    }

    private void setOnLogoutClick() {
        logout.setOnClickListener(view -> {
            UserModelStorageFunctions.instance.logout(this::startSignInActivity);
        });
    }

    private void startSignInActivity() {
        if (getActivity() != null) {
            Intent SignInActivityIntent = new Intent(getActivity(), SignInActivity.class);
            startActivity(SignInActivityIntent);
            getActivity().finish();
        }
    }

    private void setOnProfileImageClick() {
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(FeedFragmentDirections.actionFeedFragmentToProfileFragment(user.getUid()));
            }
        });
    }

    private void onFeedAdapterItemClick() {
        feedAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v,int position) {
                Log.d("TAG", PostViewModel.getData().getValue().toString());
                String userId = PostViewModel.getData().getValue().get(position).getUid();
                Log.d("TAG", String.format("%s", userId));
            }
        });
    }

    private void onAddPostFabClick() {
        addNewPostFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(FeedFragmentDirections.actionFeedFragmentToAddNewPostFragment(user.getUid()));
            }
        });
    }

    private void refresh() { feedAdapter.notifyDataSetChanged(); }

    class FeedViewHolder extends RecyclerView.ViewHolder{
        ImageView postImv;
        ImageView userImv;
        TextView contentTv;
        TextView likeCountTv;
        TextView authorTv;
        Button likeBtn;
        Button deleteBtn;
        Button editBtn;

        public FeedViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            postImv = itemView.findViewById(R.id.post_row_post_imgv);
            contentTv = itemView.findViewById(R.id.post_row_content_tv);
            likeCountTv = itemView.findViewById(R.id.post_row_like_count_tv);
            authorTv = itemView.findViewById(R.id.post_row_name_tv);
            userImv = itemView.findViewById(R.id.post_row_user_imgv);
            likeBtn = itemView.findViewById(R.id.post_row_like_btn);
            deleteBtn = itemView.findViewById(R.id.post_delete_btn);
            editBtn = itemView.findViewById(R.id.post_edit_btn);
            deleteBtn.setVisibility(View.VISIBLE);
            editBtn.setVisibility(View.VISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Log.d("TAG",String.format("%s",pos));
                    listener.onItemClick(v,pos);
                }
            });

        }

        void bind(Post post) {
                if (!user.getUid().equals(post.getUserId())) {
                    deleteBtn.setVisibility(View.GONE);
                    editBtn.setVisibility(View.GONE);
                }
                contentTv.setText(post.getContent());
                UserModelStorageFunctions.instance.getUserById(post.getUserId(), new UserModelStorageFunctions.GetUserById() {
                    @Override
                    public void onComplete(User user) {
                        authorTv.setText(user.getFullName());
                        userImv.setImageResource(R.drawable.avatar);
                        if (user.getProfilePhoto() != null) {
                            Picasso.get()
                                    .load(user.getProfilePhoto())
                                    .into(userImv);
                        }
                    }
                });
                Integer likeNum = post.getLikeCount();
                likeCountTv.setText(likeNum.toString());
                postImv.setImageResource(R.drawable.ic_person_black);
                if (post.getPhotoUrl() != null) {
                    Picasso.get()
                            .load(post.getPhotoUrl())
                            .into(postImv);
                }

                likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post.addLike(user.getUid());
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
                        Navigation.findNavController(v).navigate(FeedFragmentDirections.actionFeedFragmentToEditPostFragment(postId));
                    }
                });
            }
    }


    interface OnItemClickListener{
        void onItemClick(View v,int position);
    }

    class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder>  {

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
        public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.post_list_row,parent,false);
            return new FeedViewHolder(view,listener);
        }

        @Override
        public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
            Post post = PostViewModel.getData().getValue().get(position);
            holder.bind(post);
        }

        @Override
        public int getItemCount() {
            if(PostViewModel.getData().getValue() == null) {
                return 0;
            }
            return PostViewModel.getData().getValue().size();
        }
    }
}