package com.example.onlinestoreapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinestoreapp.Interface.ItemClickListener;
import com.example.onlinestoreapp.R;
import com.example.onlinestoreapp.model.DonHang;
import com.example.onlinestoreapp.model.EventBus.DonHangEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.MyViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    Context context;
    List<DonHang> listdonhang;

    public DonHangAdapter(Context context, List<DonHang> listdonhang) {
        this.context = context;
        this.listdonhang = listdonhang;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donhang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DonHang donHang = listdonhang.get(position);
        holder.txtdonhang.setText("Order: " + donHang.getId());
        holder.diachi.setText("Address: " + donHang.getDiachi());
        holder.username.setText("User: " + donHang.getUsername());
        holder.trangthai.setText(trangthaiDon(donHang.getTrangthai()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.rechitiet.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(donHang.getItems().size());
        //adapter chitiet
        ChiTietAdapter chiTietAdapter = new ChiTietAdapter(context, donHang.getItems());
        holder.rechitiet.setLayoutManager(layoutManager);
        holder.rechitiet.setAdapter(chiTietAdapter);
        holder.rechitiet.setRecycledViewPool(viewPool);
        holder.setListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if (isLongClick){
                    EventBus.getDefault().postSticky(new DonHangEvent(donHang));
                }
            }
        });

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
    public int getItemCount() {
        return listdonhang.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView txtdonhang, trangthai, diachi, username;
        RecyclerView rechitiet;
        ItemClickListener listener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtdonhang = itemView.findViewById(R.id.iddonhang);
            trangthai = itemView.findViewById(R.id.tinhtrang);
            diachi = itemView.findViewById(R.id.diachi_donhang);
            username = itemView.findViewById(R.id.user_donhang);
            rechitiet = itemView.findViewById(R.id.recycleview_chitiet);
            itemView.setOnLongClickListener(this);
        }

        public void setListener(ItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onClick(view, getAdapterPosition(), true);
            return false;
        }
    }
}
