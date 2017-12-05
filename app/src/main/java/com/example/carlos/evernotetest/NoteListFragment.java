package com.example.carlos.evernotetest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;




public class NoteListFragment extends Fragment {

    private NoteDataAdapter adapter;
    private Activity parentActivity;

    NoteList noteList;

    public NoteListFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View zoneView = inflater.inflate(R.layout.fragment_note_list, container, false);
        zoneView.findViewById(R.id.noSectorsImage).setVisibility(View.GONE);
        prepareData(zoneView);

        return zoneView;
    }


    private void prepareData(final View sectorView) {

        final EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();


        noteStoreClient.listNotebooksAsync(new EvernoteCallback<List<Notebook>>() {
            @Override
            public void onSuccess(List<Notebook> result) {
                List<String> namesList = new ArrayList<>(result.size());
                for (Notebook notebook : result) {
                    namesList.add(notebook.getName());
                }

                NoteFilter filter = new NoteFilter();
                filter.setNotebookGuid(result.get(0).getGuid());

                noteStoreClient.findNotesAsync(filter, 0, 10, new EvernoteCallback<NoteList>() {
                    @Override
                    public void onSuccess(NoteList result) {

                        Toast.makeText(getActivity(), ""+ result.getNotes().size(), Toast.LENGTH_SHORT).show();

                        noteList = result;
                        adapter = new NoteDataAdapter(noteList, getActivity());
                        initViews(sectorView);


                    }

                    @Override
                    public void onException(Exception exception) {
                        Log.e("test", "Error retrieving notebooks", exception);
                    }
                });
            }

            @Override
            public void onException(Exception exception) {
                Log.e("test", "Error retrieving notebooks", exception);
            }
        });


    }


    private void initViews(View view) {

        RecyclerView recyclerView = view.findViewById(R.id.card_recycler_view_zones);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);

                    initNewShowNoteDialog(noteList.getNotes().get(position));

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    public void initNewShowNoteDialog(Note note){

        final SimpleNote simpleNote = new SimpleNote();
        simpleNote.setTitle(note.getTitle());

        final EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

        noteStoreClient.getNoteAsync(note.getGuid(), true, true, true, false, new EvernoteCallback<Note>() {
            @Override
            public void onSuccess(Note result) {

                simpleNote.setContent(String.valueOf(Html.fromHtml(result.getContent())));

                if (result.getResources() != null) {

                    simpleNote.setImageData(result.getResources().get(0).getData().getBody());

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
                        simpleNote.setImageOcr(new String(sb));
                    }
                }
                ShowNoteDialog.newInstance(simpleNote.getTitle(), simpleNote.getContent(), simpleNote.getImageData(), simpleNote.getImageOcr()).show(getActivity().getSupportFragmentManager(), "showNoteDialog");
            }

            @Override
            public void onException(Exception exception) {

            }
        });
    }


}
