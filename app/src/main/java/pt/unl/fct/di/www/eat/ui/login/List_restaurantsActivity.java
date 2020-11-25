package pt.unl.fct.di.www.eat.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import pt.unl.fct.di.www.eat.R;
import pt.unl.fct.di.www.eat.data.RestaurantData;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class List_restaurantsActivity extends AppCompatActivity {

    ListView listView;
    String email;
    ArrayList abc = new ArrayList<RestaurantData>();
    DatabaseReference mref;

    ArrayList<String> mTitle = new ArrayList<>();
    ArrayList<String> mTag = new ArrayList<>();
    ArrayList<String> mEmail = new ArrayList<>();
    ArrayList<String> mTime = new ArrayList<>();
    //ArrayList<String> mAddress = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_restaurants);

        mref = FirebaseDatabase.getInstance().getReference();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("user");
        }

        listView = findViewById(R.id.listView);

        checkLogin();

        DatabaseReference restaurants = mref.child("Restaurants");
        restaurants.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    RestaurantData post = child.getValue(RestaurantData.class);
                    abc.add(post);
                }
                getDataInPlace();
                MyAdapter adapter = new MyAdapter(getApplicationContext());
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = (String) parent.getItemAtPosition(position);
                openMenus(s);
            }
        });
    }

    private void getDataInPlace(){
        for(int i=0; i< abc.size();i++){
            RestaurantData r = (RestaurantData) abc.get(i);
            mTitle.add(r.getName());
            mEmail.add(r.getEmail().replace(".", "_"));
            mTag.add(r.getTag());
            mTime.add(r.getTime().substring(11));
            //mAddress.add(r.getAddress());
        }
    }

    private void openMenus(String restaurant){
        Intent intent = new Intent(this, ListMenusUser.class);
        intent.putExtra("user", email);
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }

    private void redirectLogin(){
        getIntent().removeExtra("user");
        Intent intent = new Intent(this, UserLoginActivity.class);
        startActivity(intent);
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

    class MyAdapter extends ArrayAdapter<String> {

        MyAdapter(Context c){
            super(c,R.layout.row_restaurant, R.id.emailR, mEmail);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_restaurant,parent, false);
            TextView myTitle = row.findViewById(R.id.titleRestaurant);
            TextView myTag = row.findViewById(R.id.tag);
            TextView myTime = row.findViewById(R.id.time);
            //TextView myEmail = row.findViewById(R.id.emailR);

            myTitle.setText(mTitle.get(position));
            myTag.setText(mTag.get(position));
            myTime.setText(mTime.get(position));
            //myEmail.setText(rEmail.get(position));

            return row;
        }
    }
}