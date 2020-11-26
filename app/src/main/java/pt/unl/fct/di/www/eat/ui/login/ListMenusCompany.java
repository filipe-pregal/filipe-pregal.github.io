package pt.unl.fct.di.www.eat.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import pt.unl.fct.di.www.eat.R;
import pt.unl.fct.di.www.eat.data.Menu;
import pt.unl.fct.di.www.eat.data.Option;
import pt.unl.fct.di.www.eat.data.RestaurantData;

public class ListMenusCompany extends AppCompatActivity {

    ListView listView, viewDrinks, viewDesserts;
    Button btnMenu, btnRequest, btnExtra;
    String email;
    DatabaseReference mref;

    ArrayList<String> mTitle = new ArrayList<>();
    ArrayList<String> mDesserts = new ArrayList<>();
    ArrayList<String> mDrinks = new ArrayList<>();
    ArrayList<String> mTags = new ArrayList<>();
    ArrayList<String> mPrice = new ArrayList<>();
    ArrayList<Double> mTime = new ArrayList<>();
    ArrayList<Boolean> mAvailableDrinks = new ArrayList<>();
    ArrayList<Boolean> mAvailableDesserts = new ArrayList<>();
    ArrayList<Boolean> mAvailableMenus = new ArrayList<>();

    String mT[] = {"a", "b", "c"};
    String mB[] = {"d", "e", "f"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_options);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("user");
        }

        mref = FirebaseDatabase.getInstance().getReference();
        checkLogin();

        btnRequest = findViewById(R.id.seeRequests);
        listView = findViewById(R.id.listView);
        viewDrinks = findViewById(R.id.listView);
        //viewDesserts = findViewById(R.id.listViewDesserts);

        MyAdapterRequest adapterR = new MyAdapterRequest(getApplicationContext());
        listView.setAdapter(adapterR);

        btnRequest.setOnClickListener(view -> {
            MyAdapterRequest adapterR1 = new MyAdapterRequest(getApplicationContext());
            listView.setAdapter(adapterR1);
        });

        btnMenu = findViewById(R.id.editMenus);

        btnMenu.setOnClickListener(view -> {
            DatabaseReference rest = mref.child("Restaurants").child(email);
            rest.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        resetDataMenu();
                        RestaurantData post = dataSnapshot.getValue(RestaurantData.class);
                        setDataMenu(post);
                    }
                    MyAdapterMenu adapter = new MyAdapterMenu(getApplicationContext(), rest);
                    listView.setAdapter(adapter);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        });

        btnExtra = findViewById(R.id.editExtras);
        btnExtra.setOnClickListener(view -> {
            DatabaseReference rest = mref.child("Restaurants").child(email);
            rest.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        resetDataExtra();
                        RestaurantData post = dataSnapshot.getValue(RestaurantData.class);
                        setDataExtra(post);
                    }
                    //TextView t1 = findViewById(R.id.seeDrinks);
                    //Button editDrinks = findViewById(R.id.editDrinks);
                    MyAdapterExtra adapter1 = new MyAdapterExtra(getApplicationContext(), rest, mDrinks, mAvailableDrinks);
                    viewDrinks.setAdapter(adapter1);
                        /*
                    TextView t2 = findViewById(R.id.seeDesserts);
                    Button editDesserts = findViewById(R.id.editDesserts);
                    MyAdapterExtra adapter2 = new MyAdapterExtra(getApplicationContext(), rest, mDesserts, mAvailableDesserts);
                    viewDesserts.setAdapter(adapter2);*/
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        });
    }

    private void setDataMenu(RestaurantData r){
        for (Map.Entry<String, Menu> menus : r.getMenu().entrySet()){
            Menu menu = menus.getValue();
            mTitle.add(menu.getName());
            mPrice.add(menu.getPrice().toString());
            mTime.add(menu.getTime());
            mTags.add(menu.getTag());
            mAvailableMenus.add(menu.getIsAvailable());
        }
    }

    private void setDataExtra(RestaurantData r){
        for(Map.Entry<String, Option> drinks : r.getDrinks().entrySet()){
            Option drink = drinks.getValue();
            mDrinks.add(drink.getName());
            mAvailableDrinks.add(drink.getIsAvailable());
        }

        for(Map.Entry<String, Option> desserts : r.getDesserts().entrySet()){
            Option dessert = desserts.getValue();
            mDesserts.add(dessert.getName());
            mAvailableDesserts.add(dessert.getIsAvailable());
        }
    }

    private void resetDataMenu(){
        mTitle.clear();
        mTags.clear();
        mPrice.clear();
        mTime.clear();
        mAvailableMenus.clear();
    }

    private void resetDataExtra(){
        mDesserts.clear();
        mDrinks.clear();
        mAvailableDesserts.clear();
        mAvailableDrinks.clear();
    }

    class MyAdapterMenu extends ArrayAdapter<String> {

        DatabaseReference r;

        MyAdapterMenu(Context c, DatabaseReference d){
            super(c,R.layout.activity_edit_menu, R.id.titleMenu, mTitle);
            this.r = d;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.activity_edit_menu, parent, false);
            TextView myTitle = row.findViewById(R.id.titleMenu);
            TextView myDish = row.findViewById(R.id.dish);
            TextView myTag = row.findViewById(R.id.tagM);
            EditText myPrice = row.findViewById(R.id.price);
            EditText myTime = row.findViewById(R.id.timeM);
            Switch myAvailability = row.findViewById(R.id.switch1);

            Button b = row.findViewById(R.id.editMenu);
            ImageView i = row.findViewById(R.id.imageView);

            myTitle.setText("Menu: " + mTitle.get(position));
            myDish.setText(mTitle.get(position));
            myTag.setText(mTags.get(position));
            myPrice.setText(mPrice.get(position).concat("€"));
            myTime.setText(convertTime(mTime.get(position)));
            myAvailability.setChecked(mAvailableMenus.get(position));

            i.setImageResource(R.drawable.clock_icon);

            myTime.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(!isNumeric(myTime.getText().toString()))
                        myTime.setError("Time must only contain numbers, m and h. Ex: 30m or 1h30m");
                    else {
                        b.setEnabled(true);
                        myTime.setError(null);
                    }
                }
                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            myPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(!isNumericPrice(myPrice.getText().toString()))
                        myPrice.setError("Price must only contain numbers, € and be valid.");
                    else {
                        b.setEnabled(true);
                        myPrice.setError(null);
                    }
                }
                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            myAvailability.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    b.setEnabled(true);
                }
            });

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference aux = r.child("menu").child(mTitle.get(position));
                    aux.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                if (myTime.getError() == null && myPrice.getError() == null) {
                                    aux.child("time").setValue(convertDoubleTime(myTime.getText().toString()));
                                    aux.child("price").setValue(convertPrice(myPrice.getText().toString()));
                                    aux.child("isAvailable").setValue(myAvailability.isChecked());
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
                }
            });
            return row;
        }
    }

    class MyAdapterExtra extends ArrayAdapter<String> {

        DatabaseReference r;
        ArrayList<String> rType;
        ArrayList<Boolean> rAvailability;

        MyAdapterExtra(Context c, DatabaseReference d, ArrayList<String> type, ArrayList<Boolean> availability){
            super(c,R.layout.activity_edit_extra, R.id.extra, type);
            this.r = d;
            this.rType = type;
            this.rAvailability = availability;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.activity_edit_extra,parent, false);

            ListView l1 = row.findViewById(R.id.listViewDrinks);
            l1.setScrollContainer(true);
            MyAdapterRequest adapter1 = new MyAdapterRequest(getApplicationContext());
            l1.setAdapter(adapter1);

            ListView l2 = row.findViewById(R.id.listViewDesserts);
            MyAdapterRequest adapter2 = new MyAdapterRequest(getApplicationContext());
            l2.setAdapter(adapter2);

            TextView myD = row.findViewById(R.id.extra);
            Switch myS = row.findViewById(R.id.switch1);

            //myD.setText(rType.get(position));
            //myS.setChecked(rAvailability.get(position));
            return row;
        }
    }

    private boolean isNumeric(String time){
        time = time.trim();
        if(time.equals(""))
            return false;
        char unit = time.charAt(time.length()-1);
        if(unit != 'm' && unit != 'h')
            return false;
        time = time.substring(0, time.length()-1);
        if(unit == 'm'){
            if(time.contains("h")){
                String aux[] = time.split("h");
                if(aux.length != 2){
                    return false;
                }
                int minutes;
                try {
                    int hours = Integer.parseInt(aux[0]);
                    minutes = Integer.parseInt(aux[1]);
                }catch (Exception e){
                    return false;
                }
                if(minutes > 60)
                    return false;
                return true;
            }else{
                int minutes;
                try {
                    minutes = Integer.parseInt(time);
                }catch (Exception e){
                    return false;
                }
                if(minutes > 60)
                    return false;
                return true;
            }
        }else if(unit == 'h'){
            try {
                int hours = Integer.parseInt(time);
                if (hours == 0)
                    return false;
            }catch (Exception e){
                return false;
            }
            return true;
        }
        return false;
    }

    class MyAdapterRequest extends ArrayAdapter<String> {

        MyAdapterRequest(Context c){
            super(c,R.layout.row_menu_user, R.id.titleMenu, mT);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_request,parent, false);

            TextView myT = row.findViewById(R.id.teste1);
            TextView myB = row.findViewById(R.id.teste2);

            myT.setText(mT[position]);
            myB.setText(mB[position]);
            return row;
        }
    }

    private boolean isNumericPrice(String price){
        price = price.trim();
        if(price.equals(""))
            return false;
        char unit = price.charAt(price.length()-1);
        if(unit != '€')
            return false;
        System.out.println(price + " a");
        price = price.substring(0, price.length()-1);
        System.out.println(price + " b");
        try {
            Double d = Double.parseDouble(price);
            if(d ==0)
                return false;
        }catch (Exception e){
            return false;
        }
        return true;
    }

    private Double convertPrice(String price){
        price = price.substring(0, price.length()-1);
        return Double.parseDouble(price);
    }
    private String convertTime(double time){
        String[] aux = String.valueOf(time).split("\\.");
        int hours =  Integer.parseInt(aux[0]);
        int minutes = (int) (Double.parseDouble("0." + aux[1]) *60);
        if(minutes == 0)
            return hours + "h";
        if(hours == 0)
            return minutes + "m";
        return hours + "h" + minutes + "m";
    }

    private Double convertDoubleTime(String time){
        if(isNumeric(time)){
            if(time.contains("h")){
                String a[] = time.split("h");
                if(a.length > 1){
                    return Double.parseDouble(a[0]) + (Double.parseDouble(a[1].substring(0, a[1].length()-1))/60);
                }
                return Double.parseDouble(a[0]);
            }else{
                return (Double.parseDouble(time.substring(0, time.length() -1))/60);
            }
        }
        return -1.0;
    }

    private void checkLogin(){
        DatabaseReference user = mref.child("Users").child(email);
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String token = dataSnapshot.child("token").getValue().toString();
                    if(token.equals("")){
                        redirectLogin();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void redirectLogin(){
        getIntent().removeExtra("user");
        Intent intent = new Intent(this, CompanyLoginActivity.class);
        startActivity(intent);
    }
}
