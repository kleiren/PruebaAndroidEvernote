package com.example.carlos.evernotetest.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.conn.mobile.FileData;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.example.carlos.evernotetest.R;
import com.simplify.ink.InkView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.carlos.evernotetest.utils.NoteUtils.bitmapToFile;
import static com.example.carlos.evernotetest.utils.NoteUtils.createResource;
import static com.example.carlos.evernotetest.utils.NoteUtils.makeNote;


// Dialog for creating a note. Simple text fields and a basic drawing library for creating an image.
// Once all is created, creates a resource from the image and creates a note with the correct format, sending it to the server.


public class NewNoteDialog extends DialogFragment {

    @BindView(R.id.diaNewNote_etxtTitle)
    EditText etxtTitle;
    @BindView(R.id.diaNewNote_etxtContent)
    EditText etxtContent;
    @BindView(R.id.diaNewNote_btnSend)
    Button btnSend;
    @BindView(R.id.diaNewNote_ink)
    InkView inkView;
    @BindView(R.id.diaNewNote_btnInk)
    ImageButton btnInk;


    public static NewNoteDialog newInstance() {
        NewNoteDialog f = new NewNoteDialog();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);

    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_new_note,
                container, false);

        view.setMinimumWidth(1000);
        ButterKnife.bind(this, view);


        inkView.setVisibility(View.GONE);


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
                Resource resource = null;
                if (inkView.getVisibility() == View.VISIBLE) {
                    File file = bitmapToFile(inkView.getBitmap(), getActivity());
                    resource = createResource(file, getActivity());
                }
                Note note = makeNote(etxtTitle.getText().toString(), etxtContent.getText().toString(), resource);

                noteStoreClient.createNoteAsync(note, new EvernoteCallback<Note>() {
                            @Override
                            public void onSuccess(Note result) {

                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                                onDismiss(getDialog());

                            }

                            @Override
                            public void onException(Exception exception) {
                                exception.printStackTrace();
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();

                            }
                        }
                );
            }
        });

        btnInk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inkView.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }
}
