package com.fiek.hitchhikerkosova.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fiek.hitchhikerkosova.PostModel;

public class DatabaseHelper {
    Context ct;

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
}
