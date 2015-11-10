package com.reddit.material;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;

/**
 * Created by Rushil Perera on 10/28/2015.
 */
public class PostViewHolder extends RecyclerView.ViewHolder {

    final TextView title;
    final ImageView image;
    final TextView lineOneInfo;
    final TextView source;
    final TextView timeSubredditInfo;
    final TextView gilded;
    final TextView flair;
    final TextView nsfwTag;
    final ProgressBar loading;
    private final Context context;
    final CardView card;

    public PostViewHolder(final View itemView, final Context context) {
        super(itemView);
        this.context = context;
        title = (TextView) itemView.findViewById(R.id.title);
        image = (ImageView) itemView.findViewById(R.id.image);
        lineOneInfo = (TextView) itemView.findViewById(R.id.lineOneInfo);
        source = (TextView) itemView.findViewById(R.id.source);
        timeSubredditInfo = (TextView) itemView.findViewById(R.id.timeSubredditInfo);
        gilded = (TextView) itemView.findViewById(R.id.gold_count);
        flair = (TextView) itemView.findViewById(R.id.flair);
        nsfwTag = (TextView) itemView.findViewById(R.id.nsfw_tag);
        loading = (ProgressBar) itemView.findViewById(R.id.loading);
        card = (CardView) itemView.findViewById(R.id.card);
    }

    public void setImageURL(final String url) {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConstantMap.getInstance().isGIF(url)) {
                    Intent intent = new Intent(context, VideoActivity.class);
                    intent.putExtra("url", url);
                    context.startActivity(intent);
                } else if (ConstantMap.getInstance().isImage(url)) {
                    Intent intent = new Intent(context, ImageActivity.class);
                    intent.putExtra("url", url);
                    context.startActivity(intent);
                }
            }
        });
    }
}
