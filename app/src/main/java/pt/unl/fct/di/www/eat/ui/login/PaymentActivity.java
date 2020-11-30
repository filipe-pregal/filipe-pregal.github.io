package pt.unl.fct.di.www.eat.ui.login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import pt.unl.fct.di.www.eat.R;

public class PaymentActivity extends AppCompatActivity {

    Button localBtn, optionsBtn;
    String email, restaurant, payment, res_name;
    Double time, price;
    DatabaseReference mref;



    @Override
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
        setContentView(R.layout.activity_payment);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mref = FirebaseDatabase.getInstance().getReference();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("user");
            restaurant = extras.getString("restaurant");
            time = extras.getDouble("time");
            price = extras.getDouble("price");
            res_name = extras.getString("res_name");
        }

        checkLogin();

        localBtn = findViewById(R.id.local);
        optionsBtn = findViewById(R.id.options);

        localBtn.setOnClickListener(view -> {
            payment = "Cash";
            openEat();
        });

        optionsBtn.setOnClickListener(view -> {
            payment =  "Paid";
            openEat();
        });

    }

    private void openEat(){
        Intent intent = new Intent(this, EatOptionsActivity.class);
        intent.putExtra("user", email);
        intent.putExtra("restaurant", restaurant);
        intent.putExtra("time", time);
        intent.putExtra("price", price);
        intent.putExtra("payment", payment);
        intent.putExtra("res_name", res_name);
        startActivity(intent);
        finish();
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
