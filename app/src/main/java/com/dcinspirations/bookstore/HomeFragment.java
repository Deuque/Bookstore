package com.dcinspirations.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dcinspirations.bookstore.adapters.BookAdapter;
import com.dcinspirations.bookstore.adapters.SliderAdapter;
import com.dcinspirations.bookstore.models.BookModel;
import com.dcinspirations.bookstore.models.CheckoutModel;
import com.dcinspirations.bookstore.models.SliderModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    LinearLayout bottomSheet, load;
    View view, view1;
    BottomSheetBehavior bottomSheetBehavior;
    BookAdapter bookAdapter;
    ArrayList<BookModel> booklist;
    ArrayList<SliderModel> sliderlist;
    ArrayList<String> filearray;
    AutoCompleteTextView act;
    TextView heading,ca;
    CardView carr;
    ImageView od,cancelSearch;
    int car_status = 0;
    private int[] imglist = {R.mipmap.car3, R.mipmap.car1, R.mipmap.car2};
    private String[] textlist = {"If you can think it, Jojo Bookstore has it.",
            "Pick as many as you can, we got delivery covered.",
            "Have books delivered to your doorstep in record time."};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        booklist = new ArrayList<>();
        filearray = new ArrayList<>();
        od = root.findViewById(R.id.open_drawer);
        od.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).drawer.openDrawer(GravityCompat.START);
            }
        });

        load = root.findViewById(R.id.load);

        if(new Sp(getContext()).getAdminLoggedIn()) {
            root.findViewById(R.id.qcart).setVisibility(View.GONE);
            root.findViewById(R.id.cartamount).setVisibility(View.GONE);
            root.findViewById(R.id.abook).setVisibility(View.VISIBLE);
            root.findViewById(R.id.quickcartlayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(getActivity().findViewById(R.id.nav_host_fragment)).navigate(R.id.nav_Add);
                }
            });
        }else{
            resolveCart();
            root.findViewById(R.id.quickcartlayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(getActivity().findViewById(R.id.nav_host_fragment)).navigate(R.id.quickcartlayout);
                }
            });
        }

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
        cancelSearch = root.findViewById(R.id.cancel_search);
        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.setText("");
                populatePosts();
            }
        });
        ca = root.findViewById(R.id.cartamount);
        carr = root.findViewById(R.id.caroussel);
        heading = root.findViewById(R.id.heading);
        act = root.findViewById(R.id.search);
        populateAutoComplete();




        sliderlist = new ArrayList<>();
        for (int i = 0; i < imglist.length; i++) {
            SliderModel sm = new SliderModel(textlist[i], imglist[i]);
            sliderlist.add(sm);
        }
        SliderView sliderView = root.findViewById(R.id.imageSlider);

        SliderAdapter adapter = new SliderAdapter(getContext(), sliderlist);

        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.FILL); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setScrollTimeInSec(5); //set scroll delay in seconds :
        sliderView.startAutoCycle();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View root = getView();


        RecyclerView rv = root.findViewById(R.id.rv);
        GridLayoutManager glm = new GridLayoutManager(getContext(), 2);
        bookAdapter = new BookAdapter(getContext(), booklist, HomeFragment.this);
        rv.setAdapter(bookAdapter);
        rv.setLayoutManager(glm);
        populatePosts();


    }

    private void populatePosts() {
        carr.setVisibility(View.VISIBLE);
        heading.setText("Available Books");
        load.setVisibility(View.VISIBLE);
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Store");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                booklist.clear();
                for (final DataSnapshot snaps : dataSnapshot.getChildren()) {
                    BookModel bm = snaps.getValue(BookModel.class);
                    bm.setKey(snaps.getKey());
                    booklist.add(0, bm);
                }
                checkList("No books yet");
                bookAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                load.setVisibility(View.GONE);
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void controlBS(final BookModel bm,String type) {
        TextView bname, bauthor, bdesc;
        LinearLayout b1layout,confirmlayout;
        final Button cart, buy,cancel,confirm;
        View root = getView();
        b1layout = root.findViewById(R.id.b1layout);
        confirmlayout = root.findViewById(R.id.confirm_layout);
        bname = root.findViewById(R.id.btitle);
        bauthor = root.findViewById(R.id.bauthor);
        bdesc = root.findViewById(R.id.bdesc);
        buy = root.findViewById(R.id.buy);
        cart = root.findViewById(R.id.cart);


        if(type.equalsIgnoreCase("details")) {
            if(new Sp(getContext()).getAdminLoggedIn()){
                buy.setVisibility(View.GONE);
                cart.setVisibility(View.GONE);
            }
            b1layout.setVisibility(View.VISIBLE);
            confirmlayout.setVisibility(View.GONE);
            bname.setText(bm.getTitle());
            bauthor.setText("By " + bm.getAuthor());
            bdesc.setText(bm.getDesc());
            buy.setText("Buy " + "₦" + bm.getPrice());

        }else{
            confirmlayout.setVisibility(View.VISIBLE);
            b1layout.setVisibility(View.GONE);
            cancel = root.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            });
            confirm = getView().findViewById(R.id.confirm);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteBook(bm.getKey());
                }
            });
        }
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = FirebaseAuth.getInstance().getUid();
                final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("cart");
                dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int count = 0;
                        ArrayList<String> keys = new ArrayList<>();
                        for (DataSnapshot snaps : dataSnapshot.getChildren()) {
                            int last = Integer.parseInt(snaps.getKey().split("")[snaps.getKey().length()-1]);
                            if(last>count){
                                count = last;
                            }
                        }
