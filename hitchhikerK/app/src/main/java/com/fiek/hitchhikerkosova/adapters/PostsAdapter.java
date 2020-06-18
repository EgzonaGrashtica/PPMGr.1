package com.fiek.hitchhikerkosova.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.fiek.hitchhikerkosova.Db.Database;
import com.fiek.hitchhikerkosova.Db.DatabaseHelper;
import com.fiek.hitchhikerkosova.Db.RideModel;
import com.fiek.hitchhikerkosova.MainActivity;
import com.fiek.hitchhikerkosova.PostModel;
import com.fiek.hitchhikerkosova.R;
import com.fiek.hitchhikerkosova.RoadMapFragment;
import com.fiek.hitchhikerkosova.ui.AddPostFragment;
import com.fiek.hitchhikerkosova.ui.ReservedRidesFragment;
import com.google.android.gms.common.util.Predicate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {

    public List<PostModel> dataSource = new ArrayList<PostModel>();
    Context context;
    public Dialog postDialog;
    DateUtils dateUtils=new DateUtils();
    Date currentDate;
    String timeAgo;
    String checkFragment;

    public PostsAdapter(Context ct, String checkFragment) {
        context=ct;
        this.checkFragment=checkFragment;
    }


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
                createDialog(postsViewHolder);
            }
        });
        return postsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {

        holder.postRow.setAnimation(AnimationUtils.loadAnimation(context,R.anim.item_animation_from_bottom));
        holder.tvRowName.setText(dataSource.get(position).getOwnerName());
        holder.tvRowFrom.setText(dataSource.get(position).getFrom());
        holder.tvRowTo.setText(dataSource.get(position).getTo());
        holder.tvRowDepartureTime.setText(dataSource.get(position).getDepartureTime());
        holder.tvRowDate.setText(dataSource.get(position).getDate());
        holder.tvTimeOfPost.setText(getTimeAgo(position));
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }


    private String getTimeAgo(int position){
        currentDate=new Date();
        timeAgo=String.valueOf(dateUtils.getRelativeTimeSpanString(dataSource.get(position).getTimePosted(),
                currentDate.getTime(),DateUtils.MINUTE_IN_MILLIS,DateUtils.FORMAT_ABBREV_RELATIVE));
        return timeAgo;
    }


    public class PostsViewHolder extends RecyclerView.ViewHolder {
        TextView tvRowName,tvRowFrom,tvRowTo,tvRowDepartureTime,tvRowDate,tvTimeOfPost;
        ConstraintLayout postRow;

        public PostsViewHolder(@NonNull View itemView) {

            super(itemView);
            postRow=(ConstraintLayout) itemView.findViewById(R.id.postRow);
            tvRowName=itemView.findViewById(R.id.tvRowName);
            tvRowFrom=itemView.findViewById(R.id.tvRowFrom);
            tvRowTo=itemView.findViewById(R.id.tvRowTo);
            tvRowDepartureTime=itemView.findViewById(R.id.tvRowDepartureTime);
            tvRowDate=itemView.findViewById(R.id.tvRowDate);
            tvTimeOfPost=itemView.findViewById(R.id.tvTimeOfPost);

        }

    }

    private void createDialog(final PostsViewHolder postsViewHolder){
        TextView tvDialogName=postDialog.findViewById(R.id.tvDialogName);
        TextView tvDialogFrom=postDialog.findViewById(R.id.tvDialogFrom);
        TextView tvDialogTo=postDialog.findViewById(R.id.tvDialogTo);
        TextView tvDialogDepartureTime=postDialog.findViewById(R.id.tvDialogDepartureTime);
        TextView tvDialogDate=postDialog.findViewById(R.id.tvDialogDate);
        TextView tvDialogPrice=postDialog.findViewById(R.id.tvDialogPrice);
        TextView tvDialogFreeSeats=postDialog.findViewById(R.id.tvDialogFreeSeats);
        TextView tvDialogPhoneNumber=postDialog.findViewById(R.id.tvDialogPhoneNumber);
        TextView tvDialogExtraInfo=postDialog.findViewById(R.id.tvDialogExtraInfo);
        Button btnReserve=postDialog.findViewById(R.id.btnReserve);

        tvDialogName.setText(dataSource.get(postsViewHolder.getAdapterPosition()).getOwnerName());
        tvDialogFrom.setText(dataSource.get(postsViewHolder.getAdapterPosition()).getFrom());
        tvDialogTo.setText(dataSource.get(postsViewHolder.getAdapterPosition()).getTo());
        tvDialogDepartureTime.setText(dataSource.get(postsViewHolder.getAdapterPosition()).getDepartureTime());
        tvDialogDate.setText(dataSource.get(postsViewHolder.getAdapterPosition()).getDate());
        tvDialogPrice.setText(Double.toString(dataSource.get(postsViewHolder.getAdapterPosition()).getPrice()));
        tvDialogFreeSeats.setText(Integer.toString(dataSource.get(postsViewHolder.getAdapterPosition()).getFreeSeats()));
        tvDialogPhoneNumber.setText(dataSource.get(postsViewHolder.getAdapterPosition()).getPhoneNumber());
        tvDialogPhoneNumber.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvDialogExtraInfo.setText(dataSource.get(postsViewHolder.getAdapterPosition()).getExtraInfo());

        tvDialogPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri= Uri.parse("tel:"+dataSource.get(postsViewHolder.getAdapterPosition()).getPhoneNumber());
                Intent dialerIntent=new Intent(Intent.ACTION_DIAL,uri);
                context.startActivity(dialerIntent);
            }
        });

        ImageButton btnClose=postDialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDialog.cancel();
            }
        });

        Button btnMap=postDialog.findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle args=new Bundle();
                args.putString("from", dataSource.get(postsViewHolder.getAdapterPosition()).getFrom());
                args.putString("to", dataSource.get(postsViewHolder.getAdapterPosition()).getTo());
                NavController navController= Navigation.findNavController((Activity) context,R.id.nav_host_fragment);
                if(checkFragment.equals("MainPostsFragment")){
                    navController.navigate(R.id.action_mainPostsFragment_to_roadMapFragment,args);
                }else{
                    navController.navigate(R.id.action_reservedRidesFragment_to_roadMapFragment,args);
                }


                postDialog.hide();

            }

        });
        if(checkFragment.equals("MainPostsFragment")){
            btnReserve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dataSource.get(postsViewHolder.getAdapterPosition()).getFreeSeats()>0){
                        DatabaseHelper dbHelper=new DatabaseHelper(context);
                        dbHelper.saveToDatabase(dataSource.get(postsViewHolder.getAdapterPosition()).getId(),
                                dataSource.get(postsViewHolder.getAdapterPosition()).getOwnerId(),
                                dataSource.get(postsViewHolder.getAdapterPosition()).getOwnerName(),
                                dataSource.get(postsViewHolder.getAdapterPosition()).getFrom(),
                                dataSource.get(postsViewHolder.getAdapterPosition()).getTo(),
                                dataSource.get(postsViewHolder.getAdapterPosition()).getDepartureTime(),
                                dataSource.get(postsViewHolder.getAdapterPosition()).getDate(),
                                dataSource.get(postsViewHolder.getAdapterPosition()).getPrice(),
                                dataSource.get(postsViewHolder.getAdapterPosition()).getFreeSeats(),
                                dataSource.get(postsViewHolder.getAdapterPosition()).getPhoneNumber(),
                                dataSource.get(postsViewHolder.getAdapterPosition()).getExtraInfo(),
                                dataSource.get(postsViewHolder.getAdapterPosition()).getTimePosted());
                        postDialog.cancel();
                    }else{
                        Toast.makeText(context,"Nuk ka freeseats",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }else if(checkFragment.equals("ReservedRidesFragment")){
            btnReserve.setText(R.string.btnDeleteReserved);
            btnReserve.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorAccent));
            btnReserve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHelper dbHelper=new DatabaseHelper(context);
                    if(dbHelper.deleteReservation(dataSource.get(postsViewHolder.getAdapterPosition()).getId(),
                            dataSource.get(postsViewHolder.getAdapterPosition()).getFreeSeats())){
                        for(PostModel pm: dataSource){
                            if(pm.getId().equals(dataSource.get(postsViewHolder.getAdapterPosition()).getId())){
                                dataSource.remove(pm);
                            }
                        }
                        notifyDataSetChanged();
                    }

                    postDialog.cancel();
                }
            });
        }
        postDialog.show();
    }

}
