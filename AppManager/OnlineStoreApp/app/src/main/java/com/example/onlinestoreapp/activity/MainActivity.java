package com.example.onlinestoreapp.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.onlinestoreapp.R;
import com.example.onlinestoreapp.adapter.CategoryProduct;
import com.example.onlinestoreapp.adapter.NewProductAdapter;
import com.example.onlinestoreapp.model.LoaiSanPham;
import com.example.onlinestoreapp.model.NewProduct;
import com.example.onlinestoreapp.model.User;
import com.example.onlinestoreapp.retrofit.ApiStore;
import com.example.onlinestoreapp.retrofit.RetrofitClient;
import com.example.onlinestoreapp.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nex3z.notificationbadge.NotificationBadge;

import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerView;
    NavigationView navigationView;
    ListView listView;
    DrawerLayout drawerlayout;
    CategoryProduct categoryProduct;
    List<LoaiSanPham> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiStore apiStore;
    List<NewProduct> mangspmoi;
    NewProductAdapter spAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView imgsreach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        apiStore = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiStore.class);
        Paper.init(this);
        if (Paper.book().read("user") != null){
            User user = Paper.book().read("user");
            Utils.user_current = user;
        }
        getToken();
        Screen();
        ActionBar();

        if (isConnected(this)){

            ActionViewFlipper();
            getLoaiSanPham();
            getSpmoi();
            getEventClick();
        } else{
            Toast.makeText(getApplicationContext(), "No Internet, please try again", Toast.LENGTH_LONG).show();
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (!TextUtils.isEmpty(s)){
                            compositeDisposable.add(apiStore.updateToken(Utils.user_current.getId(),s)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            messageModel -> {

                                            },
                                            throwable -> {
                                                Log.d("log", throwable.getMessage());
                                            }
                                    ));
                        }
                    }
                });
    }

    private void getEventClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent home = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(home);
                        break;
                    case 1:
                        Intent book = new Intent(getApplicationContext(), BookActivity.class);
                        book.putExtra("loai",1);
                        startActivity(book);
                        break;
                    case 2:
                        Intent school = new Intent(getApplicationContext(), BookActivity.class);
                        school.putExtra("loai",2);
                        startActivity(school);
                        break;
                    case 5:
                        Intent donhang = new Intent(getApplicationContext(), HistoryActivity.class);
                        startActivity(donhang);
                        break;
                    case 6:
                        Intent manager = new Intent(getApplicationContext(), ManagerActivity.class);
                        startActivity(manager);
                        break;
                    case 7:
                        Intent chat = new Intent(getApplicationContext(), UserActivity.class);
                        startActivity(chat);
                        break;
                    case 8:
                        Intent statistic = new Intent(getApplicationContext(), ThongKeActivity.class);
                        startActivity(statistic);
                        break;
                    case 9:
                        //Delete key user
                        Paper.book().delete("user");
                        FirebaseAuth.getInstance().signOut();
                        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(login);
                        finish();
                        break;
                    case 10:
                        Intent live = new Intent(getApplicationContext(), JoinActivity.class);
                        startActivity(live);
                        finish();
                        break;
                }
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
                                mangspmoi = sanPhamMoiModel.getResult();
                                spAdapter = new NewProductAdapter(getApplicationContext(),mangspmoi);
                                recyclerView.setAdapter(spAdapter);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Can't connect with server"+throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void getLoaiSanPham() {
         compositeDisposable.add(apiStore.getloaisp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSanPhamModel -> {
                            if (loaiSanPhamModel.isSuccess()) {
                                mangloaisp = loaiSanPhamModel.getResult();
                                mangloaisp.add(new LoaiSanPham("Manager", ""));
                                mangloaisp.add(new LoaiSanPham("Chat", ""));
                                mangloaisp.add(new LoaiSanPham("Statistic", ""));
                                mangloaisp.add(new LoaiSanPham("Logout", ""));
                                mangloaisp.add(new LoaiSanPham("Live", ""));
                                categoryProduct = new CategoryProduct(getApplicationContext(),mangloaisp);
                                listView.setAdapter(categoryProduct);
                            }
                        }
                ));
    }

    private void ActionViewFlipper() {
        List<String> advertisement = new ArrayList<>();
        advertisement.add("https://muamuaonline.com/upload/deals/files/but-muc-gel-bam-deli-g08-bl-1_694485462.jpg");
        advertisement.add("https://cbu01.alicdn.com/img/ibank/O1CN01twvT1k1OeqYv9dylQ_!!2206529751731-0-cib.jpg");
        advertisement.add("https://ngoclanvpp.vn/User_folder_upload/vppngoclan/images/thuoc-ke-deli-20cm.jpeg");
        for (int i = 0; i<advertisement.size(); i++){
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(advertisement.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private void ActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                drawerlayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void Screen() {
        imgsreach = findViewById(R.id.imgsearch);
        toolbar = findViewById(R.id.toolbarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewflipper);
        recyclerView = findViewById(R.id.recylerview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        navigationView = findViewById(R.id.navigationview);
        listView = findViewById(R.id.listviewmanhinhchinh);
        drawerlayout = findViewById(R.id.drawerlayout);
        badge = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.framegiohang);
        //Create list
        mangloaisp = new ArrayList<>();
        mangspmoi = new ArrayList<>();
        if (Utils.manggiohang == null){
            Utils.manggiohang = new ArrayList<>();
        }else{
            int totalItem = 0;
            for (int i = 0; i<Utils.manggiohang.size(); i++){
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });

        imgsreach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for (int i = 0; i<Utils.manggiohang.size(); i++){
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    private boolean isConnected (Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);//them quyen
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifi != null && wifi.isConnected()) ||(mobile != null && mobile.isConnected()) ){
            return true;
        }else{
            return false;
        }
    }

    @Override
    protected void onDestroy(){
        compositeDisposable.clear();
        super.onDestroy();
    }
}