package com.dcinspirations.bookstore;

import android.content.Context;
import android.content.SharedPreferences;


import com.dcinspirations.bookstore.models.CheckoutModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Sp {
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor spe;
    private Boolean isLoggedIn = false;

    public Sp(Context ctx) {
        sharedPreferences = ctx.getSharedPreferences("default", 0);
        spe = sharedPreferences.edit();
    }

    public static void setAdminLoggedIn(boolean state){
        spe.putBoolean("loggedIn",state);
        spe.commit();
    }
    public static boolean getAdminLoggedIn(){
        boolean li = sharedPreferences.getBoolean("loggedIn",false);
        return li;
    }


    public ArrayList<CheckoutModel> getCheckOutList(){
        ArrayList<CheckoutModel> checkoutlist = new ArrayList<>();
        String colisttext = sharedPreferences.getString("coliststring","");
        if(!colisttext.isEmpty()){
            Type gsontype = new TypeToken<List<CheckoutModel>>() {
            }.getType();
            checkoutlist = new Gson().fromJson(colisttext, gsontype);
        }
        return checkoutlist;

    }

    public void setCheckOutList(ArrayList<CheckoutModel> cl){
        String checkoutAsString = new Gson().toJson(cl);
        spe.putString("coliststring",checkoutAsString);
        spe.commit();

    }
    public void clearCheckOutList(){
        ArrayList<CheckoutModel> colist = getCheckOutList();
        colist.clear();
        String checkoutAsString = new Gson().toJson(colist);
        spe.putString("coliststring",checkoutAsString);
        spe.commit();
    }


}
