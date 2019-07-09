package sk.greate43.eatr.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import sk.greate43.eatr.R;
import sk.greate43.eatr.utils.Constants;

public class PostedFoodPagerFragment extends Fragment {


    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @NonNull
    public static PostedFoodPagerFragment newInstance() {
        return new PostedFoodPagerFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frgment_posted_food_pager, container, false);


        // Set up the ViewPager with the sections adapter.
        mViewPager = view.findViewById(R.id.view_pager);

        setupViewPager(mViewPager);


        // Set Tabs inside Toolbar
        TabLayout tabs = view.findViewById(R.id.fixture_tabs);
        tabs.setupWithViewPager(mViewPager);

        return view;
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {


        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getChildFragmentManager());
        adapter.addFragment(PostedFoodFragment.newInstance(Constants.ORDER_ACTIVE), "Active");
        adapter.addFragment(PostedFoodFragment.newInstance(Constants.ORDERED_BOOKED), "Reserved");
        adapter.addFragment(PostedFoodFragment.newInstance(Constants.ORDERED_IN_PROGRESS), "In Progress");
        adapter.addFragment(PostedFoodFragment.newInstance(Constants.ORDERED_EXPIRED), "Expired");
        adapter.addFragment(PostedFoodFragment.newInstance(Constants.ORDER_PURCHASED), "Sold");
        adapter.addFragment(PostedFoodFragment.newInstance(Constants.ORDER_DRAFT), "Draft");
        adapter.addFragment(PostedFoodFragment.newInstance(Constants.ALL_ORDERS), "All");

        viewPager.setAdapter(adapter);


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
