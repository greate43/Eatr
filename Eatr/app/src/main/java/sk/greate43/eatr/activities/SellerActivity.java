package sk.greate43.eatr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Profile;
import sk.greate43.eatr.interfaces.UpdateData;
import sk.greate43.eatr.utils.Constants;
import sk.greate43.eatr.utils.DrawerUtil;

public class SellerActivity extends AppCompatActivity {
    private static final String TAG = "SellerActivity";
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase database;
    FirebaseStorage mStorage;
    StorageReference storageRef;
    UpdateData updateData;
//
//    TextView tvFullName;
//    TextView tvUserType;
//    ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);

        Toolbar toolbar = findViewById(R.id.activity_seller_toolbar);
        setSupportActionBar(toolbar);


        updateData = DrawerUtil.getInstance().getCallback();

        DrawerUtil.getInstance().getDrawer(this, toolbar);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = mStorage.getReference();


        mDatabaseReference.child(Constants.PROFILE).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: " + dataSnapshot);
                showData(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


    }


    public boolean onCreateOptionsMenu(Menu menu) {
        if (user != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);

            return true;
        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_sign_out:
                if (user != null) {
                    mAuth.signOut();
                    finish();
                    Intent intent = new Intent(SellerActivity.this, MainActivity.class);

                    startActivity(intent);
                }


                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem search = menu.findItem(R.id.menu_item_search);
        search.setVisible(false);

        super.onPrepareOptionsMenu(menu);

        return true;
    }

    private void showData(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            return;
        }

        collectProfile((Map<String, Object>) dataSnapshot.getValue());


    }

    private void collectProfile(Map<String, Object> value) {
        Profile profile = new Profile();
        profile.setUserId(String.valueOf(value.get(Constants.USER_ID)));
        profile.setFirstName(String.valueOf(value.get(Constants.FIRST_NAME)));
        profile.setLastName(String.valueOf(value.get(Constants.LAST_NAME)));
        profile.setProfilePhotoUri(String.valueOf(value.get(Constants.PROFILE_PHOTO_URI)));
        if (String.valueOf(value.get(Constants.EMAIL)) != null) {
            profile.setUserId(String.valueOf(value.get(Constants.EMAIL)));
        }
        if (updateData != null ) {
            updateData.onNavDrawerDataUpdated(profile);
        }


    }


}
