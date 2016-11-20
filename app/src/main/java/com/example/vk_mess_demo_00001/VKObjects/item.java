package com.example.vk_mess_demo_00001.VKObjects;

import com.example.vk_mess_demo_00001.VKObjects.Dialogs;

/**
 * Created by Каракатица on 29.10.2016.
 */

public class item {
    private int unread;
    private Dialogs message;

    public Dialogs getMessage() {
        return message;
    }

    public int getUnread() {
        return unread;
    }
    public item (){
        message = new Dialogs();
    }
}
