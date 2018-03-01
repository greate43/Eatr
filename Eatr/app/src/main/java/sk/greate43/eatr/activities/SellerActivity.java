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
import sk.greate43.eatr.fragments.PostedFoodFragment;
import sk.greate43.eatr.fragments.SettingFragment;
import sk.greate43.eatr.utils.Constants;

public class SellerActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            switch (item.getItemId()) {
                case R.id.navigation_seller_home:

                    fragmentManager.beginTransaction()
                            .replace(R.id.activity_seller_fragment_container, PostedFoodFragment.newInstance())
                            .addToBackStack(null)
                            .commit();
                    return true;
                case R.id.navigation_seller_history:


                    fragmentManager.beginTransaction()
                            .replace(R.id.activity_seller_fragment_container, HistoryFragment.newInstance(Constants.TYPE_SELLER))
                            .addToBackStack(null)
                            .commit();
                    return true;
                case R.id.navigation_seller_settings:
                    fragmentManager.beginTransaction()
                            .replace(R.id.activity_seller_fragment_container, SettingFragment.newInstance())
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
        setContentView(R.layout.activity_seller);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        BottomNavigationView navigation = findViewById(R.id.activity_seller_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.activity_seller_fragment_container, PostedFoodFragment.newInstance())
                .commit();

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
                    Intent intent = new Intent(SellerActivity.this, MainActivity.class);

                    startActivity(intent);

                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
