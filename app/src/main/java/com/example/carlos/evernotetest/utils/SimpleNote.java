package com.example.carlos.evernotetest.utils;

import android.graphics.Bitmap;
import android.util.Log;

// Basic object containing only the relevant data of the notes in the format needed for ist use in this application
public class SimpleNote {
    private String title;
    private String content;
    private Bitmap image;
    private String imageOcr;

    private Long created;
    private byte[] imageData;

    SimpleNote() {
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getImageOcr() {
        return imageOcr;
    }

    void setImageOcr(String imageOcr) {
        this.imageOcr = imageOcr;
    }

    void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
    public byte[] getImageData() {
        return imageData;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

}
