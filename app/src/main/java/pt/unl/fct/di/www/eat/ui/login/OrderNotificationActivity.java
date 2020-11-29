package pt.unl.fct.di.www.eat.ui.login;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pt.unl.fct.di.www.eat.R;
import pt.unl.fct.di.www.eat.StartActivity;


public class OrderNotificationActivity extends AppCompatActivity {
    String address, email, restaurant = "";

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
                //TODO
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
        setContentView(R.layout.activity_order_notification);

        ImageButton maps = findViewById(R.id.maps_url);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
            restaurant = extras.getString("restaurant");
            DatabaseReference rest = FirebaseDatabase.getInstance().getReference().child("Restaurants").child(restaurant).child("address");

            rest.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    address = dataSnapshot.getValue(String.class);
                    address = address.replace(" ", "+");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + address);
                Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                intent.setPackage("com.google.android.apps.maps");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                startActivity(intent);
            }
        });

    }
}