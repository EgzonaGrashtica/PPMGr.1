package com.fiek.hitchhikerkosova.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
    Dialog postDialog;

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.post_row,parent,false);
        final PostsViewHolder postsViewHolder=new PostsViewHolder(view);

        //Dialog

        postDialog = new Dialog(context);
        postDialog.setContentView(R.layout.dialog_ride);
        postDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



        postsViewHolder.postRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvDialogName=postDialog.findViewById(R.id.tvDialogName);
                TextView tvDialogFrom=postDialog.findViewById(R.id.tvDialogFrom);
                TextView tvDialogTo=postDialog.findViewById(R.id.tvDialogTo);
                TextView tvDialogDepartureTime=postDialog.findViewById(R.id.tvDialogDepartureTime);
                TextView tvDialogDate=postDialog.findViewById(R.id.tvDialogDate);
                TextView tvDialogPrice=postDialog.findViewById(R.id.tvDialogPrice);
                TextView tvDialogFreeSeats=postDialog.findViewById(R.id.tvDialogFreeSeats);
                TextView tvDialogPhoneNumber=postDialog.findViewById(R.id.tvDialogPhoneNumber);
                TextView tvDialogExtraInfo=postDialog.findViewById(R.id.tvDialogExtraInfo);

                tvDialogName.setText(dataSource.get(postsViewHolder.getAdapterPosition()).getOwnerName());
                tvDialogFrom.setText(dataSource.get(postsViewHolder.getAdapterPosition()).getFrom());
                tvDialogTo.setText(dataSource.get(postsViewHolder.getAdapterPosition()).getTo());
                tvDialogDepartureTime.setText(dataSource.get(postsViewHolder.getAdapterPosition()).getDepartureTime());
                tvDialogDate.setText(dataSource.get(postsViewHolder.getAdapterPosition()).getDate());
                tvDialogPrice.setText(Double.toString(dataSource.get(postsViewHolder.getAdapterPosition()).getPrice()));
                tvDialogFreeSeats.setText(Integer.toString(dataSource.get(postsViewHolder.getAdapterPosition()).getFreeSeats()));
                tvDialogPhoneNumber.setText(dataSource.get(postsViewHolder.getAdapterPosition()).getPhoneNumber());
                tvDialogExtraInfo.setText(dataSource.get(postsViewHolder.getAdapterPosition()).getExtraInfo());
                postDialog.show();
            }
        });
        return postsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        holder.tvRowName.setText(dataSource.get(position).getOwnerName());
        holder.tvRowFrom.setText(dataSource.get(position).getFrom());
        holder.tvRowTo.setText(dataSource.get(position).getTo());
        holder.tvRowDepartureTime.setText(dataSource.get(position).getDepartureTime());
        holder.tvRowDate.setText(dataSource.get(position).getDate());
        /*
        holder.tvRowPrice.setText(Double.toString(dataSource.get(position).getPrice()));
        holder.tvRowFreeSeats.setText(Integer.toString(dataSource.get(position).getFreeSeats()));
        holder.tvRowPhoneNumber.setText(dataSource.get(position).getPhoneNumber());
        holder.tvRowExtraInfo.setText(dataSource.get(position).getExtraInfo());*/
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    public class PostsViewHolder extends RecyclerView.ViewHolder {
        TextView tvRowName,tvRowFrom,tvRowTo,tvRowDepartureTime,tvRowDate;
        LinearLayout postRow;
        /*TextView tvRowPrice,tvRowFreeSeats,tvRowPhoneNumber,tvRowExtraInfo;*/
        public PostsViewHolder(@NonNull View itemView) {

            super(itemView);
            postRow=(LinearLayout)itemView.findViewById(R.id.postRow);
            tvRowName=itemView.findViewById(R.id.tvRowName);
            tvRowFrom=itemView.findViewById(R.id.tvRowFrom);
            tvRowTo=itemView.findViewById(R.id.tvRowTo);
            tvRowDepartureTime=itemView.findViewById(R.id.tvRowDepartureTime);
            tvRowDate=itemView.findViewById(R.id.tvRowDate);
            /*
            tvRowPrice=itemView.findViewById(R.id.tvRowPrice);
            tvRowFreeSeats=itemView.findViewById(R.id.tvRowFreeSeats);
            tvRowPhoneNumber=itemView.findViewById(R.id.tvRowPhoneNumber);
            tvRowExtraInfo=itemView.findViewById(R.id.tvRowExtraInfo);*/
        }

    }
}
