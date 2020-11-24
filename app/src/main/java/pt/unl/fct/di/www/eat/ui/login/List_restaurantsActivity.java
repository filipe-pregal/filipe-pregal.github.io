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

    //Map aux = new HashMap<String, RestaurantData>();
    ArrayList abc = new ArrayList<RestaurantData>();

    ArrayList<String> mTitle = new ArrayList<>();
    ArrayList<String> mTag = new ArrayList<>();
    ArrayList<String> mEmail = new ArrayList<>();
    ArrayList<Double> mTime = new ArrayList<>();
    ArrayList<String> mAddress = new ArrayList<>();


    String ab[] = {};
    //String mTitle[] = {"teste1", "teste2", "teste3"};
    //String mTag[] = {"peixe", "carne", "vegetariano"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_restaurants);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        //actionBar.setLogo();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("user");
        }

        listView = findViewById(R.id.listView);
        //getRestaurantsData();

        DatabaseReference mref = FirebaseDatabase.getInstance().getReference();
        Query postQuery = mref.child("Restaurants");
        // Attach a listener to read the data at our posts reference
        postQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    RestaurantData post = child.getValue(RestaurantData.class);
                    abc.add(post);
                    //aux.put(post.getEmail(), post);
                    //System.out.println("dentro do metodo" + aux);
                }
                getDataInPlace();
                //System.out.println(mTitle);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        getDataInPlace();
        MyAdapter adapter = new MyAdapter(getApplicationContext(), mTitle, mTag, mEmail, mAddress);
        listView.setAdapter(adapter);

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
            mEmail.add(r.getEmail());
            mTag.add(r.getTag());
            mAddress.add(r.getAddress());
        }
        System.out.println("entrei ca " + mTitle.size());
    }

    private void openMenus(String restaurant){
        Intent intent = new Intent(this, ListMenusUser.class);
        intent.putExtra("user", email);
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        ArrayList<String> rTitle, rTag, rEmail, rAddress;

        MyAdapter(Context c, ArrayList<String> title, ArrayList<String> tag, ArrayList<String> email, ArrayList<String> address){
            super(c,R.layout.row_restaurant, R.id.emailR, email);
            this.context = c;
            this.rTitle = title;
            this.rTag = tag;
            this.rEmail = email;
            this.rAddress = address;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_restaurant,parent, false);
            TextView myTitle = row.findViewById(R.id.titleRestaurant);
            TextView myTag = row.findViewById(R.id.tag);
            TextView myTime = row.findViewById(R.id.time);
            TextView myEmail = row.findViewById(R.id.emailR);

            myTitle.setText(rTitle.get(position));
            myTag.setText(rTag.get(position));
            myTime.setText(rAddress.get(position));

            return row;
        }
    }
}