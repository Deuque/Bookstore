package com.dcinspirations.bookstore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.dcinspirations.bookstore.MainActivity;
import com.dcinspirations.bookstore.R;
import com.dcinspirations.bookstore.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    LinearLayout bottomSheet;
    View view;
    BottomSheetBehavior bottomSheetBehavior;
    Button submit;
    private TextView uname, umail, unumber, uaddress;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        uname = root.findViewById(R.id.user_name);
        umail = root.findViewById(R.id.user_email);
        unumber = root.findViewById(R.id.mnumber);
        uaddress = root.findViewById(R.id.delloc);
        setData();
        ((Toolbar)root.findViewById(R.id.tb)).setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        bottomSheet = root.findViewById(R.id.bottomSheet);
        view = root.findViewById(R.id.bg);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        view.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
//                        btnBottomSheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
//                        btnBottomSheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }


            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                view.setVisibility(View.VISIBLE);
                view.setAlpha(slideOffset);
            }
        });


        submit = root.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
        root.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBS();
            }
        });
        root.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).logout();
            }
        });
        return root;
    }

    private void updateProfile() {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        dbref.child("Name").setValue(((TextView)getView().findViewById(R.id.updatename)).getText().toString().trim());
        dbref.child("Address").setValue(((TextView)getView().findViewById(R.id.updateloc)).getText().toString().trim());
        dbref.child("MNumber").setValue(((TextView)getView().findViewById(R.id.updatenumber)).getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), "Profile Updated Successfully", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setData() {
        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel um = dataSnapshot.getValue(UserModel.class);
                uname.setText(um.getName().toUpperCase());
                ((TextView)getView().findViewById(R.id.updatename)).setText(um.getName());
                umail.setText(um.getEmail());
                unumber.setText(um.getMNumber());
                ((TextView)getView().findViewById(R.id.updatenumber)).setText(um.getMNumber());
                uaddress.setText(um.getAddress().toUpperCase());
                ((TextView)getView().findViewById(R.id.updateloc)).setText(um.getAddress());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void openBS(){
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }
}