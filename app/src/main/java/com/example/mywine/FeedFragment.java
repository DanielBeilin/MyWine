package com.example.mywine;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.picasso.Picasso;

import com.example.mywine.model.Post;

import org.w3c.dom.Text;

public class FeedFragment extends Fragment {

    private FeedViewModel mViewModel;

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feed_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FeedViewModel.class);
        // TODO: Use the ViewModel
    }

//    class FeedViewHolder extends RecyclerView.ViewHolder{
//        ImageView postImv;
//        TextView contentTv;
//        TextView likeCountTv;
//        TextView autorTv;
//
//        public FeedViewHolder(@NonNull View itemView, AdapterView.OnItemClickListener listener) {
//            super(itemView);
//            postImv = itemView.findViewById(R.id.listrow_post_imv);
//            contentTv = itemView.findViewById(R.id.listrow_content_tv);
//            likeCountTv = itemView.findViewById(R.id.listrow_likecount_tv);
//            autorTv = itemView.findViewById(R.id.listrow_author_tv);
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int pos = getAdapterPosition();
//                    listener.onItemClick(v,pos);
//                }
//            });
//        }
//
//        void bind(Post post){
//            contentTv.setText(post.getContent());
//            autorTv.setText(post.getAuthor());
//            cb.setChecked(post.isFlag());
//            postImv.setImageResource(R.drawable.postImg);
//            if (post.getPhotoUrl() != null) {
//                Picasso.get()
//                        .load(student.getAvatarUrl())
//                        .into(avatarImv);
//            }
//        }
//
//    }
//
//    class FeedAdaoter extends RecyclerView.Adapter{
//
//    }
}