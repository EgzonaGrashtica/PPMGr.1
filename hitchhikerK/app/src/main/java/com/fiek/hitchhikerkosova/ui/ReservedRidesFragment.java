package com.fiek.hitchhikerkosova.ui;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.fiek.hitchhikerkosova.Db.Database;
import com.fiek.hitchhikerkosova.Db.RideModel;
import com.fiek.hitchhikerkosova.PostModel;
import com.fiek.hitchhikerkosova.R;
import com.fiek.hitchhikerkosova.adapters.PostsAdapter;

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

    RecyclerView recyclerView;
    PostsAdapter postsAdapter;
    SwipeRefreshLayout refreshLayout;

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
        postsAdapter=new PostsAdapter(getContext(),"ReservedRidesFragment");
        new LoadDataCls().execute();
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

        recyclerView=getActivity().findViewById(R.id.recyclerView);
        recyclerView.setAdapter(postsAdapter);
        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        refreshLayout=getActivity().findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // postsAdapter.notifyDataSetChanged();
                        new LoadDataCls().execute();
                    }
                });

                refreshLayout.setRefreshing(false);
            }
        });
    }

    public class LoadDataCls extends AsyncTask<Void,Void,List<PostModel>>{

        @Override
        protected List<PostModel> doInBackground(Void... voids) {
            List<PostModel> tempDataSource = new ArrayList<PostModel>();
            SQLiteDatabase objDb = new Database(getContext()).getReadableDatabase();
            Cursor cursor = objDb.query(Database.reservedTable,new String[]{RideModel.Id,
                    RideModel.OwnerID,RideModel.OwnerName,RideModel.FromWhere,RideModel.ToWhere,
                    RideModel.DepartureTime,RideModel.Date,RideModel.Price,RideModel.FreeSeats,
                    RideModel.PhoneNumber,RideModel.ExtraInfo,RideModel.TimePosted},"",
                    new String[]{},"","","");
            cursor.moveToFirst();

            while(!cursor.isAfterLast()){
                PostModel tempModel=new PostModel(cursor.getString(1),cursor.getString(2),
                        cursor.getString(3),cursor.getString(4),cursor.getString(5),
                        cursor.getString(6),cursor.getDouble(7),cursor.getInt(8),
                        cursor.getString(9),cursor.getString(10),cursor.getLong(11));
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
