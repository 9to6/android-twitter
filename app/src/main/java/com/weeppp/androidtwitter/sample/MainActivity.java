package com.weeppp.androidtwitter.sample;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.weeppp.androidtwitter.AndroidTwitter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;


public class MainActivity extends AppCompatActivity {
    private final String TAG = "AndroidTwitterSample";

    private final String CONSUMER_KEY = "your consumer key";
    private final String CONSUMER_SECRET = "your consumer secret";

    private final String PROPERTY_TOKEN = "twitter_token";
    private final String PROPERTY_TOKEN_SECRET = "twitter_token_secret";
    private SharedPreferences mSharedPreferences;

    @InjectView(R.id.twitter_oauth_button) FancyButton mLoginButton;
    @InjectView(R.id.textview) TextView mAccessTextView;
    @InjectView(R.id.edittext) EditText mEditText;
    @InjectView(R.id.submit_button) FancyButton mSubmitButton;
    @InjectView(R.id.twitter_logout_button) FancyButton mLogoutButton;

    private AndroidTwitter mAndroidTwitter;
    private MyOAuthListener mOAuthListener;
    private MyStatausUpdateListener mStatusUpdateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        initialize();
    }

    private void initialize()
    {
        try {
            AndroidTwitter.Builder builder = new AndroidTwitter.Builder()
                    .setConsumerKey(CONSUMER_KEY)
                    .setConsumerSecret(CONSUMER_SECRET);
            AccessToken accessToken = getAccessToken();
            if (accessToken != null) {
                builder.setTwitterAccessToken(accessToken.getToken())
                        .setTwitterAccessTokenSecret(accessToken.getTokenSecret());
            }
            mAndroidTwitter = builder.build();

            mOAuthListener = new MyOAuthListener();
            mStatusUpdateListener = new MyStatausUpdateListener();

            mAndroidTwitter.setOAuthListener(mOAuthListener);
            mAndroidTwitter.setStatusUpdateListener(mStatusUpdateListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AccessToken getAccessToken()
    {
        String token = mSharedPreferences.getString(PROPERTY_TOKEN, null);
        String tokenSecret = mSharedPreferences.getString(PROPERTY_TOKEN_SECRET, null);
        if (TextUtils.isEmpty(token) || TextUtils.isEmpty(tokenSecret)) return null;
        return new AccessToken(token, tokenSecret);
    }

    private void saveAccessToken(AccessToken accessToken)
    {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PROPERTY_TOKEN, accessToken.getToken());
        editor.commit();

        editor = mSharedPreferences.edit();
        editor.putString(PROPERTY_TOKEN_SECRET, accessToken.getTokenSecret());
        editor.commit();
    }

    private void deleteAccessToken()
    {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(PROPERTY_TOKEN);
        editor.commit();

        editor = mSharedPreferences.edit();
        editor.remove(PROPERTY_TOKEN_SECRET);
        editor.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.twitter_oauth_button)
    public void onClickOAuthButton(View view)
    {
        if (mAndroidTwitter != null) mAndroidTwitter.startLogin(this);
    }

    @OnClick(R.id.submit_button)
    public void onClickSubmitButton(View view)
    {
        if (mAndroidTwitter != null) {
            String twit = "hello twitter!";
            if (mEditText.getText().length() > 0) {
                twit = mEditText.getText().toString();
            }
            StatusUpdate statusUpdate = new StatusUpdate(twit);
            mAndroidTwitter.twitter(statusUpdate);
        }
    }

    @OnClick(R.id.twitter_logout_button)
    public void onClickLogoutButton(View view)
    {
        deleteAccessToken();
        initialize();
        mAccessTextView.setText("");
        Toast.makeText(MainActivity.this, "deleted access token", Toast.LENGTH_SHORT).show();
    }

    class MyOAuthListener implements AndroidTwitter.OAuthListener
    {
        @Override
        public void onSuccess(AccessToken accessToken) {
            saveAccessToken(accessToken);
            Log.d(TAG, "onSuccess: " + accessToken);
            mAccessTextView.setText(accessToken.getToken());
            Toast.makeText(MainActivity.this, "success oauth", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(int errorCode) {
            mAccessTextView.setText("");
        }
    }

    class MyStatausUpdateListener implements AndroidTwitter.StatusUpdateListener
    {

        @Override
        public void onSuccess(Status status) {
            Toast.makeText(MainActivity.this, "post id:"+status.getId(), Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onFailure(TwitterException exception) {
            Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
