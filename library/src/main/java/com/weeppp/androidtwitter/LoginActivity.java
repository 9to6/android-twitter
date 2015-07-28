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
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.neovisionaries.android.twitter.TwitterOAuthView;
import com.weeppp.androidtwitter.helper.ViewHelper;

import twitter4j.auth.AccessToken;

/**
 * Created by ktg on 2015-07-27.
 */
public class LoginActivity extends Activity implements TwitterOAuthView.Listener {
    private static final String CALLBACK_URL = "androidtwitter://twitter";

    private TwitterOAuthView mTwitterOAuthView;
    private ProgressBar mProgressBar;
    private LoginManager.OAuthListener mOAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_activity_login);
        overridePendingTransition(R.anim.anim_from_middle, R.anim.anim_no_change);

        mTwitterOAuthView = (TwitterOAuthView) findViewById(R.id.webview);
        mTwitterOAuthView.setDebugEnabled(AndroidTwitter.isDebugEnabled());
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

        mOAuthListener = LoginManager.getInstance().getListener();

        mTwitterOAuthView.start(getIntent().getStringExtra("consumerKey"),
                getIntent().getStringExtra("consumerSecret"),
                CALLBACK_URL, true, this);

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_no_change, R.anim.anim_to_middle);
    }

    private void showLoginView(boolean show)
    {
        ViewGroup rootView = (ViewGroup) ViewHelper.getRootView(this);
        if (show) {
            rootView.addView(mTwitterOAuthView);
        }
        else {
            rootView.removeView(mTwitterOAuthView);
        }
    }

    public void onClickCancel(View view)
    {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onSuccess(TwitterOAuthView view, AccessToken accessToken) {
        if (mOAuthListener != null) {
            mOAuthListener.onSuccess(accessToken);
        }
        setResult(RESULT_OK, new Intent().putExtra("accessToken", accessToken));
        finish();
    }

    @Override
    public void onFailure(TwitterOAuthView view, TwitterOAuthView.Result result) {
        if (mOAuthListener != null) {
            int errorCode = LoginManager.UNKNOWN_ERROR;
            switch (result) {
                case REQUEST_TOKEN_ERROR:
                    errorCode = LoginManager.REQUEST_TOKEN_ERROR;
                    break;
                case AUTHORIZATION_ERROR:
                    errorCode = LoginManager.AUTHORIZATION_ERROR;
                    break;
                case ACCESS_TOKEN_ERROR:
                    errorCode = LoginManager.ACCESS_TOKEN_ERROR;
                    break;
            }
            mOAuthListener.onFailure(errorCode);
        }
    }

    @Override
    public void onPageFinished(TwitterOAuthView view, String url) {
        mProgressBar.setVisibility(View.GONE);
    }
}
