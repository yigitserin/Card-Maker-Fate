package com.mts.fategocardmaker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mts.fategocardmaker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StarSpinnerAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    public StarSpinnerAdapter(Context context) {
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int i) {
        return i;
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
            view = inflater.inflate(R.layout.star_spinner_item, viewGroup, false);
            holder = new MyViewHolder(view);
            view.setTag(holder);
        }

        String text =  "★" + (i+1) ;
        holder.names.setText(text);
        return view;
    }


    static class MyViewHolder{
        @BindView(R.id.textView) TextView names;

        MyViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
