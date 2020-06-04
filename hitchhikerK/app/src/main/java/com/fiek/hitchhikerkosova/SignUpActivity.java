package com.fiek.hitchhikerkosova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    EditText etName;
    EditText etSurname;
    EditText etEmail;
    EditText etPassword;
    EditText etConfirmPassword;

    String name;
    String surname;
    String email;
    String password;
    String confirmPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName=(EditText) findViewById(R.id.etName);
        etSurname=(EditText) findViewById(R.id.etSurname);
        etEmail=(EditText) findViewById(R.id.etEmail);
        etPassword=(EditText) findViewById(R.id.etPassword);
        etConfirmPassword=(EditText) findViewById(R.id.etConfirmPassword);

        mAuth = FirebaseAuth.getInstance();
    }
    public void btnSignUpFunc(View v){

        name= etName.getText().toString().trim();
        surname= etSurname.getText().toString().trim();
        email= etEmail.getText().toString().trim();
        password= etPassword.getText().toString().trim();
        confirmPassword= etConfirmPassword.getText().toString().trim();

        if(validateSignUpData()){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Debug Info", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name+" "+surname).build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("Info", "User profile updated.");
                                                }
                                            }
                                        });

                                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Debug Info", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                etName.getText().clear();
                                etSurname.getText().clear();
                                etEmail.getText().clear();
                                etPassword.getText().clear();
                                etConfirmPassword.getText().clear();
                            }

                            // ...
                        }
                    });
        }

    }
    public boolean validateSignUpData(){

        if(TextUtils.isEmpty(name)){
            etName.setError(getString(R.string.emptyName));
            etName.requestFocus();
            return false;
        }

        if(TextUtils.isEmpty(surname)){
            etSurname.setError(getString(R.string.emptySurname));
            etSurname.requestFocus();
            return false;
        }

        //Na duhet kur te bojna me postu
       /* if(TextUtils.isEmpty(phoneNumber)){
            etPhoneNumber.setError(getString(R.string.emptyPhoneNumber));
            etPhoneNumber.requestFocus();
            return false;
        }

        if(!Patterns.PHONE.matcher(phoneNumber).matches()){
            etPhoneNumber.setError(getString(R.string.validPhoneNumber));
            etPhoneNumber.requestFocus();
            return false;
        }*/

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.validEmail));
            etEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.emptyEmail));
            etEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.emptyPassword));
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError(getString(R.string.shortPassword));
            etPassword.requestFocus();
            return false;
        }

        if(!password.equals(confirmPassword)){
            etPassword.setError(getString(R.string.confirmPassword));
            etPassword.requestFocus();
            return false;
        }

        return true;
    }
}
