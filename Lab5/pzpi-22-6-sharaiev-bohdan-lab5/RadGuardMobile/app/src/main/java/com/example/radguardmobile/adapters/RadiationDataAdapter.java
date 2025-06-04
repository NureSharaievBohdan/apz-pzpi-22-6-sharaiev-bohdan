package com.example.radguardmobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.radguardmobile.R;
import com.example.radguardmobile.models.RadiationData;

import java.util.List;

public class RadiationDataAdapter extends RecyclerView.Adapter<RadiationDataAdapter.VH> {

    private final List<RadiationData> items;

    public RadiationDataAdapter(List<RadiationData> items) {
        this.items = items;
    }

    public void setItems(List<RadiationData> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_radiation_data, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        RadiationData d = items.get(position);
        holder.tvValue.setText(String.valueOf(d.getRadiation_level()));
        holder.tvTime.setText(d.getMeasured_at());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvValue, tvTime;

        VH(View itemView) {
            super(itemView);
            tvValue = itemView.findViewById(R.id.tvRadiationValue);
            tvTime = itemView.findViewById(R.id.tvRadiationTimestamp);
        }
    }
}
