package com.example.carlos.evernotetest.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.example.carlos.evernotetest.dialogs.NewNoteDialog;
import com.example.carlos.evernotetest.R;

// Basic mainActivity, holds all the fragments of the app

public class MainActivity extends AppCompatActivity implements DialogInterface.OnDismissListener{

    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        if (!EvernoteSession.getInstance().isLoggedIn()) {
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new NoteListFragment(), "noteListFragment")

                .commit();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NewNoteDialog.newInstance().show(getSupportFragmentManager(), "newNoteDialog");
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    // When the "NewNoteDialog is dismissed, an update signal is sent to the NoteListFragment fot it to retrieve the new notes
    @Override
    public void onDismiss(DialogInterface dialogInterface) {

        NoteListFragment fragment = (NoteListFragment) getSupportFragmentManager().findFragmentByTag("noteListFragment");
        fragment.update();
    }

}
