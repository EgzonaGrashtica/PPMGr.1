package com.fiek.hitchhikerkosova;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        etEmail=(EditText) findViewById(R.id.etEmail);
        etPassword=(EditText) findViewById(R.id.etPassword);
        chbRememberMe=(CheckBox) findViewById(R.id.chbRememberMe);
        btnLogin=(Button) findViewById(R.id.btnLogIn);
        mAuth = FirebaseAuth.getInstance();

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
                                startActivity(new Intent(LogInActivity.this, MainActivity.class));
                                finish();
                            } else {
                                btnLogin.setEnabled(true);
                                // If sign in fails, display a message to the user.
                                Log.w("Debug Info", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LogInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                etEmail.getText().clear();
                                etPassword.getText().clear();
                            }


                        }
                    });
        }


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
}
