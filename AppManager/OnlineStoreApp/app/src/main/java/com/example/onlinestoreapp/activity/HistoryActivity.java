package com.example.onlinestoreapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinestoreapp.R;
import com.example.onlinestoreapp.adapter.DonHangAdapter;
import com.example.onlinestoreapp.model.DonHang;
import com.example.onlinestoreapp.model.EventBus.DonHangEvent;
import com.example.onlinestoreapp.model.NotiSendData;
import com.example.onlinestoreapp.retrofit.ApiPushNotification;
import com.example.onlinestoreapp.retrofit.ApiStore;
import com.example.onlinestoreapp.retrofit.RetrofitClient;
import com.example.onlinestoreapp.retrofit.RetrofitClientNoti;
import com.example.onlinestoreapp.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HistoryActivity extends AppCompatActivity {
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiStore apiStore;
    RecyclerView redonhang;
    Toolbar toolbar;
    DonHang donHang;
    int tinhtrang = 0;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        initView();
        initToolbar();
        getOrder();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void getOrder() {
        compositeDisposable.add(apiStore.xemDonHang(Utils.user_current.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        donHangModel -> {
                            // Kiểm tra nếu dữ liệu trả về không null
                            if (donHangModel != null && donHangModel.getResult() != null) {
                                DonHangAdapter adapter = new DonHangAdapter(getApplicationContext(), donHangModel.getResult());
                                redonhang.setAdapter(adapter);
                            } else {
                                Log.e("getOrder", "Dữ liệu trả về là null hoặc không có kết quả!");
                            }
                        },
                        throwable -> {
                            Log.e("getOrder", throwable.getMessage());
                            Toast.makeText(getApplicationContext(), "Đã xảy ra lỗi khi tải đơn hàng", Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        apiStore = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiStore.class);
        redonhang = findViewById(R.id.recycleview_donhang);
        toolbar = findViewById(R.id.toobar1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        redonhang.setLayoutManager(layoutManager);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void evenDonHang(DonHangEvent event){
        if (event != null){
            donHang = event.getDonHang();
            showCustomDialog();
        }
    }

    private void showCustomDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_donhang, null);
        Spinner spinner = view.findViewById(R.id.spinner_dialog);
        AppCompatButton btndongy = view.findViewById(R.id.dongy_dialog);
        List<String> list = new ArrayList<>();
        list.add("Order is being processed");
        list.add("Order has been processed successfully");
        list.add("Order has been delivered to the shipping unit");
        list.add("Order has been delivered");
        list.add("Order Cancelled");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,list);
        spinner.setAdapter(adapter);
        spinner.setSelection(donHang.getTrangthai());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                tinhtrang = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btndongy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capNhatDonHang();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void capNhatDonHang() {
        compositeDisposable.add(apiStore.updateOrder(donHang.getId(),tinhtrang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            getOrder();
                            dialog.dismiss();
                            pushNotiUser();
                        },
                        throwable -> {

                        }
                ));
    }

    private void pushNotiUser() {
        //gettoken
        compositeDisposable.add(apiStore.getToken(0,donHang.getIduser())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()){
                                for (int i=0; i<userModel.getResult().size(); i++){
                                    String token = "dnrdOi2eQLOIGvJfujJp-g:APA91bFC0838318wyU21fqlKc2s_MRy0Kh6mIIEuXJRZcKmmZYwOSMlNUIjd5HazJhl2ZruFbrsAbWvdZSu2yKb3DBpJ59eUXALqcvsW7hSkSIA-dMBLSry_vJ9bQxIv4-ekCpZ0jS5J";
                                    Map<String, String> data = new HashMap<>();
                                    data.put("title", "Notice");
                                    data.put("body", trangthaiDon(tinhtrang));
                                    NotiSendData notiSendData = new NotiSendData(userModel.getResult().get(i).getToken(), data);
                                    ApiPushNotification apiPushNotification = RetrofitClientNoti.getInstance().create(ApiPushNotification.class);
                                    compositeDisposable.add(apiPushNotification.sendNotification(notiSendData)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    notiResponse -> {

                                                    },
                                                    throwable -> {
                                                        Log.d("logg", throwable.getMessage());
                                                    }
                                            ));
                                }
                            }
                        },
                        throwable -> {
                            Log.d("loggg", throwable.getMessage());
                        }
                ));
    }

    private String trangthaiDon(int status){
        String result= "";
        switch (status){
            case 0:
                result = "Order is being processed";
                break;
            case 1:
                result = "Order has been processed successfully";
                break;
            case 2:
                result = "Order has been delivered to the shipping unit";
                break;
            case 3:
                result = "Order has been delivered";
                break;
            case 4:
                result = "Order Cancelled";
                break;
        }
        return result;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}