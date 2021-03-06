package com.reddit.material;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rushil Perera on 11/8/2015.
 */
public class CommentAdapter extends RecyclerView.Adapter {

    private static final String TAG = "CommentAdapter";
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
        } else if (viewType == 0) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false);
            return new CommentViewHolder(v, activity);
        }
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_unloaded_comments, parent, false);
        return new UnloadedCommentViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderParent, int position) {
        if (holderParent instanceof PostViewHolder) {
            final PostViewHolder holder = (PostViewHolder) holderParent;
            holder.init(post);
        } else if (holderParent instanceof CommentViewHolder && comments.size() > 0) {
            final CommentViewHolder holder = (CommentViewHolder) holderParent;
            holder.init((NormalComment) comments.get(post != null ? position - 1 : position));
        } else if (holderParent instanceof UnloadedCommentViewHolder) {
            final UnloadedCommentViewHolder holder = (UnloadedCommentViewHolder) holderParent;
            holder.init((UnloadedComments) comments.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        return post == null ? 0 : comments.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 1 : comments.get(position - 1) instanceof NormalComment ? 0 : 2;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
        notifyItemInserted(0);
    }

    public void addComment(Comment comment) {
        if (comment.getParent() == null) {
            comments.add(comment);
            notifyItemInserted(comments.size());
            return;
        }
        for (int i = 0; i < comments.size(); i++) {
            Comment parentComment = comments.get(i);
            if (parentComment.equals(comment.getParent())) {
                comments.add(i + 1, comment);
                notifyItemInserted(i + 2);
                break;
            }
        }
    }

    public void addComments(JSONArray commentsJSON) {
        for (int i = 0; i < commentsJSON.length(); i++) {
            JSONObject commentJSON;
            try {
                String kind = commentsJSON.getJSONObject(i).getString("kind");
                commentJSON = commentsJSON.getJSONObject(i).getJSONObject("data");
                if (kind.equals("t1")) {
                    NormalComment comment = Util.generateNormalComment(commentJSON);
                    if (comment != null) {
                        comments.add(comment);
                        if (comment.getReplies() != null)
                            addComments(comment);
                    } else
                        Log.d(TAG, "onSuccess: " + commentJSON);
                } else if (kind.equals("more")) {
                    UnloadedComments comments = Util.generateUnloadedComments(commentJSON);
                    if (comments != null) {
                        this.comments.add(comments);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }

    public void addComments(NormalComment parent) {
        JSONArray commentsJSON = parent.getReplies();
        for (int i = 0; i < commentsJSON.length(); i++) {
            try {
                String kind = commentsJSON.getJSONObject(i).getString("kind");
                JSONObject commentJSON = commentsJSON.getJSONObject(i).getJSONObject("data");
                if (kind.equals("t1")) {
                    NormalComment comment = Util.generateNormalComment(commentJSON);
                    if (comment != null) {
                        comment.setParent(parent);
                        this.comments.add(comment);
                        if (comment.getReplies() != null)
                            addComments(comment);
                    }
                } else if (kind.equals("more")) {
                    UnloadedComments comments = Util.generateUnloadedComments(commentJSON);
                    if (comments != null) {
                        comments.setParent(parent);
                        this.comments.add(comments);
                    }
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
