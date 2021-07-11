package com.dcinspirations.bookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dcinspirations.bookstore.models.CheckoutModel;
import com.dcinspirations.bookstore.models.DeliveryModel;
import com.dcinspirations.bookstore.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import pl.droidsonroids.gif.GifImageView;

public class CheckOut extends AppCompatActivity {

    LinearLayout bottomSheet, conlayout, load, selectCardLayout, firstCard, secondCard, processingPayment;
    View view;
    GifImageView processing;
    ImageView cardErrorIllustration;
    BottomSheetBehavior bottomSheetBehavior;
    TextView items, firstCardExpiryDate, secondCardExpiryDate, processingPaymentText;
    EditText cdetails, delloc;
    Button checkout, cancel, confirm, addNewCard, goBackToSelectCard;
    ArrayList<CheckoutModel> checkoutlist;
    boolean hasSent = false;
    CoordinatorLayout root;
    Toolbar tb;
    final ShowNotification notification = new ShowNotification();
    final NotificationDetails details = new NotificationDetails();

    final String tag = "Check out";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

//        String arrayAsString = getIntent().getExtras().getString("data");
//        Type gsontype = new TypeToken<List<CheckoutModel>>() {
//        }.getType();
        checkoutlist = BaseApp.checkoutlist;

        tb = findViewById(R.id.tb);
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        items = findViewById(R.id.items);
        cdetails = findViewById(R.id.cdetails);
        delloc = findViewById(R.id.delloc);

