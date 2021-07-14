package com.dcinspirations.bookstore.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dcinspirations.bookstore.R;
import com.dcinspirations.bookstore.Sp;
import com.dcinspirations.bookstore.models.BookModel;
import com.dcinspirations.bookstore.HomeFragment;

import java.util.List;

/**
 * Created by pc on 2/18/2018.
 */

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.viewHolder> {

    private List<BookModel> objectlist;
    private LayoutInflater inflater;
    private Context context;
    private String layout;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor spe;
    private HomeFragment hf;

    public BookAdapter(Context context, List<BookModel> objectlist, HomeFragment homeFragment) {
        inflater = LayoutInflater.from(context);
        this.objectlist = objectlist;
        this.context = context;
        this.hf = homeFragment;
    }


    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view   = inflater.inflate(R.layout.book_feeds, parent, false);

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
        private TextView title,author,price;
        private LinearLayout booklayout;
        private Button delete;

        public void setPosition(int position) {
            this.position = position;
        }

        public viewHolder(final View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hf.controlBS(currentObject,"details");
                }
            });
            title = itemView.findViewById(R.id.booktitle);
            author = itemView.findViewById(R.id.bookauthor);
            price = itemView.findViewById(R.id.bookprice);
            booklayout = (LinearLayout) price.getParent();
            delete = itemView.findViewById(R.id.delete);
            if(!new Sp(context).getAdminLoggedIn()){
                delete.setVisibility(View.GONE);
                price.setTextColor(context.getColor(R.color.aux5));
            }
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hf.controlBS(currentObject,"delete");
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
            this.author.setText("By "+current.getAuthor());
            this.price.setText("₦"+resolveMoney(current.getPrice()));

            this.position = position;
            this.currentObject = current;
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
             newamount = bram[0] + newamount;

            return newamount;
        }


    }
}
