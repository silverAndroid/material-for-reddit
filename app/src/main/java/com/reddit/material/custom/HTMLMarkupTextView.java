package com.reddit.material.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BulletSpan;
import android.text.style.ClickableSpan;
import android.text.style.EasyEditSpan;
import android.text.style.QuoteSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.reddit.material.Util;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Rushil Perera on 1/3/2016.
 */
public class HTMLMarkupTextView extends TextView {

    private static final String TAG = "HTMLMarkupTextView";
    private final ArrayDeque<String> urls = new ArrayDeque<>();
    private final ArrayDeque<String> lists = new ArrayDeque<>();
    private final ArrayDeque<Integer> counts = new ArrayDeque<>();
    private SpannableStringBuilder markup;
    private Activity activity;
    private DefaultHandler handler;
    private LinkMovementMethod linkMovementMethod;
    private View parent;

    public HTMLMarkupTextView(Context context) {
        super(context);
        initializeHandler();
        initializeLinkMethod();
    }

    public HTMLMarkupTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeHandler();
        initializeLinkMethod();
    }

    public HTMLMarkupTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeHandler();
        initializeLinkMethod();
    }

    public void setHTMLText(String html) {
        markup = new SpannableStringBuilder();
        if (html != null && !html.equalsIgnoreCase("null")) {
            html = html.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'").replace
                    ("&amp;", "&").replace("</del>", "</strike>").replace("<del>", "<strike>").replace("<!-- SC_OFF " +
                    "-->", "").replace("<!-- SC_ON -->", "").replace("&nbsp;", "\n").trim();
            try {
                SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                parser.parse(new InputSource(new StringReader(html)), handler);
            } catch (ParserConfigurationException | SAXException | IOException e) {
                Log.d(TAG, "setHTMLText: " + html);
                e.printStackTrace();
            }
            removeTrailingAndExtraWhitespace();
            setText(markup, BufferType.SPANNABLE);
        }
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setParent(View parent) {
        this.parent = parent;
    }

    private void startTag(Object span) {
        int length = markup.length();
        markup.setSpan(span, length, length, Spanned.SPAN_MARK_MARK);
    }

    private void endTag(Object span, Class spanClass) {
        int length = markup.length();

        Object[] spans = markup.getSpans(0, length, spanClass);
        Object lastSpan = spans.length == 0 ? null : spans[spans.length - 1];
        int start = markup.getSpanStart(lastSpan);
        markup.removeSpan(lastSpan);

        if (start != length) {
            markup.setSpan(span, start, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }

    private void startTagA(String url) {
        int length = markup.length();
        markup.setSpan(new URLSpan(url), length, length, Spanned.SPAN_MARK_MARK);
    }

    private void endTagA(final String url) {
        final int length = markup.length();

        Object[] spans = markup.getSpans(0, length, URLSpan.class);
        Object lastSpan = spans.length == 0 ? null : spans[spans.length - 1];
        final int start = markup.getSpanStart(lastSpan);
        markup.removeSpan(lastSpan);

        if (start != length && url != null) {
            HTMLMarkupTextView.this.setMovementMethod(linkMovementMethod);
            markup.setSpan(new ClickSpan(new ClickSpan.OnClickListener() {
                @Override
                public void onClick() {
                    Util.linkClicked(activity, url);
                }
            }), start, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }

    private void handleTagP() {
        int length = markup.length();

        if (length >= 1 && markup.charAt(length - 1) == '\n') {
            if (length >= 2 && markup.charAt(length - 2) == '\n') {
                return;
            }

            markup.append("\n");
            return;
        }

        if (length > 0) {
            markup.append("\n\n");
        }
    }

    private void startTagOl() {
        int length = markup.length();
        //using EasyEditSpan as there is no span for ordered lists
        markup.setSpan(new EasyEditSpan(), length, length, Spanned.SPAN_MARK_MARK);
    }

    private void endTagOl() {
        int length = markup.length();

        Object[] spans = markup.getSpans(0, length, EasyEditSpan.class);
        Object lastSpan = spans.length == 0 ? null : spans[spans.length - 1];
        int start = markup.getSpanStart(lastSpan);
        markup.removeSpan(lastSpan);

        if (start != length) {
            int count = counts.poll();
            markup.insert(start, count++ + ".");
            markup.insert(length + 2, "\n");
            counts.offer(count);
        }
    }

    private void removeTrailingAndExtraWhitespace() {
        while (markup.length() > 0 && markup.charAt(markup.length() - 1) == '\n') {
            markup.delete(markup.length() - 1, markup.length());
        }
        while (markup.length() > 0 && markup.charAt(0) == '\n') {
            markup.delete(0, 1);
        }
        Pattern pattern = Pattern.compile("\n{3,}");
        Matcher matcher = pattern.matcher(markup);
        while (matcher.find()) {
            markup.delete(matcher.start() + 2, matcher.end());
            matcher = pattern.matcher(markup);
        }
    }

    private void initializeHandler() {
        handler = new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String tagName, Attributes attributes) throws
                    SAXException {
//                Log.d(TAG, "startElement: " + tagName);
                if (tagName.equalsIgnoreCase("b") || tagName.equalsIgnoreCase("strong")) {
                    startTag(new StyleSpan(Typeface.BOLD));
                } else if (tagName.equalsIgnoreCase("strike")) {
                    startTag(new StrikethroughSpan());
                } else if (tagName.equalsIgnoreCase("a")) {
                    urls.offer(attributes.getValue("href"));
                    startTagA(attributes.getValue("href"));
                } else if (tagName.equalsIgnoreCase("i") || tagName.equalsIgnoreCase("em") || tagName
                        .equalsIgnoreCase("cite") || tagName.equalsIgnoreCase("dfn")) {
                    startTag(new StyleSpan(Typeface.ITALIC));
                } else if (tagName.equalsIgnoreCase("blockquote")) {
                    startTag(new QuoteSpan());
                    handleTagP();
                } else if (tagName.equalsIgnoreCase("u")) {
                    startTag(new UnderlineSpan());
                } else if (tagName.equalsIgnoreCase("sup")) {
                    startTag(new SuperscriptSpan());
                } else if (tagName.equalsIgnoreCase("sub")) {
                    startTag(new SubscriptSpan());
                } else if (tagName.equalsIgnoreCase("p") || tagName.equalsIgnoreCase("div")) {
                    handleTagP();
                } else if (tagName.equalsIgnoreCase("ul")) {
                    lists.offer(tagName);
                } else if (tagName.equalsIgnoreCase("ol")) {
                    lists.offer(tagName);
                    counts.offer(1);
                } else if (tagName.equalsIgnoreCase("li")) {
                    if (lists.peek().equalsIgnoreCase("ul")) {
                        startTag(new BulletSpan());
                    } else if (lists.peek().equalsIgnoreCase("ol")) {
                        startTagOl();
                    }
                }
            }

            @Override
            public void endElement(String uri, String localName, String tagName) throws SAXException {
//                Log.d(TAG, "endElement: " + tagName);
                if (tagName.equalsIgnoreCase("b") || tagName.equalsIgnoreCase("strong")) {
                    endTag(new StyleSpan(Typeface.BOLD), StyleSpan.class);
                } else if (tagName.equalsIgnoreCase("strike")) {
                    endTag(new StrikethroughSpan(), StrikethroughSpan.class);
                } else if (tagName.equalsIgnoreCase("a")) {
                    endTagA(urls.poll());
                } else if (tagName.equalsIgnoreCase("i") || tagName.equalsIgnoreCase("em") || tagName
                        .equalsIgnoreCase("cite") || tagName.equalsIgnoreCase("dfn")) {
                    endTag(new StyleSpan(Typeface.ITALIC), StyleSpan.class);
                } else if (tagName.equalsIgnoreCase("blockquote")) {
                    handleTagP();
                    endTag(new QuoteSpan(), QuoteSpan.class);
                } else if (tagName.equalsIgnoreCase("u")) {
                    endTag(new UnderlineSpan(), UnderlineSpan.class);
                } else if (tagName.equalsIgnoreCase("sup")) {
                    endTag(new SuperscriptSpan(), SuperscriptSpan.class);
                } else if (tagName.equalsIgnoreCase("sub")) {
                    endTag(new SubscriptSpan(), SubscriptSpan.class);
                } else if (tagName.equalsIgnoreCase("p") || tagName.equalsIgnoreCase("div")) {
                    handleTagP();
                } else if (tagName.equalsIgnoreCase("br")) {
                    markup.append("\n");
                } else if (tagName.equalsIgnoreCase("ul") || tagName.equalsIgnoreCase("ol")) {
                    lists.poll();
                } else if (tagName.equalsIgnoreCase("li")) {
                    if (lists.peek().equalsIgnoreCase("ul")) {
                        endTag(new BulletSpan(), BulletSpan.class);
                        markup.append("\n\n");
                    } else if (lists.peek().equalsIgnoreCase("ol")) {
                        /*int count = counts.poll();
                        markup.insert(0, count++ + ".");
                        counts.offer(count);*/
                        endTagOl();
                    }
                }
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                for (int i = 0; i < length; i++) {
                    markup.append(ch[i]);
                }
            }
        };
    }

    private void initializeLinkMethod() {
        linkMovementMethod = new LinkMovementMethod() {
            @Override
            public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    x -= widget.getTotalPaddingLeft();
                    y -= widget.getTotalPaddingTop();

                    x += widget.getScrollX();
                    y += widget.getScrollY();

                    Layout layout = widget.getLayout();
                    int line = layout.getLineForVertical(y);
                    int off = layout.getOffsetForHorizontal(line, x);
                    float maxRight = layout.getLineRight(line);

                    if (x <= maxRight) {
                        ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

                        if (link.length != 0) {
                            if (action == MotionEvent.ACTION_UP) {
                                link[0].onClick(widget);
                            } else {
                                Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
                            }
                            return true;
                        } else {
                            Selection.removeSelection(buffer);
                        }
                    }
                }
                return parent.onTouchEvent(event);
            }
        };
    }
}