package com.reddit.material;

import org.json.JSONArray;

/**
 * Created by Rushil Perera on 11/8/2015.
 */
public class Comment extends Thing {

    private String parentID;
    private JSONArray replies;
    private int depth;

    public Comment(String id, String parentID) {
        super(id);
        this.parentID = parentID;
    }

    public JSONArray getReplies() {
        return replies;
    }

    public void setReplies(JSONArray replies) {
        this.replies = replies;
    }

    public String getParentID() {
        return parentID;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
