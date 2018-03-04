package sk.greate43.eatr.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sk.greate43.eatr.R;
import sk.greate43.eatr.utils.Constants;


public class SellerFragment extends Fragment {


    @NonNull
    public static SellerFragment getInstance(){
        return new SellerFragment();
    }

    FirebaseAuth mAuth;
    FirebaseUser user;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getChildFragmentManager();

            switch (item.getItemId()) {
                case R.id.navigation_seller_home:

                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_seller_container, PostedFoodPagerFragment.newInstance())
                            .addToBackStack(null)
                            .commit();
                    return true;
                case R.id.navigation_seller_history:


                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_seller_container, HistoryFragment.newInstance(Constants.TYPE_SELLER))
                            .addToBackStack(null)
                            .commit();
                    return true;
                case R.id.navigation_seller_settings:
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_seller_container, SettingFragment.newInstance())
                            .addToBackStack(null)
                            .commit();

                    return true;
            }
            return false;
        }
    };

    public SellerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        BottomNavigationView navigation = view.findViewById(R.id.activity_seller_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fragmentManager = getChildFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.fragment_seller_container, PostedFoodPagerFragment.newInstance())
                .commit();

        return view;
    }


}
