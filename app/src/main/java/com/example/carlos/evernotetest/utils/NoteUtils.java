package com.example.carlos.evernotetest.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.evernote.client.android.EvernoteUtil;
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


// Methods used in the creation of a note, from creating the note itself to creating the resources from an image

public class NoteUtils {

    public static Resource createResource(File file, Activity activity) {
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
        Resource resource = new Resource();
        resource.setData(data);
        resource.setMime(getMimeType(Uri.parse(file.toURI().toString()), activity));
        resource.setAttributes(attributes);

        return resource;

    }

    public static String getMimeType(Uri uri, Activity activity) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = activity.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static File bitmapToFile(Bitmap bitmap, Activity activity) {
        File f = new File(activity.getCacheDir(), "temp.png");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
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
        return f;

    }


    public static Note makeNote(String noteTitle, String noteBody, Resource resource) {

        String nBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        nBody += "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">";
        nBody += "<en-note>" + noteBody + "</en-note>";

        Note note = new Note();
        note.setTitle(noteTitle);

        if (resource !=null) {
            note.addToResources(resource);
        }
        note.setContent(nBody);
        return note;

    }
}
