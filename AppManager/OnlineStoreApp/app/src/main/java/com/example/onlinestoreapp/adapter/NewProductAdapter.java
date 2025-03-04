package com.example.onlinestoreapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinestoreapp.Interface.ItemClickListener;
import com.example.onlinestoreapp.R;
import com.example.onlinestoreapp.activity.ChiTietActivity;
import com.example.onlinestoreapp.model.EventBus.SuaVaXoaEvent;
import com.example.onlinestoreapp.model.NewProduct;
import com.example.onlinestoreapp.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class NewProductAdapter extends RecyclerView.Adapter<NewProductAdapter.MyViewHolder> {
    Context context;
    List<NewProduct> array;

    public NewProductAdapter(Context context, List<NewProduct> array){
        this.context = context;
        this.array = array;
    }
    
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sp_moi, parent, false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NewProduct newProduct = array.get(position);
        holder.txtname.setText(newProduct.getName());
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        holder.txtprice.setText("Price: "+decimalFormat.format(Double.parseDouble(newProduct.getPrice()))+ " VND");
        if (newProduct.getImage().contains("http")){
            Glide.with(context).load(newProduct.getImage()).into(holder.imghinhanh);
        }else {
            String hinh = Utils.BASE_URL+"images/"+newProduct.getImage();
            Glide.with(context).load(hinh).into(holder.imghinhanh);
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if (!isLongClick){
                    //click
                    Intent intent = new Intent(context, ChiTietActivity.class);
                    intent.putExtra("chitiet",newProduct);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else {
                    EventBus.getDefault().postSticky(new SuaVaXoaEvent(newProduct));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, View.OnLongClickListener {
        TextView txtprice, txtname;
        ImageView imghinhanh;
        private ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtprice = itemView.findViewById(R.id.itemsp_price);
            txtname = itemView.findViewById(R.id.itemsp_name);
            imghinhanh = itemView.findViewById(R.id.itemsp_image);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(0,0,getAdapterPosition(),"Edit");
            contextMenu.add(0,1,getAdapterPosition(),"Delete");
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(),true);
            return false;
        }
    }
}
