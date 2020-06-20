package com.fiek.hitchhikerkosova.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.fiek.hitchhikerkosova.models.PostModel;
import com.fiek.hitchhikerkosova.models.RideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;


public class DatabaseHelper {
    private Context ct;
    private final String DELETING_RESERVATION="DELETING_RESERVATION";
    private final String MAKING_RESERVATION="MAKING_RESERVATION";

    public DatabaseHelper(Context ct) {
        this.ct = ct;
    }


    public void checkIfRideIsReservedAndUpdate(PostModel pm){
        SQLiteDatabase objDbRead = new Database(ct).getReadableDatabase();
        SQLiteDatabase objDbWrite=new Database(ct).getWritableDatabase();
        Cursor cursor = objDbRead.query(Database.reservedTable,new String[]{RideModel.Id},"",
                new String[]{},"","","");
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){

            if(cursor.getString(0).equals(pm.getId())){
                ContentValues cv=new ContentValues();
                cv.put(RideModel.FreeSeats,pm.getFreeSeats());
                cv.put(RideModel.NumberOfReservations,pm.getNumberOfReservations());
                objDbWrite.update(Database.reservedTable,cv,"id = ?",new String[]{pm.getId()});
            }

            cursor.moveToNext();
        }
        cursor.close();
        objDbRead.close();
        objDbWrite.close();
    }

    public void checkIfRideIsReservedAndDelete(PostModel pm){
        SQLiteDatabase objDbRead = new Database(ct).getReadableDatabase();
        SQLiteDatabase objDbWrite=new Database(ct).getWritableDatabase();
        Cursor cursor = objDbRead.query(Database.reservedTable,new String[]{RideModel.Id},"",
                new String[]{},"","","");
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){

            if(cursor.getString(0).equals(pm.getId())){
                objDbWrite.delete(Database.reservedTable,"id = ?",new String[]{pm.getId()});
            }

            cursor.moveToNext();
        }
        cursor.close();
        objDbRead.close();
        objDbWrite.close();
    }

    public void checkIfReservedExist(List<PostModel> tempDataSource){
        SQLiteDatabase objDbRead = new Database(ct).getReadableDatabase();
        SQLiteDatabase objDbWrite=new Database(ct).getWritableDatabase();
        Cursor cursor = objDbRead.query(Database.reservedTable,new String[]{RideModel.Id},"",
                new String[]{},"","","");
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){

            if(!isIdInList(cursor.getString(0),tempDataSource)){
                objDbWrite.delete(Database.reservedTable,"id = ?",new String[]{cursor.getString(0)});
            }
            cursor.moveToNext();
        }
        cursor.close();
        objDbRead.close();
        objDbWrite.close();
    }

    private boolean isIdInList(String id,List<PostModel> tempDataSource){
        for(PostModel pm:tempDataSource){
            if(id.equals(pm.getId())){
               return true;
            }
        }
        return false;
    }


    public void saveToDatabase(String id,String ownerId,String ownerName,String from,String to,String departureTime,String date,
                                double price,int freeSeats,String phoneNumber,String extraInfo,long timePosted,int numberOfReservations){
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
        cv.put(RideModel.NumberOfReservations,numberOfReservations);

        SQLiteDatabase objDb=new Database(ct).getWritableDatabase();
        try{
            long retValue=objDb.insert(Database.reservedTable,null,cv);
            if(retValue>0){
                saveToRealTimeDb(id, freeSeats,numberOfReservations,MAKING_RESERVATION);
            }
            else {
                Toast.makeText(ct,"U rezevua njehere",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception ex){
            Log.e("Exception",ex.getMessage());

        }
        finally {
            objDb.close();
        }
    }
    private void saveToRealTimeDb(String id,int freeSeats,int numberOfReservations,String action){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        if(action.equals(MAKING_RESERVATION)){
            HashMap<String,Object> updates=new HashMap<>();
            updates.put("freeSeats",freeSeats-1);
            updates.put("numberOfReservations",numberOfReservations+1);
            mDatabase.child("Posts").child(id).updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Toast.makeText(ct,"U rezervua",Toast.LENGTH_SHORT).show();
                }
            });
        }else if(action.equals(DELETING_RESERVATION)){
            HashMap<String,Object> updates=new HashMap<>();
            updates.put("freeSeats",freeSeats+1);
            updates.put("numberOfReservations",numberOfReservations-1);
            mDatabase.child("Posts").child(id).updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ct,"U fshi",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public boolean deleteReservation(String id, int freeSeats,int numberOfReservations){
        SQLiteDatabase objDb=new Database(ct).getWritableDatabase();
        try{
            int retValue = objDb.delete(Database.reservedTable,"ID = ?",new String[] {id});
            if(retValue>0){
                saveToRealTimeDb(id,freeSeats,numberOfReservations,DELETING_RESERVATION);
                return true;

            }else{
                Toast.makeText(ct,"No rows affected",Toast.LENGTH_SHORT).show();
                return false;
            }
        }catch (Exception ex){
            if(ex.getMessage()!=null){
                Log.e("Exception",ex.getMessage());}
            return false;
        }finally {
            objDb.close();
        }

    }

    public void deletePostInFirebase(String id){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Posts").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ct,"Post Deleted!",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ct,"Post Wasnt Deleted. Something went wrong",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
