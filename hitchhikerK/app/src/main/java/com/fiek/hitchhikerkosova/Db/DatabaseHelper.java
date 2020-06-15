package com.fiek.hitchhikerkosova.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;


import com.fiek.hitchhikerkosova.MainActivity;
import com.fiek.hitchhikerkosova.PostModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class DatabaseHelper {
    private Context ct;
    private final String DELETING_RESERVATION="DELETING_RESERVATION";
    private final String MAKING_RESERVATION="MAKING_RESERVATION";

    public DatabaseHelper(Context ct) {
        this.ct = ct;
    }


    public void checkIfRideIsReserved(PostModel pm){
        SQLiteDatabase objDbRead = new Database(ct).getReadableDatabase();
        SQLiteDatabase objDbWrite=new Database(ct).getWritableDatabase();
        Cursor cursor = objDbRead.query(Database.reservedTable,new String[]{RideModel.Id,
                        RideModel.OwnerID,RideModel.OwnerName,RideModel.FromWhere,RideModel.ToWhere,
                        RideModel.DepartureTime,RideModel.Date,RideModel.Price,RideModel.FreeSeats,
                        RideModel.PhoneNumber,RideModel.ExtraInfo,RideModel.TimePosted},"",
                new String[]{},"","","");
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){

            if(cursor.getString(0).equals(pm.getId())){
                ContentValues cv=new ContentValues();
                cv.put(RideModel.FreeSeats,pm.getFreeSeats());
                objDbWrite.update(Database.reservedTable,cv,"id = ?",new String[]{pm.getId()});
            }

            cursor.moveToNext();
        }
        cursor.close();
        objDbRead.close();
        objDbWrite.close();
    }


    public void saveToDatabase(String id,String ownerId,String ownerName,String from,String to,String departureTime,String date,
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

        SQLiteDatabase objDb=new Database(ct).getWritableDatabase();
        try{
            long retValue=objDb.insert(Database.reservedTable,null,cv);
            if(retValue>0){
                saveToRealTimeDb(id, freeSeats,MAKING_RESERVATION);
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
    private void saveToRealTimeDb(String id,int freeSeats,String action){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        if(action.equals(MAKING_RESERVATION)){
            mDatabase.child("Posts").child(id).child("freeSeats").setValue(freeSeats-1).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ct,"U rezervua",Toast.LENGTH_SHORT).show();
                }
            });
        }else if(action.equals(DELETING_RESERVATION)){
            mDatabase.child("Posts").child(id).child("freeSeats").setValue(freeSeats+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ct,"U fshi",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public boolean deleteReservation(String id, int freeSeats){
        SQLiteDatabase objDb=new Database(ct).getWritableDatabase();
        try{
            int retValue = objDb.delete(Database.reservedTable,"ID = ?",new String[] {id});
            if(retValue>0){
                saveToRealTimeDb(id,freeSeats,DELETING_RESERVATION);
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


}
