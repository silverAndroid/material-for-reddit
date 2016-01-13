package com.reddit.material;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.reddit.material.custom.HTMLMarkupTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Rushil Perera on 11/8/2015.
 */
public class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final ArrayList<Integer> sideColors;
    private static final String UPVOTE_HEX = "#FF4081";
    private static final String DOWNVOTE_HEX = "#4D42FC";

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

    final TextView username;
    final TextView numPoints;
    final TextView time;
    final HTMLMarkupTextView text;
    final TextView gilded;
    final ImageButton upvote;
    final ImageButton downvote;
    final View sideColor;
    final LinearLayout view;
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
    final RelativeLayout optionsView;
    private final Activity activity;

    public CommentViewHolder(View itemView, final Activity activity) {
        super(itemView);
        this.activity = activity;
        username = (TextView) itemView.findViewById(R.id.username);
        numPoints = (TextView) itemView.findViewById(R.id.num_points);
        time = (TextView) itemView.findViewById(R.id.time);
        text = (HTMLMarkupTextView) itemView.findViewById(R.id.text);
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
        reply.setOnClickListener(this);
        editMessage = (EditText) view.findViewById(R.id.edit_message);
        cancel = (Button) view.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(this);
        submit = (Button) view.findViewById(R.id.btn_submit);

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

    public void init(final Comment comment) {
        username.setText(comment.getAuthor());
        text.setActivity(activity);
        text.setParent(itemView);
        text.setHTMLText(comment.getBodyHTML());
        numPoints.setText(String.format("%d pts", comment.getScore()));
        gilded.setVisibility(comment.getGilded() == 0 ? View.GONE : View.VISIBLE);
        gilded.setText(String.format("%d", comment.getGilded()));
        time.setText(DateUtils.getRelativeTimeSpanString(comment.getCreatedUTC() * 1000,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
        if (comment.getDepth() > 0) {
            sideColor.setVisibility(View.VISIBLE);
            sideColor.setBackgroundColor(sideColors.get(comment.getDepth() < 10 ? comment.getDepth() : 9));
            DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
            view.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f * comment
                    .getDepth(), metrics), 0, 0, 0);
        } else {
            sideColor.setVisibility(View.GONE);
            view.setPadding(0, 0, 0, 0);
        }

        upvote.setSelected(comment.getVote() == 1);
        upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Authentication.getInstance().isLoggedIn()) {
                    Toast.makeText(activity, "You must be logged in to vote!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int oldScore = comment.getScore();
                int newScore = upvote.isSelected() ? oldScore - 1 : downvote.isSelected() ? oldScore + 2 : oldScore + 1;
                comment.setScore(newScore);
                upvote.setSelected(!upvote.isSelected());
                downvote.setSelected(false);
                comment.vote(upvote.isSelected() ? 1 : 0);
                numPoints.setTextColor(upvote.isSelected() ? Color.parseColor(UPVOTE_HEX) : Color.BLACK);
                numPoints.setText(String.format("%d pts", comment.getScore()));
            }
        });

        downvote.setSelected(comment.getVote() == -1);
        numPoints.setTextColor(upvote.isSelected() ? Color.parseColor(UPVOTE_HEX) : downvote.isSelected() ? Color
                .parseColor(DOWNVOTE_HEX) : Color.BLACK);
        downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Authentication.getInstance().isLoggedIn()) {
                    Toast.makeText(activity, "You must be logged in to vote!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int oldScore = comment.getScore();
                int newScore = downvote.isSelected() ? oldScore + 1 : upvote.isSelected() ? oldScore - 2 : oldScore - 1;
                comment.setScore(newScore);
                downvote.setSelected(!downvote.isSelected());
                upvote.setSelected(false);
                comment.vote(downvote.isSelected() ? -1 : 0);
                numPoints.setTextColor(downvote.isSelected() ? Color.parseColor(DOWNVOTE_HEX) : Color.BLACK);
                numPoints.setText(String.format("%d pts", comment.getScore()));
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsView.setVisibility(optionsView.getVisibility() == View.GONE ? View.VISIBLE :
                        View.GONE);
                sendLayout.setVisibility(View.GONE);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment(editMessage.getText().toString(), comment.getID());
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
                final AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.DialogTheme)
                        .setView(R.layout.layout_url_dialog)
                        .setTitle("Enter URL")
                        .setPositiveButton("OK", null)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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

    private void comment(final String text, final String id) {
        AsyncHttpClient commentClient = new AsyncHttpClient();
        commentClient.addHeader("Authorization", "bearer " + Authentication.getInstance().getAccessToken());
        commentClient.setUserAgent(ConstantMap.getInstance().getConstant("user_agent"));
        HashMap<String, String> bodyParams = new HashMap<>(3);
        bodyParams.put("api_type", "json");
        bodyParams.put("text", text);
        bodyParams.put("thing_id", id);
        RequestParams params = new RequestParams(bodyParams);
        commentClient.post("https://oauth.reddit.com/api/comment/.json", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject commentsJSON = response.getJSONObject("json").getJSONObject("data").getJSONArray
                            ("things").getJSONObject(0);
                    String kind = commentsJSON.getString("kind");
                    JSONObject commentJSON = commentsJSON.getJSONObject("data");
                    if (kind.equals("t1")) {
                        Comment comment = Util.generateComment(commentJSON);
                        editMessage.getText().clear();
                        CommentActivity.getAdapter().addComment(comment);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
