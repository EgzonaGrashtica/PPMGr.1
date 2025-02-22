package com.fiek.hitchhikerkosova.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.fiek.hitchhikerkosova.models.RideModel;

public class Database extends SQLiteOpenHelper {
    public static final String reservedTable= "Reserved";

    public Database(@Nullable Context context) {
        super(context, "hitchikerDb", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String strQuery="create table "+reservedTable+" ("+
               RideModel.Id+" text primary key,"+
                RideModel.OwnerID+" text not null,"+
                RideModel.OwnerName+" text not null,"+
                RideModel.FromWhere+" text not null,"+
                RideModel.ToWhere+" text not null,"+
                RideModel.DepartureTime+" text not null,"+
                RideModel.Date+" text not null,"+
                RideModel.Price+" real not null,"+
                RideModel.FreeSeats+" integer not null,"+
                RideModel.PhoneNumber+" text not null,"+
                RideModel.ExtraInfo+" text not null,"+
                RideModel.TimePosted+" integer not null,"+
                RideModel.NumberOfReservations+" integer not null"+")";
        db.execSQL(strQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE "+reservedTable+" ADD COLUMN "+RideModel.NumberOfReservations+" integer default 0 not null");
    }
}



