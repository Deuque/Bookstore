package com.dcinspirations.bookstore.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dcinspirations.bookstore.BaseApp;
import com.dcinspirations.bookstore.CheckOut;
import com.dcinspirations.bookstore.R;
import com.dcinspirations.bookstore.Sp;
import com.dcinspirations.bookstore.adapters.CartAdapter;
import com.dcinspirations.bookstore.models.BookModel;
import com.dcinspirations.bookstore.models.CheckoutModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    CartAdapter cartAdapter;
    Button checkout;
    ArrayList<BookModel> booklist;
    public ArrayList<CheckoutModel> checkoutlist;
    TextView empty,totamount;
    LinearLayout load;
    RelativeLayout total;
    Toolbar tb;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        booklist = new ArrayList<>();
        checkoutlist = new ArrayList<>();
        checkout = root.findViewById(R.id.checkout);
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processCheckout();
            }
        });
        load = root.findViewById(R.id.load);
        empty = root.findViewById(R.id.empty);
        tb = root.findViewById(R.id.tb);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        total = root.findViewById(R.id.total);
        totamount = root.findViewById(R.id.totamount);
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View root = getView();
        RecyclerView rv = getView().findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(RecyclerView.VERTICAL);
        cartAdapter = new CartAdapter(getContext(), booklist, CartFragment.this);
        rv.setAdapter(cartAdapter);
        rv.setLayoutManager(llm);
        populatePosts();
    }

    public void populatePosts() {
        booklist.clear();
        String uid = FirebaseAuth.getInstance().getUid();
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("cart");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                booklist.clear();
                int count = 0;
                for(final DataSnapshot snaps : dataSnapshot.getChildren()){
                    count++;
                    String bookkeys= snaps.getValue(String.class);
                    DatabaseReference dbref2 = FirebaseDatabase.getInstance().getReference().child("Store").child(bookkeys);
                    dbref2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                BookModel bm = dataSnapshot.getValue(BookModel.class);
                                try{
                                    bm.setKey(snaps.getKey());
                                    booklist.add(0, bm);
                                    cartAdapter.notifyDataSetChanged();
                                }catch (Exception e) {
                                   return;
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                load.setVisibility(View.VISIBLE);
                if(count<1){
                    load.getChildAt(0).setVisibility(View.GONE);
                    empty.setText("No item here");
                    checkout.setVisibility(View.GONE);
                    total.setVisibility(View.GONE);
                }else{
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

    private void processCheckout() {
        if (BaseApp.checkoutlist.isEmpty()){
            Toast.makeText(getContext(), "No Selected Item", Toast.LENGTH_LONG).show();
        }else{
//            String checkoutAsString = new Gson().toJson(checkoutlist);
             startActivity(new Intent(getContext(), CheckOut.class));
        }

    }

    public void checkList(){
        load.setVisibility(View.VISIBLE);
        if(booklist.size()<1){
            load.getChildAt(0).setVisibility(View.GONE);
            empty.setText("No item here");
            checkout.setVisibility(View.GONE);
            total.setVisibility(View.GONE);
        }else{
            load.setVisibility(View.GONE);
        }
    }

    public void setCheckoutAmount(){
        totamount.setText("₦"+resolveMoney(Integer.toString(getAmount())));
    }
    private int getAmount() {
        int amount = 0;
        for (CheckoutModel cm : BaseApp.checkoutlist) {
            int tam = Integer.parseInt(cm.getBookModel().getPrice().replaceAll(",", ""))*Integer.parseInt(cm.getQuantity());
            amount = amount + tam;
        }
        return amount;

    }

    private String resolveMoney(String amount) {
        String newamount = "";
        String bram[] = amount.split("");
        int count = 0;
        for (int i = bram.length - 1; i >= 1; i--) {
            newamount = bram[i] + newamount;
            count++;
            if (count % 3==0) {
                newamount = "," + newamount;

            }
        }

        return newamount;
    }

    @Override
    public void onResume() {
        super.onResume();
        setCheckoutAmount();
    }
}