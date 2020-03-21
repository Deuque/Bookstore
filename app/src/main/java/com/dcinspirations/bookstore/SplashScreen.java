package com.dcinspirations.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    Handler h= new Handler();
    Runnable r =new Runnable() {
        @Override
        public void run() {
            if(FirebaseAuth.getInstance().getCurrentUser()==null&&!new Sp(getApplicationContext()).getAdminLoggedIn()) {
                startActivity(new Intent(SplashScreen.this, Validation.class));
            }else{
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
            }
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        try{
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch (Exception e){}
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((TextView)findViewById(R.id.logo_text)).setVisibility(View.VISIBLE);
            }
        },1500);



    }

    @Override
    protected void onResume() {
        super.onResume();
        h.postDelayed(r,4000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        h.removeCallbacks(r);
    }
}
