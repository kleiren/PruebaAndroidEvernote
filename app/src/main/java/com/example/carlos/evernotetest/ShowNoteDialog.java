package com.example.carlos.evernotetest;

import android.app.Dialog;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.conn.mobile.FileData;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;

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

public class ShowNoteDialog extends DialogFragment {

    @BindView(R.id.diaShNote_txtTitle)
    TextView txtTitle;
    @BindView(R.id.diaShNote_txtContent)
    TextView txtContent;
    @BindView(R.id.diaShNote_imgResource)
    ImageView imgResource;
    @BindView(R.id.diaShNote_txtResourceOcr)
    TextView txtResourceOcr;
    @BindView(R.id.diaShNote_btnOk)
    Button btnOk;

    String title;
    String content;
    Bitmap image;
    String resourceOcr;


    public static ShowNoteDialog newInstance(String title, String content, byte[] image, String resourceOcr) {
        ShowNoteDialog f = new ShowNoteDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("content", content);
        args.putByteArray("image", image);
        args.putString("resourceOcr", title);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            title = getArguments().getString("title");
            content = getArguments().getString("content");
            byte[] byteArray = getArguments().getByteArray("image");
            image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            resourceOcr = getArguments().getString("resourceOcr");
        }


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_show_note,
                container, false);
        view.setMinimumWidth(1000);
        ButterKnife.bind(this,  view);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             dismiss();
            }
        });
        txtTitle.setText(title);
        txtContent.setText(content);
        imgResource.setImageBitmap(image);
        txtResourceOcr.setText(resourceOcr);


        return view;
    }


}
