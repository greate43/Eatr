package sk.greate43.eatr.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.fragments.AddFoodItemFragment;
import sk.greate43.eatr.interfaces.ReplaceFragment;
import sk.greate43.eatr.utils.Constants;


public class FoodItemContainerActivity extends AppCompatActivity implements ReplaceFragment {
    private static final String TAG = "FoodItemContainerActivi";
    private Food mFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_containter);
        Intent intent = getIntent();
        mFood = (Food) intent.getSerializableExtra(Constants.ARGS_FOOD);

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (mFood != null) {
            Log.d(TAG, "onCreate: " + mFood.getPushId());
            fragmentManager.beginTransaction()
                    .replace(R.id.activity_food_item_container_fragment, AddFoodItemFragment.newInstance(mFood))
                    .commit();
        } else {
            Log.d(TAG, "onCreate: new data added");

            fragmentManager.beginTransaction()
                    .replace(R.id.activity_food_item_container_fragment, AddFoodItemFragment.newInstance())
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
