package com.example.carlos.evernotetest;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.evernote.client.android.EvernoteSession;

public class App extends Application {


    private static final String CONSUMER_KEY = "nope";
    private static final String CONSUMER_SECRET = "nope";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;



    @Override
    public void onCreate() {
        super.onCreate();

        String consumerKey;

            consumerKey = CONSUMER_KEY;

        String consumerSecret;

            consumerSecret = CONSUMER_SECRET;


        //Set up the Evernote singleton session, use EvernoteSession.getInstance() later
        new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .setForceAuthenticationInThirdPartyApp(true)
//                .setLocale(Locale.SIMPLIFIED_CHINESE)
                .build(consumerKey, consumerSecret)
                .asSingleton();

        registerActivityLifecycleCallbacks(new LoginChecker());
    }

}
