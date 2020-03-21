package com.dcinspirations.bookstore.ui.send;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.dcinspirations.bookstore.R;
import com.dcinspirations.bookstore.models.BookModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;

import pl.droidsonroids.gif.GifImageView;

public class AddBook extends Fragment {

    private SendViewModel sendViewModel;
    TextInputLayout btlayout,balayout,bplayout,bdlayout;
    EditText btitle,bauthor,bprice,bdesc;
    TextView valaction;
    RelativeLayout valactionlayout;
    GifImageView lgif;
    Toolbar tb;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(SendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_addbook, container, false);
        tb = root.findViewById(R.id.tb);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        lgif = root.findViewById(R.id.lgif);
        btitle=root.findViewById(R.id.btitle);
        bauthor=root.findViewById(R.id.bauthor);
        bprice = root.findViewById(R.id.bprice);
        bdesc=root.findViewById(R.id.bdesc);
        btlayout = root.findViewById(R.id.title_layout);
        balayout = root.findViewById(R.id.author_layout);
        bplayout = root.findViewById(R.id.price_layout);
        bdlayout = root.findViewById(R.id.desc_layout);
        valactionlayout = (RelativeLayout) lgif.getParent();
        valaction = root.findViewById(R.id.val_action);
        valactionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    UploadBook();
            }
        });
        return root;
    }

    private void UploadBook() {
        final String bttext = btitle.getText().toString().trim();
        final String batext = bauthor.getText().toString().trim();
        String bptext = bprice.getText().toString().trim();
        final String bdtext = bdesc.getText().toString().trim();

        if(bttext.isEmpty()){
            btlayout.setError("A title is required");
            btitle.requestFocus();
            return;
        }
        if(batext.isEmpty()){
            balayout.setError("An author is required");
            bauthor.requestFocus();
            return;
        }
        if(bptext.isEmpty()){
            bplayout.setError("A price is required");
            bprice.requestFocus();
            return;
        }
        if(bdtext.isEmpty()){
            bdlayout.setError("A description is required");
            bdesc.requestFocus();
            return;
        }

        valaction.setText("Uploading...");
        lgif.setVisibility(View.VISIBLE);

        BookModel bm = new BookModel(bttext,batext,bptext,bdtext,"");
        FirebaseDatabase.getInstance().getReference().child("Store").push().setValue(bm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    valaction.setText("UPLOAD BOOK");
                    lgif.setVisibility(View.INVISIBLE);
                    btitle.setText("");
                    bauthor.setText("");
                    bprice.setText("");
                    bdesc.setText("");
                    btitle.requestFocus();
                    Toast.makeText(getContext(), "Book Uploaded Successfully", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    valaction.setText("UPLOAD BOOK");
                    lgif.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}