package sk.greate43.eatr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.fragments.DetailFoodFragment;
import sk.greate43.eatr.utils.Constants;

public class DetailFoodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_food);
        Intent intent = getIntent();
        Food food = (Food) intent.getSerializableExtra(Constants.ARGS_FOOD);

        if (food != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.activity_detail_food_fragment_container, DetailFoodFragment.newInstance(food))
                    .commit();

        }

    }
}
