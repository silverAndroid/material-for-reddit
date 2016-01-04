package com.reddit.material;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Rushil Perera on 1/2/2016.
 */
public class SubredditViewHolder extends RecyclerView.ViewHolder {

    final TextView title;
    final Button subscribe;
    final TextView rSubreddit;
    final TextView subscribersCount;
    final TextView description;

    public SubredditViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        subscribe = (Button) itemView.findViewById(R.id.btn_subscribe);
        rSubreddit = (TextView) itemView.findViewById(R.id.rSubreddit);
        subscribersCount = (TextView) itemView.findViewById(R.id.subscriberCount);
        description = (TextView) itemView.findViewById(R.id.subreddit_description);
    }
}
