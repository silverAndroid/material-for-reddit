package com.reddit.material;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.reddit.material.custom.HTMLMarkupTextView;

/**
 * Created by Rushil Perera on 1/2/2016.
 */
public class SubredditViewHolder extends RecyclerView.ViewHolder {

    final TextView title;
    final Button subscribe;
    final TextView rSubreddit;
    final TextView subscribersCount;
    final HTMLMarkupTextView description;

    public SubredditViewHolder(View itemView, final Activity activity) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        subscribe = (Button) itemView.findViewById(R.id.btn_subscribe);
        rSubreddit = (TextView) itemView.findViewById(R.id.rSubreddit);
        subscribersCount = (TextView) itemView.findViewById(R.id.subscriberCount);
        description = (HTMLMarkupTextView) itemView.findViewById(R.id.subreddit_description);
        description.setParent(itemView);
        description.setActivity(activity);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MainActivity.class);
                intent.putExtra("subreddit", rSubreddit.getText().toString().split("/")[1]);
                activity.startActivity(intent);
            }
        });
    }
}
