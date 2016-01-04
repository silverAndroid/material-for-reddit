package com.reddit.material;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.common.util.UriUtil;

import java.util.ArrayList;

/**
 * Created by Rushil Perera on 10/28/2015.
 */
public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private final ArrayList<Post> posts;
    private final Activity activity;

    public PostAdapter(Activity activity) {
        this.activity = activity;
        posts = new ArrayList<>();
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_thread_subreddit, parent, false);
        return new PostViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {
        final Post post = posts.get(position);
        holder.init(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void addPost(Post post) {
        posts.add(post);
        notifyItemInserted(posts.size());
    }

    public void clearPosts() {
        notifyItemRangeRemoved(0, posts.size());
        posts.clear();
    }

    public void addPosts(ArrayList<Post> posts) {
        int initialSize = this.posts.size();
        this.posts.addAll(posts);
        notifyItemRangeInserted(initialSize, posts.size());
    }
}
