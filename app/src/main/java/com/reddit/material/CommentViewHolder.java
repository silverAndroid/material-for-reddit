package com.reddit.material;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Rushil Perera on 11/8/2015.
 */
public class CommentViewHolder extends RecyclerView.ViewHolder {

    final TextView username;
    final TextView numPoints;
    final TextView time;
    final TextView text;
    final TextView gilded;
    final ImageButton upvote;
    final ImageButton downvote;
    final View sideColor;
    final LinearLayout view;
    final ImageButton reply;
    final Button cancel;
    final Button submit;
    final EditText editMessage;
    final LinearLayout sendLayout;
    final RelativeLayout optionsView;

    public CommentViewHolder(View itemView, final Context context) {
        super(itemView);
        username = (TextView) itemView.findViewById(R.id.username);
        numPoints = (TextView) itemView.findViewById(R.id.num_points);
        time = (TextView) itemView.findViewById(R.id.time);
        text = (TextView) itemView.findViewById(R.id.text);
        gilded = (TextView) itemView.findViewById(R.id.gild_count);
        upvote = (ImageButton) itemView.findViewById(R.id.btn_upvote);
        downvote = (ImageButton) itemView.findViewById(R.id.btn_downvote);
        sideColor = itemView.findViewById(R.id.side_color);
        view = (LinearLayout) itemView.findViewById(R.id.linear_layout);
        optionsView = (RelativeLayout) view.findViewById(R.id.options_view);
        optionsView.setVisibility(View.GONE);

        sendLayout = (LinearLayout) view.findViewById(R.id.include_send);
        sendLayout.setVisibility(View.GONE);
        reply = (ImageButton) view.findViewById(R.id.btn_reply);
        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Authentication.getInstance().isLoggedIn())
                    Toast.makeText(context, "You must be logged in to comment!", Toast.LENGTH_SHORT).show();
                else
                    sendLayout.setVisibility(View.VISIBLE);
            }
        });
        editMessage = (EditText) view.findViewById(R.id.edit_message);
        cancel = (Button) view.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLayout.setVisibility(View.GONE);
                editMessage.getText().clear();
            }
        });
        submit = (Button) view.findViewById(R.id.btn_submit);
    }
}
