package com.mts.fategocardmaker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mts.fategocardmaker.R;
import com.mts.fategocardmaker.model.ServantSpinnerItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServantSpinnerAdapter extends BaseAdapter {

    private  LayoutInflater inflater;
    private ArrayList<ServantSpinnerItem> arrayList;

    public ServantSpinnerAdapter(Context context, ArrayList<ServantSpinnerItem> arrayList) {
        this.arrayList = arrayList;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        MyViewHolder holder;
        if (view != null){
            holder = (MyViewHolder)view.getTag();
        }else{
            view = inflater.inflate(R.layout.servant_spinner_item, viewGroup, false);
            holder = new MyViewHolder(view);
            view.setTag(holder);
        }

        holder.icon.setImageResource(arrayList.get(i).getId());
        holder.names.setText(arrayList.get(i).getName());
        return view;
    }

    static class MyViewHolder{
        @BindView(R.id.imageView) ImageView icon;
        @BindView(R.id.textView) TextView names;

        MyViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
