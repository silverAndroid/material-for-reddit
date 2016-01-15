package com.reddit.material;

import org.json.JSONArray;

/**
 * Created by silver_android on 10/01/16.
 */
public class UnloadedComments extends Comment {

    private final int count;

    public UnloadedComments(String id, int count, String parentID, JSONArray children) {
        super(id, parentID);
        setReplies(children);
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
