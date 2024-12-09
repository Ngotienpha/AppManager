package com.example.onlinestoreapp.retrofit;

import com.example.onlinestoreapp.model.NotiResponse;
import com.example.onlinestoreapp.model.NotiSendData;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiPushNotification {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAzyvr:APA91bHDixvlV5WC7rlYGw6-ImRM-DJbr1PWw0TYTtnyxJrLOahstNcYqpcDUSJZsJAm0YahVH85X5DHp54GbyFROHyZzxkCrC_MIH5ZPVlY6SyDcUNeXTSQs9F4Zxtql8S1yenstP2i"
            }
    )
    @POST("fcm/send")
    Observable<NotiResponse> sendNotification(@Body NotiSendData data);
}
