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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pt.unl.fct.di.www.eat.R;
import pt.unl.fct.di.www.eat.StartActivity;
import pt.unl.fct.di.www.eat.data.Menu;
import pt.unl.fct.di.www.eat.data.RestaurantData;

public class ListMenusUser extends AppCompatActivity implements RestaurantTagsDialog.RestaurantTagsListener {

    ListView listView;
    Button btn;
    String email, restaurant, res_name;
    DatabaseReference mref;

    ArrayList<String> mTitle = new ArrayList<>();
    ArrayList<String> menuKeys = new ArrayList<>();
    ArrayList<String> mTags = new ArrayList<>();
    ArrayList<Double> mPrice = new ArrayList<>();
    ArrayList<Double> mTime = new ArrayList<>();
    ArrayList<Bitmap> mImg = new ArrayList<>();
    String[] mtags;
    boolean[] mselectedTags;
    ArrayList<String> msTags = new ArrayList<String>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_search:
                super.onOptionsItemSelected(item);
                return true;
            case R.id.action_cart:
                Intent intent = new Intent(this, CartUser.class);
                intent.putExtra("user", email);
                intent.putExtra("restaurant", restaurant);
                startActivity(intent);
                return true;
            case R.id.action_filter:
                RestaurantTagsDialog d = new RestaurantTagsDialog(mtags, mselectedTags, msTags);
                d.show(getSupportFragmentManager(), "Menu Tags");
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
            case android.R.id.home:
                Intent i1 = new Intent(this, List_restaurantsActivity.class);
                i1.putExtra("user", email);
                startActivity(i1);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.lists_restaurants_menus_user, menu);

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                DatabaseReference rest = mref.child("Restaurants").child(restaurant);
                searchQuery(rest, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                DatabaseReference rest = mref.child("Restaurants").child(restaurant);
                searchQuery(rest, newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void searchFilter(DatabaseReference rest) {
        listView = findViewById(R.id.listViewMenuUser);

        rest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                resetData();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot menu : dataSnapshot.child("menu").getChildren()) {

                        RestaurantData post = dataSnapshot.getValue(RestaurantData.class);
                        Menu menuSelected = menu.getValue(Menu.class);

                        if (msTags.contains(menuSelected.getTag())) {
                            Map<String, Menu> postMenu = new HashMap<String, Menu>();
                            postMenu.put(menuSelected.getName(), menuSelected);
                            post.setMenu(postMenu);
                            setData(post);
                        }
                    }
                }
                MyAdapter adapter = new MyAdapter(getApplicationContext());
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    @Override
    public void onDialogPositiveClick(RestaurantTagsDialog dialog) {
        msTags = dialog.sTags;
        DatabaseReference rest = mref.child("Restaurants").child(restaurant);
        searchFilter(rest);
    }

    @Override
    public void onDialogNegativeClick(RestaurantTagsDialog dialog) {
        msTags = dialog.sTags;
        DatabaseReference rest = mref.child("Restaurants").child(restaurant);
        searchFilter(rest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_menus_user);

        getMenuTags();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("user");
            restaurant = extras.getString("restaurant");
        }

        //Adicionado para a toolbar
        Toolbar myToolbar = findViewById(R.id.list_menus_user);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        mref = FirebaseDatabase.getInstance().getReference();

        btn = findViewById(R.id.cartBtn);

        listView = findViewById(R.id.listViewMenuUser);
        checkLogin();

        DatabaseReference rest = mref.child("Restaurants").child(restaurant);
        rest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                resetData();
                if (dataSnapshot.exists()) {
                    RestaurantData post = dataSnapshot.getValue(RestaurantData.class);
                    setData(post);
                }
                MyAdapter adapter = new MyAdapter(getApplicationContext());
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            checkLogin();
            redirectExtras(menuKeys.get(position));
        });

        btn.setOnClickListener(view -> {
            checkLogin();
            redirectCart();
        });
    }

    private void getMenuTags() {
        mref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference rest = mref.child("select_lists").child("menu_tags");
        rest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mtags = new String[(int) dataSnapshot.getChildrenCount()];
                    mselectedTags = new boolean[(int) dataSnapshot.getChildrenCount()];
                    int counter = 0;
                    for (DataSnapshot tag : dataSnapshot.getChildren()) {
                        mtags[counter] = tag.getValue(String.class);
                        msTags.add(tag.getValue(String.class));
                        mselectedTags[counter] = true;
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

    private void setData(RestaurantData r) {
        for (Map.Entry<String, Menu> menus : r.getMenu().entrySet()) {
            Menu menu = menus.getValue();
            if (menu.getIsAvailable()) {
                mTitle.add(menu.getName());
                mPrice.add(menu.getPrice());
                mTime.add(menu.getTime());
                mTags.add(menu.getTag());
                menuKeys.add(menus.getKey());
                setImage(menu.getImage_url());
            }
        }
        res_name = r.getName();
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
        mTitle.clear();
        mTags.clear();
        mPrice.clear();
        mTime.clear();
        mImg.clear();
    }

    private void redirectExtras(String menu) {
        Intent intent = new Intent(this, ListMenusUserExtra.class);
        intent.putExtra("user", email);
        intent.putExtra("restaurant", restaurant);
        intent.putExtra("menu", menu);
        startActivity(intent);
    }

    private void redirectCart() {
        Intent intent = new Intent(this, CartUser.class);
        intent.putExtra("user", email);
        intent.putExtra("restaurant", restaurant);
        intent.putExtra("res_name", res_name);
        startActivity(intent);
    }

    private String convertTime(double time) {
        String[] aux = String.valueOf(time).split("\\.");
        int hours = Integer.parseInt(aux[0]);
        int minutes = (int) (Double.parseDouble("0." + aux[1]) * 60);
        if (minutes == 0)
            return hours + "h";
        if (hours == 0)
            return minutes + "m";
        return hours + "h" + minutes + "m";
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

    private void searchQuery(DatabaseReference rest, String query) {
        listView = findViewById(R.id.listViewMenuUser);

        rest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                resetData();
                if (dataSnapshot.exists() && !query.isEmpty()) {
                    for (DataSnapshot menu : dataSnapshot.child("menu").getChildren()) {
                        if (menu.getKey().toLowerCase().contains(query.toLowerCase())) {
                            RestaurantData post = dataSnapshot.getValue(RestaurantData.class);
                            Menu menuSelected = menu.getValue(Menu.class);
                            Map<String, Menu> postMenu = new HashMap<String, Menu>();
                            postMenu.put(menuSelected.getName(), menuSelected);
                            post.setMenu(postMenu);
                            setData(post);
                        }
                    }
                } else if (query.isEmpty()) {
                    RestaurantData post = dataSnapshot.getValue(RestaurantData.class);
                    setData(post);
                }
                MyAdapter adapter = new MyAdapter(getApplicationContext());
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    class MyAdapter extends ArrayAdapter<String> {

        MyAdapter(Context c) {
            super(c, R.layout.row_menu_user, R.id.titleMenu, mTitle);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_menu_user, parent, false);
            TextView myDish = row.findViewById(R.id.dish);
            TextView myTag = row.findViewById(R.id.tagM);
            TextView myPrice = row.findViewById(R.id.price);
            TextView myTime = row.findViewById(R.id.timeM);
            ImageView img = row.findViewById(R.id.imgM);


            myDish.setText(mTitle.get(position));
            myTag.setText("#" + mTags.get(position));
            myPrice.setText(mPrice.get(position).toString().concat("€"));
            myTime.setText(convertTime(mTime.get(position)));

            img.setImageBitmap(mImg.get(position));
            return row;
        }
    }
}
