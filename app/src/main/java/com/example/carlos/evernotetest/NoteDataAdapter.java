package com.example.carlos.evernotetest;

/**
 * Created by Carlos on 11/05/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;


import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NoteDataAdapter extends RecyclerView.Adapter<NoteDataAdapter.ViewHolder>{


    List<Note> notes;
    Context context;
    public NoteDataAdapter(NoteList notes, Context context) {
        this.context = context;
        this.notes = notes.getNotes();
    }

    @Override
    public NoteDataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sector_row, viewGroup, false);
        return new ViewHolder(view);
    }




    @Override
    public void onBindViewHolder(final NoteDataAdapter.ViewHolder viewHolder, int i) {
        
        viewHolder.txt_title.setText(notes.get(i).getTitle());

        final EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

        noteStoreClient.getNoteAsync(notes.get(i).getGuid(), true, true, true, false, new EvernoteCallback<Note>() {
            @Override
            public void onSuccess(Note result) {

                viewHolder.txt_content.setText(Html.fromHtml(result.getContent()));

                if (result.getResources() != null) {


                    Bitmap bitmap = BitmapFactory.decodeByteArray(result.getResources().get(0).getData().getBody(), 0, result.getResources().get(0).getData().getBody().length);
                    viewHolder.img_resource.setImageBitmap(bitmap);


                    ResourceOcrXmlParser resourceOcrXmlParser = new ResourceOcrXmlParser();

                    if (result.getResources().get(0).getRecognition() != null) {
                        InputStream in = new ByteArrayInputStream(result.getResources().get(0).getRecognition().getBody());

                        List<ResourceOcrXmlParser.Item> a = null;
                        try {
                            a = resourceOcrXmlParser.parse(in);
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        StringBuilder sb = new StringBuilder();
                        for (ResourceOcrXmlParser.Item s : a) {
                            sb.append(s.result.get(0));
                            sb.append("\t");
                        }
                        viewHolder.txt_resourceRec.setText(new String(sb));
                    }

                }
            }

            @Override
            public void onException(Exception exception) {

            }
        });
    }



    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_title)
        TextView txt_title;
        @BindView(R.id.txt_content)
        TextView txt_content;
        @BindView(R.id.web_content)
        WebView web_content;
        @BindView(R.id.img_resource)
        ImageView img_resource;
        @BindView(R.id.txt_resourceRec)
        TextView txt_resourceRec;

        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

        }
    }

}