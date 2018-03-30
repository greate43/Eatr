package sk.greate43.eatr.utils;

/**
 * Created by great on 3/4/2018.
 */


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.BuyerActivity;
import sk.greate43.eatr.activities.MainActivity;
import sk.greate43.eatr.activities.SellerActivity;
import sk.greate43.eatr.entities.Profile;
import sk.greate43.eatr.fragments.BuyerFragment;
import sk.greate43.eatr.fragments.NotificationFragment;
import sk.greate43.eatr.fragments.ProfileFragment;
import sk.greate43.eatr.fragments.SellerFragment;
import sk.greate43.eatr.fragments.SettingFragment;
import sk.greate43.eatr.fragments.UserTrackerFragment;
import sk.greate43.eatr.interfaces.UpdateData;


public class DrawerUtil implements UpdateData {

    private static final DrawerUtil drawer = new DrawerUtil();
    private static final String TAG = "DrawerUtil";
    private AccountHeader headerResult;
    private Drawer result;
    private UpdateData updateData = this;


    private DrawerUtil() {

    }

    public static DrawerUtil getInstance() {
        return drawer;
    }

    public void getDrawer(final AppCompatActivity activity, Toolbar toolbar) {
        //if you want to update the items at a later time it is recommended to keep it in a variable


        PrimaryDrawerItem drawerItemHome = new PrimaryDrawerItem().withIdentifier(1)
                .withName("Home").withIcon(R.drawable.ic_home_black_24dp);


        SecondaryDrawerItem drawerItemSettings = new SecondaryDrawerItem().withIdentifier(2)
                .withName("Settings").withIcon(R.drawable.ic_settings_black_24dp);
        SecondaryDrawerItem drawerItemLogout = new SecondaryDrawerItem().withIdentifier(3)
                .withName("Logout").withIcon(R.drawable.logout);
       // SecondaryDrawerItem drawerItemMap = new SecondaryDrawerItem().withIdentifier(5)
         //       .withName("Map").withIcon(R.drawable.logout);
        SecondaryDrawerItem drawerProfile = new SecondaryDrawerItem().withIdentifier(4)
                .withName("Profile").withIcon(R.drawable.ic_account_box_black_24dp);
        SecondaryDrawerItem drawerNotification = new SecondaryDrawerItem().withIdentifier(6)
                .withName("Notification").withIcon(R.drawable.ic_notifications_black_24dp);

        SecondaryDrawerItem drawerPickUpLocation = new SecondaryDrawerItem().withIdentifier(7);
        if (activity instanceof BuyerActivity){
            drawerPickUpLocation.withName("Pick Location").withIcon(R.drawable.ic_explore_black_24dp);
        } else if (activity instanceof SellerActivity){
            drawerPickUpLocation.withName("Track Buyer").withIcon(R.drawable.ic_track_changes_black_24dp);
        }



        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).fit().centerCrop().into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }

    /*
    @Override
    public Drawable placeholder(Context ctx) {
        return super.placeholder(ctx);
    }

    @Override
    public Drawable placeholder(Context ctx, String tag) {
        return super.placeholder(ctx, tag);
    }
    */
        });


// Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.side_nav_bar)
                .withSelectionListEnabledForSingleProfile(false)

                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {

                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();


        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        drawerItemHome,
                        drawerPickUpLocation,
                        drawerNotification,
                        drawerProfile,
                        //  new DividerDrawerItem(),
                        drawerItemSettings,
                        drawerItemLogout
                     //   drawerItemMap

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 1 && (activity instanceof SellerActivity)) {
                            // load tournament screen
//                            Intent intent = new Intent(activity, MainActivity.class);
//                            view.getContext().startActivity(intent);

                            FragmentManager fragment = activity.getSupportFragmentManager();
                            fragment.beginTransaction().replace(R.id.content_seller_container, SellerFragment.newInstance()).commit();
                        } else if (drawerItem.getIdentifier() == 1 && (activity instanceof BuyerActivity)) {
                            // load tournament screen
//                            Intent intent = new Intent(activity, MainActivity.class);
//                            view.getContext().startActivity(intent);

                            FragmentManager fragment = activity.getSupportFragmentManager();
                            fragment.beginTransaction().replace(R.id.content_buyer_container, BuyerFragment.newInstance()).commit();
                        } else if (drawerItem.getIdentifier() == 2 && (activity instanceof SellerActivity)) {

                            FragmentManager fragment = activity.getSupportFragmentManager();
                            fragment.beginTransaction().replace(R.id.content_seller_container, SettingFragment.newInstance()).commit();

                        } else if (drawerItem.getIdentifier() == 2 && (activity instanceof BuyerActivity)) {

                            FragmentManager fragment = activity.getSupportFragmentManager();
                            fragment.beginTransaction().replace(R.id.content_buyer_container, SettingFragment.newInstance()).commit();

                        } else if (drawerItem.getIdentifier() == 3) {
                            FirebaseAuth mAuth;
                            FirebaseUser user;
                            mAuth = FirebaseAuth.getInstance();
                            user = mAuth.getCurrentUser();
                            if (user != null) {
                                mAuth.signOut();
                                activity.finish();
                                Intent intent = new Intent(activity, MainActivity.class);

                                activity.startActivity(intent);
                            }

                        } else if (drawerItem.getIdentifier() == 4 && activity instanceof SellerActivity) {
                            FragmentManager fragment = activity.getSupportFragmentManager();
                            fragment.beginTransaction().replace(R.id.content_seller_container, ProfileFragment.newInstance(profile)).commit();
                        } else if (drawerItem.getIdentifier() == 4 && activity instanceof BuyerActivity) {
                            if (profile != null) {
                                FragmentManager fragment = activity.getSupportFragmentManager();
                                fragment.beginTransaction().replace(R.id.content_buyer_container, ProfileFragment.newInstance(profile)).commit();
                            }
                        }

//                        else if (drawerItem.getIdentifier() == 5 && activity instanceof SellerActivity) {
//                            if (profile != null) {
//                                FragmentManager fragment = activity.getSupportFragmentManager();
//                                fragment.beginTransaction().replace(R.id.content_seller_container, MapFragment.newInstance()).commit();
//                            }
//                        }

                        else if (drawerItem.getIdentifier() == 6 && activity instanceof SellerActivity) {
                            FragmentManager fragment = activity.getSupportFragmentManager();
                            fragment.beginTransaction().replace(R.id.content_seller_container, NotificationFragment.newInstance()).commit();

                        } else if (drawerItem.getIdentifier() == 6 && activity instanceof BuyerActivity) {
                            FragmentManager fragment = activity.getSupportFragmentManager();
                            fragment.beginTransaction().replace(R.id.content_buyer_container, NotificationFragment.newInstance()).commit();

                        }
                        else if (drawerItem.getIdentifier() == 7 && activity instanceof SellerActivity) {
                            FragmentManager fragment = activity.getSupportFragmentManager();
                            fragment.beginTransaction().replace(R.id.content_seller_container, UserTrackerFragment.newInstance(Constants.TYPE_SELLER)).commit();

                        } else if (drawerItem.getIdentifier() == 7 && activity instanceof BuyerActivity) {
                            FragmentManager fragment = activity.getSupportFragmentManager();
                            fragment.beginTransaction().replace(R.id.content_buyer_container, UserTrackerFragment.newInstance(Constants.TYPE_BUYER)).commit();

                        }



                        closeDrawer();

                        return true;
                    }
                })
                .build();


        result.setSelection(1, true);



    }

    private void closeDrawer() {
        result.closeDrawer();
    }

    public UpdateData getCallback() {
        return updateData;
    }

    private Profile profile;

    @Override
    public void onNavDrawerDataUpdated(Profile data) {
        profile = data;

        if (data != null && data.getProfilePhotoUri() != null) {
            headerResult.clear();
            headerResult.addProfiles(new ProfileDrawerItem().withName(data.getFullname()).withIcon(Uri.parse(data.getProfilePhotoUri())));
        }

    }
}