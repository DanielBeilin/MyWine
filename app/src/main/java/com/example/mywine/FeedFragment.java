package com.example.mywine;

import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
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
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class FeedFragment extends Fragment {
    PostListRvViewModel PostViewModel;
    FeedAdapter feedAdapter;
    SwipeRefreshLayout swipeRefresh;
    FirebaseUser user;
    ProgressDialog pd;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        PostViewModel = new ViewModelProvider(this).get(PostListRvViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        user = UserModelStorageFunctions.instance.getLoggedInUser();
        pd = new ProgressDialog(getActivity());

        View view = inflater.inflate(R.layout.feed_fragment,container,false);

        swipeRefresh = view.findViewById(R.id.feedFragment_swiperefresh);
        swipeRefresh.setOnRefreshListener(PostModelStorageFunctions.instance::refreshPostList);

        RecyclerView list = view.findViewById(R.id.postlist_rv);
        list.setHasFixedSize(true);

        list.setLayoutManager(new LinearLayoutManager(getContext()));

        feedAdapter = new FeedAdapter();
        list.setAdapter(feedAdapter);

        feedAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v,int position) {
                Log.d("TAG",PostViewModel.getData().getValue().toString());
                String stId = PostViewModel.getData().getValue().get(position).getUid();
                Log.d("TAG",String.format("%s",stId));

                //Navigation.findNavController(v).navigate(StudentListRvFragmentDirections.actionStudentListRvFragmentToStudentDetailsFragment(stId));

            }
        });

        setHasOptionsMenu(true);
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

    private void showEditPostDialog(Post post) {
        String options[] = {"edit body", "edit photo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // edit name
                    pd.setMessage("updating name");
                    showFieldUpdateDialog(post);
                } else if (which == 1) {
                    // edit profile photo
                    pd.setMessage("updating profile picture");
                    //showDialog(getView());
                }
            }
        });
        builder.create().show();
    }


    private void showFieldUpdateDialog(Post post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update Content");

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);
        EditText editText = new EditText(getActivity());
        editText.setText(post.getContent());
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        // update
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)) {
                    pd.show();
                    post.setContent(value);
                    PostModelStorageFunctions.instance.updatePost(post, () -> {
                        pd.dismiss();
                        //navController.navigate(R.id.profileFragment);
                    });
                } else {
                    Toast.makeText(getActivity(), "Please enter Content" , Toast.LENGTH_SHORT).show();
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


    private void refresh() { feedAdapter.notifyDataSetChanged(); }

    class FeedViewHolder extends RecyclerView.ViewHolder{
        ImageView postImv;
        ImageView userImv;
        TextView contentTv;
        TextView likeCountTv;
        TextView authorTv;
        TextView commentCountTv;
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
            commentCountTv = itemView.findViewById(R.id.post_comments_count_tv);
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