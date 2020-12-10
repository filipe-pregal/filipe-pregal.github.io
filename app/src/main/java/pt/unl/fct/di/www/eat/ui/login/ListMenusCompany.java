package pt.unl.fct.di.www.eat.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pt.unl.fct.di.www.eat.R;
import pt.unl.fct.di.www.eat.StartActivity;
import pt.unl.fct.di.www.eat.data.Menu;
import pt.unl.fct.di.www.eat.data.Option;
import pt.unl.fct.di.www.eat.data.Request;
import pt.unl.fct.di.www.eat.data.RequestItem;
import pt.unl.fct.di.www.eat.data.RestaurantData;

public class ListMenusCompany extends AppCompatActivity {

    ListView listView1, listView2;
    Button btnMenu, btnRequest, btnExtra, btnEditExtra;
    TextView sDrinks, sDesserts, emptyText;
    String email;
    DatabaseReference mref;
    DatabaseReference extraD, menuD, reqsD;
    ValueEventListener extraV, menuV, reqsV;

    ArrayList<String> mTitle = new ArrayList<>();
    ArrayList<String> mDesserts = new ArrayList<>();
    ArrayList<String> mDrinks = new ArrayList<>();
    ArrayList<String> mTags = new ArrayList<>();
    ArrayList<String> mPrice = new ArrayList<>();
    ArrayList<Double> mTime = new ArrayList<>();
    ArrayList<Boolean> mAvailableDrinks = new ArrayList<>();
    ArrayList<Boolean> mAvailableDesserts = new ArrayList<>();
    ArrayList<Boolean> mAvailableMenus = new ArrayList<>();
    ArrayList<String> tagDrinks = new ArrayList<>();
    ArrayList<String> tagDesserts = new ArrayList<>();

    ArrayList<String> mCode = new ArrayList<>();
    ArrayList<String> mPayment = new ArrayList<>();
    ArrayList<Double> mPriceR = new ArrayList<>();
    ArrayList<String> mTimeR = new ArrayList<>();
    ArrayList<List<RequestItem>> mItem = new ArrayList<>();
    ArrayList<String> rKeys = new ArrayList<>();


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_search:

