package sk.greate43.eatr.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import sk.greate43.eatr.R;
import sk.greate43.eatr.fragments.AuthenticationFragment;
import sk.greate43.eatr.interfaces.ReplaceFragment;


public class MainActivity extends AppCompatActivity implements ReplaceFragment{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.actvivity_main_frame_layout_fragment_container, AuthenticationFragment.newInstance())
                .commit();

    }


    @Override
    public void onFragmentReplaced(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.actvivity_main_frame_layout_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}