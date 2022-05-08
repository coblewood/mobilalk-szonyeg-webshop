package com.example.szonyegwebshop;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ShopActivity extends AppCompatActivity {
    private static final String LOG_TAG = ShopActivity.class.getName();
    private FirebaseUser user;

    private RecyclerView mRecyclerView;
    private ArrayList<Szonyeg> itemList;
    private SzonyegItemAdapter adapter;
    private int gridNumber = 1;
    private boolean viewRow = true;
    private int cartItems = 0;
    private FrameLayout redCircle;
    private TextView countTextView;

    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            Log.d(LOG_TAG, "Unauthenticated user!");
            Toast.makeText(ShopActivity.this, "Unauthenticated user!", Toast.LENGTH_LONG).show();
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        itemList = new ArrayList<>();
        adapter = new SzonyegItemAdapter(this, itemList);
        mRecyclerView.setAdapter(adapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");

        queryData();
    }

    private void queryData(){
        itemList.clear();
        mItems.orderBy("cartedCount", Query.Direction.DESCENDING).limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                Szonyeg item = document.toObject(Szonyeg.class);
                item.setId(document.getId());
                itemList.add(item);
            }
            if (itemList.size() == 0){
                initializeData();
                queryData();
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void initializeData() {
        String[] itemsList = getResources().getStringArray(R.array.szonyeg_names);
        String[] itemsDesc = getResources().getStringArray(R.array.szonyeg_descriptions);
        String[] itemsPrice = getResources().getStringArray(R.array.szonyeg_prices);
        TypedArray itemsImage = getResources().obtainTypedArray(R.array.szonyeg_images);
        TypedArray itemsRate = getResources().obtainTypedArray(R.array.szonyeg_ratings);



        for (int i = 0; i < itemsList.length; i++) {
            mItems.add(new Szonyeg(
                    itemsList[i],
                    itemsDesc[i],
                    itemsPrice[i],
                    itemsRate.getFloat(i, 0),
                    itemsImage.getResourceId(i, 0),
                    0));
        }
        itemsImage.recycle();
    }

    public void deleteItem(Szonyeg item){
        DocumentReference ref = mItems.document(item._getId());

        ref.delete().addOnSuccessListener(success -> {
            Log.d(LOG_TAG, "Item deleted: " + item._getId());
        }).addOnFailureListener(failure -> {
            Toast.makeText(this, "Item " + item._getId() +" couldn't be deleted", Toast.LENGTH_LONG).show();
        });
        queryData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out_button:
                Log.d(LOG_TAG, "Log out clicked");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.settings:
                Log.d(LOG_TAG, "Settings clicked");
                FirebaseAuth.getInstance().signOut();
                return true;
            case R.id.cart:
                Log.d(LOG_TAG, "Cart clicked");
                return true;
            case R.id.view_selector:
                Log.d(LOG_TAG, "View clicked");
                if (viewRow){
                    changeSpanCount(item, R.drawable.ic_view_grid, 1);
                } else{
                    changeSpanCount(item, R.drawable.ic_view_row, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void changeSpanCount(MenuItem item, int drawableID, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableID);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        countTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(Szonyeg item){
        cartItems = (cartItems + 1);
        if (0 < cartItems){
            countTextView.setText(String.valueOf(cartItems));
        } else {
            countTextView.setText("");
        }
        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);

        mItems.document(item._getId()).update("cartedCount", item.getCartedCount() + 1)
                .addOnFailureListener(failure -> {
                    Toast.makeText(this, "Item " + item._getId() +" couldn't be changed", Toast.LENGTH_LONG).show();
                });
        queryData();
    }
}