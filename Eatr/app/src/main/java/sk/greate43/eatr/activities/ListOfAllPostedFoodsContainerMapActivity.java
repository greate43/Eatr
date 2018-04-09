package sk.greate43.eatr.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import sk.greate43.eatr.R;
import sk.greate43.eatr.fragments.ListOfAllFoodsMapFragment;
import sk.greate43.eatr.interfaces.ReplaceFragment;

public class ListOfAllPostedFoodsContainerMapActivity extends AppCompatActivity implements ReplaceFragment {
    private static final String TAG = "ListOfAllPostedFoodsCon";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_all_posted_foods_conatiner_map);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.activity_list_of_all_posted_foods_container_map_fragment, ListOfAllFoodsMapFragment.newInstance())
                .commit();

    }


    @Override
    public void onFragmentReplaced(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.activity_list_of_all_posted_foods_container_map_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }
}
