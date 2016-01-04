package com.reddit.material;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.common.util.UriUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rushil Perera on 11/8/2015.
 */
public class CommentAdapter extends RecyclerView.Adapter {

    private final Activity activity;
    private Post post;
    private ArrayList<Comment> comments;

    public CommentAdapter(Post post, Activity activity) {
        this.activity = activity;
        comments = new ArrayList<>();
        this.post = post;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_thread_subreddit, parent, false);
            return new PostViewHolder(v, activity);
        }
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false);
        return new CommentViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderParent, int position) {
        if (holderParent instanceof PostViewHolder) {
            final PostViewHolder holder = (PostViewHolder) holderParent;
            holder.init(post);
        } else if (holderParent instanceof CommentViewHolder) {
            final CommentViewHolder holder = (CommentViewHolder) holderParent;
            holder.init(comments.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        return comments.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 1 : 0;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
        notifyItemChanged(0);
    }

    public void addComment(Comment comment) {
        if (post.getID().equals(comment.getParentID())) {
            comment.setDepth(0);
            comments.add(comment);
            notifyItemInserted(comments.size());
            return;
        }
        for (int i = 0; i < comments.size(); i++) {
            Comment parentComment = comments.get(i);
            if (parentComment.getID().equals(comment.getParentID())) {
                comment.setDepth(parentComment.getDepth() + 1);
                comments.add(i + 1, comment);
                notifyItemInserted(i + 2);
            }
        }
    }

    public void addComments(ArrayList<Comment> comments) {
        for (Comment comment : comments) {
            comment.setDepth(0);
            this.comments.add(comment);
            if (comment.getReplies() != null)
                addComments(comment.getReplies(), 1);
        }
        notifyDataSetChanged();
    }

    public void addComments(JSONArray comments, int depth) {
        for (int i = 0; i < comments.length(); i++) {
            try {
                String kind = comments.getJSONObject(i).getString("kind");
                JSONObject commentJSON = comments.getJSONObject(i).getJSONObject("data");
                if (kind.equals("t1")) {
                    Comment comment = Util.generateComment(commentJSON);
                    comment.setDepth(depth);
                    this.comments.add(comment);
                    if (comment.getReplies() != null)
                        addComments(comment.getReplies(), ++depth);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String clearData() {
        notifyItemRangeRemoved(1, comments.size());
        comments.clear();
        return post.getPermalink();
    }
}
