package com.example.onlinestoreapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinestoreapp.R;
import com.example.onlinestoreapp.adapter.NewProductAdapter;
import com.example.onlinestoreapp.model.EventBus.SuaVaXoaEvent;
import com.example.onlinestoreapp.model.NewProduct;
import com.example.onlinestoreapp.retrofit.ApiStore;
import com.example.onlinestoreapp.retrofit.RetrofitClient;
import com.example.onlinestoreapp.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import soup.neumorphism.NeumorphCardView;

public class ManagerActivity extends AppCompatActivity {
    ImageView img_them;
    RecyclerView recyclerView;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiStore apiStore;
    List<NewProduct> list;
    NewProductAdapter adapter;
    NewProduct sanPhamSuaXoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager);
        apiStore = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiStore.class);
        initView();
        initControl();
        getSpmoi();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initControl() {
        img_them.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddProductActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getSpmoi() {
        compositeDisposable.add(apiStore.getSpmoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if (sanPhamMoiModel.isSuccess()) {
                                list = sanPhamMoiModel.getResult();
                                adapter = new NewProductAdapter(getApplicationContext(), list);
                                recyclerView.setAdapter(adapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Can't connect with server"+throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void initView() {
        img_them = findViewById(R.id.img_them);
        recyclerView = findViewById(R.id.recycleview_ql);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("Edit")){
            suaSanPham();
        } else if (item.getTitle().equals("Delete")) {
            xoaSanPham();
        }
        return super.onContextItemSelected(item);
    }

    private void xoaSanPham() {
        compositeDisposable.add(apiStore.xoaSanPham(sanPhamSuaXoa.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if (messageModel.isSuccess()){
                                Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_LONG).show();
                                getSpmoi();
                            }else {
                                Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> {
                            Log.d("log", throwable.getMessage());
                        }
                ));
    }

    private void suaSanPham() {
        Intent intent = new Intent(getApplicationContext(), AddProductActivity.class);
        intent.putExtra("Edit", sanPhamSuaXoa);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void evenSuaXoa(SuaVaXoaEvent event){
        if (event != null){
            sanPhamSuaXoa = event.getNewProduct();
        }
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