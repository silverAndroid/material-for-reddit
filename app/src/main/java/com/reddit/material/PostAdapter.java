package com.reddit.material;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        holder.init(post, true, true, true);
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
