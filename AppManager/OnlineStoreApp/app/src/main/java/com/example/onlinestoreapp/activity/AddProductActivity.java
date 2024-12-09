package com.example.onlinestoreapp.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.onlinestoreapp.R;
import com.example.onlinestoreapp.databinding.ActivityAddProductBinding;
import com.example.onlinestoreapp.model.MessageModel;
import com.example.onlinestoreapp.model.NewProduct;
import com.example.onlinestoreapp.retrofit.ApiStore;
import com.example.onlinestoreapp.retrofit.RetrofitClient;
import com.example.onlinestoreapp.utils.Utils;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProductActivity extends AppCompatActivity {
    Spinner spinner;
    int loai = 0;
    ActivityAddProductBinding binding;
    ApiStore apiStore;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    String mediaPath;
    NewProduct sanPhamEdit;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        apiStore = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiStore.class);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        initView();
        initData();
        Intent intent = getIntent();
        sanPhamEdit = (NewProduct) intent.getSerializableExtra("Edit");
        if (sanPhamEdit == null){
            //add
            flag = false;
        }else {
            //edit
            flag = true;
            binding.btnthem.setText("Edit Product");
            //show data
            binding.mota.setText(sanPhamEdit.getDescribe());
            binding.giasp.setText(sanPhamEdit.getPrice());
            binding.tensp.setText(sanPhamEdit.getName());
            binding.hinhanh.setText(sanPhamEdit.getImage());
            binding.slsp.setText(sanPhamEdit.getSltonkho()+"");
            binding.spinnerLoai.setSelection(sanPhamEdit.getCategory());
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initData() {
        List<String> stringList = new ArrayList<>();
        stringList.add("Please choose category");
        stringList.add("Category 1");
        stringList.add("Category 2");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stringList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loai = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == false){
                    themsanpham();
                }else {
                    suaSanPham();
                }

            }
        });

        binding.imgcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(AddProductActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });
    }

    private void suaSanPham() {
        String str_ten = binding.tensp.getText().toString().trim();
        String str_gia = binding.giasp.getText().toString().trim();
        String str_mota = binding.mota.getText().toString().trim();
        String str_hinhanh = binding.hinhanh.getText().toString().trim();
        String str_sl = binding.slsp.getText().toString();
        if (TextUtils.isEmpty(str_sl) || TextUtils.isEmpty(str_ten) || TextUtils.isEmpty(str_gia) || TextUtils.isEmpty(str_mota) || TextUtils.isEmpty(str_hinhanh) || loai == 0){
            Toast.makeText(getApplicationContext(), "Please input all information", Toast.LENGTH_LONG).show();
        }else {
            compositeDisposable.add(apiStore.updateSp(str_ten,str_gia,str_hinhanh,str_mota,loai, sanPhamEdit.getId(), Integer.parseInt(str_sl))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if (messageModel.isSuccess()){
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                            }
                    ));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaPath = data.getDataString();
        uploadMultipleFiles();
        Log.d("log", "onActivityResult: "+mediaPath);
    }

    private void themsanpham() {
        String str_ten = binding.tensp.getText().toString().trim();
        String str_gia = binding.giasp.getText().toString().trim();
        String str_mota = binding.mota.getText().toString().trim();
        String str_hinhanh = binding.hinhanh.getText().toString().trim();
        String str_sl = binding.slsp.getText().toString();
        if (TextUtils.isEmpty(str_sl) || TextUtils.isEmpty(str_ten) || TextUtils.isEmpty(str_gia) || TextUtils.isEmpty(str_mota) || TextUtils.isEmpty(str_hinhanh) || loai == 0){
            Toast.makeText(getApplicationContext(), "Please input all information", Toast.LENGTH_LONG).show();
        }else {
            compositeDisposable.add(apiStore.insertSp(str_ten,str_gia,str_hinhanh,str_mota,(loai), Integer.parseInt(str_sl))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if (messageModel.isSuccess()){
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_LONG).show();
                                    binding.tensp.setText("");
                                    binding.giasp.setText("");
                                }else{
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                            }
                    ));
        }
    }

    private String getPath(Uri uri){
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null){
            result = uri.getPath();
        }else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }

    private void uploadMultipleFiles() {

        Uri uri = Uri.parse(mediaPath);
        File file = new File(getPath(uri));
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("file", file.getName(), requestBody1);
        Call<MessageModel> call = apiStore.uploadFile(fileToUpload1);
        call.enqueue(new Callback <MessageModel> () {
            @Override
            public void onResponse(Call < MessageModel > call, Response< MessageModel > response) {
                MessageModel serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.isSuccess()) {
                        binding.hinhanh.setText(serverResponse.getName());
                    } else {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.v("Response", serverResponse.toString());
                }
            }
            @Override
            public void onFailure(Call < MessageModel > call, Throwable t) {
                Log.d("log", t.getMessage());
            }
        });
    }

    private void initView() {
        spinner = findViewById(R.id.spinner_loai);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}