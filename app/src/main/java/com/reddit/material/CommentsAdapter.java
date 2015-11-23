package com.reddit.material;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
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
public class CommentsAdapter extends RecyclerView.Adapter {

    private final static ArrayList<Integer> sideColors;

    static {
        sideColors = new ArrayList<>();
        sideColors.add(Color.TRANSPARENT);
        sideColors.add(Color.rgb(39, 164, 221));
        sideColors.add(Color.rgb(39, 169, 101));
        sideColors.add(Color.rgb(157, 213, 192));
        sideColors.add(Color.rgb(243, 115, 104));
        sideColors.add(Color.rgb(243, 156, 195));
        sideColors.add(Color.rgb(248, 153, 71));
        sideColors.add(Color.rgb(241, 100, 108));
        sideColors.add(Color.rgb(87, 129, 192));
        sideColors.add(Color.rgb(250, 193, 116));
        sideColors.add(Color.rgb(92, 73, 112));
    }

    private final Activity activity;
    private Post post;
    private ArrayList<Comment> comments;

    public CommentsAdapter(Post post, Activity activity) {
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
            initPostView(holder);
        } else if (holderParent instanceof CommentViewHolder) {
            final CommentViewHolder holder = (CommentViewHolder) holderParent;
            initCommentView(holder, position);
        }
    }

    private void initPostView(final PostViewHolder holder) {
        String imageURL;
        if (post.getPreviewImageURL() == null) {
            if (post.getURL() == null) {
                holder.image.setVisibility(View.GONE);
                holder.loading.setVisibility(View.GONE);
            } else {
                holder.setURL(imageURL = post.getURL());
                if (post.isOver18()) {
                    Uri nsfwPath = new Uri.Builder()
                            .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                            .path(String.valueOf(R.drawable.nsfw_reddit_icon))
                            .build();
                    holder.image.setImageURI(nsfwPath);
                    holder.loading.setVisibility(View.GONE);
                    holder.nsfwTag.setVisibility(View.VISIBLE);
                } else {
                    if (ConstantMap.getInstance().isImage(post.getURL())) {
                        holder.image.setVisibility(View.VISIBLE);
                        holder.loading.setVisibility(View.VISIBLE);
                        ConnectionSingleton.getInstance().loadImage(imageURL, holder.image, holder.loading);
                    } else {
                        holder.image.setVisibility(View.GONE);
                        holder.loading.setVisibility(View.GONE);
                    }
                    holder.nsfwTag.setVisibility(View.GONE);
                }
            }
        } else {
            holder.setURL(post.getURL());
            if (post.isOver18()) {
                Uri nsfwPath = new Uri.Builder()
                        .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                        .path(String.valueOf(R.drawable.nsfw_reddit_icon))
                        .build();
                holder.image.setImageURI(nsfwPath);
                holder.loading.setVisibility(View.GONE);
                holder.nsfwTag.setVisibility(View.VISIBLE);
            } else {
                if (ConstantMap.getInstance().isImage(post.getPreviewImageURL())) {
                    holder.image.setVisibility(View.VISIBLE);
                    holder.loading.setVisibility(View.VISIBLE);
                } else {
                    holder.image.setVisibility(View.GONE);
                    holder.loading.setVisibility(View.GONE);
                }
                holder.nsfwTag.setVisibility(View.GONE);
                ConnectionSingleton.getInstance().loadImage(post.getPreviewImageURL(), holder.image, holder
                        .loading);
            }
        }

        holder.title.setText(post.getTitle());
        holder.flair.setVisibility(post.getLinkFlairText() == null ? View.GONE : post.getLinkFlairText().equals
                ("") ? View.GONE : View.VISIBLE);
        holder.lineOneInfo.setText(Html.fromHtml("<b><font size=\"20\">" + post.getScore() + "</font></b> pts " +
                "<b>" + post.getNumComments() + "</b> comments by <b>" + post.getAuthor() + "</b>"));
        holder.source.setText(post.getDomain());
        holder.timeSubredditInfo.setText(Html.fromHtml("<b>" + DateUtils.getRelativeTimeSpanString(post
                .getCreatedUTC() * 1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS) + "</b> to " +
                "r/<b>" + post.getSubreddit() + "</b>"));
        holder.gilded.setVisibility(post.getGilded() == 0 ? View.GONE : View.VISIBLE);
        holder.gilded.setText(String.format("%d", post.getGilded()));
        holder.flair.setText(post.getLinkFlairText());

        if (!post.getSelfText().equals("")) {
            holder.selfText.setVisibility(View.VISIBLE);
            holder.selfText.setText(post.getSelfText());
        } else
            holder.selfText.setVisibility(View.GONE);

        final ImageButton upvote = holder.upvote;
        final ImageButton downvote = holder.downvote;

        upvote.setSelected(post.getVote() == 1);
        upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Authentication.getInstance().isLoggedIn()) {
                    Toast.makeText(activity, "You must be logged in to vote!", Toast.LENGTH_SHORT).show();
                    return;
                }

                upvote.setSelected(!upvote.isSelected());
                downvote.setSelected(false);
                post.vote(upvote.isSelected() ? 1 : 0);
            }
        });

        downvote.setSelected(post.getVote() == -1);
        downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Authentication.getInstance().isLoggedIn()) {
                    Toast.makeText(activity, "You must be logged in to vote!", Toast.LENGTH_SHORT).show();
                    return;
                }

                downvote.setSelected(!downvote.isSelected());
                upvote.setSelected(false);
                post.vote(downvote.isSelected() ? -1 : 0);
            }
        });

        if (holder.gilded.getVisibility() == View.VISIBLE) {
            holder.card.setCardBackgroundColor(Color.rgb(253, 221, 98));
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, activity.getResources()
                    .getDisplayMetrics());
            holder.card.setContentPadding(padding, padding, padding, padding);
        } else if (post.isStickied()) {
            holder.card.setCardBackgroundColor(Color.rgb(164, 208, 95));
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, activity.getResources()
                    .getDisplayMetrics());
            holder.card.setContentPadding(padding, padding, padding, padding);
        } else {
            holder.card.setCardBackgroundColor(Color.rgb(245, 243, 242));
            holder.card.setContentPadding(0, 0, 0, 0);
        }

        holder.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionSingleton.getInstance().comment(holder.editMessage.getText().toString(), post.getID(),
                        holder.editMessage);
            }
        });
    }

    private void initCommentView(final CommentViewHolder holder, int position) {
        final Comment comment = comments.get(position - 1);
        holder.username.setText(comment.getAuthor());
        holder.text.setText(comment.getBody());
        holder.numPoints.setText(String.format("%d pts", comment.getScore()));
        holder.gilded.setVisibility(comment.getGilded() == 0 ? View.GONE : View.VISIBLE);
        holder.gilded.setText(String.format("%d", comment.getGilded()));
        holder.time.setText(DateUtils.getRelativeTimeSpanString(comment.getCreatedUTC() * 1000,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
        if (comment.getDepth() > 0) {
            holder.sideColor.setVisibility(View.VISIBLE);
            holder.sideColor.setBackgroundColor(sideColors.get(comment.getDepth() < 10 ? comment.getDepth() : 9));
            DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
            holder.view.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f * comment
                    .getDepth(), metrics), 0, 0, 0);
        } else {
            holder.sideColor.setVisibility(View.GONE);
            holder.view.setPadding(0, 0, 0, 0);
        }

        final ImageButton upvote = holder.upvote;
        final ImageButton downvote = holder.downvote;

        upvote.setSelected(comment.getVote() == 1);
        upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Authentication.getInstance().isLoggedIn()) {
                    Toast.makeText(activity, "You must be logged in to vote!", Toast.LENGTH_SHORT).show();
                    return;
                }

                upvote.setSelected(!upvote.isSelected());
                downvote.setSelected(false);
                comment.vote(upvote.isSelected() ? 1 : 0);
                holder.numPoints.setTextColor(upvote.isSelected() ? Color.parseColor("#FF4081") : Color.BLACK);
            }
        });

        downvote.setSelected(comment.getVote() == -1);
        holder.numPoints.setTextColor(upvote.isSelected() ? Color.parseColor("#FF4081") : downvote.isSelected() ? Color
                .parseColor("#880E4F") : Color.BLACK);
        downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Authentication.getInstance().isLoggedIn()) {
                    Toast.makeText(activity, "You must be logged in to vote!", Toast.LENGTH_SHORT).show();
                    return;
                }

                downvote.setSelected(!downvote.isSelected());
                upvote.setSelected(false);
                comment.vote(downvote.isSelected() ? -1 : 0);
                holder.numPoints.setTextColor(downvote.isSelected() ? Color.parseColor("#880E4F") : Color.BLACK);
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.optionsView.setVisibility(holder.optionsView.getVisibility() == View.GONE ? View.VISIBLE :
                        View.GONE);
                holder.sendLayout.setVisibility(View.GONE);
            }
        });

        holder.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionSingleton.getInstance().comment(holder.editMessage.getText().toString(), comment.getID(),
                        holder.editMessage);
            }
        });
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
                    Comment comment = new Comment(commentJSON.getString("subreddit_id"), commentJSON
                            .getString("link_id"), commentJSON.getBoolean("saved"), commentJSON.getString
                            ("name"), commentJSON.getInt("gilded"), commentJSON.getBoolean("archived"),
                            commentJSON.getString("author"), commentJSON.getInt("score"), commentJSON
                            .getString("body"), commentJSON.optDouble("edited", -1.0), commentJSON.getString
                            ("body_html"), commentJSON.getBoolean("score_hidden"), commentJSON.getLong
                            ("created_utc"), commentJSON.isNull("author_flair_text") ? "" : commentJSON
                            .getString("author_flair_text"), commentJSON.isNull("likes") ? 0 : commentJSON
                            .getBoolean("likes") ? 1 : -1);
                    comment.setReplies(commentJSON.optJSONObject("replies") == null ? null : commentJSON
                            .getJSONObject("replies").getJSONObject("data").getJSONArray("children"));
                    comment.setUserReports(commentJSON.getJSONArray("user_reports"));
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
