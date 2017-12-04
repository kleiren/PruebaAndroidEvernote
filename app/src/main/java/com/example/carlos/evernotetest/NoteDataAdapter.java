package com.example.carlos.evernotetest;

/**
 * Created by Carlos on 11/05/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteClientFactory;
import com.evernote.client.android.asyncclient.EvernoteHtmlHelper;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.squareup.okhttp.Response;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NoteDataAdapter extends RecyclerView.Adapter<NoteDataAdapter.ViewHolder>  {


    List<Note> sectors;
    Context context;
    public NoteDataAdapter(NoteList sectors, Context context) {
        this.context = context;
        this.sectors = sectors.getNotes();
    }

    @Override
    public NoteDataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sector_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NoteDataAdapter.ViewHolder viewHolder, int i) {

        viewHolder.txt_title.setText(sectors.get(i).getTitle());

        EvernoteClientFactory clientFactory = EvernoteSession.getInstance().getEvernoteClientFactory();

        EvernoteHtmlHelper htmlHelper;

        htmlHelper = clientFactory.getHtmlHelperDefault();

        try {
            htmlHelper.downloadNoteAsync(sectors.get(i).getGuid(), new EvernoteCallback<Response>() {
                @Override
                public void onSuccess(Response result) {

                    try {
                        viewHolder.txt_content.setText(Html.fromHtml(result.body().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onException(Exception exception) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return sectors.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_title;
        TextView txt_content;

        public ViewHolder(View view) {
            super(view);
            txt_title = view.findViewById(R.id.txt_title);

            txt_content = view.findViewById(R.id.txt_content);
        }
    }

}