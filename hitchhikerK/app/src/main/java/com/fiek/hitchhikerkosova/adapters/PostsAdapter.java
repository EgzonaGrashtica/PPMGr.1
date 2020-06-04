package com.fiek.hitchhikerkosova.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fiek.hitchhikerkosova.PostModel;
import com.fiek.hitchhikerkosova.R;

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {

    public List<PostModel> dataSource = new ArrayList<PostModel>();
    Context context;
    public PostsAdapter(Context ct) {
        context=ct;
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.post_row,parent,false);

        return new PostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        holder.tvRowName.setText(dataSource.get(position).getOwnerName());
        holder.tvRowFrom.setText(dataSource.get(position).getFrom());
        holder.tvRowTo.setText(dataSource.get(position).getTo());
        holder.tvRowDepartureTime.setText(dataSource.get(position).getDepartureTime());
        holder.tvRowDate.setText(dataSource.get(position).getDate());
        holder.tvRowPrice.setText(Double.toString(dataSource.get(position).getPrice()));
        holder.tvRowFreeSeats.setText(Integer.toString(dataSource.get(position).getFreeSeats()));
        holder.tvRowPhoneNumber.setText(dataSource.get(position).getPhoneNumber());
        holder.tvRowExtraInfo.setText(dataSource.get(position).getExtraInfo());
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public class PostsViewHolder extends RecyclerView.ViewHolder {
        TextView tvRowName,tvRowFrom,tvRowTo,tvRowDepartureTime,tvRowDate,tvRowPrice,tvRowFreeSeats,tvRowPhoneNumber,tvRowExtraInfo;
        public PostsViewHolder(@NonNull View itemView) {

            super(itemView);
            tvRowName=itemView.findViewById(R.id.tvRowName);
            tvRowFrom=itemView.findViewById(R.id.tvRowFrom);
            tvRowTo=itemView.findViewById(R.id.tvRowTo);
            tvRowDepartureTime=itemView.findViewById(R.id.tvRowDepartureTime);
            tvRowDate=itemView.findViewById(R.id.tvRowDate);
            tvRowPrice=itemView.findViewById(R.id.tvRowPrice);
            tvRowFreeSeats=itemView.findViewById(R.id.tvRowFreeSeats);
            tvRowPhoneNumber=itemView.findViewById(R.id.tvRowPhoneNumber);
            tvRowExtraInfo=itemView.findViewById(R.id.tvRowExtraInfo);
        }

    }
}
