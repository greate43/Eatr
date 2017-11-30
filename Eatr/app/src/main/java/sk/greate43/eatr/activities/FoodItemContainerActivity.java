package sk.greate43.eatr.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import sk.greate43.eatr.R;
import sk.greate43.eatr.fragments.AddFoodItemFragment;
import sk.greate43.eatr.interfaces.ReplaceFragment;


public class FoodItemContainerActivity extends AppCompatActivity implements ReplaceFragment{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_containter);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.activity_food_item_container_fragment, new AddFoodItemFragment())
                .commit();

    }


    @Override
    public void onFragmentReplaced(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.activity_food_item_container_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }
}
