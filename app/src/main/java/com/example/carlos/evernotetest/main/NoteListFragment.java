package com.example.carlos.evernotetest.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Notebook;
import com.example.carlos.evernotetest.R;
import com.example.carlos.evernotetest.utils.ObservableNoteList;
import com.example.carlos.evernotetest.utils.SimpleNote;
import com.example.carlos.evernotetest.dialogs.ShowNoteDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


// Main view of the app, calls the necesary functions and displays all notes, listening to its changes

public class NoteListFragment extends Fragment {

    private NoteDataAdapter adapter;

    private ObservableNoteList observableNote;

    // In this arraylist the notes will be updated by the observable, and will be the source for the recyclerview, ordering and Dialogs
    ArrayList<SimpleNote> simpleNotes = new ArrayList<>();

    NoteList noteList;

    private RecyclerView recyclerView;

    // Observes the note retrieval process, updating the adapter and the simpleNoteList with its changes (there's a lot of ways to do this, but this is mine)
    private Observer simpleNoteListChanged = new Observer() {
        @Override
        public void update(Observable o, Object newValue) {

            simpleNotes = (ArrayList<SimpleNote>)newValue;
            adapter = new NoteDataAdapter(simpleNotes, getActivity());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }
    };

    public NoteListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View zoneView = inflater.inflate(R.layout.fragment_note_list, container, false);
        prepareData();
        initViews(zoneView);
        return zoneView;
    }

    public void update() {
        prepareData();
    }


    // First evernote API "call for notes". Gets the list of notebooks, and then gets the list of notes from the first (default) notebook.
    // After getting the notelist, creates an observable that will get all the note contents
    // All this could be done with more observables and outside the fragment, but this ways was quicker to develop
    private void prepareData() {

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
                        noteList = result;

                        // After getting the noteList, a ObservableNoteList is created that will get all the notes content
                        observableNote = new ObservableNoteList();
                        observableNote.getSimpleNotesFromNoteList(result);
                        observableNote.addObserver(simpleNoteListChanged);

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

    // This defines the recyclerview to show all notes and sets its adapter, that will be updated from the observable. Also calls the "showNoteDialog" when a card is tapped
    private void initViews(View view) {

        recyclerView = view.findViewById(R.id.card_recycler_view_notes);
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
                    // Gets the position tapped and shows the showNotedialog with the corresponding simpleNote from the arraylist already downloaded
                    initNewShowNoteDialog(simpleNotes.get(position));
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

    // Shows the ShowNoteDialog from a simpleNote
    public void initNewShowNoteDialog(SimpleNote simpleNote) {

        ShowNoteDialog.newInstance(simpleNote.getTitle(), simpleNote.getContent(), simpleNote.getImageData(), simpleNote.getImageOcr()).show(getActivity().getSupportFragmentManager(), "showNoteDialog");

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    // The optionsMenu is used to show sorting methods, updating the adapter when selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.sort_date) {

            // Instead of getting the notes all over again, uses the simpleNotes observable array that should be already full
            ArrayList<SimpleNote> notes = sortByDate(simpleNotes);
            adapter = new NoteDataAdapter(notes, getActivity());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            return true;
        }
        if (id == R.id.sort_name) {

            ArrayList<SimpleNote> notes = sortByTitle(simpleNotes);
            adapter = new NoteDataAdapter(notes, getActivity());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Sorting methods, applied to simpleNote arraylist
    ArrayList<SimpleNote> sortByTitle(ArrayList<SimpleNote> notes) {

        notes.sort(new Comparator<SimpleNote>() {
            @Override
            public int compare(SimpleNote note, SimpleNote note1) {
                return note.getTitle().compareTo(note1.getTitle());
            }
        });
        return notes;

    }

    ArrayList<SimpleNote> sortByDate(ArrayList<SimpleNote> notes) {

        notes.sort(new Comparator<SimpleNote>() {
            @Override
            public int compare(SimpleNote note, SimpleNote note1) {
                return note.getCreated().compareTo(note1.getCreated());
            }
        });
        Collections.reverse(notes);
        return notes;
    }




}
