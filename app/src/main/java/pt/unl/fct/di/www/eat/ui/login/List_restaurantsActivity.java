package pt.unl.fct.di.www.eat.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pt.unl.fct.di.www.eat.R;
import pt.unl.fct.di.www.eat.data.RestaurantData;

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_search:

                super.onOptionsItemSelected(item);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_toolbar, menu);

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println(newText);
                return false;
            }
        });
        //SearchView searchView = (SearchView) searchItem.getActionView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_restaurants);

        //Adicionado para a toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_restaurants);
        setSupportActionBar(myToolbar);

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
                resetData();
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
        }
    }

    private void resetData(){
        abc.clear();
        mTitle.clear();
        mTime.clear();
        mEmail.clear();
        mTag.clear();
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