        checkout = findViewById(R.id.action_checkout);
        checkout.setText("CHECKOUT - " + "₦" + resolveMoney(Integer.toString(getAmount())));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            checkout.setFocusedByDefault(false);
        }
        conlayout = findViewById(R.id.confirm_layout);
        selectCardLayout = findViewById(R.id.select_card_layout);
        firstCard = findViewById(R.id.first_card);
        firstCardExpiryDate = findViewById(R.id.first_card_expiry_date);
        secondCard = findViewById(R.id.second_card);
        secondCardExpiryDate = findViewById(R.id.second_card_expiry_date);
        addNewCard = findViewById(R.id.add_new_card);
        processingPayment = findViewById(R.id.processing_payment);
        processing = findViewById(R.id.processing);
        cardErrorIllustration = findViewById(R.id.card_error_illustration);
        processingPaymentText = findViewById(R.id.processing_payment_text);
        goBackToSelectCard = findViewById(R.id.go_back_to_select_card);
        load = findViewById(R.id.load);
        setData();

        bottomSheet = findViewById(R.id.bottomSheet);
        view = findViewById(R.id.bg);
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
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasSent) {
                    openBottomSheet();
                } else {
                    Toast.makeText(CheckOut.this, "This Order has already been placed", Toast.LENGTH_LONG).show();
                }
            }
        });
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCard();
            }
        });

        firstCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chargeCard(getString(R.string.test_card_1_number), cardExpiry().getFutureMonth(), cardExpiry().getFutureYear(), getAmount(), getString(R.string.test_card_1_cvv));
            }
        });

        secondCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chargeCard(getString(R.string.test_card_2_number), cardExpiry().getFutureMonth(), cardExpiry().getFutureYear(), getAmount(), getString(R.string.test_card_2_cvv));
            }
        });

        firstCardExpiryDate.setText(cardExpiry().getFutureMonth() + "/" + cardExpiry().getFutureYear());
        secondCardExpiryDate.setText(cardExpiry().getFutureMonth() + "/" + cardExpiry().getFutureYear());

        addNewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You cannot add a card at this time. Please select one of the existing cards", Toast.LENGTH_LONG).show();
            }
        });

        goBackToSelectCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf(tag, "goBackToSelectCard was clicked");
                processingPayment.setVisibility(View.GONE);
                selectCardLayout.setVisibility(View.VISIBLE);

                processing.setVisibility(View.VISIBLE);
                cardErrorIllustration.setVisibility(View.GONE);
                goBackToSelectCard.setVisibility(View.GONE);
                processingPaymentText.setText(getString(R.string.processing_payment_text));
            }
        });
    }

    private void setData() {
        String itemsstring = "";
        for (CheckoutModel cm : checkoutlist) {
            itemsstring = itemsstring + cm.getQuantity() + " copies of " + cm.getBookModel().getTitle() + "(By " + cm.getBookModel().getAuthor() + " )";
            if (checkoutlist.indexOf(cm) != checkoutlist.size() - 1) {
                itemsstring = itemsstring + "\n";
            }
        }
        items.setText(itemsstring);
        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel um = dataSnapshot.getValue(UserModel.class);
                String cdetailstext = um.getName() + "\n" + um.getMNumber();
                cdetails.setText(cdetailstext);
                delloc.setText(um.getAddress());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    delloc.setFocusedByDefault(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openBottomSheet() {
        final String cdetailstext = cdetails.getText().toString().trim();
        final String loc = delloc.getText().toString().trim();
        if (cdetailstext.isEmpty()) {
            cdetails.requestFocus();
            cdetails.setError("Enter contact details");
            return;
        }

        if (loc.isEmpty()) {
            delloc.requestFocus();
            delloc.setError("Enter Delivery Address");
            return;
        }

        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    private void selectCard() {
        conlayout.setVisibility(View.GONE);
        selectCardLayout.setVisibility(View.VISIBLE);
    }

    private void chargeCard(String cardNumber, int cardExpiryMonth, int cardExpiryYear, int amount, String cardCvv) {
        selectCardLayout.setVisibility(View.GONE);
        processingPayment.setVisibility(View.VISIBLE);

        PaystackSdk.initialize(getApplicationContext());
        PaystackSdk.setPublicKey(getString(R.string.test_public_key));

        Card card = new Card(cardNumber, cardExpiryMonth, cardExpiryYear, cardCvv);

        Charge charge = new Charge();
        charge.setAmount(amount * 100);
        charge.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        charge.setCard(card);

        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                notification.notifyUser(CheckOut.this,
                        details.paymentSuccessfulNotificationId,
                        details.paymentSuccessfulNotificationChannelId,
                        details.paymentSuccessfulNotificationChannelName,
                        details.paymentSuccessfulNotificationChannelDescription,
                        details.paymentSuccessfulNotificationTitle,
                        "You have successfully paid ₦" + resolveMoney(Integer.toString(getAmount())) + ". Your transaction reference is " + transaction.getReference() + ". Your payment receipt has also been sent to your email");
                uploadOrder();
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                Log.wtf(tag, transaction.getReference());
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                Log.wtf(tag, "Error: " + error.getMessage() + ", transaction: " + transaction.getReference());

                processing.setVisibility(View.GONE);
                cardErrorIllustration.setVisibility(View.VISIBLE);
                goBackToSelectCard.setVisibility(View.VISIBLE);
                processingPaymentText.setText(error.getMessage());
            }
        });
    }

    private void uploadOrder() {
        processingPayment.setVisibility(View.GONE);
        load.setVisibility(View.VISIBLE);

        final String itemtext = items.getText().toString().trim();
        final String cdetailstext = cdetails.getText().toString().trim();
        final String loc = delloc.getText().toString().trim();

        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Delivery");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot snaps : dataSnapshot.getChildren()) {
                    int last = Integer.parseInt(snaps.getKey().split("")[snaps.getKey().length()]);
                    if (last > count) {
                        count = last;
                    }
                }
                DatabaseReference dbref2 = dbref.child("del" + (count + 1));
                dbref2.child("Items").setValue(itemtext);
                dbref2.child("CDetails").setValue(cdetailstext);
                dbref2.child("Publisher").setValue(FirebaseAuth.getInstance().getUid());
                dbref2.child("Status").setValue("pending");
                dbref2.child("Price").setValue(Integer.toString(getAmount()));
                dbref2.child("DLocation").setValue(loc).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            clearCart();
                            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }

                            GifImageView giv = (GifImageView) load.getChildAt(1);
                            LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) giv.getLayoutParams();
                            lp1.width = 500;
                            lp1.height = 500;
                            giv.setLayoutParams(lp1);
                            giv.setImageResource(R.drawable.suc1);


                            TextView tv = (TextView) load.getChildAt(2);
                            tv.setText("Order sent successfully, check Delivery.");
                            tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            tv.setTextSize(17);

                            hasSent = true;

                            notification.notifyUser(CheckOut.this,
                                    details.orderSuccessfulNotificationId,
                                    details.orderSuccessfulNotificationChannelId,
                                    details.orderSuccessfulNotificationChannelName,
                                    details.orderSuccessfulNotificationChannelDescription,
                                    details.orderSuccessfulNotificationTitle,
                                    details.orderSuccessfulNotificationBody);

                        } else {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            Toast.makeText(CheckOut.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                Toast.makeText(CheckOut.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearCart() {
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("cart");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (CheckoutModel cm : checkoutlist) {
//                    for (DataSnapshot snaps : dataSnapshot.getChildren()) {
//                        String bookid = snaps.getKey();
//                        if (cm.getBookModel().getKey().equalsIgnoreCase(bookid)) {
//                            dbref.child(snaps.getKey()).removeValue();
//                            break;
//                        }
//                    }
                    dbref.child(cm.getBookModel().getKey()).removeValue();

                }
                BaseApp.checkoutlist.clear();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CheckOut.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private int getAmount() {
        int amount = 0;
        for (CheckoutModel cm : checkoutlist) {
            int tam = Integer.parseInt(cm.getBookModel().getPrice().replaceAll(",", "")) * Integer.parseInt(cm.getQuantity());
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
            if (count % 3 == 0) {
                newamount = "," + newamount;

            }

        }

        return newamount;
    }

    private FutureDate cardExpiry() {
        int date = Integer.parseInt(DateFormat.format("dd", new Date()).toString());
        int currentMonth = Integer.parseInt(DateFormat.format("MM", new Date()).toString());
        int currentYear = Integer.parseInt(DateFormat.format("yyyy", new Date()).toString());

        int futureMonth;
        int futureYear;

        if (currentMonth == 12) {
            futureMonth = currentMonth - 11;
            futureYear = currentYear + 1;
        } else {
            futureMonth = currentMonth + 1;
            futureYear = currentYear;
        }

        return new FutureDate(date, futureMonth, futureYear);

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
