package com.fiek.hitchhikerkosova.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fiek.hitchhikerkosova.R;
import com.fiek.hitchhikerkosova.activities.MainActivity;
import com.fiek.hitchhikerkosova.utils.UploadProfilePicture;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final int TAKE_IMAGE_CODE=10001;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Bitmap bitmap;
    private TextView tvDeletePic;
    private EditText etEditProfileEmail,etEditProfileName;
    private ShapeableImageView profilePic;
    private ImageButton btnEditName;
    private Button btnSaveChanges;
    private Boolean changedProfilePic=false;
    private Boolean deletedProfilePic=false;
    private FirebaseUser user ;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
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
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvDeletePic=(TextView)view.findViewById(R.id.tvEditProfileDeletePic);
        etEditProfileEmail=(EditText)view.findViewById(R.id.etEditProfileEmail);
        etEditProfileName=(EditText)view.findViewById(R.id.etEditProfileName);
        profilePic=(ShapeableImageView)view.findViewById(R.id.profilePicEditProfile);
        btnEditName=(ImageButton)view.findViewById(R.id.btnEditName);
        btnSaveChanges=(Button)view.findViewById(R.id.btnSaveChanges);

        etEditProfileName.setClickable(false);
        etEditProfileName.setEnabled(false);
        etEditProfileEmail.setClickable(false);
        etEditProfileEmail.setEnabled(false);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProfilePic(v);
            }
        });
        btnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("testingIMG","test");
                etEditProfileName.setClickable(true);
                etEditProfileName.setEnabled(true);
                etEditProfileName.requestFocus();

            }
        });
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileChanges();
            }
        });

        tvDeletePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePicFunc();
            }
        });
        if(user.getPhotoUrl() !=null){
            Glide.with(getActivity()).load(user.getPhotoUrl()).into(profilePic);
        }
        etEditProfileName.setText(user.getDisplayName());
        etEditProfileEmail.setText(user.getEmail());

    }

    private void addProfilePic(View view){
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(cameraIntent, TAKE_IMAGE_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TAKE_IMAGE_CODE){
            switch (resultCode){
                case RESULT_OK:
                    bitmap=(Bitmap) data.getExtras().get("data");
                    profilePic.setImageBitmap(bitmap);
                    deletedProfilePic=false;
                    changedProfilePic=true;
            }
        }
    }

    private void deleteProfilePic(){
        profilePic.setImageResource(R.drawable.add_photo);
        changedProfilePic=false;
        deletedProfilePic=true;
    }

    Boolean nameChanged = false;
    Boolean picChanged=false;
    private void saveProfileChanges(){
        String name = etEditProfileName.getText().toString().trim();

        if(name.isEmpty()){
            etEditProfileName.setError(getString(R.string.emptyName));
            etEditProfileName.requestFocus();
            return;
        }
        if(!user.getDisplayName().equals(name)){
            UserProfileChangeRequest request= new UserProfileChangeRequest.Builder().
                    setDisplayName(name).build();
            user.updateProfile(request);
            etEditProfileName.setEnabled(false);
            etEditProfileName.setClickable(false);
            nameChanged=true;
        }
        if(deletedProfilePic){
            UserProfileChangeRequest request= new UserProfileChangeRequest.Builder().setPhotoUri(null).build();
            user.updateProfile(request);
             StorageReference reference= FirebaseStorage.getInstance().getReference()
                    .child("profileImages").child(user.getUid()+".jpeg");
             reference.delete();
             picChanged=true;
        }else if(changedProfilePic){
             UploadProfilePicture upPic=new UploadProfilePicture(getContext(),user);
            upPic.handleUpload(bitmap);
            profilePic.setImageBitmap(bitmap);
            picChanged=true;
        }
        if(nameChanged || picChanged){
            Toast.makeText(getContext(),"Profile updated!",Toast.LENGTH_LONG).show();
        }
        changedProfilePic=false;
        deletedProfilePic=false;
        nameChanged=false;
        picChanged=false;
    }

    private void deletePicFunc(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(R.string.alertDeletePic)
                .setPositiveButton(R.string.btnConfrimDelete, new DialogInterface.OnClickListener()                 {

                    public void onClick(DialogInterface dialog, int which) {
                        changedProfilePic=false;
                        deletedProfilePic=true;
                        profilePic.setImageResource(R.drawable.add_photo);

                    }
                }).setNegativeButton(R.string.btnCancel, null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }
}
