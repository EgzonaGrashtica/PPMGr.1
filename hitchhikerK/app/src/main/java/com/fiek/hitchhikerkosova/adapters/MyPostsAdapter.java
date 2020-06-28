package com.fiek.hitchhikerkosova.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.fiek.hitchhikerkosova.db.DatabaseHelper;
import com.fiek.hitchhikerkosova.models.PostModel;
import com.fiek.hitchhikerkosova.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.MyPostsViewHolder> {
    public List<PostModel> dataSource = new ArrayList<PostModel>();
    private Context context;

    public MyPostsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyPostsAdapter.MyPostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.my_post_row,parent,false);
        final MyPostsViewHolder myPostsViewHolder=new MyPostsViewHolder(view);

        myPostsViewHolder.btnMyPostRowDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMyPost(myPostsViewHolder);
            }
        });
        return myPostsViewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull MyPostsAdapter.MyPostsViewHolder holder, int position) {
        holder.myPostRow.setAnimation(AnimationUtils.loadAnimation(context,R.anim.item_animation_from_bottom));
        holder.tvMyReservationNO.setText(String.valueOf(dataSource.get(position).getNumberOfReservations()));
        holder.tvTimeOfPost.setText(getTimeAgo(position));
        holder.tvMyRowFrom.setText(dataSource.get(position).getFrom());
        holder.tvMyRowTo.setText(dataSource.get(position).getTo());
        holder.tvMyRowDepartureTime.setText(dataSource.get(position).getDepartureTime());
        holder.tvMyRowDate.setText(dataSource.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    private String getTimeAgo(int position){
        Date currentDate=new Date();
        String timeAgo=String.valueOf(DateUtils.getRelativeTimeSpanString(dataSource.get(position).getTimePosted(),
                currentDate.getTime(), DateUtils.MINUTE_IN_MILLIS,DateUtils.FORMAT_ABBREV_RELATIVE));
        return timeAgo;
    }
    private void deleteMyPost(final MyPostsViewHolder myPostsViewHolder) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(R.string.alertDeletePost)
                .setPositiveButton(R.string.btnDeleteReserved, new DialogInterface.OnClickListener()                 {

                    public void onClick(DialogInterface dialog, int which) {

                        new DatabaseHelper(context).deletePostInFirebase(dataSource.get(myPostsViewHolder.getAdapterPosition()).getId());

                    }
                }).setNegativeButton(R.string.btnCancel, null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    public class MyPostsViewHolder extends RecyclerView.ViewHolder {

        TextView tvMyReservationNO,tvTimeOfPost,tvMyRowDepartureTime,tvMyRowFrom,tvMyRowTo,tvMyRowDate;
        ImageButton btnMyPostRowDelete;
        ConstraintLayout myPostRow;

        public MyPostsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMyReservationNO=(TextView)itemView.findViewById(R.id.tvMyReservationsNO);
            tvTimeOfPost=(TextView)itemView.findViewById(R.id.tvTimeOfPost);
            tvMyRowFrom=(TextView)itemView.findViewById(R.id.tvMyRowFrom);
            tvMyRowTo=(TextView)itemView.findViewById(R.id.tvMyRowTo);
            tvMyRowDepartureTime=(TextView)itemView.findViewById(R.id.tvMyRowDepartureTime);
            tvMyRowDate=(TextView)itemView.findViewById(R.id.tvMyRowDate);
            btnMyPostRowDelete=(ImageButton)itemView.findViewById(R.id.btnMyPostRowDelete);
            myPostRow=(ConstraintLayout)itemView.findViewById(R.id.myPostRow);
        }
    }

}
