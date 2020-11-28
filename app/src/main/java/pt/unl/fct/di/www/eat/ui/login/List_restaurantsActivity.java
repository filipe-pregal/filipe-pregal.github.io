package pt.unl.fct.di.www.eat.ui.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import pt.unl.fct.di.www.eat.R;
import pt.unl.fct.di.www.eat.StartActivity;
import pt.unl.fct.di.www.eat.data.RestaurantData;

public class List_restaurantsActivity extends AppCompatActivity implements RestaurantTagsDialog.RestaurantTagsListener {

    ListView listView;
    String email;
    ArrayList abc = new ArrayList<RestaurantData>();
    DatabaseReference mref;

    ArrayList<String> mTitle = new ArrayList<>();
    ArrayList<String> mTag = new ArrayList<>();
    ArrayList<String> mEmail = new ArrayList<>();
    ArrayList<String> mTime = new ArrayList<>();
    ArrayList<Bitmap> mImg = new ArrayList<>();
    //ArrayList<String> mAddress = new ArrayList<>();
    String[] tags;
    boolean[] selectedTags;
    ArrayList<String> sTags = new ArrayList<String>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_search:
                super.onOptionsItemSelected(item);
                return true;
            case R.id.action_cart:
                Intent intent = new Intent(this, CartUser.class);
                intent.putExtra("user", email);
                intent.putExtra("restaurant", "");
                startActivity(intent);
                return true;
            case R.id.action_filter:
                RestaurantTagsDialog d = new RestaurantTagsDialog(tags, selectedTags, sTags);
                d.show(getSupportFragmentManager(), "Restaurant Tags");
                return true;
            case R.id.action_logout:
                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("Users").child(email).child("token");
                user.setValue("");
                Intent it = new Intent(this, StartActivity.class);
                startActivity(it);
                return true;
            case R.id.action_settings:
                //TODO
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.my_toolbar, menu);

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                DatabaseReference rest = mref.child("Restaurants");
                searchQuery(rest, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                DatabaseReference rest = mref.child("Restaurants");
                searchQuery(rest, newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getRestaurantTags();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_restaurants);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        //Adicionado para a toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar_restaurants);
        setSupportActionBar(myToolbar);

        mref = FirebaseDatabase.getInstance().getReference();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("user");
        }

        listView = findViewById(R.id.listView);

        checkLogin();

        DatabaseReference restaurants = mref.child("Restaurants");
        restaurants.addListenerForSingleValueEvent(new ValueEventListener() {
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
                checkLogin();
                String s = (String) parent.getItemAtPosition(position);
                openMenus(s);
            }
        });
    }

    private void getRestaurantTags() {
        mref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference rest = mref.child("select_lists").child("restaurant_tags");
        rest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tags = new String[(int) dataSnapshot.getChildrenCount()];
                    selectedTags = new boolean[(int) dataSnapshot.getChildrenCount()];
                    int counter = 0;
                    for (DataSnapshot tag : dataSnapshot.getChildren()) {
                        tags[counter] = tag.getValue(String.class);
                        sTags.add(tag.getValue(String.class));
                        selectedTags[counter] = true;
                        counter++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void searchFilter(DatabaseReference rest) {
        listView = findViewById(R.id.listView);

        rest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                resetData();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        RestaurantData post = child.getValue(RestaurantData.class);
                        if (sTags.contains(post.getTag())) {
                            abc.add(post);
                        }
                    }
                }
                getDataInPlace();
                MyAdapter adapter = new MyAdapter(getApplicationContext());
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void searchQuery(DatabaseReference rest, String query) {
        listView = findViewById(R.id.listView);

        rest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                resetData();
                if (dataSnapshot.exists() && !query.isEmpty()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        RestaurantData post = child.getValue(RestaurantData.class);
                        if (post.getName().toLowerCase().contains(query.toLowerCase())) {
                            abc.add(post);
                        }
                    }
                } else if (query.isEmpty()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        RestaurantData post = child.getValue(RestaurantData.class);
                        abc.add(post);
                    }
                }
                getDataInPlace();
                MyAdapter adapter = new MyAdapter(getApplicationContext());
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void getDataInPlace() {
        for (int i = 0; i < abc.size(); i++) {
            RestaurantData r = (RestaurantData) abc.get(i);
            mTitle.add(r.getName());
            mEmail.add(r.getEmail().replace(".", "_"));
            mTag.add(r.getTag());
            mTime.add(r.getTime().substring(11, 16));
            setImage(r.getImage_url());
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
            mImg.add(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetData() {
        abc.clear();
        mTitle.clear();
        mTime.clear();
        mEmail.clear();
        mTag.clear();
        mImg.clear();
    }

    private void openMenus(String restaurant) {
        Intent intent = new Intent(this, ListMenusUser.class);
        intent.putExtra("user", email);
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }

    private void redirectLogin() {
        getIntent().removeExtra("user");
        Intent intent = new Intent(this, UserLoginActivity.class);
        startActivity(intent);
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

    @Override
    public void onDialogPositiveClick(RestaurantTagsDialog dialog) {
        sTags = dialog.sTags;
        DatabaseReference rest = mref.child("Restaurants");
        searchFilter(rest);
    }

    @Override
    public void onDialogNegativeClick(RestaurantTagsDialog dialog) {
        sTags = dialog.sTags;
        DatabaseReference rest = mref.child("Restaurants");
        searchFilter(rest);
    }

    class MyAdapter extends ArrayAdapter<String> {

        MyAdapter(Context c) {

            super(c, R.layout.row_restaurant, R.id.emailR, mEmail);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_restaurant, parent, false);
            TextView myTitle = row.findViewById(R.id.titleRestaurant);
            TextView myTag = row.findViewById(R.id.tag);
            TextView myTime = row.findViewById(R.id.time);
            ImageView myImg = row.findViewById(R.id.imgR);

            myTitle.setText(mTitle.get(position));
            myTag.setText(mTag.get(position));
            myTime.setText("Close time: " + mTime.get(position));
            myImg.setImageBitmap(mImg.get(position));

            return row;
        }
    }
}