package com.example.carlos.evernotetest.root;

import android.app.Application;

import com.evernote.client.android.EvernoteSession;
import com.example.carlos.evernotetest.login.LoginChecker;


// Needed for getting evernote API to work, this registers the app and creates the API singletons for data retrieval

public class App extends Application {

    private static final String CONSUMER_KEY = "nope"; // This should be a valid one if not, the app will crash!
    private static final String CONSUMER_SECRET = "nope"; // This should be a valid one
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;


    @Override
    public void onCreate() {
        super.onCreate();

        String consumerKey;

            consumerKey = CONSUMER_KEY;

        String consumerSecret;

            consumerSecret = CONSUMER_SECRET;

        new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .setForceAuthenticationInThirdPartyApp(true)
                .build(consumerKey, consumerSecret)
                .asSingleton();

        registerActivityLifecycleCallbacks(new LoginChecker());
    }

}
