package pt.unl.fct.di.www.eat.ui.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

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
import java.util.UUID;

import pt.unl.fct.di.www.eat.R;
import pt.unl.fct.di.www.eat.StartActivity;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("Users").child(email).child("token");
                user.setValue("");
                Intent it = new Intent(this, StartActivity.class);
                startActivity(it);
                return true;
            case R.id.action_settings:
                Intent i2 = new Intent(this, Settings_page.class);
                i2.putExtra("user", email);
                i2.putExtra("restaurant", restaurant);
                startActivity(i2);
                return true;
            case android.R.id.home:
                Intent i1 = new Intent(this, ListMenusUser.class);
                i1.putExtra("user", email);
                i1.putExtra("restaurant", restaurant);
                startActivity(i1);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.logout_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

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
                RestaurantData aux = null ;
                if (dataSnapshot.exists()) {
                    aux = dataSnapshot.getValue(RestaurantData.class);
                }
                if(aux != null) {
                    setDataExtra(aux);
                    addRadioDrinks();
                    addRadioDesserts(mDrinks.size());
                }
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
            if (b != null)
                drink = b.getText().toString();
            if (a != null)
                dessert = a.getText().toString();

            DatabaseReference m = mref.child("Restaurants").child(restaurant).child("menu").child(menu);
            m.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Menu menuValue = dataSnapshot.getValue(Menu.class);
                        Cart cart = new Cart(menuValue.getName(), drink, dessert, menuValue.getPrice(), menuValue.getTime());
                        String random = UUID.randomUUID().toString().substring(0, 8);
                        DatabaseReference addCart = mref.child("Carts").child(restaurant).child(email).child(random);
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
        finish();
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

    private void addRadioDrinks() {
        for (int i = 0; i < mDrinks.size(); i++) {
            RadioButton b = new RadioButton(this);
            b.setText(mDrinks.get(i));
            b.setId(i);
            b.setTextSize(18);
            radioDrink.addView(b);
        }
    }

    private void addRadioDesserts(int a) {
        for (int i = a; i < mDesserts.size() + a; i++) {
            RadioButton b = new RadioButton(this);
            b.setText(mDesserts.get(i - a));
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
        radioDrink.removeAllViews();
        radioDessert.removeAllViews();
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
        finish();
    }
}
