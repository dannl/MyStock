/**
 *
 */

package com.dolphin.browser.util;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.UnderlineSpan;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class UrlFormatUtil {

    private static final String[] PREFIXES = new String[] {
            "www.", "m.", "3g.", "moble.", "wap.", "web."
    };

    public static final int STYLE_TYPEFACE_MASK = 0x000000FF;

    public static final int STYLE_LINE_MASK = 0x0000FF00;
    public static final int STYLE_STRIKETHROUGH = 0x00000100;
    public static final int STYLE_UNDERLINE = 0x00000200;

    public static final int STYLE_SCRIPT_MASK = 0x00FF0000;
    public static final int STYLE_SUBSCRIPT = 0x00010000;
    public static final int STYLE_SUPERSCRIPT = 0x00020000;

    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";
    private static final String URL_DIVIDER = "/";


    public static class UrlFormatContext {

        public static final int STATE_INIT = 0;
        public static final int STATE_MODIFYING = 1;
        public static final int STATE_DECORATING = 2;
        public static final int STATE_DONE = 3;

        private int mState;

        private final String mOriginalUrl;	// Original URL passed in
        private String mUrl;				// Modified URL, might be null, empty, and with wrong case.
        private String mNormalizedUrl;		// Modified URL, all in lower case, would be empty instead of null.

        private SpannableStringBuilder mUrlBuilder;
        private final Map<String, Object> mUserDataMap = new HashMap<String, Object>();

        UrlFormatContext(CharSequence url) {
            this.mState = STATE_INIT;
            this.mOriginalUrl = url.toString();
            this.mUrl = url.toString();
            this.mNormalizedUrl = normalizeUrl(mUrl);
        }

        public int state() {
            return mState;
        }

        void beginModify() {
            assertState(STATE_INIT, "Can only begin modify in STATE_INIT state.");
            this.mState = STATE_MODIFYING;
        }

        void beginDecorate() {
            assertState(STATE_MODIFYING, "Can only begin modify in STATE_MODIFYING state.");
            this.mState = STATE_DECORATING;
            this.mUrlBuilder = new SpannableStringBuilder(mUrl);
        }

        void done() {
            assertState(STATE_DECORATING, "Can only begin modify in STATE_DECORATING state.");
            this.mState = STATE_DONE;
        }

        public CharSequence getCharSequence() {
            assertState(STATE_DONE, "Can only get result in STATE_DONE state.");
            return mUrlBuilder;
        }

        public SpannableStringBuilder builder() {
            assertState(STATE_DECORATING, "Can only build span in STATE_DECORATING state.");
            return mUrlBuilder;
        }

        public String normalizedUrl() {
            return mNormalizedUrl;
        }

        public String originalUrl() {
            return mOriginalUrl;
        }

        public String getUrl() {
            return mUrl;
        }

        public void setUrl(String url) {
            assertState(STATE_MODIFYING, "Can only change URL in STATE_MODIFYING state.");
            mUrl = url;
            mNormalizedUrl = normalizeUrl(url);
        }

        public Object getUserData(String key) {
            if (TextUtils.isEmpty(key)) {
                throw new IllegalArgumentException("key may not be null.");
            }
            return mUserDataMap.get(key);
        }

        public void setUserData(String key, Object userData) {
            if (TextUtils.isEmpty(key)) {
                throw new IllegalArgumentException("key may not be null.");
            }
            mUserDataMap.put(key, userData);
        }

        protected String normalizeUrl(String url) {
            if (TextUtils.isEmpty(url)) {
                return "";
            }
            return url.toLowerCase(Locale.US);
        }

        protected void assertState(int state, String message) {
            assertState(mState != state, message);
        }


        protected void assertState(boolean condition, String message) {
            if (condition) {
                throw new IllegalStateException(message);
            }
        }


    }

    public static abstract class UrlFormatStrategy {

        protected UrlFormatStrategy() {
        }

        /**
         * Called in {@link UrlFormatContext#STATE_INIT} state, do initialization.
         * @param context the context.
         */
        public void setUp(UrlFormatContext context) {
        }

        /**
         * Called in {@link UrlFormatContext#STATE_DONE} state, do clean ups.
         * @param context the context.
         */
        public void cleanUp(UrlFormatContext context) {
        }

        /**
         * Called in {@link UrlFormatContext#STATE_MODIFYING} state, you might modify the URL here.
         * @param context the context.
         * @return the same context.
         */
        public UrlFormatContext modifyUrl(UrlFormatContext context) {
            return context;
        }

        /**
         * Called in {@link UrlFormatContext#STATE_DECORATING state, you add spans to the URL here.
         * @param context the context.
         * @return the same context.
         */
        public UrlFormatContext decorateUrlWithSpans(UrlFormatContext context) {
            return context;
        }
    }

    public static interface URLSecureStatusChecker  {

        enum SecureStatus {
            UNSECURE, SECURE, PARTIAL;
        }

        SecureStatus getSecureStatus();
    }

    /**
     * This strategy removes "http://" from the URL and bold https in the URL.
     * @author chzhong
     *
     */
    public static final class SchemeFormatStrategy extends UrlFormatStrategy {

        public static final SchemeFormatStrategy INSTANCE = new SchemeFormatStrategy();

        private static final Integer[] SECURE_STATUS_FOREGROUNDCOLORS = new Integer[] {
            Color.rgb(128, 0, 0),
            Color.GREEN,
            Color.rgb(128, 255, 0),
        };

        private static final Integer[] SECURE_STATUS_STYLES = new Integer[] {
            STYLE_STRIKETHROUGH | Typeface.ITALIC,
            Typeface.BOLD,
            Typeface.BOLD_ITALIC,

        };

        private URLSecureStatusChecker mSecureStatueChecker;

        public void setSecureStatueChecker(
                URLSecureStatusChecker secureStatueChecker) {
            mSecureStatueChecker = secureStatueChecker;
        }

        @Override
        public UrlFormatContext modifyUrl(UrlFormatContext context) {
            String url = context.normalizedUrl();
            if (url.startsWith(HTTP_PREFIX)) {
                url = url.substring(HTTP_PREFIX.length());
                context.setUrl(url);
            }
            return context;
        }

        @Override
        public UrlFormatContext decorateUrlWithSpans(UrlFormatContext context) {
            SpannableStringBuilder builder = context.builder();
            String url = context.normalizedUrl();
            if (url.startsWith(HTTPS_PREFIX)) {
                URLSecureStatusChecker checker = mSecureStatueChecker;
                int start = 0;
                int end = HTTPS_PREFIX.length() - 3;
                int style = Typeface.BOLD;
                Integer foregroundColor, backgroundColor = null;
                if (checker != null) {
                    int status = checker.getSecureStatus().ordinal();
                    foregroundColor = SECURE_STATUS_FOREGROUNDCOLORS[status];
                    style = SECURE_STATUS_STYLES[status];
                } else {
                    foregroundColor = null;
                    backgroundColor = null;
                }
                setColorStyleSpan(builder, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE, style, foregroundColor, backgroundColor);
            }
            return context;
        }

    }

    /**
     * This strategy applies specified color (foreground and background) and style to the whole URL.
     * @author chzhong
     *
     */
    public static class ColorStyleStrategy extends UrlFormatStrategy {

        public final Integer style;
        public final Integer foregroundColor;
        public final Integer backgroundColor;

        /**
         * Initiate an instance of {@linkplain ColorStyleStrategy}.
         * @param style the style to apply. Can be {@link Typeface#NORMAL}, {@link Typeface#BOLD}, etc. {@code null} means don't change.
         * @param foregroundColor foreground color to apply, in 0xAARRGGBB format.
         * @param backgroundColor background color to apply, in 0xAARRGGBB format.
         */
        public ColorStyleStrategy(Integer style, Integer foregroundColor, Integer backgroundColor) {
            this.foregroundColor = foregroundColor;
            this.style = style;
            this.backgroundColor = backgroundColor;
        }

        public UrlFormatContext decorateUrlWithSpans(UrlFormatContext context) {
            if (null == context) {
                return context;
            }
            SpannableStringBuilder builder = context.builder();
            String url = context.normalizedUrl();
            setColorStyleSpan(builder, 0, url.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
                    style, foregroundColor, backgroundColor);
            return context;
        }

    }

    /**
     * This strategy applies highlight style and color to the top-level domain on the URL,
     *  normal style and color to reset of the URL..
     * @author chzhong
     *
     */
    public static final class HighlightTopDomainStrategy extends ColorStyleStrategy {

        public final Integer highlightStyle;
        public final Integer highlightForegroundColor;
        public final Integer highlightBackgroundColor;

        /**
         * Initiate an instance of {@linkplain HighlightTopDomainStrategy}.
         * @param style the style to apply. Can be {@link Typeface#NORMAL}, {@link Typeface#BOLD}, etc. {@code null} means don't change.
         * @param foregroundColor foreground color to apply, in 0xAARRGGBB format.
         * @param backgroundColor background color to apply, in 0xAARRGGBB format.
         * @param highlightStyle the style to apply for highlighted part. Can be {@link Typeface#NORMAL}, {@link Typeface#BOLD}, etc. {@code null} means don't change.
         * @param highlightForegroundColor foreground color to apply for highlighted part, in 0xAARRGGBB format.
         * @param highlightBackgroundColor background color to apply for highlighted part, in 0xAARRGGBB format.
         *
         */
        public HighlightTopDomainStrategy(Integer style, Integer foregroundColor, Integer backgroundColor,
                Integer highlightStyle, Integer highlightForegroundColor, Integer highlightBackgroundColor) {
            super(style, foregroundColor, backgroundColor);
            this.highlightStyle = highlightStyle;
            this.highlightForegroundColor = highlightForegroundColor;
            this.highlightBackgroundColor = highlightBackgroundColor;
        }

        public UrlFormatContext decorateUrlWithSpans(UrlFormatContext context) {
            if (null == context) {
                return context;
            }
            SpannableStringBuilder builder = context.builder();
            String url = context.normalizedUrl();
            String topDomain = URIUtil.getTopLevelDomain(context.originalUrl());
            if (TextUtils.isEmpty(topDomain)) {
                return context;
            }
            int highlightStart = url.indexOf(topDomain);
            if (highlightStart >= 0) {
                int highlightEnd = highlightStart + topDomain.length();
                // [0,4)[4,      14)[14,...
                // www.  google.com /search?q=Foo
                setColorStyleSpan(builder, 0, highlightStart, Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
                        style, foregroundColor, backgroundColor);
                setColorStyleSpan(builder, highlightStart, highlightEnd, Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
                        highlightStyle, highlightForegroundColor, highlightBackgroundColor);
                setColorStyleSpan(builder, highlightEnd, url.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
                        style, foregroundColor, backgroundColor);
            } else {
                setColorStyleSpan(builder, 0, url.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE,
                        style, foregroundColor, backgroundColor);
            }
            return context;
        }

    }

    public static final CharSequence formatColored(String url, final int color) {

        return formatColored(url, color, null, null, null);
    }


    public static final CharSequence formatColored(String url, final int color, final Integer style,
            final Integer highlightColor, final Integer highlightStyle) {

        if (TextUtils.isEmpty(url)) {
            return url;
        }
        UrlFormatStrategy[] strategies = new UrlFormatStrategy[] {
                SchemeFormatStrategy.INSTANCE,
                new HighlightTopDomainStrategy(style, color, null, highlightStyle, highlightColor, null)
        };
        return formatWithStrategiesInternal(url, strategies);
    }

    public static CharSequence formatWithStrategies(CharSequence url,
            UrlFormatStrategy[] strategies) {
        if (TextUtils.isEmpty(url) || null == strategies || 0 == strategies.length) {
            return url;
        }
        return formatWithStrategiesInternal(url, strategies);
    }

    private static CharSequence formatWithStrategiesInternal(CharSequence url,
            UrlFormatStrategy[] strategies) {
        UrlFormatContext context = new UrlFormatContext(url);
        for (UrlFormatStrategy strategy : strategies) {
            strategy.setUp(context);
        }
        context.beginModify();
        for (UrlFormatStrategy strategy : strategies) {
            strategy.modifyUrl(context);
        }
        context.beginDecorate();
        for (UrlFormatStrategy strategy : strategies) {
            strategy.decorateUrlWithSpans(context);
        }
        context.done();
        return context.getCharSequence();
    }

    /*
    private static CharSequence formatColored(String url, final int color,
            final int style, final Integer highlightColor, final Integer highlightStyle) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        int contentStart = url.indexOf(HTTP_PREFIX);
        if (contentStart == 0) {
            url = url.substring(HTTP_PREFIX.length(), url.length());
        } else {
            contentStart = url.indexOf("://");
            if (contentStart > 0) {
                contentStart += 3;
            } else {
                contentStart = 0;
            }
        }
        final SpannableStringBuilder builder = new SpannableStringBuilder(url);
        final int prefixesLength = PREFIXES.length;
        int highlightStart = 0;
        for (int i = 0; i < prefixesLength; i++) {
            int index = url.indexOf(PREFIXES[i]);
            if (index == contentStart) {
                highlightStart = index + PREFIXES[i].length();
                setColorStyleSpan(builder, index, highlightStart, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE, style,
                        color, null);
                break;
            }
        }
        int highlightEnd = url.indexOf(URL_DIVIDER, contentStart);
        if (highlightEnd > 0) {
            setColorStyleSpan(builder, highlightEnd, url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE, style,
                    color, null);
            if (highlightStart > 0) {
                setColorStyleSpan(builder, highlightStart, highlightEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                        highlightStyle, highlightColor, null);
            }
        }
        return builder;
    }
    */

    private static void setColorStyleSpan(SpannableStringBuilder builder, int start, int end, int flags,
            Integer style, Integer foregroundColor, Integer backgroundColor) {

        if (style != null) {
            int typefaceStyle = style & STYLE_TYPEFACE_MASK;
            final StyleSpan styleSpan = new StyleSpan(typefaceStyle);
            builder.setSpan(styleSpan, start, end, flags);

            int lineStyle = style & STYLE_LINE_MASK;
            if (lineStyle != 0) {
                Object lineSpan = STYLE_STRIKETHROUGH == lineStyle ? new StrikethroughSpan() : new UnderlineSpan();
                builder.setSpan(lineSpan, start, end, flags);
            }

            int scriptStyle = style & STYLE_SCRIPT_MASK;
            if (scriptStyle != 0) {
                Object scriptSpan = STYLE_SUBSCRIPT == lineStyle ? new SubscriptSpan() : new SuperscriptSpan();
                builder.setSpan(scriptSpan, start, end, flags);
            }
        }
        if (foregroundColor != null) {
            final ForegroundColorSpan colorSpan = new ForegroundColorSpan(foregroundColor);
            builder.setSpan(colorSpan, start, end, flags);
        }

        if (backgroundColor != null) {
            final BackgroundColorSpan colorSpan = new BackgroundColorSpan(backgroundColor);
            builder.setSpan(colorSpan, start, end, flags);
        }
    }
}
