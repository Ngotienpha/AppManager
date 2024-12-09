package com.example.onlinestoreapp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinestoreapp.R;
import com.example.onlinestoreapp.adapter.BookAdapter;
import com.example.onlinestoreapp.model.NewProduct;
import com.example.onlinestoreapp.retrofit.ApiStore;
import com.example.onlinestoreapp.retrofit.RetrofitClient;
import com.example.onlinestoreapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BookActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ApiStore apiStore;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    int page =1;
    int category;
    BookAdapter adapterBook;
    List<NewProduct> newProductList;
    LinearLayoutManager linearLayoutManager;
    Handler handler = new Handler();
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book);
        apiStore = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiStore.class);
        category = getIntent().getIntExtra("loai",1);
        Screen();
        ActionToolBar();
        getData(page);
        addEventLoad();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addEventLoad() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLoading == false){
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == newProductList.size()-1){
                        isLoading = true;
                        loadMore();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void loadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //add null
                newProductList.add(null);
                adapterBook.notifyItemInserted(newProductList.size()-1);
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //remove null
                newProductList.remove(newProductList.size()-1);
                adapterBook.notifyItemRemoved(newProductList.size());
                page = page+1;
                getData(page);
                adapterBook.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);
    }

    private void getData(int page) {
        compositeDisposable.add(apiStore.getSanPham(page, category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        newProductModel -> {
                            if(newProductModel.isSuccess()){
                               if (adapterBook == null){
                                   newProductList = newProductModel.getResult();
                                   adapterBook = new BookAdapter(getApplicationContext(), newProductList);
                                   recyclerView.setAdapter(adapterBook);
                               }else{
                                   int vitri = newProductList.size()-1;
                                   int soluongadd = newProductModel.getResult().size();
                                   for (int i=0; i<soluongadd; i++){
                                       newProductList.add(newProductModel.getResult().get(i));
                                   }
                                   adapterBook.notifyItemRangeInserted(vitri,soluongadd);
                               }
                            }else{
                                Toast.makeText(getApplicationContext(),"Out of data", Toast.LENGTH_LONG).show();
                                isLoading = true;
                            }

                        },
                        throwable ->{
                            Toast.makeText(getApplicationContext(), "Can't connect with server", Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void Screen() {
        toolbar = findViewById(R.id.book);
        recyclerView = findViewById(R.id.recycleview_book);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        newProductList = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}