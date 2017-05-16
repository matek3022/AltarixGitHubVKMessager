package com.example.vk_mess_demo_00001.vkobjects;

import com.example.vk_mess_demo_00001.utils.Link;
import com.example.vk_mess_demo_00001.vkobjects.attachmenttype.AudioMess;
import com.example.vk_mess_demo_00001.vkobjects.attachmenttype.DocMess;
import com.example.vk_mess_demo_00001.vkobjects.attachmenttype.PhotoMess;
import com.example.vk_mess_demo_00001.vkobjects.attachmenttype.VideoMess;

public class Attachment {

    private String type;
    private PhotoMess photo;
    private PhotoMess sticker;
    private PhotoMess gift;
    private VideoMess video;
    private Link link;
    private DocMess doc;
    private AudioMess audio;
    public String getType() {
        return type;
    }

    public PhotoMess getPhoto() {
        return photo;
    }

    public PhotoMess getGift() {
        return gift;
    }

    public PhotoMess getSticker() {
        return sticker;
    }

    public VideoMess getVideo() {
        return video;
    }

    public Link getLink() {
        return link;
    }

    public DocMess getDoc() {
        return doc;
    }

    public AudioMess getAudio() {
        return audio;
    }
}
