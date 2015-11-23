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
public class SubredditAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private final ArrayList<Post> posts;
    private final Activity activity;

    public SubredditAdapter(Activity activity) {
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
        String imageURL;
        Uri nsfwPath = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(R.drawable.nsfw_reddit_icon))
                .build();
        if (post.getPreviewImageURL() == null) {
            if (post.getURL() == null) {
                holder.image.setVisibility(View.GONE);
                holder.loading.setVisibility(View.GONE);
            } else {
                holder.setURL(imageURL = post.getURL());
                if (post.isOver18()) {
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
                ConnectionSingleton.getInstance().loadImage(post.getPreviewImageURL(), holder.image, holder.loading);
            }
        }

        holder.title.setText(post.getTitle());
        holder.flair.setVisibility(post.getLinkFlairText() == null ? View.GONE : post.getLinkFlairText().equals("") ?
                View.GONE : View.VISIBLE);
        holder.lineOneInfo.setText(Html.fromHtml("<b><font size=\"20\">" + post.getScore() + "</font></b> pts " +
                "<b>" + post.getNumComments() + "</b> comments by <b>" + post.getAuthor() + "</b>"));
        holder.source.setText(post.getDomain());
        holder.timeSubredditInfo.setText(Html.fromHtml("<b>" + DateUtils.getRelativeTimeSpanString(post.getCreatedUTC
                () * 1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS) + "</b> to r/<b>" + post
                .getSubreddit() + "</b>"));
        holder.gilded.setVisibility(post.getGilded() == 0 ? View.GONE : View.VISIBLE);
        holder.gilded.setText(String.format("%d", post.getGilded()));
        holder.flair.setText(post.getLinkFlairText());
        if (!post.getSelfText().equals("")) {
            holder.selfText.setMaxLines(3);
            holder.selfText.setEllipsize(TextUtils.TruncateAt.END);
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
                if (!Authentication.getInstance().isLoggedIn())
                    Toast.makeText(activity, "You must be logged in to vote!", Toast.LENGTH_SHORT).show();
                else {
                    upvote.setSelected(!upvote.isSelected());
                    downvote.setSelected(false);
                    post.vote(upvote.isSelected() ? 1 : 0);
                }
            }
        });

        downvote.setSelected(post.getVote() == -1);
        downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Authentication.getInstance().isLoggedIn())
                    Toast.makeText(activity, "You must be logged in to vote!", Toast.LENGTH_SHORT).show();
                else {
                    downvote.setSelected(!downvote.isSelected());
                    upvote.setSelected(false);
                    post.vote(downvote.isSelected() ? -1 : 0);
                }
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

        holder.reply.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, CommentsActivity.class);
                intent.putExtra("post", post);
                activity.startActivity(intent);
            }
        });
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
