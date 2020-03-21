package com.dcinspirations.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dcinspirations.bookstore.models.AdminModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pl.droidsonroids.gif.GifImageView;

public class Validation extends AppCompatActivity {

    TextInputLayout elayout,nlayout,playout,nmlayout;
    EditText email,num,pass,name;
    TextView valtext,next1,next2;
    TextView valaction;
    RelativeLayout valactionlayout;
    GifImageView lgif;
    boolean login = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);

        lgif = findViewById(R.id.lgif);
        email=findViewById(R.id.email);
        num=findViewById(R.id.number);
        name = findViewById(R.id.name);
        pass=findViewById(R.id.password);
        elayout = findViewById(R.id.email_layout);
        nlayout = findViewById(R.id.num_layout);
        playout = findViewById(R.id.pass_layout);
        nmlayout = findViewById(R.id.name_layout);
        valtext= findViewById(R.id.val_text);
        next1 = findViewById(R.id.next1);
        next2 = findViewById(R.id.next2);
        next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeValidationType();
            }
        });
        valaction = findViewById(R.id.val_action);
        valactionlayout = (RelativeLayout) valaction.getParent();
        valactionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(login){
                    Login();
                }else {
                    Register();
                }
            }
        });

    }

    private void changeValidationType(){
        if(login){
            valtext.setText("Welcome, \n Lets Get Started.");
            nlayout.setVisibility(View.VISIBLE);
            nmlayout.setVisibility(View.VISIBLE);
            next1.setText("Have Account?");
            next2.setText("Sign In");
            valaction.setText("SIGN UP");
            login = false;
        }else{
            valtext.setText("Hello, \n Welcome Back.");
            nlayout.setVisibility(View.GONE);
            nmlayout.setVisibility(View.GONE);
            next1.setText("New User?");
            next2.setText("Sign Up");
            valaction.setText("SIGN IN");
            login = true;
        }
        email.setText("");
        name.setText("");
        num.setText("");
        pass.setText("");

    }

    private void Register(){

        final String emailtext = email.getText().toString().trim();
        final String numtext = num.getText().toString().trim();
        String passtext = pass.getText().toString().trim();
        final String nmtext = name.getText().toString().trim();

        if(emailtext.isEmpty()){
            elayout.setError("Email is required");
            email.requestFocus();
            return;
        }
        if(nmtext.isEmpty()){
            nlayout.setError("Name is required");
            name.requestFocus();
            return;
        }
        if(numtext.isEmpty()){
            nlayout.setError("Mobile number is required");
            num.requestFocus();
            return;
        }
        if(passtext.isEmpty()){
            playout.setError("Set a password");
            pass.requestFocus();
            return;
        }
        valaction.setText("Please wait...");
        lgif.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailtext,passtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String uid = FirebaseAuth.getInstance().getUid();
                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    dbref.child("Email").setValue(emailtext);
                    dbref.child("Name").setValue(nmtext);
                    dbref.child("Address").setValue("");
                    dbref.child("MNumber").setValue(numtext).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(Validation.this,MainActivity.class));
                                finish();
                            }else{
                                Toast.makeText(Validation.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                valaction.setText("SIGN UP");
                                lgif.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

                }else{
                    Toast.makeText(Validation.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    valaction.setText("SIGN UP");
                    lgif.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void Login(){

        final String emailtext = email.getText().toString().trim();
        String passtext = pass.getText().toString().trim();

        if(emailtext.isEmpty()){
            elayout.setError("Email is required");
            email.requestFocus();
            return;
        }
        if(passtext.isEmpty()){
            playout.setError("Set a password");
            pass.requestFocus();
            return;
        }
        valaction.setText("Please wait...");
        lgif.setVisibility(View.VISIBLE);
        AdminLogin(emailtext,passtext);


    }

    private void AdminLogin(final String email, final String pass){
        FirebaseDatabase.getInstance().getReference().child("Admins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snaps:dataSnapshot.getChildren()){
                    AdminModel am = snaps.getValue(AdminModel.class);
                    if(email.equalsIgnoreCase(am.getA_email())&&pass.equalsIgnoreCase(am.getA_pass())){
                        startActivity(new Intent(Validation.this,MainActivity.class));
                        new Sp(getApplicationContext()).setAdminLoggedIn(true);
                        finish();
                        return;
                    }
                }
                UserLogin(email,pass);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UserLogin(String email, String pass){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(Validation.this,MainActivity.class));
                    finish();
                }else{
                    Toast.makeText(Validation.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    valaction.setText("SIGN IN");
                    lgif.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
