package com.dcinspirations.bookstore.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.dcinspirations.bookstore.BaseApp;
import com.dcinspirations.bookstore.R;
import com.dcinspirations.bookstore.models.BookModel;
import com.dcinspirations.bookstore.models.CheckoutModel;
import com.dcinspirations.bookstore.CartFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by pc on 2/18/2018.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.viewHolder> {

    private List<BookModel> objectlist;
    private LayoutInflater inflater;
    private Context context;
    private String layout;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor spe;
    private CartFragment hf;

    public CartAdapter(Context context, List<BookModel> objectlist, CartFragment homeFragment) {
        inflater = LayoutInflater.from(context);
        this.objectlist = objectlist;
        this.context = context;
        this.hf = homeFragment;
    }


    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cart_item, parent, false);

        viewHolder vholder = new viewHolder(view);
        return vholder;
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        BookModel current = objectlist.get(position);
        holder.setData(current, position);

    }

    @Override
    public int getItemCount() {
        return objectlist.size();
    }

    public void refreshEvents() {
        notifyDataSetChanged();
    }


    public class viewHolder extends RecyclerView.ViewHolder {
        private LinearLayout otherimgs;
        private int position;
        private BookModel currentObject;
        private TextView title, author, price,cancel;
        private EditText quantity;
        private ImageView add, remove;
        private CheckBox checkBox;
        private LinearLayout booklayout;

        public void setPosition(int position) {
            this.position = position;
        }

        public viewHolder(final View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkbox);
            title = itemView.findViewById(R.id.booktitle);
            author = itemView.findViewById(R.id.bookauthor);
            price = itemView.findViewById(R.id.bookprice);
            booklayout = (LinearLayout) price.getParent();
            quantity = itemView.findViewById(R.id.quantity);
            add = itemView.findViewById(R.id.add);
            remove = itemView.findViewById(R.id.remove);
            cancel = itemView.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentCheckout(currentObject)!=null){
                        BaseApp.checkoutlist.remove(currentCheckout(currentObject));
                    }
                    objectlist.remove(currentObject);
                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("cart");
                    dbref.child(currentObject.getKey()).removeValue();
                    refreshEvents();
                    hf.checkList();
                    hf.setCheckoutAmount();
                }
            });
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if(!wasBmAdded(currentObject)){
                            CheckoutModel cm = new CheckoutModel(currentObject,quantity.getText().toString());
                            BaseApp.checkoutlist.add(cm);

                        }
                    }else{
                        if(currentCheckout(currentObject)!=null){
                            BaseApp.checkoutlist.remove(currentCheckout(currentObject));
                        }
                    }
                    hf.setCheckoutAmount();
                }
            });
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        quantity.setText(Integer.toString(Integer.parseInt(quantity.getText().toString().trim()) + 1));
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(Integer.parseInt(quantity.getText().toString().trim())>1) {
                            quantity.setText(Integer.toString(Integer.parseInt(quantity.getText().toString().trim()) - 1));
                        }
                    } catch (Exception e) {

                    }
                }
            });
            quantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(wasBmAdded(currentObject)){
                        CheckoutModel cm = currentCheckout(currentObject);
                        BaseApp.checkoutlist.remove(cm);
                        cm = new CheckoutModel(currentObject,quantity.getText().toString());
                        BaseApp.checkoutlist.add(cm);
                        hf.setCheckoutAmount();
                    }
                }
            });


        }


        public void setData(BookModel current, int position) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            hf.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

//            LinearLayout.LayoutParams clayoutparams = (LinearLayout.LayoutParams) this.booklayout.getLayoutParams();
//            clayoutparams.width = (width)/2;
//            this.booklayout.setLayoutParams(clayoutparams);
            this.title.setText(current.getTitle());
            this.author.setText("By " + current.getAuthor());
            this.price.setText("₦" + current.getPrice());

            this.position = position;
            this.currentObject = current;
        }

        private boolean wasBmAdded(BookModel bm){
            for(CheckoutModel cm : BaseApp.checkoutlist){
                if(cm.getBookModel().equals(bm)){
                    return true;
                }
            }
            return false;
        }

        private CheckoutModel currentCheckout(BookModel bm){
            for(CheckoutModel cm : BaseApp.checkoutlist){
                if(cm.getBookModel().equals(bm)){
                    return cm;
                }
            }
            return null;
        }



    }
}
