package pt.unl.fct.di.www.eat.ui.login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import pt.unl.fct.di.www.eat.R;
import pt.unl.fct.di.www.eat.data.Cart;
import pt.unl.fct.di.www.eat.data.Request;
import pt.unl.fct.di.www.eat.data.RequestItem;

public class PickTableActivity extends AppCompatActivity {

    String table = "";
    Button btn2, btn3, btn6, btnSave;

    String email, restaurant, payment, eat, code, res_name;
    Double time, price;
    DatabaseReference mref;
    HashMap<String, RequestItem> aux = new HashMap<>();
    ArrayList<RequestItem> items = new ArrayList<>();

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_table);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mref = FirebaseDatabase.getInstance().getReference();

        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn6 = findViewById(R.id.btn6);
        btnSave = findViewById(R.id.eatBtn);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("user");
            restaurant = extras.getString("restaurant");
            time = extras.getDouble("time");
            price = extras.getDouble("price");
            payment = extras.getString("payment");
            res_name = extras.getString("res_name");
        }

        checkLogin();

        code = UUID.randomUUID().toString().substring(0, 5);

        btn2.setOnClickListener(view -> aux(btn2, "2"));

        btn3.setOnClickListener(view -> aux(btn3, "3"));

        btn6.setOnClickListener(view -> aux(btn6, "6"));

        btnSave.setOnClickListener(view -> {
            if(table.equals(""))
                Toast.makeText(getApplicationContext(), "You have to select a table!", Toast.LENGTH_SHORT).show();
            else {
                createRequest();
                openFinal();
            }
        });
    }

    private void openFinal(){
        Intent intent = new Intent(this, OrderNotificationActivity.class);
        intent.putExtra("user", email);
        intent.putExtra("restaurant", restaurant);
        intent.putExtra("time", time);
        intent.putExtra("price", price);
        intent.putExtra("payment", payment);
        intent.putExtra("eat", eat);
        intent.putExtra("code", code);
        startActivity(intent);
        finish();
    }

    private void createRequest(){
        DatabaseReference addRequest = mref.child("Requests").child(restaurant).child(email);

        DatabaseReference cartRef = mref.child("Carts").child(restaurant).child(email);
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checkLogin();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Cart cart = child.getValue(Cart.class);
                        setData(cart);
                    }
                    for (Map.Entry<String, RequestItem> i : aux.entrySet())
                        items.add(i.getValue());
                    Request request = new Request(time,price,payment,"Table " + table, items, res_name);
                    addRequest.child(code).setValue(request);
                    cartRef.removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void setData(Cart cart){
        String drink = cart.getDrink();
        setAux(drink);
        String dessert = cart.getDessert();
        setAux(dessert);
        String dish = cart.getName();
        setAux(dish);
    }

    private void setAux(String type){
        if (!type.equals("")) {
            if (aux.containsKey(type))
                aux.get(type).aux();
            else
                aux.put(type, new RequestItem(type, 1.0));
        }
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

    private void aux(Button b, String a){
        if(table.equals("")){
            b.setBackgroundColor(Color.BLUE);
            table = b.getText().toString();
        }
        else if(table.equals(a)){
            b.setBackgroundColor(Color.GREEN);
            table = "";
        }else {
            if(a.equals("2")){
                btn2.setBackgroundColor(Color.BLUE);
                btn3.setBackgroundColor(Color.GREEN);
                btn6.setBackgroundColor(Color.GREEN);
                table = a;
            }else if (a.equals("3")){
                btn3.setBackgroundColor(Color.BLUE);
                btn2.setBackgroundColor(Color.GREEN);
                btn6.setBackgroundColor(Color.GREEN);
                table = a;
            }else {
                btn6.setBackgroundColor(Color.BLUE);
                btn2.setBackgroundColor(Color.GREEN);
                btn3.setBackgroundColor(Color.GREEN);
                table = a;
            }
        }
    }
}