package com.reddit.material;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Rushil Perera on 11/8/2015.
 */
public class CommentViewHolder extends RecyclerView.ViewHolder {

    final TextView username;
    final TextView numPoints;
    final TextView time;
    final TextView text;

    public CommentViewHolder(View itemView) {
        super(itemView);
        username = (TextView) itemView.findViewById(R.id.username);
        numPoints = (TextView) itemView.findViewById(R.id.num_points);
        time = (TextView) itemView.findViewById(R.id.time);
        text = (TextView) itemView.findViewById(R.id.text);
    }
}
