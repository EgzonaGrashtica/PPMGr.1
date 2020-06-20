package com.fiek.hitchhikerkosova.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fiek.hitchhikerkosova.db.Database;
import com.fiek.hitchhikerkosova.db.DatabaseHelper;
import com.fiek.hitchhikerkosova.db.RideModel;
import com.fiek.hitchhikerkosova.models.PostModel;
import com.fiek.hitchhikerkosova.R;
import com.fiek.hitchhikerkosova.adapters.PostsAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReservedRidesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReservedRidesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public List<PostModel> tempDataSource = new ArrayList<PostModel>();
    RecyclerView recyclerView;
    private PostsAdapter postsAdapter;
    SwipeRefreshLayout refreshLayout;
    DatabaseHelper dbHelper;


    public ReservedRidesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReservedRidesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReservedRidesFragment newInstance(String param1, String param2) {
        ReservedRidesFragment fragment = new ReservedRidesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        dbHelper=new DatabaseHelper(getContext());

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PostModel postModel=dataSnapshot.getValue(PostModel.class);
                postModel.setId(dataSnapshot.getKey());
                tempDataSource.add(postModel);
                dbHelper.checkIfRideIsReservedAndUpdate(postModel);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PostModel postModelChanged=dataSnapshot.getValue(PostModel.class);
                postModelChanged.setId(dataSnapshot.getKey());
                dbHelper.checkIfRideIsReservedAndUpdate(postModelChanged);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                PostModel postModelRemoved=dataSnapshot.getValue(PostModel.class);
                postModelRemoved.setId(dataSnapshot.getKey());
                dbHelper.checkIfRideIsReservedAndDelete(postModelRemoved);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.ReservedRidesTitle));

        recyclerView=view.findViewById(R.id.recyclerView);

        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postsAdapter=new PostsAdapter(getContext(),"ReservedRidesFragment");
        recyclerView.setAdapter(postsAdapter);

        refreshLayout=view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new LoadDataCls().execute();
                    }
                });

                refreshLayout.setRefreshing(false);
            }
        });
        refreshLayout.setRefreshing(true);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dbHelper.checkIfReservedExist(tempDataSource);
                new LoadDataCls().execute();
                refreshLayout.setRefreshing(false);
            }
        }, 3000);
    }

    public class LoadDataCls extends AsyncTask<Void,Void,List<PostModel>>{

        @Override
        protected List<PostModel> doInBackground(Void... voids) {
            List<PostModel> tempDataSource = new ArrayList<PostModel>();
            SQLiteDatabase objDb = new Database(getContext()).getReadableDatabase();
            Cursor cursor = objDb.query(Database.reservedTable,new String[]{RideModel.Id,
                    RideModel.OwnerID,RideModel.OwnerName,RideModel.FromWhere,RideModel.ToWhere,
                    RideModel.DepartureTime,RideModel.Date,RideModel.Price,RideModel.FreeSeats,
                    RideModel.PhoneNumber,RideModel.ExtraInfo,RideModel.TimePosted,RideModel.NumberOfReservations},"",
                    new String[]{},"","","");
            cursor.moveToFirst();

            while(!cursor.isAfterLast()){
                PostModel tempModel=new PostModel(cursor.getString(1),cursor.getString(2),
                        cursor.getString(3),cursor.getString(4),cursor.getString(5),
                        cursor.getString(6),cursor.getDouble(7),cursor.getInt(8),
                        cursor.getString(9),cursor.getString(10),cursor.getLong(11),cursor.getInt(12));
                tempModel.setId(cursor.getString(0));

                tempDataSource.add(tempModel);
                cursor.moveToNext();
            }
            cursor.close();
            objDb.close();
            return tempDataSource;
        }

        @Override
        protected void onPostExecute(List<PostModel> postModels) {
            super.onPostExecute(postModels);
            postsAdapter.dataSource=postModels;
            postsAdapter.notifyDataSetChanged();
        }
    }
}
