package com.example.carlos.evernotetest.main;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carlos.evernotetest.R;
import com.example.carlos.evernotetest.utils.SimpleNote;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// Simple RecyclerView Data adapter, gets a list of simpleNotes and fills its content. If the notes dont have image, it will hide the unnecessary views
public class NoteDataAdapter extends RecyclerView.Adapter<NoteDataAdapter.ViewHolder>{


    private List<SimpleNote> notes;
    private Context context;
    NoteDataAdapter(List<SimpleNote> notes, Context context) {
        this.context = context;
        this.notes = notes;
    }

    @Override
    public NoteDataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_row, viewGroup, false);
        return new ViewHolder(view);
    }

    private void cleanView(final NoteDataAdapter.ViewHolder viewHolder){
        viewHolder.txtTitle.setText("");
        viewHolder.txtImageOcr.setText("");
        viewHolder.txtContent.setText("");
    }


    @Override
    public void onBindViewHolder(final NoteDataAdapter.ViewHolder viewHolder, int i) {
        cleanView(viewHolder);
        viewHolder.txtTitle.setText(notes.get(i).getTitle());
        viewHolder.txtContent.setText(notes.get(i).getContent());

        if (notes.get(i).getImage() != null){
            viewHolder.imgLayout.setVisibility(View.VISIBLE);
            viewHolder.imgResource.setImageBitmap(notes.get(i).getImage());
            viewHolder.txtImageOcr.setText(notes.get(i).getImageOcr());
        }else  {
            viewHolder.imgLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.noteRow_txtTitle)
        TextView txtTitle;
        @BindView(R.id.noteRow_txtContent)
        TextView txtContent;
        @BindView(R.id.noteRow_imgResource)
        ImageView imgResource;
        @BindView(R.id.noteRow_txtImageOcr)
        TextView txtImageOcr;
        @BindView(R.id.noteRow_imgLayout)
        View imgLayout;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}