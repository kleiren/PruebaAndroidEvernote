package com.example.carlos.evernotetest;

import android.app.Dialog;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteHtmlHelper;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.conn.mobile.FileData;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Data;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
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

/**
 * Created by carlos on 4/12/17.
 */

public class NewNoteDialog extends DialogFragment {

    @BindView(R.id.diaNewNote_etxtTitle)
    EditText etxtTitle;
    @BindView(R.id.diaNewNote_etxtContent)
    EditText etxtContent;
    @BindView(R.id.diaNewNote_btnSend)
    Button btnSend;
    @BindView(R.id.diaNewNote_ink)
    InkView inkView;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_new_note,
                container, false);

        view.setMinimumWidth(1000);

        ButterKnife.bind(this,  view);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();


                File file = bitmapToFile(inkView.getBitmap());

                Resource resource = createResource(file);

                Note note = makeNote(etxtTitle.getText().toString(),etxtContent.getText().toString(), resource);

                noteStoreClient.createNoteAsync(note, new EvernoteCallback<Note>() {
                            @Override
                            public void onSuccess(Note result) {

                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();

                                dismiss();

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



        return view;
    }

    public Resource createResource(File file){
        InputStream in = null;
        FileData data = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file.getPath()));
            data = new FileData(EvernoteUtil.hash(in), new File(file.getPath()));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ResourceAttributes attributes = new ResourceAttributes();
        attributes.setFileName("image");

        // Create a new Resource
        Resource resource = new Resource();
        resource.setData(data);
        resource.setMime(getMimeType(Uri.parse(file.toURI().toString())));
        resource.setAttributes(attributes);

        return resource;

    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getActivity().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public File bitmapToFile(Bitmap bitmap){
        //create a file to write bitmap data
        File f = new File(getActivity().getCacheDir(), "temp.png");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos);
        byte[] bitmapdata = bos.toByteArray();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  f;

    }


    public Note makeNote(String noteTitle, String noteBody, Resource resource) {

        String nBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        nBody += "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">";
        nBody += "<en-note>" + noteBody + "</en-note>";

        Note note = new Note();
        note.setTitle(noteTitle);
        note.addToResources(resource);
        note.setContent(nBody);
        return note;

    }


}
