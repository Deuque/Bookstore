package com.dcinspirations.bookstore.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dcinspirations.bookstore.BaseApp;
import com.dcinspirations.bookstore.R;
import com.dcinspirations.bookstore.Sp;
import com.dcinspirations.bookstore.adapters.DeliverAdapter;
import com.dcinspirations.bookstore.models.DeliveryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DeliveryFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    LinearLayout load, bottomSheet;
    View view;
    BottomSheetBehavior bottomSheetBehavior;
    Toolbar tb;
    TextView empty;
    DeliverAdapter deliverAdapter;
    ArrayList<DeliveryModel> dellist;
    Button cancel, confirm;
    public String delkey, confirmkey = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_delivery, container, false);
        dellist = new ArrayList<>();
        tb = root.findViewById(R.id.tb);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        empty = root.findViewById(R.id.empty);
        load = root.findViewById(R.id.load);
        RecyclerView rv = root.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(RecyclerView.VERTICAL);
        deliverAdapter = new DeliverAdapter(getContext(), dellist, DeliveryFragment.this);
        rv.setAdapter(deliverAdapter);
        rv.setLayoutManager(llm);

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

        cancel = root.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        confirm = root.findViewById(R.id.confirm);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void deleteOrder() {
        FirebaseDatabase.getInstance().getReference().child("Delivery").child(delkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    openBS();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    Toast.makeText(getContext(), "Order Deleted Successfully", Toast.LENGTH_LONG).show();
                    populatePosts();
                }else{
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void ConfirmDelivery() {
        FirebaseDatabase.getInstance().getReference().child("Delivery").child(confirmkey).child("Status").setValue("delivered");
        openBS();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        Toast.makeText(getContext(), "Order Delivered Successfully", Toast.LENGTH_LONG).show();
    }

    public void openBS() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    public void populatePosts() {
        dellist.clear();
        final String uid = FirebaseAuth.getInstance().getUid();
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Delivery");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dellist.clear();
                int count = 0;

                for (final DataSnapshot snaps : dataSnapshot.getChildren()) {

                    DeliveryModel dm = snaps.getValue(DeliveryModel.class);
                    if (new Sp(getContext()).getAdminLoggedIn()) {
                        dm.setDkey(snaps.getKey());
                        dellist.add(0, dm);
                        count++;
                    } else {
                        if (dm.getPublisher().equalsIgnoreCase(uid)) {
                            dm.setDkey(snaps.getKey());
                            dellist.add(0, dm);
                            count++;

                        }
                    }
                    deliverAdapter.notifyDataSetChanged();
                }

                load.setVisibility(View.VISIBLE);
                if (count < 1) {
                    load.getChildAt(0).setVisibility(View.GONE);
                    empty.setText("No Deliveries yet");
                } else {
                    load.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                load.setVisibility(View.GONE);
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void setBS(String type) {
        TextView heading = getView().findViewById(R.id.heading);
        TextView title = getView().findViewById(R.id.title);
        if (type.equalsIgnoreCase("delete")) {
            heading.setText("Delete Order");
            title.setText("Do you want to delete this order?");
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteOrder();
                }
            });

        } else {
            heading.setText("Confirm Delivery");
            title.setText("Are you sure this item has been delivered");

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmDelivery();

                }
            });
        }


    }

    public void checkdellist(){
        load.setVisibility(View.VISIBLE);
        if (dellist.size() < 1) {
            load.getChildAt(0).setVisibility(View.GONE);
            empty.setText("No Deliveries yet");
        } else {
            load.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        populatePosts();
    }
}