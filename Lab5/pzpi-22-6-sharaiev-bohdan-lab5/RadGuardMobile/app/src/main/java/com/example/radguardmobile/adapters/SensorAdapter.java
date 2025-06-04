package com.example.radguardmobile.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.radguardmobile.R;
import com.example.radguardmobile.models.Sensor;
import com.example.radguardmobile.ui.SensorDetailActivity;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {

    private final List<Sensor> sensorList;
    private final Context context;

    public SensorAdapter(Context context, List<Sensor> sensorList) {
        this.context = context;
        this.sensorList = sensorList;
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_sensor, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        Sensor sensor = sensorList.get(position);
        holder.tvName.setText(sensor.getSensor_name());
        holder.tvStatus.setText("Статус: " + sensor.getStatus());

        View.OnClickListener goDetail = v -> {
            Log.d("SensorAdapter", "Clicked sensor: " + sensor.getId());
            Intent intent = new Intent(context, SensorDetailActivity.class);
            intent.putExtra("sensor_id", sensor.getId());
            if (!(context instanceof Activity)) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        };

        holder.itemView.setOnClickListener(goDetail);
        holder.btnView.setOnClickListener(goDetail);
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    static class SensorViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus;
        Button btnView;

        public SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSensorName);
            tvStatus = itemView.findViewById(R.id.tvSensorStatus);
            btnView = itemView.findViewById(R.id.btnView);
        }
    }
}
