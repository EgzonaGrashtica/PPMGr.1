package com.fiek.hitchhikerkosova.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fiek.hitchhikerkosova.db.DatabaseHelper;
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
 * Use the {@link MainPostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainPostsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private DatabaseReference mDatabase;

    RecyclerView recyclerView;
    PostsAdapter postsAdapter;
    SwipeRefreshLayout refreshLayout;
    DatabaseHelper dbHelper;
    public MainPostsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainPostsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainPostsFragment newInstance(String param1, String param2) {
        MainPostsFragment fragment = new MainPostsFragment();
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

        dbHelper = new DatabaseHelper(getContext());
        postsAdapter=new PostsAdapter(getContext(),"MainPostsFragment");
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Posts");

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PostModel postModel=dataSnapshot.getValue(PostModel.class);
                postModel.setId(dataSnapshot.getKey());
                postsAdapter.dataSource.add(postModel);
                Log.i("xona","Erdh");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PostModel postModelChanged=dataSnapshot.getValue(PostModel.class);
                postModelChanged.setId(dataSnapshot.getKey());
                for(PostModel pm:postsAdapter.dataSource){
                    if(pm.getId().equals(postModelChanged.getId())){
                        pm.setFreeSeats(postModelChanged.getFreeSeats());
                        pm.setNumberOfReservations(postModelChanged.getNumberOfReservations());
                        postsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                PostModel postModelRemoved=dataSnapshot.getValue(PostModel.class);
                postModelRemoved.setId(dataSnapshot.getKey());
                List<PostModel> dataToBeRemoved=new ArrayList<PostModel>();
                for(PostModel pm:postsAdapter.dataSource){
                    if(pm.getId().equals(postModelRemoved.getId())){
                        dataToBeRemoved.add(pm);
                    }
                }
                postsAdapter.dataSource.removeAll(dataToBeRemoved);
                postsAdapter.notifyDataSetChanged();

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
        toolbar.setTitle(getResources().getString(R.string.MainPostTitle));

        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(postsAdapter);
        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        refreshLayout=view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postsAdapter.notifyDataSetChanged();
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
                postsAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        }, 3000);


    }

}
