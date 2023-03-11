package com.example.nakama.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nakama.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class OverallImpressionsRecycledViewAdapter extends RecyclerView.Adapter<OverallImpressionsRecycledViewAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private ArrayList<String> overallReasons;
    private ArrayList<Integer> pointsRemoved;

    public OverallImpressionsRecycledViewAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        overallReasons = new ArrayList<>();
        overallReasons = new ArrayList<>();
    }

    // inflates view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.edit_text_view, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        if(position == 0){
//            holder.removeButton.setVisibility(View.INVISIBLE);
//        }
//        else {
//            holder.removeButton.setVisibility(View.VISIBLE);
//        }
//        holder.tempTv.setText(desiredTemp.get(position) + "\u2103");
//        holder.timeTv.setText(timeForStep.get(position) + " min");
    }

    //get reasons and points
    public ArrayList<String> getOverallReasons() {
        return overallReasons;
    }
    public ArrayList<Integer> getPointsRemoved() {
        return pointsRemoved;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return overallReasons.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        MaterialButton removeButton;
        MaterialButton editButton;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.textInput);
            itemView.setOnClickListener(this);

            removeButton.setOnClickListener(v -> {
                if (mClickListener != null) {
                    overallReasons.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    mClickListener.onRemoveButtonClick();
                }
            });

            editButton.setOnClickListener(v -> {
                if (mClickListener != null){
                    int index = getAdapterPosition();
                    mClickListener.onEditItemClick();
                }
            });

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null){
                mClickListener.onItemClick();
            }
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick();
        void onEditItemClick();
        void onRemoveButtonClick();
    }
}
