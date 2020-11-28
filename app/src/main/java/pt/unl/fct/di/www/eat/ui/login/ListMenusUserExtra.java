package pt.unl.fct.di.www.eat.ui.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import pt.unl.fct.di.www.eat.R;
import pt.unl.fct.di.www.eat.data.Cart;
import pt.unl.fct.di.www.eat.data.Menu;
import pt.unl.fct.di.www.eat.data.Option;
import pt.unl.fct.di.www.eat.data.RestaurantData;

public class ListMenusUserExtra extends AppCompatActivity {

    ArrayList<String> mDesserts = new ArrayList<>();
    ArrayList<String> mDrinks = new ArrayList<>();
    String drink = "";
    String dessert = "";

    RadioGroup radioDrink, radioDessert;
    Button btnAdd;
    TextView sDrinks, sDesserts, name;
    String email, restaurant, menu;
    DatabaseReference mref;
    ImageView img;

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
        radioDrink = findViewById(R.id.drinksGroup);
        radioDessert = findViewById(R.id.dessertsGroup);

        //listView1 = findViewById(R.id.listViewDrinksUser);
        //listView2 = findViewById(R.id.listViewDessertsUser);
        sDrinks = findViewById(R.id.seeDrinksUser);
        sDesserts = findViewById(R.id.seeDessertsUser);

        img = findViewById(R.id.imageExtra);
        name = findViewById(R.id.menuName);

        DatabaseReference rest = mref.child("Restaurants").child(restaurant);
        rest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checkLogin();
                resetDataExtra();
                if (dataSnapshot.exists()) {
                    RestaurantData post = dataSnapshot.getValue(RestaurantData.class);
                    setDataExtra(post);
                }
                addRadioDrinks();
                addRadioDesserts(mDrinks.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        btnAdd.setOnClickListener(view -> {
            checkLogin();
            RadioButton b = findViewById(radioDrink.getCheckedRadioButtonId());
            RadioButton a = findViewById(radioDessert.getCheckedRadioButtonId());
            if(b!=null)
            drink = b.getText().toString();
            if(a!=null)
            dessert = a.getText().toString();

            DatabaseReference m = mref.child("Restaurants").child(restaurant).child("menu").child(menu);
            m.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Menu menuValue = dataSnapshot.getValue(Menu.class);
                        Cart cart = new Cart(menuValue.getName(), drink, dessert, menuValue.getPrice(), menuValue.getTime());
                        String random = UUID.randomUUID().toString().substring(0, 8);
                        DatabaseReference addCart = mref.child("Carts").child(email).child(random);
                        addCart.setValue(cart);
                        openMenu();
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

    private void openMenu() {
        Intent intent = new Intent(this, ListMenusUser.class);
        intent.putExtra("user", email);
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }

    private void setDataExtra(RestaurantData r) {
        setImage(r.getMenu().get(menu).getImage_url());
        name.setText(menu);
        for (Map.Entry<String, Option> drinks : r.getDrinks().entrySet()) {
            Option drink = drinks.getValue();
            if (drink.getIsAvailable())
                mDrinks.add(drink.getName());
        }
        for (Map.Entry<String, Option> desserts : r.getDesserts().entrySet()) {
            Option dessert = desserts.getValue();
            if (dessert.getIsAvailable())
                mDesserts.add(dessert.getName());
        }
    }

    private void addRadioDrinks(){
        for(int i=0; i<mDrinks.size(); i++) {
            RadioButton b = new RadioButton(this);
            b.setText(mDrinks.get(i));
            b.setId(i);
            b.setTextSize(18);
            radioDrink.addView(b);
        }
    }

    private void addRadioDesserts(int a){
        for(int i=a; i<mDesserts.size() + a; i++) {
            RadioButton b = new RadioButton(this);
            b.setText(mDesserts.get(i-a));
            b.setId(i);
            b.setTextSize(18);
            radioDessert.addView(b);
        }
    }

    private void setImage(String u) {
        URL url = null;
        try {
            url = new URL(u);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bmp;
        try {
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            img.setImageBitmap(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetDataExtra() {
        mDesserts.clear();
        mDrinks.clear();
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
        Intent intent = new Intent(this, UserLoginActivity.class);
        startActivity(intent);
    }
}
