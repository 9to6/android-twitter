/*
 * author: Teukgeon Kwon
 * email: ktg@weeppp.com
 * blog: weeppp.com
 *
 * Copyright (c) 2015. all rights reserved.
 */

package com.weeppp.androidtwitter;

import android.app.Activity;
import android.content.Intent;

import twitter4j.auth.AccessToken;

/**
 * Created by ktg on 2015-07-28.
 */
public class LoginManager {

    public static int UNKNOWN_ERROR = -1;
    /**
     * Twitter OAuth process was not even started due to
     * failure of getting a request token. The pair of
     * consumer key and consumer secret was wrong or some
     * kind of network error occurred.
     */
    public static int REQUEST_TOKEN_ERROR = 1;

    /**
     * The application has not been authorized by the user,
     * or a network error occurred during the OAuth handshake.
     */
    public static int AUTHORIZATION_ERROR = 2;

    /**
     * The application has been authorized by the user but
     * failed to get an access token.
     */
    public static int ACCESS_TOKEN_ERROR = 3;

    public static final int REQCODE = 1000;

    private static LoginManager _loginManager;

    private OAuthListener mListener;

    private LoginManager() {
    }

    public static LoginManager getInstance() {
        if (_loginManager == null) {
            synchronized (LoginManager.class) {
                if (_loginManager == null) {
                    _loginManager = new LoginManager();
                }
            }
        }
        return _loginManager;
    }

    public void startLogin(Activity activity, String consumerKey, String consumerSecret) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtra("consumerKey", consumerKey);
        intent.putExtra("consumerSecret", consumerSecret);
        activity.startActivityForResult(intent, REQCODE);
    }

    public void registerListener(OAuthListener listener)
    {
        mListener = listener;
    }

    public OAuthListener getListener()
    {
        return mListener;
    }

    public interface OAuthListener {
        void onSuccess(AccessToken accessToken);

        void onFailure(int errorCode);
    }
}
