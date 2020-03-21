package com.dcinspirations.bookstore;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dcinspirations.bookstore.models.CheckoutModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BaseApp extends Application {

    public static ArrayList<CheckoutModel> checkoutlist;
    public static Context ctx;

    @Override
    public void onCreate() {
        super.onCreate();
        checkoutlist = new ArrayList<>();
        ctx = getApplicationContext();

        FirebaseDatabase.getInstance().getReference().child("paydeveloper").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                if(!status.equalsIgnoreCase("true")){

                    Toast.makeText(ctx, "Pay the developer", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
