package com.example.onlinestoreapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.onlinestoreapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class JoinActivity extends AppCompatActivity {
    private String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGlrZXkiOiI3YzJmNjEwNC04NzEyLTRkMDAtODNkZC04ZjJhZmFiYjEyMjUiLCJwZXJtaXNzaW9ucyI6WyJhbGxvd19qb2luIl0sImlhdCI6MTczMDY5OTM0MSwiZXhwIjoxNzMxMzA0MTQxfQ.jvNMhxrCnT8E7fDUGtWeSXtp2JbYV6vwhD0fVR8MKQE";
    private static final int PERMISSION_REQ_ID = 22;

    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join);
        checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID);
        checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID);

        final Button btnCreate = findViewById(R.id.btnCreateMeeting);
        final Button btnJoinHost = findViewById(R.id.btnJoinHostMeeting);
        final EditText etMeetingId = findViewById(R.id.etMeetingId);

        // create meeting and join as Host
        btnCreate.setOnClickListener(v -> createMeeting(token));

        // Join as Host
        btnJoinHost.setOnClickListener(v -> {
            Intent intent = new Intent(JoinActivity.this, MeetingActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("meetingId", etMeetingId.getText().toString().trim());
            intent.putExtra("mode", "CONFERENCE");
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void createMeeting(String token) {
        // we will make an API call to VideoSDK Server to get a roomId
        AndroidNetworking.post("https://api.videosdk.live/v2/rooms")
                .addHeaders("Authorization", token) //we will pass the token in the Headers
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // response will contain `roomId`
                            final String meetingId = response.getString("roomId");

                            // starting the MeetingActivity with received roomId and our sampleToken
                            Intent intent = new Intent(JoinActivity.this, MeetingActivity.class);
                            intent.putExtra("token", token);
                            intent.putExtra("meetingId", meetingId);
                            intent.putExtra("mode", "CONFERENCE");
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(JoinActivity.this, anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
        }
    }
}