package com.dcinspirations.bookstore.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dcinspirations.bookstore.R;
import com.dcinspirations.bookstore.Sp;
import com.dcinspirations.bookstore.models.DeliveryModel;
import com.dcinspirations.bookstore.ui.slideshow.DeliveryFragment;

import java.util.List;

/**
 * Created by pc on 2/18/2018.
 */

public class DeliverAdapter extends RecyclerView.Adapter<DeliverAdapter.viewHolder> {

    private List<DeliveryModel> objectlist;
    private LayoutInflater inflater;
    private Context context;
    private String layout;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor spe;
    private DeliveryFragment hf;

    public DeliverAdapter(Context context, List<DeliveryModel> objectlist, DeliveryFragment sf) {
        inflater = LayoutInflater.from(context);
        this.objectlist = objectlist;
        this.context = context;
        this.hf=sf;
    }


    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.deliver_layout, parent, false);

        viewHolder vholder = new viewHolder(view);
        return vholder;
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        DeliveryModel current = objectlist.get(position);
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
        private int position;
        private DeliveryModel currentObject;
        private TextView status,bdetails,deldetails,price,qty;
        private ImageButton delete;
        private Button deliver;

        public void setPosition(int position) {
            this.position = position;
        }

        public viewHolder(final View itemView) {
            super(itemView);


            status = itemView.findViewById(R.id.status);
            bdetails = itemView.findViewById(R.id.booktitle);
            qty = itemView.findViewById(R.id.qty);
            deliver = itemView.findViewById(R.id.deliver);
            deldetails = itemView.findViewById(R.id.deldetails);
            deldetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(deldetails.getMaxLines()==4){
                        deldetails.setMaxLines(100);
                    }else{
                        deldetails.setMaxLines(4);
                    }
                }
            });
            price = itemView.findViewById(R.id.bookprice);
            delete = itemView.findViewById(R.id.delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hf.delkey = currentObject.getDkey();
                    hf.setBS("delete");
                    hf.openBS();

                }
            });

            deliver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hf.confirmkey = currentObject.getDkey();
                    hf.setBS("deliver");
                    hf.openBS();
                }
            });

            if(new Sp(context).getAdminLoggedIn()){
                delete.setVisibility(View.GONE);
                deliver.setVisibility(View.VISIBLE);
            }


        }


        public void setData(DeliveryModel current, int position) {

            this.status.setText(current.getStatus());
            if(current.getStatus().equalsIgnoreCase("pending")){
                this.status.setTextColor(context.getColor(R.color.aux8));
                this.status.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.status),null,null,null);
                this.status.setText("Pending");
            }else{
                this.status.setTextColor(context.getColor(R.color.aux9));
                this.status.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.status2),null,null,null);
                this.status.setText("Delivered");
                this.deliver.setVisibility(View.INVISIBLE);
            }
            this.bdetails.setText(current.getItems());
            this.deldetails.setText(current.getCDetails()+"\n"+current.getDLocaion());
            this.price.setText("₦" + resolveMoney(current.getPrice()));
            int qtynum = getQty(current.getItems());
            this.qty.setText(qtynum>9?"9+ Items":qtynum>1?Integer.toString(qtynum)+" Items":"1 Item");
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

            return newamount;
        }

        private int getQty(String s){
            String sarray[] = s.split("\n");
            int qty = 0;
            for(String s2:sarray){
                String sarray2[] = s2.split(" ");
                qty = qty + Integer.parseInt(sarray2[0]);
            }
            return qty;
        }


    }
}
