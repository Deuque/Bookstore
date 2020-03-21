package com.dcinspirations.bookstore;

import android.content.Intent;
import android.os.Bundle;

import com.dcinspirations.bookstore.models.UserModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    ImageView od;
    public DrawerLayout drawer;
    public NavigationView navigationView;
    TextView name,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        name = navigationView.getHeaderView(0).findViewById(R.id.name);
        email = navigationView.getHeaderView(0).findViewById(R.id.email);
        if(new Sp(this).getAdminLoggedIn()){
            navigationView.inflateMenu(R.menu.activity_main_drawer2);
        }else{
            navigationView.inflateMenu(R.menu.activity_main_drawer);
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_cart, R.id.nav_delivery,
                R.id.nav_profile,R.id.nav_Add)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawer.closeDrawer(navigationView);
                if(menuItem.getItemId() == R.id.nav_logout){
                    logout();
                }else{

                    Navigation.findNavController(findViewById(R.id.nav_host_fragment)).navigate(menuItem.getItemId());
                }
                return false;
            }
        });
        setData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_logout:
               logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setData(){
        if(!new Sp(this).getAdminLoggedIn()) {
            String uid = FirebaseAuth.getInstance().getUid();
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel um = dataSnapshot.getValue(UserModel.class);
                    name.setText(um.getName().toUpperCase());
                    email.setText(um.getEmail());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            name.setText("ADMIN");
            email.setText("Admin");
        }

    }

    public void logout(){
        if(!new Sp(this).getAdminLoggedIn()) {
            FirebaseAuth.getInstance().signOut();
        }else{
            new Sp(this).setAdminLoggedIn(false);
        }
        startActivity(new Intent(this,Validation.class));
        finish();

    }
//    public void changeNavigation(){
//        NavHostFragment finalHost = NavHostFragment.create(R.navigation.example_graph);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.nav_host_fragment, finalHost)
//                .setPrimaryNavigationFragment(finalHost) // equivalent to app:defaultNavHost="true"
//                .commit();
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Sp(this).clearCheckOutList();
    }
}
