package com.example.onlinestoreapp.retrofit;

import io.reactivex.rxjava3.core.Observable;

import com.example.onlinestoreapp.model.DonHangModel;
import com.example.onlinestoreapp.model.LoaiSanPhamModel;
import com.example.onlinestoreapp.model.MessageModel;
import com.example.onlinestoreapp.model.NewProductModel;
import com.example.onlinestoreapp.model.ThongKeModel;
import com.example.onlinestoreapp.model.UserModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiStore {
    //GET
    @GET("getloaisp.php")
    Observable<LoaiSanPhamModel> getloaisp();
    
    @GET("getspmoi.php")
    Observable<NewProductModel> getSpmoi();

    @GET("thongke.php")
    Observable<ThongKeModel> getthongke();

    @GET("thongkethang.php")
    Observable<ThongKeModel> getthongkethang();

    //POST DATA
    @POST("chitiet.php")
    @FormUrlEncoded
    Observable<NewProductModel> getSanPham(
            @Field("page") int page,
            @Field("category") int category
    );

    @POST("dangki.php")
    @FormUrlEncoded
    Observable<UserModel> dangKi(
            @Field("email") String email,
            @Field("password") String password,
            @Field("username") String username,
            @Field("mobile") String mobile,
            @Field("uid") String uid
    );

    @POST("gettoken.php")
    @FormUrlEncoded
    Observable<UserModel> gettoken(
      @Field("status") int status,
      @Field("iduser") int iduser
    );

    @POST("dangnhap.php")
    @FormUrlEncoded
    Observable<UserModel> dangnhap(
            @Field("email") String email,
            @Field("password") String password
    );

    @POST("insertmeeting.php")
    @FormUrlEncoded
    Observable<MessageModel> postMeeting(
            @Field("meetingId") String meetingid,
            @Field("token") String token
    );

    @POST("reset.php")
    @FormUrlEncoded
    Observable<UserModel> resetPass(
            @Field("email") String email
    );

    @POST("donhang.php")
    @FormUrlEncoded
    Observable<UserModel> createOrder(
            @Field("email") String email,
            @Field("sdt") String sdt,
            @Field("tongtien") String tongtien,
            @Field("iduser") int id,
            @Field("diachi") String diachi,
            @Field("soluong") int soluong,
            @Field("chitiet") String chitiet
    );

    @POST("xemdonhang.php")
    @FormUrlEncoded
    Observable<DonHangModel> xemDonHang(
            @Field("iduser") int id
    );

    @POST("timkiem.php")
    @FormUrlEncoded
    Observable<NewProductModel> search(
            @Field("search") String search
    );

    @POST("gettoken.php")
    @FormUrlEncoded
    Observable<UserModel> getToken(
            @Field("status") int status,
            @Field("iduser") int iduser
    );

    @POST("xoa.php")
    @FormUrlEncoded
    Observable<MessageModel> xoaSanPham(
            @Field("id") int id
    );

    @POST("insertsp.php")
    @FormUrlEncoded
    Observable<MessageModel> insertSp(
            @Field("tensp") String tensp,
            @Field("gia") String gia,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("loai") int id,
            @Field("slsp") int sl
    );

    @POST("updatesp.php")
    @FormUrlEncoded
    Observable<MessageModel> updateSp(
            @Field("tensp") String tensp,
            @Field("gia") String gia,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("loai") int idloai,
            @Field("id") int id,
            @Field("slsp") int slsp
    );

    @POST("updatetoken.php")
    @FormUrlEncoded
    Observable<MessageModel> updateToken(
            @Field("id") int id,
            @Field("token") String token
    );

    @POST("updatedonhang.php")
    @FormUrlEncoded
    Observable<MessageModel> updateOrder(
            @Field("id") int id,
            @Field("trangthai") int trangthai
    );

    @Multipart
    @POST("upload.php")
    Call<MessageModel> uploadFile(@Part MultipartBody.Part file);

}
