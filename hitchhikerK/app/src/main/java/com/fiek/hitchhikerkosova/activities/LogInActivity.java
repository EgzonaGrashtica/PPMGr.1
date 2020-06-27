package com.fiek.hitchhikerkosova.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.fiek.hitchhikerkosova.R;
import com.fiek.hitchhikerkosova.language.LocaleHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    EditText etEmail;
    EditText etPassword;
    String email;
    String password;
    CheckBox chbRememberMe;
    SharedPreferences sharedPreferences;
    Button btnLogin;
    ConstraintLayout logInView;
    Snackbar snackbar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        etEmail=(EditText) findViewById(R.id.etEmail);
        etPassword=(EditText) findViewById(R.id.etPassword);
        chbRememberMe=(CheckBox) findViewById(R.id.chbRememberMe);
        btnLogin=(Button) findViewById(R.id.btnLogIn);
        logInView=(ConstraintLayout) findViewById(R.id.logInView);


        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogInFunc(v);
            }
        });


    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null){
            startActivity(new Intent(LogInActivity.this, MainActivity.class));
            finish();
        }
        sharedPreferences=getPreferences(0);
        String storedEmail=sharedPreferences.getString(getString(R.string.prefEmailKey),null);
        if(storedEmail != null){
            etEmail.setText(storedEmail);
            chbRememberMe.setChecked(true);
        }


    }

    public void btnLogInFunc(View v){
        email= etEmail.getText().toString().trim();
        password= etPassword.getText().toString().trim();
        if(validateLogInData()){
            btnLogin.setEnabled(false);

            showLogInSnackBar();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Debug Info", "signInWithEmail:success");
                                if(chbRememberMe.isChecked()){
                                    sharedPreferences=getPreferences(0);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(getString(R.string.prefEmailKey),etEmail.getText().toString());
                                    editor.apply();
                                }
                                btnLogin.setEnabled(true);
                                snackbar.dismiss();
                                startActivity(new Intent(LogInActivity.this, MainActivity.class));
                                finish();
                            } else {
                                btnLogin.setEnabled(true);
                                snackbar.dismiss();
                                // If sign in fails, display a message to the user.
                                Log.w("Debug Info", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LogInActivity.this,R.string.toastAuthFailed,
                                        Toast.LENGTH_SHORT).show();
                                etPassword.getText().clear();
                            }


                        }
                    });
        }


    }

    public void tvForgotPasswordFunc(View v){
        startActivity(new Intent(LogInActivity.this, ResetPasswordActivity.class));
    }

    public void tvRegisterFunc(View v){
        startActivity(new Intent(LogInActivity.this, SignUpActivity.class));

    }
    public boolean validateLogInData(){

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

        if (password.length() < 6) {
            etPassword.setError(getString(R.string.shortPassword));
            etPassword.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.emptyPassword));
            etPassword.requestFocus();
            return false;
        }



        return true;
    }

    private void showLogInSnackBar(){
        snackbar=Snackbar.make(logInView,"",Snackbar.LENGTH_INDEFINITE);
        View customSnackBarView=getLayoutInflater().inflate(R.layout.snackbar_login_custom,null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout=(Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0,0,0,0);
        snackbarLayout.addView(customSnackBarView,0);
        snackbar.show();

    }
}
