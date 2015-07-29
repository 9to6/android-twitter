/*
 * author: Teukgeon Kwon
 * email: ktg@weeppp.com
 * blog: weeppp.com
 *
 * Copyright (c) 2015. all rights reserved.
 */

package com.weeppp.androidtwitter;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by ktg on 2015-07-27.
 */
public final class AndroidTwitter {
    public static final String TAG = "androidtwitter";
    public static final int REQCODE = 1000;

    private String mTwitterConsumerKey;
    private String mTwitterConsumerSecret;
    private AccessToken mAccessToken;

    private StatusUpdateListener mStatusUpdateListener;

    private static boolean mDebugEnabled;

    private AndroidTwitter() {
        mDebugEnabled = false;
    }

    public void startLogin(Activity activity) {
        LoginManager.getInstance().startLogin(activity, mTwitterConsumerKey, mTwitterConsumerSecret);
    }

    public void twitter(final StatusUpdate statusUpdate) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(mTwitterConsumerKey);
        configurationBuilder.setOAuthConsumerSecret(mTwitterConsumerSecret);
        configurationBuilder.setOAuthAccessToken(mAccessToken.getToken());
        configurationBuilder.setOAuthAccessTokenSecret(mAccessToken.getTokenSecret());
        final Configuration configuration = configurationBuilder.build();

        new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... params) {
                Object ret;
                try {
                    Twitter twitter = new TwitterFactory(configuration).getInstance();
                    twitter4j.Status status = twitter.updateStatus(statusUpdate);
                    ret = status;
                } catch (TwitterException e) {
                    ret = e;
                    e.printStackTrace();
                }
                return ret;
            }

            @Override
            protected void onPostExecute(Object result) {
                if (result instanceof twitter4j.Status) {
                    twitter4j.Status status = (twitter4j.Status) result;
                    if (mStatusUpdateListener != null) mStatusUpdateListener.onSuccess(status);
                }
                else if (result instanceof TwitterException) {
                    TwitterException e = (TwitterException) result;
                    if (mStatusUpdateListener != null) mStatusUpdateListener.onFailure(e);
                }
            }
        }.execute();
    }

    public boolean hasAccessToken() {
        return mAccessToken != null;
    }

    public void setOAuthListener(final OAuthListener listener) {
        LoginManager.getInstance().registerListener(new LoginManager.OAuthListener() {
            @Override
            public void onSuccess(AccessToken accessToken) {
                mAccessToken = accessToken;
                Log.d(TAG, "onSuccess in AndroidTwitter LoginManager.OAuthListener, "+listener);
                if (listener != null) listener.onSuccess(accessToken);
            }

            @Override
            public void onFailure(int errorCode) {
                if (listener != null) listener.onFailure(errorCode);
            }
        });
    }

    public void setStatusUpdateListener(final StatusUpdateListener listener) {
        mStatusUpdateListener = listener;
    }

    public String getTwitterConsumerKey() {
        return mTwitterConsumerKey;
    }

    public void setTwitterConsumerKey(String twitterConsumerKey) {
        mTwitterConsumerKey = twitterConsumerKey;
    }

    public String getTwitterConsumerSecret() {
        return mTwitterConsumerSecret;
    }

    public void setTwitterConsumerSecret(String twitterConsumerSecret) {
        mTwitterConsumerSecret = twitterConsumerSecret;
    }

    public void setAccessToken(AccessToken accessToken) {
        mAccessToken = accessToken;
    }

    public static void setDebugEnabled(boolean enabled) {
        mDebugEnabled = enabled;
    }

    public static boolean isDebugEnabled() { return mDebugEnabled; }

//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK) {
//            AccessToken accessToken = (AccessToken) data.getSerializableExtra("accessToken");
//            mAccessToken = accessToken;
//        }
//    }

    public interface StatusUpdateListener {
        void onSuccess(twitter4j.Status status);

        void onFailure(TwitterException exception);
    }

    public interface OAuthListener {
        void onSuccess(AccessToken accessToken);

        void onFailure(int errorCode);
    }

    public static class Builder {
        private String mTwitterConsumerKey;
        private String mTwitterConsumerSecret;
        private String mTwitterAccessToken;
        private String mTwitterAccessTokenSecret;

        public Builder() {}

        public Builder setConsumerKey(String consumerKey) {
            mTwitterConsumerKey = consumerKey;
            return this;
        }

        public Builder setConsumerSecret(String consumerSecret) {
            mTwitterConsumerSecret = consumerSecret;
            return this;
        }

        public Builder setTwitterAccessToken(String twitterAccessToken) {
            mTwitterAccessToken = twitterAccessToken;
            return this;
        }

        public Builder setTwitterAccessTokenSecret(String twitterAccessTokenSecret) {
            mTwitterAccessTokenSecret = twitterAccessTokenSecret;
            return this;
        }

        public AndroidTwitter build() throws Exception {
            AndroidTwitter at = new AndroidTwitter();
            if (TextUtils.isEmpty(mTwitterConsumerKey) || TextUtils.isEmpty(mTwitterConsumerSecret)) {
                throw new Exception("Must input twitter consumer key and consumer secret");
            }
            at.setTwitterConsumerKey(mTwitterConsumerKey);
            at.setTwitterConsumerSecret(mTwitterConsumerSecret);
            if (!TextUtils.isEmpty(mTwitterAccessToken) && !TextUtils.isEmpty(mTwitterAccessTokenSecret)) {
                at.setAccessToken(new AccessToken(mTwitterAccessToken, mTwitterAccessTokenSecret));
            }
            return at;
        }

    }
}
