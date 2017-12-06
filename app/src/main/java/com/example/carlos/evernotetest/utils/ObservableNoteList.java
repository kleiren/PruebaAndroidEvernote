package com.example.carlos.evernotetest.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.view.View;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

// From the noteList retrieved from the Evernote API, we have to get all the notes content and put it into a simpler object list, more useful for this concrete app (ArrayList<SimpleNote>)
// To get all the notes content, instead of doing that for every note at the same time and making the user wait, im using an obserbale to update the list as the data is retrieved
// For each note, when the data is retrieved, only the relevant data is stored in a simplenote and added to a simplenotelist, sent back to the observers
// (there's a lot of ways to do this, but this is mine)

public class ObservableNoteList extends Observable {

    private ArrayList<SimpleNote> simpleNotes = new ArrayList<>();

    public void getSimpleNotesFromNoteList(NoteList noteList) {

        for (Note note : noteList.getNotes()) {
            getSimpleNoteFromNote(note);
        }

    }

    public void getSimpleNoteFromNote(Note note) {

        final SimpleNote simpleNote = new SimpleNote();

        simpleNote.setTitle(note.getTitle());

        final EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

        noteStoreClient.getNoteAsync(note.getGuid(), true, true, true, false, new EvernoteCallback<Note>() {
            @Override
            public void onSuccess(Note result) {

                simpleNote.setContent(String.valueOf(Html.fromHtml(result.getContent())));
                simpleNote.setCreated(result.getCreated());

                if (result.getResources() != null) {

                    // Gets the data from the image and also converts it to a bitmap
                    simpleNote.setImageData(result.getResources().get(0).getData().getBody());
                    Bitmap bitmap = BitmapFactory.decodeByteArray(result.getResources().get(0).getData().getBody(), 0, result.getResources().get(0).getData().getBody().length);
                    simpleNote.setImage(bitmap);


                    // if there is any ocr result, parses it
                    if (result.getResources().get(0).getRecognition() != null) {
                        String imageOcrText = parseTextFromOcrResult(result.getResources().get(0).getRecognition().getBody());
                        if (imageOcrText.equals("")) simpleNote.setImageOcr("No text detected");
                        else simpleNote.setImageOcr(imageOcrText);
                    } else {
                        simpleNote.setImageOcr("Image OCR not ready");
                    }
                }

                simpleNotes.add(simpleNote);

                // Notifies the observes that a new simplenote is available
                setChanged();
                notifyObservers(simpleNotes);

            }

            @Override
            public void onException(Exception exception) {

            }
        });
    }


    // Calls the ocr parser with the data from the note resource
    private String parseTextFromOcrResult(byte[] data){
        ImageOcrXmlParser imageOcrXmlParser = new ImageOcrXmlParser();

        InputStream in = new ByteArrayInputStream(data);

        List<ImageOcrXmlParser.Item> a = null;
        try {
            a = imageOcrXmlParser.parse(in);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        for (ImageOcrXmlParser.Item s : a) {
            sb.append(s.result.get(0));
            sb.append("\t");
        }
         return new String(sb);
    }
}
