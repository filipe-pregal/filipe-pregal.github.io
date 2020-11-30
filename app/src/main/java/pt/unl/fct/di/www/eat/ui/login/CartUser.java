package pt.unl.fct.di.www.eat.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

import pt.unl.fct.di.www.eat.R;
import pt.unl.fct.di.www.eat.StartActivity;
import pt.unl.fct.di.www.eat.data.Cart;

public class CartUser extends AppCompatActivity {

    Button btnPayment;
    ListView listView;
    TextView emptyText;
    String email, restaurant, res_name;
    DatabaseReference mref;

    ArrayList<String> mTitle = new ArrayList<>();
    ArrayList<String> mDrink = new ArrayList<>();
    ArrayList<String> mDessert = new ArrayList<>();
    ArrayList<Double> mPrice = new ArrayList<>();
    ArrayList<String> cartKey = new ArrayList<>();
    Double time = 0.0;

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
        setContentView(R.layout.activity_cart_user);

        mref = FirebaseDatabase.getInstance().getReference();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("user");
            restaurant = extras.getString("restaurant");
            res_name = extras.getString("res_name");
        }

        listView = findViewById(R.id.listViewCart);
        emptyText = findViewById(R.id.emptyText);
        checkLogin();

        btnPayment = findViewById(R.id.payment);

        DatabaseReference cartRef = mref.child("Carts").child(restaurant).child(email);
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checkLogin();
                resetData();
                listView.setEmptyView(emptyText);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Cart cart = child.getValue(Cart.class);
                        setData(child.getKey(), cart);
                    }
                }
                double total = getTotalPrice();
                if (total == 0.0) {
                    btnPayment.setText("Proceed to Payment");
                    btnPayment.setEnabled(false);
                } else {
                    btnPayment.setText("Proceed to Payment (" + format2decimal(total) +"€)");
                    btnPayment.setEnabled(true);
                }
                MyAdapter adapter = new MyAdapter(getApplicationContext());
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        btnPayment.setOnClickListener(view -> {
            checkLogin();
            if(!cartKey.isEmpty())
                openPayment();
            else
                Toast.makeText(getApplicationContext(), "You can't advance with an empty cart!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setData(String key, Cart cart) {
        cartKey.add(key);
        mTitle.add(cart.getName());
        mDrink.add(cart.getDrink());
        mDessert.add(cart.getDessert());
        mPrice.add(cart.getPrice());
        Double t = cart.getTime();
        if (t > time)
            time = t;
    }

    private void resetData() {
        time = 0.0;
        mTitle.clear();
        mDrink.clear();
        mDessert.clear();
        mPrice.clear();
        cartKey.clear();
    }

    private void openMenus() {
        Intent intent = new Intent(this, ListMenusUser.class);
        intent.putExtra("user", email);
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }

    private void openPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        Double price = getTotalPrice();
        intent.putExtra("user", email);
        intent.putExtra("restaurant", restaurant);
        intent.putExtra("time", time);
        intent.putExtra("price", price);
        intent.putExtra("res_name", res_name);
        startActivity(intent);
    }

    private double getTotalPrice() {
        Double price = 0.0;
        for (Double i : mPrice)
            price += i;
        return price;
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

    class MyAdapter extends ArrayAdapter<String> {

        MyAdapter(Context c) {
            super(c, R.layout.row_cart, R.id.menuTitle, mTitle);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_cart, parent, false);

            TextView myTitle = row.findViewById(R.id.menuTitle);
            TextView myDrink = row.findViewById(R.id.menuDrink);
            TextView myDessert = row.findViewById(R.id.menuDessert);
            TextView myPrice = row.findViewById(R.id.menuPrice);
            ImageButton myRemove = row.findViewById(R.id.remove);

            myRemove.setImageResource(R.drawable.remove);

            myRemove.setOnClickListener(view -> {
                DatabaseReference cartRef = mref.child("Carts").child(restaurant).child(email).child(cartKey.get(position));
                cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            cartRef.removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            });

            String drink = mDrink.get(position);
            if (drink.equals("")) {
                myDrink.setVisibility(View.GONE);
            }

            String dessert = mDessert.get(position);
            if (dessert.equals("")) {
                myDessert.setVisibility(View.GONE);
            }

            myTitle.setText(mTitle.get(position));
            myDrink.setText(Html.fromHtml("<b>Drink</b> " + drink));
            myDessert.setText(Html.fromHtml("<b>Dessert</b> " + dessert));
            myPrice.setText(format2decimal(mPrice.get(position)).concat("€"));

            return row;
        }
    }

    private String format2decimal(double num) {
        return String.format("%.2f", num);
    }
}
