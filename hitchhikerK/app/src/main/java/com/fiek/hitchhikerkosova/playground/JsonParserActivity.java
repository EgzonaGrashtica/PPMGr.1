package com.fiek.hitchhikerkosova.playground;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fiek.hitchhikerkosova.R;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JsonParserActivity extends AppCompatActivity {
    List<UserParser> users=new ArrayList<UserParser>();
    Button btnLogJson;
    TextView tvID,tvName,tvUsername,tvEmail,tvStreet,tvSuite,tvCity,tvZipcode,tvGeoLat,
            tvGeoLang,tvPhone,tvWebsite,tvCompanyName,tvCatchphrase,tvBs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_parser);
        new LoadJsonDataCls().execute();
        btnLogJson=findViewById(R.id.btnLogJson);
        tvID=findViewById(R.id.tvId);
        tvName=findViewById(R.id.tvName);
        tvUsername=findViewById(R.id.tvUsername);
        tvEmail=findViewById(R.id.tvEmail);
        tvStreet=findViewById(R.id.tvStreet);
        tvSuite=findViewById(R.id.tvSuite);
        tvCity=findViewById(R.id.tvCity);
        tvZipcode=findViewById(R.id.tvZipcode);
        tvGeoLat=findViewById(R.id.tvLat);
        tvGeoLang=findViewById(R.id.tvLang);
        tvPhone=findViewById(R.id.tvPhone);
        tvWebsite=findViewById(R.id.tvWebsite);
        tvCompanyName=findViewById(R.id.tvCompanyName);
        tvCatchphrase=findViewById(R.id.tvCatchphrase);
        tvBs=findViewById(R.id.tvBs);


        btnLogJson.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tvID.setText(String.valueOf(users.get(0).getId()));
                tvName.setText(users.get(0).getName());
                tvUsername.setText(users.get(0).getUsername());
                tvEmail.setText(users.get(0).getEmail());
                tvStreet.setText(users.get(0).getAddress().getStreet());
                tvSuite.setText(users.get(0).getAddress().getSuite());
                tvCity.setText(users.get(0).getAddress().getCity());
                tvZipcode.setText(users.get(0).getAddress().getZipcode());
                tvGeoLat.setText(users.get(0).getAddress().getGeo().getLat());
                tvGeoLang.setText(users.get(0).getAddress().getGeo().getLng());
                tvPhone.setText(users.get(0).getPhone());
                tvWebsite.setText(users.get(0).getWebsite());
                tvCompanyName.setText(users.get(0).getCompany().getName());
                tvCatchphrase.setText(users.get(0).getCompany().getCatchPhrase());
                tvBs.setText(users.get(0).getCompany().getBs());

            }
        });

    }

    public class LoadJsonDataCls extends AsyncTask<Void,Void, List<UserParser>>{

        @Override
        protected List<UserParser> doInBackground(Void... voids) {
            InputStream inputStream;
            List<UserParser> tempUsers=new ArrayList<UserParser>();
            try{
                inputStream=getAssets().open("jsonToParse.json");
                InputStreamReader isReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isReader);
                StringBuffer sb = new StringBuffer();
                String str;
                while((str = reader.readLine())!= null){
                    sb.append(str);
                }
                JSONArray jsonArray=new JSONArray(sb.toString());
                for(int i=0; i<jsonArray.length();i++){
                    UserParser userParserObject = new Gson().fromJson(jsonArray.getString(i),UserParser.class);
                    tempUsers.add(userParserObject);
                }
                return tempUsers;

            }catch (Exception e){
                Log.e("JsonParsingError",e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<UserParser> tempUsers) {
            super.onPostExecute(tempUsers);
            users=tempUsers;
        }
    }
}
