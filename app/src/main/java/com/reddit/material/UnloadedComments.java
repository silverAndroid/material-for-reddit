package com.reddit.material;

import org.json.JSONArray;

/**
 * Created by silver_android on 10/01/16.
 */
public class UnloadedComments extends Thing {

    private final int count;
    private final String parentID;
    private final JSONArray children;
    private int depth;

    public UnloadedComments(String id, int count, String parentID, JSONArray children) {
        super(id);
        this.count = count;
        this.parentID = parentID;
        this.children = children;
    }

    public int getCount() {
        return count;
    }

    public String getParentID() {
        return parentID;
    }

    public JSONArray getChildren() {
        return children;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
