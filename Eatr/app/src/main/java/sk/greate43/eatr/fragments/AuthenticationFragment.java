package sk.greate43.eatr.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.BuyerActivity;
import sk.greate43.eatr.activities.SellerActivity;
import sk.greate43.eatr.entities.Profile;
import sk.greate43.eatr.interfaces.ReplaceFragment;
import sk.greate43.eatr.utils.Constants;


public class AuthenticationFragment extends Fragment {

    private static final String TAG = "AuthenticationFragment";
    FirebaseAuth mAuth;
    FirebaseUser user;
    private ReplaceFragment mListener;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Button btnAuthPhoneNo;
    private ImageView imgAppLogo;
    private ProgressDialog mProgressDialog;

    public AuthenticationFragment() {
        // Required empty public constructor
    }


    public static AuthenticationFragment newInstance() {
        AuthenticationFragment fragment = new AuthenticationFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authentation, container, false);
        btnAuthPhoneNo = view.findViewById(R.id.fragment_authentication_button_open_phone_auth);
        imgAppLogo = view.findViewById(R.id.fragment_authemtation_app_logo);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        showProgressDialog();
        hideAllUiWidgets();

        if (user != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    showData(dataSnapshot, user.getUid());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        } else {
            hideProgressDialog();
            showAllUiWidgets();
        }


        btnAuthPhoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null) {
                    mListener.onFragmentReplaced(PhoneNoValidationFragment.newInstance());
                }

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void showData(DataSnapshot dataSnapshot, String uid) {
        if (dataSnapshot.getValue() == null) {
            if (mListener != null) {
                mListener.onFragmentReplaced(ProfileFragment.newInstance());
            }
            hideProgressDialog();
            Log.d(TAG, "showData: no value at all in database");
            return;
        }
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Profile profile = ds.child(uid).getValue(Profile.class);

            if (profile != null && profile.getUserId() != null) {

                Log.d(TAG, "showData: 2 " + profile.getUserType());


                String userType = String.valueOf(profile.getUserType());
                if (userType != null) {

                    if (userType.equalsIgnoreCase(Constants.TYPE_SELLER)) {
                        Intent intent = new Intent(getActivity(), SellerActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();

                    } else if (userType.equalsIgnoreCase(Constants.TYPE_BUYER)) {
                        Intent intent = new Intent(getActivity(), BuyerActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    }
                    database.goOffline();

                }
            } else {
                if (mListener != null) {
                    mListener.onFragmentReplaced(ProfileFragment.newInstance());
                }

            }
        }
        hideProgressDialog();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ReplaceFragment) {
            mListener = (ReplaceFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ReplaceFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void hideAllUiWidgets() {
        imgAppLogo.setVisibility(View.GONE);
        btnAuthPhoneNo.setVisibility(View.GONE);
    }

    private void showAllUiWidgets() {
        imgAppLogo.setVisibility(View.VISIBLE);
        btnAuthPhoneNo.setVisibility(View.VISIBLE);
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


}
