package com.example.vk_mess_demo_00001.Utils;

import com.example.vk_mess_demo_00001.Utils.namesChat;
import com.example.vk_mess_demo_00001.VKObjects.AttachmentType.photo_mess;
import com.example.vk_mess_demo_00001.VKObjects.Dialogs;
import com.example.vk_mess_demo_00001.VKObjects.ItemMess;
import com.example.vk_mess_demo_00001.VKObjects.ServerResponse;
import com.example.vk_mess_demo_00001.VKObjects.User;
import com.example.vk_mess_demo_00001.VKObjects.item;
import com.example.vk_mess_demo_00001.VKObjects.video_iformation;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Каракатица on 07.10.2016.
 */

public interface VKService {

    @GET("users.get?v=5.57")
    Call<ServerResponse<ArrayList<User>>> getUser(@Query("access_token") String access_token,
                                                  @Query("user_ids") String user_ids,
                                                  @Query("fields") String fields);
    @GET("messages.getDialogs?v=5.57&preview_length=20")
    Call<ServerResponse<ItemMess<ArrayList<item>>>> getDialogs (@Query("access_token") String access_token,
                                                                @Query("count") int count,
                                                                @Query("offset") int offset);

    @GET("messages.getHistory?v=5.57")
    Call<ServerResponse<ItemMess<ArrayList<Dialogs>>>> getHistory(@Query("access_token") String access_token,
                                                                  @Query("count") int count,
                                                                  @Query("offset") int offset,
                                                                  @Query("user_id") int user_id);
    @GET("messages.send?v=5.57")
    Call<ServerResponse> sendMessage(@Query("access_token") String access_token,
                                     @Query("user_id") int user_id,
                                     @Query("message") String message,
                                     @Query("chat_id") int chat_id,
                                     @Query("peer_id") int peer_id);
    @GET("messages.getChatUsers?v=5.59")
    Call<ServerResponse<ArrayList<namesChat>>> getChatUsers(@Query("access_token") String access_token,
                                                            @Query("chat_id") int chat_id,
                                                            @Query("fields") String fields);
    @GET("account.setOnline?v=5.59")
    Call<ServerResponse> setOnline (@Query("access_token") String access_token);

    @GET("video.get?v=5.59&offset=0")
    Call<ServerResponse<ItemMess<ArrayList<video_iformation>>>> getVideos (@Query("access_token") String access_token,
                                                                           @Query("videos") String videos);
    @GET("friends.get?&v=5.60&order=hints&name_case=nom")
    Call<ServerResponse<ItemMess<ArrayList<User>>>> getFriends (@Query("access_token") String access_token,
                                                                @Query("user_id") int user_id,
                                                                @Query("fields") String fields);
    @GET ("photos.get?&v=5.60&count=1000&album_id=profile&rev=1")
    Call<ServerResponse<ItemMess<ArrayList<photo_mess>>>> getPhotos (@Query("access_token") String access_token,
                                                                        @Query("owner_id") int owner_id);
}
