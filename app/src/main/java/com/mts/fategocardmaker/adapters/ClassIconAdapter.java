package com.mts.fategocardmaker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mts.fategocardmaker.R;
import com.mts.fategocardmaker.model.ServantSpinnerItem;

import java.util.ArrayList;

public class ClassIconAdapter extends RecyclerView.Adapter<ClassIconAdapter.RarityVH> {

    private ArrayList<ServantSpinnerItem> items;
    private ClassIconItemCallback itemCallback;

    public interface ClassIconItemCallback {
        void onItemClicked(int itemIndex, String iconName);
    }

    public void setItemCallback(ClassIconItemCallback itemCallback) {
        this.itemCallback = itemCallback;
    }

    public ClassIconAdapter(ArrayList<ServantSpinnerItem> items) {
        this.items = items;
    }

    @Override
    public RarityVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_icon, parent, false);
        return new RarityVH(view, this);
    }

    @Override
    public void onBindViewHolder(RarityVH holder, int position) {
        ServantSpinnerItem item = items.get(position);
        holder.tvTitle.setText(item.getName());
        holder.ivIcon.setImageResource(item.getId());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class RarityVH extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView tvTitle;
        final ImageView ivIcon;
        final ClassIconAdapter adapter;

        RarityVH(View itemView, ClassIconAdapter adapter) {
            super(itemView);
            tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
            ivIcon = (ImageView)itemView.findViewById(R.id.ivIcon);

            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (adapter.itemCallback == null){
                return;
            }else{
                adapter.itemCallback.onItemClicked(getAdapterPosition(),adapter.items.get(getAdapterPosition()).getName());
            }
        }
    }

}
