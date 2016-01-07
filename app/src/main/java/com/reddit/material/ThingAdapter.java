package com.reddit.material;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Rushil Perera on 1/1/2016.
 */
public class ThingAdapter extends RecyclerView.Adapter {

    public ArrayList<Thing> things;
    private Activity activity;

    public ThingAdapter(Activity activity) {
        this.activity = activity;
        things = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == 0) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_subreddit, parent, false);
            return new SubredditViewHolder(v, activity);
        }
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_thread_subreddit, parent, false);
        return new PostViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holderParent, int position) {
        if (holderParent instanceof SubredditViewHolder) {
            SubredditViewHolder holder = (SubredditViewHolder) holderParent;
            Subreddit subreddit = (Subreddit) things.get(position);
            holder.title.setText(subreddit.getTitle());
            holder.subscribe.setVisibility(Authentication.getInstance().isLoggedIn() ? View.VISIBLE : View.GONE);
            holder.subscribe.setText(subreddit.isSubscriber() ? "Unsubscribe" : "Subscribe");
            holder.rSubreddit.setText(String.format("r/%s", subreddit.getName()));
            holder.subscribersCount.setText(String.format("%d subscribers", subreddit.getSubscribers()));
            holder.description.setHTMLText(subreddit.getPublicDescription());
        } else if (holderParent instanceof PostViewHolder) {
            PostViewHolder holder = (PostViewHolder) holderParent;
            Post post = (Post) things.get(position);
            holder.init(post, true, true, true);
        }
    }

    @Override
    public int getItemCount() {
        return things.size();
    }

    @Override
    public int getItemViewType(int position) {
        String id = things.get(position).getID();
        if (id.startsWith("t5")) {
            return 0;
        }
        return 1;
    }

    public void addItems(ArrayList<Thing> thingArrayList) {
        int initialSize = things.size();
        things.addAll(thingArrayList);
        Collections.sort(things);
        notifyItemRangeInserted(initialSize, thingArrayList.size());
    }

    public void clear() {
        int initialSize = things.size();
        things.clear();
        notifyItemRangeRemoved(0, initialSize);
    }
}