                super.onOptionsItemSelected(item);
                return true;
            case R.id.action_logout:
            case android.R.id.home:
                logout();
                return true;
            case R.id.action_settings:
                Intent i2 = new Intent(this, Settings_page.class);
                i2.putExtra("user", email);
                startActivity(i2);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.company_settings, menu);

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println(email);
                DatabaseReference rest = mref.child("Restaurants").child(email);
                searchQuery(rest, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                DatabaseReference rest = mref.child("Restaurants").child(email);
                searchQuery(rest, newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_options);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("user");
        }

        SharedPreferences sp = getSharedPreferences("myuser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("user", "");
        editor.putString("company", email);
        editor.commit();

        mref = FirebaseDatabase.getInstance().getReference();
        checkLogin();

        listView1 = findViewById(R.id.listViewDrinks);
        btnRequest = findViewById(R.id.seeRequests);
        listView2 = findViewById(R.id.listViewDesserts);
        sDrinks = findViewById(R.id.seeDrinks);
        sDesserts = findViewById(R.id.seeDesserts);
        emptyText = findViewById(R.id.emptyTextR);

        menus();

        btnRequest.setOnClickListener(view -> {
            checkLogin();
            setVisibility(8);
            if(menuD != null && extraD != null) {
                menuD.removeEventListener(menuV);
                extraD.removeEventListener(extraV);
            }
            requests();
        });

        btnMenu = findViewById(R.id.editMenus);
        btnEditExtra = findViewById(R.id.extrasEdit);

        btnMenu.setOnClickListener(view -> {
            checkLogin();
            setVisibility(8);
            if(reqsD != null && extraD != null){
                reqsD.removeEventListener(reqsV);
                extraD.removeEventListener(extraV);
            }
            menus();
        });

        btnExtra = findViewById(R.id.editExtras);
        btnExtra.setOnClickListener(view -> {
            checkLogin();
            setVisibility(0);
            if(reqsD != null && menuD!= null) {
                reqsD.removeEventListener(reqsV);
                menuD.removeEventListener(menuV);
            }

            extraD = mref.child("Restaurants").child(email);
            extraV = extraD.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        resetDataExtra();
                        RestaurantData post = dataSnapshot.getValue(RestaurantData.class);
                        setDataExtra(post);
                    }
                    MyAdapterExtra adapter1 = new MyAdapterExtra(getApplicationContext(), extraD, mDrinks, mAvailableDrinks, "drink");
                    listView1.setAdapter(adapter1);

                    MyAdapterExtra adapter2 = new MyAdapterExtra(getApplicationContext(), extraD, mDesserts, mAvailableDesserts, "dessert");
                    listView2.setAdapter(adapter2);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        });

        btnEditExtra.setOnClickListener(view -> {
            checkLogin();
            DatabaseReference d = mref.child("Restaurants").child(email);
            d.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        DatabaseReference drinks = d.child("drinks");
                        for (int i =0; i<tagDrinks.size(); i++){
                            DatabaseReference aux = drinks.child(tagDrinks.get(i)).child("isAvailable");
                            aux.setValue(mAvailableDrinks.get(i));
                        }
                        DatabaseReference desserts = d.child("desserts");
                        for (int i=0; i<tagDesserts.size(); i++){
                            DatabaseReference aux = desserts.child(tagDesserts.get(i)).child("isAvailable");
                            aux.setValue(mAvailableDesserts.get(i));
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        });
    }

    private void logout() {
        DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("Users").child(email).child("token");
        user.setValue("");
        try {
            SharedPreferences preferences = getSharedPreferences("myuser",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            if(reqsD != null)
                reqsD.removeEventListener(reqsV);
            if(menuD != null)
                reqsD.removeEventListener(menuV);
            if(extraD != null)
                reqsD.removeEventListener(extraV);
            finish();
        } catch (Exception e) {

        }
    }

    private void menus(){
        menuD = mref.child("Restaurants").child(email);
        menuV = menuD.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    resetDataMenu();
                    RestaurantData post = dataSnapshot.getValue(RestaurantData.class);
                    setDataMenu(post);
                }
                MyAdapterMenu adapter = new MyAdapterMenu(getApplicationContext(), menuD);
                listView1.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void requests(){
        reqsD = mref.child("Requests").child(email);
        reqsV = reqsD.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checkLogin();
                resetDataRequest();
                listView1.setEmptyView(emptyText);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Iterator<DataSnapshot> it = child.getChildren().iterator();
                        while (it.hasNext()){
                            DataSnapshot data = it.next();
                            Request request = data.getValue(Request.class);
                            setDataRequest(request, data.getKey(), child.getKey());
                        }
                    }
                }
                MyAdapterRequest adapterR1 = new MyAdapterRequest(getApplicationContext());
                listView1.setAdapter(adapterR1);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void searchQuery(DatabaseReference rest, String query) {
        listView1 = findViewById(R.id.listViewDrinks);

        rest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                resetDataMenu();
               // resetDataExtra();
                if (dataSnapshot.exists() && !query.isEmpty()) {
                    for (DataSnapshot menu : dataSnapshot.child("menu").getChildren()) {
                        if (menu.getKey().toLowerCase().contains(query.toLowerCase())) {
                            RestaurantData post = dataSnapshot.getValue(RestaurantData.class);
                            Menu menuSelected = menu.getValue(Menu.class);
                            Map<String, Menu> postMenu = new HashMap<String, Menu>();
                            postMenu.put(menuSelected.getName(), menuSelected);
                            post.setMenu(postMenu);
                            setDataMenu(post);
                           // setDataExtra(post);
                        }
                    }
                } else if (query.isEmpty()) {
                    RestaurantData post = dataSnapshot.getValue(RestaurantData.class);
                    setDataMenu(post);
                  //  setDataExtra(post);
                }
                MyAdapterMenu adapterR = new MyAdapterMenu(getApplicationContext(), rest);
                listView1.setAdapter(adapterR);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void setVisibility(int state) {
        btnEditExtra.setVisibility(state);
        sDrinks.setVisibility(state);
        sDesserts.setVisibility(state);
        listView2.setVisibility(state);
    }

    private void setDataMenu(RestaurantData r) {
        for (Map.Entry<String, Menu> menus : r.getMenu().entrySet()) {
            Menu menu = menus.getValue();
            mTitle.add(menu.getName());
            mPrice.add(menu.getPrice().toString());
            mTime.add(menu.getTime());
            mTags.add(menu.getTag());
            mAvailableMenus.add(menu.getIsAvailable());
        }
    }

    private void setDataExtra(RestaurantData r) {
        for (Map.Entry<String, Option> drinks : r.getDrinks().entrySet()) {
            tagDrinks.add(drinks.getKey());
            Option drink = drinks.getValue();
            mDrinks.add(drink.getName());
            mAvailableDrinks.add(drink.getIsAvailable());
        }

        for (Map.Entry<String, Option> desserts : r.getDesserts().entrySet()) {
            tagDesserts.add(desserts.getKey());
            Option dessert = desserts.getValue();
            mDesserts.add(dessert.getName());
            mAvailableDesserts.add(dessert.getIsAvailable());
        }
    }

    private void setDataRequest(Request r, String code, String key){
        mCode.add(code);
        mPayment.add(r.getPayment());
        mPriceR.add(r.getPrice());
        mTimeR.add(convertTime(r.getTime()));
        mItem.add(r.getItems());
        rKeys.add(key);
    }

    private void resetDataRequest(){
        mCode.clear();
        mPayment.clear();
        mPriceR.clear();
        mTimeR.clear();
        mItem.clear();
    }

    private void resetDataMenu() {
        mTitle.clear();
        mTags.clear();
        mPrice.clear();
        mTime.clear();
        mAvailableMenus.clear();
    }

    private void resetDataExtra() {
        mDesserts.clear();
        mDrinks.clear();
        mAvailableDesserts.clear();
        mAvailableDrinks.clear();
        tagDesserts.clear();
        tagDrinks.clear();
    }

    private boolean isNumeric(String time) {
        time = time.trim();
        if (time.equals(""))
            return false;
        if(time.contains("-"))
            return false;
        char unit = time.charAt(time.length() - 1);
        if (unit != 'm' && unit != 'h')
            return false;
        time = time.substring(0, time.length() - 1);
        if (unit == 'm') {
            if (time.contains("h")) {
                String[] aux = time.split("h");
                if (aux.length != 2) {
                    return false;
                }
                int minutes;
                try {
                    int hours = Integer.parseInt(aux[0]);
                    minutes = Integer.parseInt(aux[1]);
                } catch (Exception e) {
                    return false;
                }
                return minutes <= 60;
            } else {
                int minutes;
                try {
                    minutes = Integer.parseInt(time);
                } catch (Exception e) {
                    return false;
                }
                if (minutes > 60)
                    return false;
                return minutes != 0;
            }
        } else if (unit == 'h') {
            try {
                int hours = Integer.parseInt(time);
                if (hours == 0)
                    return false;
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean isNumericPrice(String price) {
        price = price.trim();
        if (price.equals(""))
            return false;
        if(price.contains("-"))
            return false;
        char unit = price.charAt(price.length() - 1);
        if (unit != '€')
            return false;
        price = price.substring(0, price.length() - 1);
        try {
            Double d = Double.parseDouble(price);
            if (d == 0)
                return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private Double convertPrice(String price) {
        price = price.substring(0, price.length() - 1);
        return Double.parseDouble(price);
    }

    private String convertTime(double time) {
        String[] aux = String.valueOf(time).split("\\.");
        int hours = Integer.parseInt(aux[0]);
        int minutes = (int) (Double.parseDouble("0." + aux[1]) * 60);
        if (minutes == 0)
            return hours + "h";
        if (hours == 0)
            return minutes + "m";
        return hours + "h" + minutes + "m";
    }

    private Double convertDoubleTime(String time) {
        if (isNumeric(time)) {
            if (time.contains("h")) {
                String[] a = time.split("h");
                if (a.length > 1) {
                    return Double.parseDouble(a[0]) + (Double.parseDouble(a[1].substring(0, a[1].length() - 1)) / 60);
                }
                return Double.parseDouble(a[0]);
            } else {
                return (Double.parseDouble(time.substring(0, time.length() - 1)) / 60);
            }
        }
        return -1.0;
    }

    private void checkLogin() {
        DatabaseReference user = mref.child("Users").child(email);
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String token = dataSnapshot.child("token").getValue().toString();
                    if (token.equals("")) {
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

    private void redirectLogin() {
        getIntent().removeExtra("user");
        Intent intent = new Intent(this, CompanyLoginActivity.class);
        startActivity(intent);
        finish();
    }

    class MyAdapterMenu extends ArrayAdapter<String> {

        DatabaseReference r;

        MyAdapterMenu(Context c, DatabaseReference d) {
            super(c, R.layout.activity_edit_menu, R.id.titleMenu, mTitle);
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
                    if (!isNumeric(myTime.getText().toString()))
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
                    if (!isNumericPrice(myPrice.getText().toString()))
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

            b.setOnClickListener(view -> {
                checkLogin();
                DatabaseReference aux = r.child("menu").child(mTitle.get(position));
                aux.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
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
            });
            return row;
        }
    }

    class MyAdapterExtra extends ArrayAdapter<String> {

        DatabaseReference r;
        ArrayList<String> rType;
        ArrayList<Boolean> rAvailability;
        String aux;

        MyAdapterExtra(Context c, DatabaseReference d, ArrayList<String> type, ArrayList<Boolean> availability, String aux) {
            super(c, R.layout.row_edit_extra, R.id.switch1, type);
            this.r = d;
            this.rType = type;
            this.rAvailability = availability;
            this.aux = aux;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_edit_extra, parent, false);

            Switch myS = row.findViewById(R.id.switch1);

            myS.setText(rType.get(position));
            myS.setChecked(rAvailability.get(position));

            myS.setOnClickListener(view -> {
                if (aux.equals("drink"))
                    mAvailableDrinks.set(position, myS.isChecked());
                if (aux.equals("dessert"))
                    mAvailableDesserts.set(position, myS.isChecked());
            });
            return row;
        }
    }

    class MyAdapterRequest extends ArrayAdapter<String> {

        MyAdapterRequest(Context c) {
            super(c, R.layout.row_request, R.id.codeR, mCode);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_request, parent, false);

            TextView myCode = row.findViewById(R.id.codeR);
            TextView myPayment = row.findViewById(R.id.paymentR);
            TextView myPrice = row.findViewById(R.id.priceR);
            TextView myTime = row.findViewById(R.id.timeR);
            ImageView img = row.findViewById(R.id.removeR);

            img.setImageResource(R.drawable.done);

            img.setOnClickListener(view -> {
                DatabaseReference rRef = mref.child("Requests").child(email).child(rKeys.get(position)).child(mCode.get(position));
                rRef.removeValue();
            });

            ListView list = row.findViewById(R.id.itemsR);

            ArrayList<String> item = new ArrayList<>();
            ArrayList<Double> quantity = new ArrayList<>();

            for(RequestItem a : mItem.get(position)){
                item.add(a.getItem());
                quantity.add(a.getQuantity());
            }

            myCode.setText("Code " +mCode.get(position));
            myPayment.setText(mPayment.get(position));
            myPrice.setText(mPriceR.get(position).toString().concat("€"));
            myTime.setText(mTimeR.get(position));

            MyAdapterAux adpt = new MyAdapterAux(getApplicationContext(), item, quantity);
            list.setAdapter(adpt);

            return row;
        }
    }

    class MyAdapterAux extends ArrayAdapter<String> {

        ArrayList<String> rItems;
        ArrayList<Double> rQuantity;

        MyAdapterAux(Context c, ArrayList<String> items, ArrayList<Double> quantity) {
            super(c, R.layout.row_item_request, R.id.codeR, items);
            this.rItems = items;
            this.rQuantity = quantity;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_item_request, parent, false);

            TextView myItem = row.findViewById(R.id.itemR);
            TextView myQuantity = row.findViewById(R.id.quantityR);

            myItem.setText(rItems.get(position));
            myQuantity.setText("x"+ rQuantity.get(position).toString());

            return row;
        }
    }
}