//                        if (keys.isEmpty()) {
//                            dbref.child("citem0").setValue(bm.getKey());
////                            Toast.makeText(getContext(), "Empty Mehn", Toast.LENGTH_SHORT).show();
//                        } else {
//                            String key = keys.get(keys.size() - 1);
//                            int num = Integer.parseInt(key.split("")[key.length()]) + 1;
//                            dbref.child("citem" + num).setValue(bm.getKey());
//                        }
                        dbref.child("citem"+(count+1)).setValue(bm.getKey());
                        Toast.makeText(getContext(), "Book Added Successfully", Toast.LENGTH_LONG).show();
                        setCartAmount();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<CheckoutModel> checkoutlist = new ArrayList<>();
                CheckoutModel cm = new CheckoutModel(bm, "1");
                checkoutlist.add(cm);
                BaseApp.checkoutlist.clear();
                BaseApp.checkoutlist.add(cm);
                String checkoutAsString = new Gson().toJson(checkoutlist);
                startActivity(new Intent(getContext(), CheckOut.class));
                checkoutlist.clear();

            }

        });
    }

    public void setCartAmount() {
        try {
            String uid = FirebaseAuth.getInstance().getUid();
            final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("cart");
            dbref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int count = 0;
                    for (final DataSnapshot snaps : dataSnapshot.getChildren()) {

                        count++;
                    }

                    if (count < 1) {
                        ca.setVisibility(View.GONE);
                    } else {
                        ca.setVisibility(View.VISIBLE);
                        ca.setText(count > 9 ? "9+" : Integer.toString(count));
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    load.setVisibility(View.GONE);
                    Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
        }

    }

    public void setAutoComplete() {
//        Collections.sort(filearray);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getView().getContext(), android.R.layout.simple_list_item_1, filearray.toArray(new String[filearray.size()]));
        act.setAdapter(adapter);
        act.setDropDownBackgroundResource(R.color.aux1);
        act.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               performSearch(act.getText().toString());
            }
        });
        act.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0){
                    populatePosts();
                    cancelSearch.setVisibility(View.GONE);
                }else{
                    cancelSearch.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        act.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    if(!act.getText().toString().trim().isEmpty()){
                        performSearch(act.getText().toString().trim());
                    }
                }
                return false;
            }
        });


    }

    private void performSearch(final String s) {
        carr.setVisibility(View.GONE);
        heading.setText("Search results for ("+s+")");
        load.setVisibility(View.VISIBLE);
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Store");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                booklist.clear();
                for (final DataSnapshot snaps : dataSnapshot.getChildren()) {
                    BookModel bm = snaps.getValue(BookModel.class);
                    if(bm.getTitle().contains(s)) {
                        bm.setKey(snaps.getKey());
                        booklist.add(0, bm);
                    }
                }
                checkList("No result");
                bookAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                load.setVisibility(View.GONE);
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateAutoComplete() {

        FirebaseDatabase.getInstance().getReference().child("Store").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                filearray.clear();
                for (DataSnapshot snaps : dataSnapshot.getChildren()) {
                    BookModel bm = snaps.getValue(BookModel.class);
                    filearray.add(bm.getTitle());
                }
                try {
                    setAutoComplete();
                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkList(String s){
        load.setVisibility(View.VISIBLE);
        if(booklist.size()<1){
            load.getChildAt(0).setVisibility(View.GONE);
            ((TextView)getView().findViewById(R.id.empty)).setText(s);
        }else{
            load.setVisibility(View.GONE);
        }
    }

    private void deleteBook(String key) {
        FirebaseDatabase.getInstance().getReference().child("Store").child(key).removeValue();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        Toast.makeText(getContext(), "Book Deleted Successfully", Toast.LENGTH_LONG).show();
    }

    private void resolveCart(){
        String uid = FirebaseAuth.getInstance().getUid();
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("cart");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot snaps:dataSnapshot.getChildren()){

                    final String bkey = snaps.getValue(String.class);
                    FirebaseDatabase.getInstance().getReference().child("Store").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean found = false;
                            for(DataSnapshot snaps2:dataSnapshot.getChildren()){
                                if(bkey.equalsIgnoreCase(snaps2.getKey())){
                                    found = true;
                                }
                            }

                            if(!found) {
                                dbref.child(snaps.getKey()).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                setCartAmount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}