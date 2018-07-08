package com.mts.fategocardmaker.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mts.fategocardmaker.R;

public class RarityAdapter extends RecyclerView.Adapter<RarityAdapter.StarVH> {

    private Context context;
    private RarityItemCallback itemCallback;
    private int[] colors = new int[6];

    public interface RarityItemCallback {
        void onItemClicked(int itemIndex);
    }

    public void setItemCallback(RarityItemCallback itemCallback) {
        this.itemCallback = itemCallback;
    }

    public RarityAdapter(Context context) {
        this.context = context;
        colors[1] = ContextCompat.getColor(context,R.color.star_1);
        colors[2] = ContextCompat.getColor(context,R.color.star_2);
        colors[3] = ContextCompat.getColor(context,R.color.star_3);
        colors[4] = ContextCompat.getColor(context,R.color.star_4);
        colors[5] = ContextCompat.getColor(context,R.color.star_5);
    }

    @Override
    public StarVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rarity, parent, false);
        return new StarVH(view, this);
    }

    @Override
    public void onBindViewHolder(StarVH holder, int position) {
        int starCount = position + 1;
        String starIcon = context.getResources().getString(R.string.star_icon);
        String text = starIcon + " " + context.getResources().getQuantityString(R.plurals.dialog_star_count,starCount, starCount) ;
        Spannable spannableText = new SpannableString(text);
        spannableText.setSpan(new ForegroundColorSpan(colors[starCount]),0,starIcon.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvTitle.setText(spannableText);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    static class StarVH extends RecyclerView.ViewHolder implements View.OnClickListener{

        final TextView tvTitle;
        final RarityAdapter adapter;

        StarVH(View itemView, RarityAdapter adapter){
            super(itemView);
            tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);

            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (adapter.itemCallback == null){
                return;
            }else{
                adapter.itemCallback.onItemClicked(getAdapterPosition()+1);
            }
        }
    }

}
