package sk.greate43.eatr.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class BuyerFragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseUser user;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getChildFragmentManager();

            switch (item.getItemId()) {
                case R.id.navigation_buyer_buy:

                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_buyer_container, ListOfAllPostedFoodFragment.newInstance())
                            .addToBackStack(null)
                            .commit();
                    return true;
                case R.id.navigation_buyer_history:
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_buyer_container, HistoryFragment.newInstance(Constants.TYPE_BUYER))
                            .addToBackStack(null)
                            .commit();
                    return true;

            }
            return false;
        }
    };

    public BuyerFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static BuyerFragment newInstance() {
        return new BuyerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null)
            getActivity().setTitle("Buyer Fragment");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buyer, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_buyer_container, ListOfAllPostedFoodFragment.newInstance())
                .commit();

        BottomNavigationView navigation = view.findViewById(R.id.activity_buyer_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        return view;
    }

}
