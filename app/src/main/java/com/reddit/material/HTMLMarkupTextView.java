package com.reddit.material;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.QuoteSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
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
    private final SpannableStringBuilder markup = new SpannableStringBuilder();

    public HTMLMarkupTextView(Context context) {
        super(context);
    }

    public HTMLMarkupTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setHTMLText(String html) {
        setText(html);
        setMovementMethod(LinkMovementMethod.getInstance());
        html = html.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'").replace
                ("&amp;", "&").replace("<li><p>", "<p>• ").replace("</li>", "<br>").replaceAll("<li.*?>", "• ")
                .replace("</del>", "</strike>").replace("<del>", "<strike>").replace("<!-- SC_OFF -->", "").replace
                        ("<!-- SC_ON -->", "").trim();
        try {
            Log.i(TAG, "setHTMLText: " + html);
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            final ArrayList<String> urls = new ArrayList<>();
            parser.parse(new InputSource(new StringReader(html)), new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String tagName, Attributes attributes) throws
                        SAXException {
//                    Log.d(TAG, "startElement: " + tagName);
                    if (tagName.equalsIgnoreCase("b") || tagName.equalsIgnoreCase("strong")) {
                        startTag(new StyleSpan(Typeface.BOLD));
                    } else if (tagName.equalsIgnoreCase("strike")) {
                        startTag(new StrikethroughSpan());
                    } else if (tagName.equalsIgnoreCase("a")) {
                        urls.add(attributes.getValue("href"));
                        startTagA(attributes.getValue("href"));
                    } else if (tagName.equalsIgnoreCase("i") || tagName.equalsIgnoreCase("em") || tagName
                            .equalsIgnoreCase("cite") || tagName.equalsIgnoreCase("dfn")) {
                        startTag(new StyleSpan(Typeface.ITALIC));
                    } else if (tagName.equalsIgnoreCase("blockquote")) {
                        handleTagP();
                        startTag(new QuoteSpan());
                    } else if (tagName.equalsIgnoreCase("u")) {
                        startTag(new UnderlineSpan());
                    } else if (tagName.equalsIgnoreCase("sup")) {
                        startTag(new SuperscriptSpan());
                    } else if (tagName.equalsIgnoreCase("sub")) {
                        startTag(new SubscriptSpan());
                    } else if (tagName.equalsIgnoreCase("p") || tagName.equalsIgnoreCase("div")) {
                        handleTagP();
                    }
                }

                @Override
                public void endElement(String uri, String localName, String tagName) throws SAXException {
//                    Log.d(TAG, "endElement: " + tagName);
                    if (tagName.equalsIgnoreCase("b")) {
                        endTag(new StyleSpan(Typeface.BOLD), StyleSpan.class);
                    } else if (tagName.equalsIgnoreCase("strike")) {
                        endTag(new StrikethroughSpan(), StrikethroughSpan.class);
                    } else if (tagName.equalsIgnoreCase("a")) {
                        endTagA(urls.remove(urls.size() - 1));
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
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    for (int i = 0; i < length; i++) {
                        markup.append(ch[i]);
                    }
                }
            });
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
//            throw new RuntimeException(e);
        }
        removeTrailingAndExtraWhitespace();
        Log.d(TAG, "setHTMLText: " + markup);
        setText(markup, BufferType.SPANNABLE);
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

        Log.d(TAG, "endElement: " + start + ", " + length);
        if (start != length) {
            markup.setSpan(span, start, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }

    private void startTagA(String url) {
        int length = markup.length();
        markup.setSpan(new URLSpan(url), length, length, Spanned.SPAN_MARK_MARK);
    }

    private void endTagA(String url) {
        int length = markup.length();

        Object[] spans = markup.getSpans(0, length, URLSpan.class);
        Object lastSpan = spans.length == 0 ? null : spans[spans.length - 1];
        int start = markup.getSpanStart(lastSpan);
        markup.removeSpan(lastSpan);

        Log.d(TAG, "endElement: " + start + ", " + length);
        if (start != length) {
            markup.setSpan(new URLSpan(url), start, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
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

    private void removeTrailingAndExtraWhitespace() {
        while (markup.charAt(markup.length() - 1) == '\n') {
            markup.delete(markup.length() - 1, markup.length());
        }
        while (markup.charAt(0) == '\n') {
            markup.delete(0, 1);
        }
        Pattern pattern = Pattern.compile("\n{3,}");
        Matcher matcher = pattern.matcher(markup);
        while (matcher.find()) {
            markup.delete(matcher.start() + 2, matcher.end());
            matcher = pattern.matcher(markup);
        }
    }
}