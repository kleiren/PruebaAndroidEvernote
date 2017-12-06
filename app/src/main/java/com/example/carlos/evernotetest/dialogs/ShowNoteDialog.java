package com.example.carlos.evernotetest.dialogs;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carlos.evernotetest.R;

import butterknife.BindView;
import butterknife.ButterKnife;



// Dialog showing all the required information of the note tapped.

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
    @BindView(R.id.diaShNote_imgLayout)
    View imgLayout;

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
        args.putString("resourceOcr", resourceOcr);
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

            if (byteArray != null) {
                image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                resourceOcr = getArguments().getString("resourceOcr");
            }
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

        if (image != null){
            imgLayout.setVisibility(View.VISIBLE);
            imgResource.setImageBitmap(image);
            txtResourceOcr.setText(resourceOcr);
        }else  {
            imgLayout.setVisibility(View.GONE);
        }

        return view;
    }


}
