package com.reddit.material;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

public class SearchFragment extends Fragment {

    private CheckBox nsfw;
    private CheckBox selfPost;
    private EditText subreddit;
    private EditText author;
    private EditText url;
    private EditText site;
    private EditText selfText;
    private EditText flair;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_filters, container, false);
        view.setBackgroundColor(getResources().getColor(android.R.color.background_light, null));
        nsfw = (CheckBox) view.findViewById(R.id.nsfw_checkbox);
        selfPost = (CheckBox) view.findViewById(R.id.self_post_checkbox);
        subreddit = (EditText) view.findViewById(R.id.subreddit_edit);
        author = (EditText) view.findViewById(R.id.author_edit);
        url = (EditText) view.findViewById(R.id.url_edit);
        site = (EditText) view.findViewById(R.id.site_edit);
        selfText = (EditText) view.findViewById(R.id.self_text_edit);
        flair = (EditText) view.findViewById(R.id.flair_edit);
        return view;
    }

    public String getFilters() {
        String extraSearchTerms = "";
        String string;
        if (nsfw.isChecked())
            extraSearchTerms += "nsfw:yes ";
        if (selfPost.isChecked())
            extraSearchTerms += "self:yes ";
        if (!(string = subreddit.getText().toString()).isEmpty())
            extraSearchTerms += "subreddit:" + string + " ";
        if (!(string = author.getText().toString()).isEmpty())
            extraSearchTerms += "author:" + string + " ";
        if (!(string = url.getText().toString()).isEmpty())
            extraSearchTerms += "url:" + string + " ";
        if (!(string = site.getText().toString()).isEmpty())
            extraSearchTerms += "site:" + string + " ";
        if (!(string = selfText.getText().toString()).isEmpty())
            extraSearchTerms += "selftext:" + string + " ";
        if (!(string = flair.getText().toString()).isEmpty())
            extraSearchTerms += "flair:" + string;
        return extraSearchTerms.trim();
    }
}
