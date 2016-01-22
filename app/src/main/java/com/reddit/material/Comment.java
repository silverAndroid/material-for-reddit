package com.reddit.material;

import org.json.JSONArray;

/**
 * Created by Rushil Perera on 11/8/2015.
 */
public class Comment extends Thing {

    private Comment parent;
    private JSONArray replies;

    public Comment(String id) {
        super(id);
    }

    public JSONArray getReplies() {
        return replies;
    }

    public void setReplies(JSONArray replies) {
        this.replies = replies;
    }

    public Comment getParent() {
        return parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public int getDepth() {
        return parent == null ? 0 : parent.getDepth() + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Comment) {
            Comment comment = (Comment) o;
            if (comment.getID().equals(getID()))
                return true;
        }
        return false;
    }
}
