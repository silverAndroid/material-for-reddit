package com.reddit.material;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Rushil Perera on 11/8/2015.
 */
public class CommentsAdapter extends RecyclerView.Adapter {

    private final Context context;
    private Post post;
    private ArrayList<Comment> comments;

    public CommentsAdapter(Post post, Context context) {
        this.context = context;
        comments = new ArrayList<>();
        this.post = post;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_thread_subreddit, parent, false);
            return new PostViewHolder(v, parent.getContext());
        }
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false);
        return new CommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderParent, int position) {
        if (holderParent instanceof PostViewHolder) {
            PostViewHolder holder = (PostViewHolder) holderParent;

            String imageURL;
            if (post.getPreviewImageURL() == null) {
                if (post.getURL() == null) {
                    holder.image.setVisibility(View.GONE);
                    holder.loading.setVisibility(View.GONE);
                } else {
                    holder.setImageURL(imageURL = post.getURL());
                    if (post.isOver18()) {
                        holder.image.setImageResource(R.drawable.nsfw_reddit_icon);
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
                holder.setImageURL(post.getURL());
                if (post.isOver18()) {
                    holder.image.setImageResource(R.drawable.nsfw_reddit_icon);
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

            if (holder.gilded.getVisibility() == View.VISIBLE) {
                holder.card.setCardBackgroundColor(Color.rgb(253, 221, 98));
                int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources()
                        .getDisplayMetrics());
                holder.card.setContentPadding(padding, padding, padding, padding);
            } else if (post.isStickied()) {
                holder.card.setCardBackgroundColor(Color.rgb(164, 208, 95));
                int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources()
                        .getDisplayMetrics());
                holder.card.setContentPadding(padding, padding, padding, padding);
            } else {
                holder.card.setCardBackgroundColor(Color.rgb(245, 243, 242));
                holder.card.setContentPadding(0, 0, 0, 0);
            }
        } else if (holderParent instanceof CommentViewHolder) {
            CommentViewHolder holder = (CommentViewHolder) holderParent;
            holder.username.setText(comments.get(position - 1).getAuthor());
            holder.text.setText(comments.get(position - 1).getBody());
            holder.numPoints.setText(String.format("%d pts", comments.get(position - 1).getScore()));
            holder.time.setText(DateUtils.getRelativeTimeSpanString(comments.get(position - 1).getCreatedUTC() * 1000,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
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
    }

    public void addComments(ArrayList<Comment> comments) {
        int initialSize = this.comments.size();
        this.comments.addAll(comments);
        notifyItemRangeInserted(initialSize, comments.size());
    }
}
