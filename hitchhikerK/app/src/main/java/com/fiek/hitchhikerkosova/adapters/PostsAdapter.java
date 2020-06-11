package com.fiek.hitchhikerkosova.adapters;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.recyclerview.widget.RecyclerView;

import com.fiek.hitchhikerkosova.Db.Database;
import com.fiek.hitchhikerkosova.Db.RideModel;
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
    Dialog postDialog;
    DateUtils dateUtils=new DateUtils();
    Date currentDate;
    String timeAgo;
    String checkFragment;
    Fragment fragment = null;
    Class fragmentClass;
    FragmentManager fragmentManager;
    private DatabaseReference mDatabase;
    private final String DELETING_RESERVATION="DELETING_RESERVATION";
    private final String MAKING_RESERVATION="MAKING_RESERVATION";


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
                tvDialogExtraInfo.setText(dataSource.get(postsViewHolder.getAdapterPosition()).getExtraInfo());
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
                        fragmentClass = RoadMapFragment.class;
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.fContent, fragment).addToBackStack(null).commit();
                        postDialog.hide();
                    }

                });
                if(checkFragment.equals("MainPostsFragment")){
                    btnReserve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(dataSource.get(postsViewHolder.getAdapterPosition()).getFreeSeats()>0){
                                saveToDatabase(dataSource.get(postsViewHolder.getAdapterPosition()).getId(),
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
                            deleteReservation(dataSource.get(postsViewHolder.getAdapterPosition()).getId(),
                                    dataSource.get(postsViewHolder.getAdapterPosition()).getFreeSeats());
                            postDialog.cancel();
                        }
                    });
                }
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
    private void saveToDatabase(String id,String ownerId,String ownerName,String from,String to,String departureTime,String date,
                                double price,int freeSeats,String phoneNumber,String extraInfo,long timePosted){
        ContentValues cv=new ContentValues();
        cv.put(RideModel.Id,id);
        cv.put(RideModel.OwnerID,ownerId);
        cv.put(RideModel.OwnerName,ownerName);
        cv.put(RideModel.FromWhere,from);
        cv.put(RideModel.ToWhere,to);
        cv.put(RideModel.DepartureTime,departureTime);
        cv.put(RideModel.Date,date);
        cv.put(RideModel.Price,price);
        cv.put(RideModel.FreeSeats,freeSeats-1);
        cv.put(RideModel.PhoneNumber,phoneNumber);
        cv.put(RideModel.ExtraInfo,extraInfo);
        cv.put(RideModel.TimePosted,timePosted);

        SQLiteDatabase objDb=new Database(context).getWritableDatabase();
        try{
            long retValue=objDb.insert(Database.reservedTable,null,cv);
            if(retValue>0){
            saveToRealTimeDb(id, freeSeats,MAKING_RESERVATION);
            }
            else {
                Toast.makeText(context,"U rezevua njehere",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception ex){
            Log.e("Exception",ex.getMessage());

        }
        finally {
            objDb.close();
        }
    }
    private void saveToRealTimeDb(String id,int freeSeats,String action){
        mDatabase=FirebaseDatabase.getInstance().getReference();
        if(action.equals(MAKING_RESERVATION)){
            mDatabase.child("Posts").child(id).child("freeSeats").setValue(freeSeats-1).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(context,"U rezervua",Toast.LENGTH_SHORT).show();
                }
            });
        }else if(action.equals(DELETING_RESERVATION)){
            mDatabase.child("Posts").child(id).child("freeSeats").setValue(freeSeats+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(context,"U fshi",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void deleteReservation(String id, int freeSeats){
        SQLiteDatabase objDb=new Database(context).getWritableDatabase();
        try{
            int retValue = objDb.delete(Database.reservedTable,"ID = ?",new String[] {id});
            if(retValue>0){
                saveToRealTimeDb(id,freeSeats,DELETING_RESERVATION);
                for(PostModel pm: dataSource){
                    if(pm.getId().equals(id)){
                        dataSource.remove(pm);
                    }
                }
                notifyDataSetChanged();
            }else{
                Toast.makeText(context,"No rows affected",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception ex){
            if(ex.getMessage()!=null){
            Log.e("Exception",ex.getMessage());}

        }finally {
            objDb.close();
        }

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
}
