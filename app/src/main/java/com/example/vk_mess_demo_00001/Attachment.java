package com.example.vk_mess_demo_00001;

/**
 * Created by Каракатица on 16.10.2016.
 */

public class Attachment {

    private String type;
    private photo_mess photo;
    private photo_mess sticker;
    private video_mess video;
    private link link;
    private doc_mess doc;
    private audio_mess audio;
    public String getType() {
        return type;
    }
    public photo_mess getPhoto() {
        return photo;
    }

    public photo_mess getSticker() {
        return sticker;
    }

    public video_mess getVideo() {
        return video;
    }

    public com.example.vk_mess_demo_00001.link getLink() {
        return link;
    }

    public doc_mess getDoc() {
        return doc;
    }

    public audio_mess getAudio() {
        return audio;
    }
}
