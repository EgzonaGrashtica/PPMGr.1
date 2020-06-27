package com.fiek.hitchhikerkosova.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fiek.hitchhikerkosova.R;
import com.fiek.hitchhikerkosova.adapters.LanguageAdapter;
import com.fiek.hitchhikerkosova.language.LocaleHelper;
import com.fiek.hitchhikerkosova.models.LanguageModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;

public class WelcomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN=10002;
    Spinner langSpinner;
    private ArrayList<LanguageModel> languageModels;
    private LanguageAdapter languageAdapter;
    TextView tvTitle,tvAlreadyRegistered;
    Button btnWelcomeSignUp,btnGoogleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        String prefLang=LocaleHelper.getLanguage(WelcomeActivity.this);
        tvTitle=(TextView) findViewById(R.id.tvWelcomeTitle);
        tvAlreadyRegistered=(TextView) findViewById(R.id.tvAlreadyRegistered);
        btnGoogleSignIn=(Button) findViewById(R.id.btnGoogleSignIn);
        btnWelcomeSignUp=(Button) findViewById(R.id.btnWelcomeSignUp);


        mAuth=FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_sign_in_token))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        languageModels=new ArrayList<>();
        LanguageModel enModel=new LanguageModel("En",R.drawable.en_flag);
        LanguageModel alModel=new LanguageModel("Al",R.drawable.al_flag);
        languageModels.add(enModel);
        languageModels.add(alModel);

        langSpinner=(Spinner) findViewById(R.id.langSpinner);
        languageAdapter=new LanguageAdapter(this, languageModels);
        langSpinner.setAdapter(languageAdapter);


        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LanguageModel langClicked = (LanguageModel) parent.getItemAtPosition(position);
                String clickedLanguageTxt = langClicked.getLanguageTxt();
                String localeLanguage;
                switch(clickedLanguageTxt) {
                    case "En":
                        localeLanguage="en";
                        break;
                    case "Al":
                        localeLanguage="sq";
                        break;
                    default:
                        localeLanguage="en";
                        break;
                }
                LocaleHelper.setLocale(WelcomeActivity.this, localeLanguage);
                refreshLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        switch (prefLang){
            case "en":
                langSpinner.setSelection(languageAdapter.getPosition(enModel));
                break;
            case "sq":
                langSpinner.setSelection(languageAdapter.getPosition(alModel));
                break;
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null){
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        }
    }

    public void btnWelcomeSignUp(View v){
        startActivity(new Intent(WelcomeActivity.this, SignUpActivity.class));
    }
    public void tvAlreadyRegistered(View v){
        startActivity(new Intent(WelcomeActivity.this, LogInActivity.class));
    }
    public void btnGoogleSignIn(View v){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("Welcome Activity", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed
                Log.w("Welcome Activity", "Google sign in failed", e);
                Toast.makeText(WelcomeActivity.this,R.string.google_sign_in_failed,Toast.LENGTH_LONG).show();
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d("Welcome Activity", "signInWithCredential:success");
                            startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                            finish();
                        } else {
                            //Sign in fails
                            Log.w("Welcome Activity", "signInWithCredential:failure", task.getException());
                            Toast.makeText(WelcomeActivity.this,R.string.google_sign_in_failed,Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
    private void refreshLayout(){
        tvTitle.setText(R.string.tvWelcome);
        tvAlreadyRegistered.setText(R.string.tvAlreadyRegistered);
        btnWelcomeSignUp.setText(R.string.btnSignUpTxt);
        btnGoogleSignIn.setText(R.string.btnGoogleSignInTxt);
    }
}
