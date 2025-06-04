package com.example.radguardmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.radguardmobile.R;
import com.example.radguardmobile.api.response.AlertResponse;

import java.util.List;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.AlertViewHolder> {

    private final Context context;
    private final List<AlertResponse> alertList;
    private final OnDeleteClickListener deleteClickListener;
    public AlertAdapter(Context context, List<AlertResponse> alertList, OnDeleteClickListener listener) {
        this.context = context;
        this.alertList = alertList;
        this.deleteClickListener = listener;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alert, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        AlertResponse alert = alertList.get(position);
        holder.txtMessage.setText(alert.getAlert_message());
        holder.txtLevel.setText(alert.getAlert_level());
        holder.txtTime.setText(alert.getTriggered_at());

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(alert);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(AlertResponse alert);
    }

    public static class AlertViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage, txtLevel, txtTime;
        Button btnDelete;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtAlertMessage);
            txtLevel = itemView.findViewById(R.id.txtAlertLevel);
            txtTime = itemView.findViewById(R.id.txtAlertTime);
            btnDelete = itemView.findViewById(R.id.btnDeleteAlert);
        }
    }
}
