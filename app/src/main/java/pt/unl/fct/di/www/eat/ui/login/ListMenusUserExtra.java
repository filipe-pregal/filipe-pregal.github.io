package pt.unl.fct.di.www.eat.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
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
import java.util.UUID;

import pt.unl.fct.di.www.eat.R;
import pt.unl.fct.di.www.eat.data.Cart;
import pt.unl.fct.di.www.eat.data.Menu;
import pt.unl.fct.di.www.eat.data.Option;
import pt.unl.fct.di.www.eat.data.RestaurantData;

public class ListMenusUserExtra extends AppCompatActivity {

    ArrayList<String> mDesserts = new ArrayList<>();
    ArrayList<String> mDrinks = new ArrayList<>();
    ArrayList<Boolean> checkDrinks = new ArrayList<>();
    ArrayList<Boolean> checkDesserts = new ArrayList<>();
    String drink = "";
    String dessert = "";

    ListView listView1, listView2;
    Button btnAdd, btnMenus, btnCart;
    TextView sDrinks, sDesserts;
    String email, restaurant, menu;
    DatabaseReference mref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_menus_user_extra);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("user");
            restaurant = extras.getString("restaurant");
            menu = extras.getString("menu");
        }

        mref = FirebaseDatabase.getInstance().getReference();
        checkLogin();

        btnAdd = findViewById(R.id.addToCart);

        listView1 = findViewById(R.id.listViewDrinksUser);
        listView2 = findViewById(R.id.listViewDessertsUser);
        sDrinks = findViewById(R.id.seeDrinksUser);
        sDesserts = findViewById(R.id.seeDessertsUser);

        DatabaseReference rest = mref.child("Restaurants").child(restaurant);
        rest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checkLogin();
                if(dataSnapshot.exists()) {
                    resetDataExtra();
                    RestaurantData post = dataSnapshot.getValue(RestaurantData.class);
                    setDataExtra(post);
                }
                MyAdapterExtra adapter1 = new MyAdapterExtra(getApplicationContext(), rest, mDrinks, "drinks");
                listView1.setAdapter(adapter1);

                MyAdapterExtra adapter2 = new MyAdapterExtra(getApplicationContext(), rest, mDesserts, "desserts");
                listView2.setAdapter(adapter2);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        btnAdd.setOnClickListener(view -> {
            checkLogin();
            int d = -1;
            int ds = -1;
            for (int i = 0; i < checkDrinks.size(); i++) {
                if (checkDrinks.get(i))
                    d = i;
            }
            for (int i = 0; i < checkDesserts.size(); i++){
                if (checkDesserts.get(i))
                    ds = i;
            }
            if(d!=-1)
                drink = mDrinks.get(d);
            if(ds!=-1)
                dessert = mDesserts.get(ds);

            DatabaseReference m = mref.child("Restaurants").child(restaurant).child("menu").child(menu);
            m.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        Menu menuValue = dataSnapshot.getValue(Menu.class);
                        Cart cart = new Cart(menuValue.getName(), drink, dessert,menuValue.getPrice(),menuValue.getTime());
                        String random = UUID.randomUUID().toString().substring(0, 8);
                        DatabaseReference addCart = mref.child("Carts").child(email).child(random);
                        addCart.setValue(cart);
                        openCart();
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

    class MyAdapterExtra extends ArrayAdapter<String> {

        DatabaseReference r;
        ArrayList<String> rType;
        String type;

        MyAdapterExtra(Context c, DatabaseReference d, ArrayList<String> type, String aux){
            super(c,R.layout.row_extra_user, R.id.extraU, type);
            this.r = d;
            this.rType = type;
            this.type = aux;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_extra_user, parent, false);

            TextView myD = row.findViewById(R.id.extraU);
            CheckBox c = row.findViewById(R.id.checkBox);

            myD.setText(rType.get(position));

            if(type.equals("drinks"))
                c.setChecked(checkDrinks.get(position));
            if(type.equals("desserts"))
                c.setChecked(checkDesserts.get(position));

            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(type.equals("drinks")){
                        if(checkDrinks.contains(true)) {
                            checkDrinks.set(position, false);
                            c.setChecked(false);
                        }
                        else {
                            c.setChecked(true);
                            checkDrinks.set(position, true);
                        }
                    }
                    if(type.equals("desserts")){
                        if(checkDesserts.contains(true)){
                            checkDesserts.set(position, false);
                            c.setChecked(false);
                        }
                        else {
                            checkDesserts.set(position, true);
                            c.setChecked(true);
                        }
                    }
                }
            });
            return row;
        }
    }

    private void openCart(){
        Intent intent = new Intent(this, CartUser.class);
        intent.putExtra("user", email);
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }

    private void setDataExtra(RestaurantData r){
        for(Map.Entry<String, Option> drinks : r.getDrinks().entrySet()){
            Option drink = drinks.getValue();
            if(drink.getIsAvailable()) {
                mDrinks.add(drink.getName());
                checkDrinks.add(false);
            }
        }
        for(Map.Entry<String, Option> desserts : r.getDesserts().entrySet()){
            Option dessert = desserts.getValue();
            if(dessert.getIsAvailable()) {
                mDesserts.add(dessert.getName());
                checkDesserts.add(false);
            }
        }
    }

    private void resetDataExtra(){
        mDesserts.clear();
        mDrinks.clear();
        checkDesserts.clear();
        checkDrinks.clear();
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
        Intent intent = new Intent(this, UserLoginActivity.class);
        startActivity(intent);
    }
}
