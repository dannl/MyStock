package com.dolphin.browser.util;

import android.text.TextUtils;

public class PasswordVerifier {
    public static final int VERIFY_SUCCESS = 0;
    public static final int VERIFY_EMPTY = 1;
    public static final int VERIFY_TOO_SHORT = 2;
    public static final int VERIFY_TOO_LONG = 3;
    public static final int VERIFY_INVALID_FORMAT= 4;
    public static final int VERIFY_NOT_MATCH = 5;

    private static final String VALID_SPECIAL_PASSWORD_CHARS = "!@#$%^&*()_+-=[]{}\\|;':\",.<>/?";
    private int mMinLen;
    private int mMaxLen;

    public PasswordVerifier(int minLen, int maxLen) {
        if (maxLen < minLen) {
            throw new IllegalArgumentException("min must less or equal than max");
        }

        mMinLen = minLen;
        mMaxLen = maxLen;
    }

    public int verifyPair(CharSequence password, CharSequence confirmedPassword) {

        int result = verifySingle(password);
        if (result != VERIFY_SUCCESS) {
            return result;
        }

        if (!TextUtils.equals(password, confirmedPassword)) {
            return VERIFY_NOT_MATCH;
        }

        return VERIFY_SUCCESS;
    }

    public int verifySingle(CharSequence password) {
        final int pwdLen = password.length();

        if (pwdLen < mMinLen) {
            return VERIFY_TOO_SHORT;
        }

        if (pwdLen > mMaxLen) {
            return VERIFY_TOO_LONG;
        }

        for (int i = 0; i < pwdLen; i++) {
            char c = password.charAt(i);
            if (!isValidPasswordChar(c)) {
                return VERIFY_INVALID_FORMAT;
            }
        }

        return VERIFY_SUCCESS;
    }

    private static boolean isValidPasswordChar(int codePoint) {
        // XXX only valid under ASCII
        return ('A' <= codePoint && codePoint <= 'Z') ||
               ('a' <= codePoint && codePoint <= 'z') ||
               ('0' <= codePoint && codePoint <= '9') ||
               VALID_SPECIAL_PASSWORD_CHARS.indexOf(codePoint) != -1;
    }
}
