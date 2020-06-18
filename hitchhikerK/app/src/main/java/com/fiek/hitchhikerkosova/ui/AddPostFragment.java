package com.fiek.hitchhikerkosova.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fiek.hitchhikerkosova.LogInActivity;
import com.fiek.hitchhikerkosova.MainActivity;
import com.fiek.hitchhikerkosova.PostModel;
import com.fiek.hitchhikerkosova.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPostFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Class fragmentClass;
    FragmentManager fragmentManager;
    Fragment fragment = null;

    TimePickerDialog picker;
    EditText etSelectTime;
    EditText etSelectDate;
    Button btnAddPostFunc;
    Spinner spFrom,spTo,spFreeSeats;
    EditText etPrice, etPhoneNumber,etExtraInfo;
    DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Date date=new Date();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public AddPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPostFragment newInstance(String param1, String param2) {
        AddPostFragment fragment = new AddPostFragment();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.AddPostTitle));
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        spFrom=view.findViewById(R.id.spFrom);
        spTo=view.findViewById(R.id.spTo);
        etSelectTime=view.findViewById(R.id.etSelectTime);
        etSelectDate=view.findViewById(R.id.etSelectDate);
        etPrice=view.findViewById(R.id.etPrice);
        spFreeSeats=view.findViewById(R.id.spFreeSeats);
        etPhoneNumber=view.findViewById(R.id.etPhoneNumber);
        etExtraInfo=view.findViewById(R.id.etExtraInfo);
        btnAddPostFunc=view.findViewById(R.id.btnAddPost);

        etSelectTime.setInputType(InputType.TYPE_NULL);
        etSelectDate.setInputType(InputType.TYPE_NULL);

        mDatabase= FirebaseDatabase.getInstance().getReference();
        btnAddPostFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPostToDatabase(spFrom.getSelectedItem().toString(),
                        spTo.getSelectedItem().toString(),
                        etSelectTime.getText().toString(),
                        etSelectDate.getText().toString(),
                        Double.parseDouble(etPrice.getText().toString()),
                        Integer.parseInt(spFreeSeats.getSelectedItem().toString()),
                        etPhoneNumber.getText().toString(),
                        etExtraInfo.getText().toString(),
                        date.getTime());
            }
        });


        //Listener per selectimin e ores permes klases TimePickerDialog
        etSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                //formatimi i ores
                                if(sHour<10 && sMinute<10) {
                                    etSelectTime.setText("0" + sHour + ":0" + sMinute);
                                }else if(sHour<10 ) {
                                    etSelectTime.setText("0" + sHour + ":" + sMinute);
                                }else if(sMinute<10){
                                    etSelectTime.setText(sHour + ":0" + sMinute);
                                }else{
                                etSelectTime.setText(sHour + ":" + sMinute);
                                }
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        //Listener per selektimin e dates permes klases DatePickerDialog
        etSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                //formatimi i ores (+1 per arsye se DatePickerDialog e shfaq muajin per 1 ma pak !?)
                                if(dayOfMonth <10 && monthOfYear<9) {
                                    etSelectDate.setText("0" + dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year);
                                }else if(dayOfMonth<10){
                                    etSelectDate.setText("0"+dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                }else if(monthOfYear<9){
                                    etSelectDate.setText(dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year);
                                }else{
                                    etSelectDate.setText(dayOfMonth + "/" + (monthOfYear+1) + "/" + year);
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });


    }
    private void addNewPostToDatabase(String from,String to,String departureTime,String date,double price,
                                      int freeSeats,String phoneNumber,String extraInfo,long currentTime){
        PostModel postModel=new PostModel(currentUser.getUid(),currentUser.getDisplayName(),from,to,
                departureTime,date,price,freeSeats,phoneNumber,extraInfo,currentTime);
        mDatabase.child("Posts").push().setValue(postModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(),"U postu",Toast.LENGTH_SHORT).show();

                NavController navController= Navigation.findNavController(getActivity(),R.id.nav_host_fragment);
                navController.navigate(R.id.mainPostsFragment);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }



}
