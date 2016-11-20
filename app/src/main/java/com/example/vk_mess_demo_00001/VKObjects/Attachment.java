package com.example.vk_mess_demo_00001.VKObjects;

import com.example.vk_mess_demo_00001.VKObjects.AttachmentType.audio_mess;
import com.example.vk_mess_demo_00001.VKObjects.AttachmentType.doc_mess;
import com.example.vk_mess_demo_00001.VKObjects.AttachmentType.photo_mess;
import com.example.vk_mess_demo_00001.VKObjects.AttachmentType.video_mess;

public class Attachment {

    private String type;
    private photo_mess photo;
    private photo_mess sticker;
    private video_mess video;
    private com.example.vk_mess_demo_00001.Utils.link link;
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

    public com.example.vk_mess_demo_00001.Utils.link getLink() {
        return link;
    }

    public doc_mess getDoc() {
        return doc;
    }

    public audio_mess getAudio() {
        return audio;
    }
}
