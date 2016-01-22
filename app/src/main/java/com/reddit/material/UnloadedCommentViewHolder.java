package com.reddit.material;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by silver_android on 10/01/16.
 */
public class UnloadedCommentViewHolder extends RecyclerView.ViewHolder {

    private static final ArrayList<Integer> sideColors;

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

    final TextView loadComments;
    final ProgressBar loading;
    final View sideColor;
    private final Activity activity;
    private final LinearLayout view;

    public UnloadedCommentViewHolder(View itemView, Activity activity) {
        super(itemView);
        this.activity = activity;
        loadComments = (TextView) itemView.findViewById(R.id.load_comments);
        loading = (ProgressBar) itemView.findViewById(R.id.loading);
        sideColor = itemView.findViewById(R.id.side_color);
        view = (LinearLayout) itemView.findViewById(R.id.view);
    }

    public void init(final UnloadedComments comments) {
        if (comments.getCount() > 0)
            loadComments.setText(String.format("Load %d comments...", comments.getCount()));
        else
            loadComments.setText(R.string.continue_thread);

        if (comments.getDepth() > 0) {
            sideColor.setVisibility(View.VISIBLE);
            sideColor.setBackgroundColor(sideColors.get(comments.getDepth() < 10 ? comments.getDepth() : 9));
            DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
            view.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f * comments.getDepth(),
                    metrics), 0, 0, 0);
        } else {
            sideColor.setVisibility(View.GONE);
            view.setPadding(0, 0, 0, 0);
        }

        loading.setVisibility(View.GONE);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
            }
        });
    }
}
