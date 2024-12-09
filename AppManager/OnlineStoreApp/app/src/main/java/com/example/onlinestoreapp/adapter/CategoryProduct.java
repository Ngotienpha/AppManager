package com.example.onlinestoreapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.onlinestoreapp.R;
import com.example.onlinestoreapp.model.LoaiSanPham;

import java.util.List;

public class CategoryProduct extends BaseAdapter {

    List<LoaiSanPham> array;
    Context context;

    public CategoryProduct(Context context, List<LoaiSanPham> array) {
        this.array = array;
        this.context = context;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class ViewHolder{
        TextView textname;
        ImageView img;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_product, null);
            viewHolder.textname = view.findViewById(R.id.name);
            viewHolder.img = view.findViewById(R.id.item_image);
            view.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textname.setText(array.get(i).getName());
        Glide.with(context).load(array.get(i).getImage()).into(viewHolder.img);

        return view;
    }
}
