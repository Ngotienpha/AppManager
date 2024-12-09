package com.example.onlinestoreapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.onlinestoreapp.R;
import com.example.onlinestoreapp.activity.fragment.SpeakerFragment;
import com.example.onlinestoreapp.retrofit.ApiStore;
import com.example.onlinestoreapp.retrofit.RetrofitClient;
import com.example.onlinestoreapp.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import live.videosdk.rtc.android.Meeting;
import live.videosdk.rtc.android.VideoSDK;
import live.videosdk.rtc.android.listeners.MeetingEventListener;

public class MeetingActivity extends AppCompatActivity {
    private Meeting meeting;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiStore apiStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meeting);
        apiStore = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiStore.class);
        final String meetingId = getIntent().getStringExtra("meetingId");
        String token = getIntent().getStringExtra("token");
        String mode = getIntent().getStringExtra("mode");
        String localParticipantName = "Online Store App";
        boolean streamEnable = mode.equals("CONFERENCE");
        postDataToMeeting(meetingId, token);

        // initialize VideoSDK
        VideoSDK.initialize(getApplicationContext());

        // Configuration VideoSDK with Token
        VideoSDK.config(token);

        // Initialize VideoSDK Meeting
        meeting = VideoSDK.initMeeting(
                MeetingActivity.this, meetingId, localParticipantName,
                streamEnable, streamEnable, null, mode, false, null, null);

        // join Meeting
        meeting.join();
        // if mode is CONFERENCE than replace mainLayout with SpeakerFragment otherwise with ViewerFragment
        meeting.addEventListener(new MeetingEventListener() {
            @Override
            public void onMeetingJoined() {
                if (meeting != null) {
                    if (mode.equals("CONFERENCE")) {
                        //pin the local participant
                        meeting.getLocalParticipant().pin("SHARE_AND_CAM");
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main, new SpeakerFragment(), "MainFragment")
                                .commit();
                    }
                }
            }
        });
    }

    private void postDataToMeeting(String meetingId, String token) {
        compositeDisposable.add(apiStore.postMeeting(meetingId, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            Log.d("loggg", "post ok");
                        },
                        throwable -> {
                            Log.d("loggg", throwable.getMessage());
                        }
                ));
    }

    public Meeting getMeeting() {
        return meeting;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}