package sk.greate43.eatr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.fragments.AddFoodItemFragment;
import sk.greate43.eatr.interfaces.ReplaceFragment;
import sk.greate43.eatr.utils.Constants;


public class FoodItemContainerActivity extends AppCompatActivity implements ReplaceFragment {
    private static final String TAG = "FoodItemContainerActivi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_containter);
        Intent intent = getIntent();
        Food food = (Food) intent.getSerializableExtra(Constants.ARGS_FOOD);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (food != null) {
            Log.d(TAG, "onCreate: "+food.getPushId());
            fragmentManager.beginTransaction()
                    .add(R.id.activity_food_item_container_fragment, AddFoodItemFragment.newInstance(food))
                    .commit();
        } else {
            Log.d(TAG, "onCreate: new data added");

            fragmentManager.beginTransaction()
                    .add(R.id.activity_food_item_container_fragment, AddFoodItemFragment.newInstance())
                    .commit();
        }
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
