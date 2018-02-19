package sk.greate43.eatr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sk.greate43.eatr.R;
import sk.greate43.eatr.fragments.HistoryFragment;
import sk.greate43.eatr.fragments.ListOfAllPostedFoodFragment;
import sk.greate43.eatr.fragments.SettingFragment;

public class BuyerActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            switch (item.getItemId()) {
                case R.id.navigation_buyer_buy:

                    fragmentManager.beginTransaction()
                            .replace(R.id.activity_buyer_fragment_container, ListOfAllPostedFoodFragment.newInstance())
                            .addToBackStack(null)
                            .commit();
                    return true;
                case R.id.navigation_buyer_history:
                    fragmentManager.beginTransaction()
                            .replace(R.id.activity_buyer_fragment_container, HistoryFragment.newInstance())
                            .addToBackStack(null)
                            .commit();
                    return true;
                case R.id.navigation_buyer_settings:
                    fragmentManager.beginTransaction()
                            .replace(R.id.activity_buyer_fragment_container, SettingFragment.newInstance())
                            .addToBackStack(null)
                            .commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        BottomNavigationView navigation = findViewById(R.id.activity_buyer_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        if (user != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
            return true;
        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_sign_out:
                if (user != null) {
                    mAuth.signOut();


                    finish();
                    Intent intent = new Intent(BuyerActivity.this, MainActivity.class);

                    startActivity(intent);

                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
