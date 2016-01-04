package com.reddit.material;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.reddit.material.libraries.google.CustomTabActivityHelper;

/**
 * Created by Rushil Perera on 10/28/2015.
 */
public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    final TextView title;
    final TextView lineOneInfo;
    final TextView source;
    final TextView timeSubredditInfo;
    final TextView gilded;
    final TextView flair;
    final TextView nsfwTag;
    final HTMLMarkupTextView selfText;
    final SimpleDraweeView image;
    final ProgressBar loading;
    final CardView card;
    final ImageButton upvote;
    final ImageButton downvote;
    final ImageButton reply;
    final ImageButton formatBold;
    final ImageButton formatItalics;
    final ImageButton formatStrikethrough;
    final ImageButton formatSuperscript;
    final ImageButton formatLink;
    final ImageButton formatQuote;
    final ImageButton formatCodeTags;
    final ImageButton formatBulletList;
    final ImageButton formatNumberList;
    final Button cancel;
    final Button submit;
    final EditText editMessage;
    final LinearLayout sendLayout;
    private final Activity activity;

    public PostViewHolder(final View itemView, final Activity activity) {
        super(itemView);
        this.activity = activity;
        title = (TextView) itemView.findViewById(R.id.title);
        image = (SimpleDraweeView) itemView.findViewById(R.id.image);
        lineOneInfo = (TextView) itemView.findViewById(R.id.lineOneInfo);
        source = (TextView) itemView.findViewById(R.id.source);
        timeSubredditInfo = (TextView) itemView.findViewById(R.id.timeSubredditInfo);
        gilded = (TextView) itemView.findViewById(R.id.gild_count);
        flair = (TextView) itemView.findViewById(R.id.flair);
        nsfwTag = (TextView) itemView.findViewById(R.id.nsfw_tag);
        loading = (ProgressBar) itemView.findViewById(R.id.loading);
        card = (CardView) itemView.findViewById(R.id.card);
        upvote = (ImageButton) itemView.findViewById(R.id.btn_upvote);
        downvote = (ImageButton) itemView.findViewById(R.id.btn_downvote);
        selfText = (HTMLMarkupTextView) itemView.findViewById(R.id.self_text);

        LinearLayout linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout);
        sendLayout = (LinearLayout) linearLayout.findViewById(R.id.include_send);
        sendLayout.setVisibility(View.GONE);
        reply = (ImageButton) linearLayout.findViewById(R.id.btn_reply);
        reply.setOnClickListener(this);
        editMessage = (EditText) linearLayout.findViewById(R.id.edit_message);

        cancel = (Button) linearLayout.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(this);
        submit = (Button) linearLayout.findViewById(R.id.btn_submit);

        formatBold = (ImageButton) sendLayout.findViewById(R.id.btn_bold);
        formatBold.setOnClickListener(this);
        formatItalics = (ImageButton) sendLayout.findViewById(R.id.btn_italics);
        formatItalics.setOnClickListener(this);
        formatStrikethrough = (ImageButton) sendLayout.findViewById(R.id.btn_strikethrough);
        formatStrikethrough.setOnClickListener(this);
        formatSuperscript = (ImageButton) sendLayout.findViewById(R.id.btn_superscript);
        formatSuperscript.setOnClickListener(this);
        formatLink = (ImageButton) sendLayout.findViewById(R.id.btn_link);
        formatLink.setOnClickListener(this);
        formatQuote = (ImageButton) sendLayout.findViewById(R.id.btn_quote);
        formatQuote.setOnClickListener(this);
        formatCodeTags = (ImageButton) sendLayout.findViewById(R.id.btn_code);
        formatCodeTags.setOnClickListener(this);
        formatBulletList = (ImageButton) sendLayout.findViewById(R.id.btn_bullet_list);
        formatBulletList.setOnClickListener(this);
        formatNumberList = (ImageButton) sendLayout.findViewById(R.id.btn_number_list);
        formatNumberList.setOnClickListener(this);
    }

    public void init(final Post post) {
        init(post, false, false);
    }

    public void init(final Post post, boolean ellipsize, boolean hideReply) {
        String imageURL;
        if (post.getPreviewImageURL() == null) {
            if (post.getURL() == null) {
                image.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
            } else {
                setURL(imageURL = post.getURL());
                if (post.isOver18()) {
                    Uri nsfwPath = new Uri.Builder()
                            .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                            .path(String.valueOf(R.drawable.nsfw_reddit_icon))
                            .build();
                    image.setImageURI(nsfwPath);
                    loading.setVisibility(View.GONE);
                    nsfwTag.setVisibility(View.VISIBLE);
                } else {
                    if (ConstantMap.getInstance().isImage(post.getURL())) {
                        image.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.VISIBLE);
                        ConnectionSingleton.getInstance().loadImage(imageURL, image, loading);
                    } else {
                        image.setVisibility(View.GONE);
                        loading.setVisibility(View.GONE);
                    }
                    nsfwTag.setVisibility(View.GONE);
                }
            }
        } else {
            setURL(post.getURL());
            if (post.isOver18()) {
                Uri nsfwPath = new Uri.Builder()
                        .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                        .path(String.valueOf(R.drawable.nsfw_reddit_icon))
                        .build();
                image.setImageURI(nsfwPath);
                loading.setVisibility(View.GONE);
                nsfwTag.setVisibility(View.VISIBLE);
            } else {
                if (ConstantMap.getInstance().isImage(post.getPreviewImageURL())) {
                    image.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.VISIBLE);
                } else {
                    image.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                }
                nsfwTag.setVisibility(View.GONE);
                ConnectionSingleton.getInstance().loadImage(post.getPreviewImageURL(), image, loading);
            }
        }

        title.setText(post.getTitle());
        flair.setVisibility(post.getLinkFlairText() == null ? View.GONE : post.getLinkFlairText().equals
                ("") ? View.GONE : View.VISIBLE);
        lineOneInfo.setText(Html.fromHtml("<b><font size=\"20\">" + post.getScore() + "</font></b> pts " +
                "<b>" + post.getNumComments() + "</b> comments by <b>" + post.getAuthor() + "</b>"));
        source.setText(post.getDomain());
        timeSubredditInfo.setText(Html.fromHtml("<b>" + DateUtils.getRelativeTimeSpanString(post
                .getCreatedUTC() * 1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS) + "</b> to " +
                "r/<b>" + post.getSubreddit() + "</b>"));
        gilded.setVisibility(post.getGilded() == 0 ? View.GONE : View.VISIBLE);
        gilded.setText(String.format("%d", post.getGilded()));
        flair.setText(post.getLinkFlairText());

        if (!post.getSelfTextHTML().equals("")) {
            if (ellipsize) {
                selfText.setMaxLines(3);
                selfText.setEllipsize(TextUtils.TruncateAt.END);
            }
            selfText.setVisibility(View.VISIBLE);
            selfText.setHTMLText(post.getSelfTextHTML());
        } else
            selfText.setVisibility(View.GONE);

        upvote.setSelected(post.getVote() == 1);
        upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Authentication.getInstance().isLoggedIn()) {
                    Toast.makeText(activity, "You must be logged in to vote!", Toast.LENGTH_SHORT).show();
                    return;
                }

                upvote.setSelected(!upvote.isSelected());
                downvote.setSelected(false);
                post.vote(upvote.isSelected() ? 1 : 0);
            }
        });

        downvote.setSelected(post.getVote() == -1);
        downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Authentication.getInstance().isLoggedIn()) {
                    Toast.makeText(activity, "You must be logged in to vote!", Toast.LENGTH_SHORT).show();
                    return;
                }

                downvote.setSelected(!downvote.isSelected());
                upvote.setSelected(false);
                post.vote(downvote.isSelected() ? -1 : 0);
            }
        });

        if (gilded.getVisibility() == View.VISIBLE) {
            card.setCardBackgroundColor(Color.rgb(253, 221, 98));
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, activity.getResources()
                    .getDisplayMetrics());
            card.setContentPadding(padding, padding, padding, padding);
        } else if (post.isStickied()) {
            card.setCardBackgroundColor(Color.rgb(164, 208, 95));
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, activity.getResources()
                    .getDisplayMetrics());
            card.setContentPadding(padding, padding, padding, padding);
        } else {
            card.setCardBackgroundColor(Color.rgb(245, 243, 242));
            card.setContentPadding(0, 0, 0, 0);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLayout.setVisibility(View.GONE);
                ConnectionSingleton.getInstance().comment(editMessage.getText().toString(), post.getID(),
                        editMessage);
            }
        });

        reply.setVisibility(hideReply ? View.GONE : View.VISIBLE);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, CommentActivity.class);
                intent.putExtra("post", post);
                activity.startActivity(intent);
            }
        });
    }

    public void setURL(final String url) {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConstantMap.getInstance().isYoutube(url)) {
                    Intent intent = new Intent(activity, YouTubeActivity.class);
                    intent.putExtra("url", url);
                    activity.startActivity(intent);
                } else if (ConstantMap.getInstance().isGIF(url)) {
                    Intent intent = new Intent(activity, VideoActivity.class);
                    intent.putExtra("url", url);
                    activity.startActivity(intent);
                } else if (ConstantMap.getInstance().isImage(url)) {
                    Intent intent = new Intent(activity, ImageActivity.class);
                    intent.putExtra("url", url);
                    activity.startActivity(intent);
                } else {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        builder.setToolbarColor(activity.getResources().getColor(R.color.colorPrimary, null));
                    } else {
                        builder.setToolbarColor(activity.getResources().getColor(R.color.colorPrimary));
                    }
                    builder.setShowTitle(true);
                    CustomTabActivityHelper.openCustomTab(activity, builder.build(), Uri.parse(url), null);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                sendLayout.setVisibility(View.GONE);
                editMessage.getText().clear();
                break;
            case R.id.btn_reply:
                if (!Authentication.getInstance().isLoggedIn())
                    Toast.makeText(activity, "You must be logged in to comment!", Toast.LENGTH_SHORT).show();
                else
                    sendLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_bold:
                int selectionStart = editMessage.getSelectionStart();
                int selectionEnd = editMessage.getSelectionEnd();
                if (selectionStart != -1 || selectionEnd == -1) {
                    String originalMessage = editMessage.getText().toString();
                    String newMessage = originalMessage.substring(0, selectionStart) + "**" + originalMessage
                            .substring(selectionStart, selectionEnd).replaceAll("(\\*\\*)", "") + "**" +
                            originalMessage.substring(selectionEnd, originalMessage.length());
                    editMessage.setText(newMessage);
                } else
                    editMessage.append(" ****");
                break;
            case R.id.btn_italics:
                selectionStart = editMessage.getSelectionStart();
                selectionEnd = editMessage.getSelectionEnd();
                if (selectionStart != -1 || selectionEnd == -1) {
                    String originalMessage = editMessage.getText().toString();
                    String newMessage = originalMessage.substring(0, selectionStart) + "*" + originalMessage
                            .substring(selectionStart, selectionEnd).replaceAll("(?<![*])[*](?![*])", "") + "*" +
                            originalMessage.substring(selectionEnd, originalMessage.length());
                    editMessage.setText(newMessage);
                } else
                    editMessage.append(" **");
                break;
            case R.id.btn_strikethrough:
                selectionStart = editMessage.getSelectionStart();
                selectionEnd = editMessage.getSelectionEnd();
                if (selectionStart != -1 || selectionEnd == -1) {
                    String originalMessage = editMessage.getText().toString();
                    String newMessage = originalMessage.substring(0, selectionStart) + "~~" + originalMessage
                            .substring(selectionStart, selectionEnd).replaceAll("(~~)", "") + "~~" +
                            originalMessage.substring(selectionEnd, originalMessage.length());
                    editMessage.setText(newMessage);
                } else
                    editMessage.append(" ~~");
                break;
            case R.id.btn_superscript:
                selectionStart = editMessage.getSelectionStart();
                selectionEnd = editMessage.getSelectionEnd();
                if (selectionStart != -1 || selectionEnd == -1) {
                    String originalMessage = editMessage.getText().toString();
                    String newMessage = originalMessage.substring(0, selectionStart) + "^" + originalMessage
                            .substring(selectionStart, selectionEnd).replace("^", "") + originalMessage.substring
                            (selectionEnd, originalMessage.length());
                    editMessage.setText(newMessage);
                } else
                    editMessage.append("^");
                break;
            case R.id.btn_link:
                final int selectionStartURL = editMessage.getSelectionStart();
                final int selectionEndURL = editMessage.getSelectionEnd();
                final AlertDialog alertDialog = new AlertDialog.Builder(activity)
                        .setView(R.layout.layout_url_dialog)
                        .setTitle("Enter URL")
                        .setPositiveButton("OK", null)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialogInterface) {
                        Button ok = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialog dialog = (Dialog) dialogInterface;
                                EditText urlEdit = (EditText) dialog.findViewById(R.id.url);
                                String url = urlEdit.getText().toString();
                                if (URLUtil.isValidUrl(url)) {
                                    if (selectionStartURL != -1 || selectionEndURL == -1) {
                                        String originalMessage = editMessage.getText().toString();
                                        String newMessage = originalMessage.substring(0, selectionStartURL) + "[" +
                                                originalMessage.substring(selectionStartURL, selectionEndURL) + "]("
                                                + url + ")" + originalMessage.substring(selectionEndURL,
                                                originalMessage.length());
                                        editMessage.setText(newMessage);
                                    } else
                                        editMessage.append("[](" + url + ")");
                                    dialog.dismiss();
                                } else
                                    urlEdit.setError(activity.getResources().getString(R.string.error_invalid_url));
                            }
                        });
                    }
                });
                alertDialog.show();
                break;
            case R.id.btn_quote:
                selectionStart = editMessage.getSelectionStart();
                selectionEnd = editMessage.getSelectionEnd();
                //TODO: Adding the > to the beginning of the line
                if (selectionStart != -1 || selectionEnd == -1) {
                    String originalMessage = editMessage.getText().toString();
                    String newMessage = originalMessage.substring(0, selectionStart) + "\n> " + originalMessage
                            .substring(selectionStart, selectionEnd) + originalMessage.substring(selectionEnd,
                            originalMessage.length());
                    editMessage.setText(newMessage);
                } else {
                    if (editMessage.length() > 0)
                        editMessage.append("\n> ");
                    else
                        editMessage.setText(">");
                }
                break;
            case R.id.btn_code:
                selectionStart = editMessage.getSelectionStart();
                selectionEnd = editMessage.getSelectionEnd();
                //TODO: Adding 4 spaces to the beginning of the line
                if (selectionStart != -1 || selectionEnd == -1) {
                    String originalMessage = editMessage.getText().toString();
                    String newMessage = originalMessage.substring(0, selectionStart) + "\n    " + originalMessage
                            .substring(selectionStart, selectionEnd) + originalMessage.substring(selectionEnd,
                            originalMessage.length());
                    editMessage.setText(newMessage);
                } else {
                    if (editMessage.length() > 0)
                        editMessage.append("\n    ");
                    else
                        editMessage.setText("    ");
                }
                break;
            case R.id.btn_bullet_list:
                selectionStart = editMessage.getSelectionStart();
                selectionEnd = editMessage.getSelectionEnd();
                //TODO: Adding bullet to the beginning of the line
                if (selectionStart != -1 || selectionEnd == -1) {
                    String originalMessage = editMessage.getText().toString();
                    String newMessage = originalMessage.substring(0, selectionStart) + "\n*" + originalMessage
                            .substring(selectionStart, selectionEnd) + originalMessage.substring(selectionEnd,
                            originalMessage.length());
                    editMessage.setText(newMessage);
                } else {
                    if (editMessage.length() > 0)
                        editMessage.append("\n*");
                    else
                        editMessage.setText("*");
                }
                break;
            case R.id.btn_number_list:
                selectionStart = editMessage.getSelectionStart();
                selectionEnd = editMessage.getSelectionEnd();
                //TODO: Adding numbering to the beginning of the line
                if (selectionStart != -1 || selectionEnd == -1) {
                    String originalMessage = editMessage.getText().toString();
                    String newMessage = originalMessage.substring(0, selectionStart) + "\n1. " + originalMessage
                            .substring(selectionStart, selectionEnd) + originalMessage.substring(selectionEnd,
                            originalMessage.length());
                    editMessage.setText(newMessage);
                } else {
                    if (editMessage.length() > 0)
                        editMessage.append("\n1. ");
                    else
                        editMessage.setText("1. ");
                }
            default:
                break;
        }
    }
}
