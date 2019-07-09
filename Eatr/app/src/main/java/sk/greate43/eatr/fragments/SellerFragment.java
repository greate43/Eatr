package sk.greate43.eatr.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sk.greate43.eatr.R;
import sk.greate43.eatr.utils.Constants;


public class SellerFragment extends Fragment {


    FirebaseAuth mAuth;
    FirebaseUser user;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
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

                }
                return false;
            };
    private BottomNavigationView mNavigation;

    public SellerFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static SellerFragment newInstance() {
        return new SellerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null)
            getActivity().setTitle("Seller Fragment");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        mNavigation = view.findViewById(R.id.activity_seller_navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fragmentManager = getChildFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.fragment_seller_container, PostedFoodPagerFragment.newInstance())
                .commit();

        return view;
    }

    @Override
    public void onDetach() {
        mNavigation.setOnNavigationItemSelectedListener(null);

        super.onDetach();

    }
}
