package sk.greate43.eatr.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.BuyerActivity;
import sk.greate43.eatr.activities.SellerActivity;
import sk.greate43.eatr.interfaces.ReplaceFragment;
import sk.greate43.eatr.utils.Constants;


public class AuthenticationFragment extends Fragment {

    private static final String TAG = "AuthenticationFragment";
    private static final int RC_SIGN_IN = 1111;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private ReplaceFragment mListener;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Button btnAuthPhoneNo;
    private ImageView imgAppLogo;
    private ProgressDialog mProgressDialog;
    private SignInButton btnAuthGoogle;
    private GoogleSignInClient mGoogleSignInClient;
    private ValueEventListener authenticationValueListener;

    public AuthenticationFragment() {
        // Required empty public constructor
    }


    public static AuthenticationFragment newInstance() {
        AuthenticationFragment fragment = new AuthenticationFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getActivity() != null)
            getActivity().setTitle("Authentication Fragment");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authentation, container, false);

        btnAuthPhoneNo = view.findViewById(R.id.fragment_authentication_button_open_phone_auth);
        btnAuthGoogle = view.findViewById(R.id.fragment_authentation_button_google_auth);
        imgAppLogo = view.findViewById(R.id.fragment_authemtation_app_logo);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        showProgressDialog();
        hideAllUiWidgets();
        if (user != null) {
            authenticationValueListener = databaseReference.child(Constants.PROFILE).child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    showData(dataSnapshot);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    hideProgressDialog();
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        } else {
            hideProgressDialog();
            showAllUiWidgets();
        }


        btnAuthPhoneNo.setOnClickListener(v -> {

            if (mListener != null) {
                mListener.onFragmentReplaced(PhoneNoValidationFragment.newInstance());
            }

        });

        btnAuthGoogle.setOnClickListener(v -> signIn());

        return view;
    }

    private void signIn() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))

                .requestEmail()
                .build();


        if (getActivity() != null) {

            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle: " + account.getId());
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        if (getActivity() != null)
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                final FirebaseUser user = task.getResult().getUser();

                                databaseReference.child(Constants.PROFILE).child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        Log.d(TAG, "onDataChange: " + dataSnapshot);
                                        showData(dataSnapshot);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        hideProgressDialog();
                                        System.out.println("The read failed: " + databaseError.getCode());
                                    }
                                });

                                // updateUI(user);
                            } else {
                                hideProgressDialog();
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                // updateUI(null);
                            }
                            // ...
                        }
                    });

    }


    private void showData(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {

            if (mListener != null) {
                mListener.onFragmentReplaced(ProfileFragment.newInstance());
            }
            Log.d(TAG, "showData: no value at all in database");

            hideProgressDialog();
            return;
        }

        collectProfile((Map<String, Object>) dataSnapshot.getValue());


    }

    private void collectProfile(Map<String, Object> value) {
        String userType = String.valueOf(value.get(Constants.USER_TYPE));
        if (userType != null) {
            if (userType.equalsIgnoreCase(Constants.TYPE_SELLER)) {

                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), SellerActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                    hideProgressDialog();

                }

            } else if (userType.equalsIgnoreCase(Constants.TYPE_BUYER)) {

                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), BuyerActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                    hideProgressDialog();

                }

            }

        } else {
            if (mListener != null) {
                mListener.onFragmentReplaced(ProfileFragment.newInstance());
                hideProgressDialog();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

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
        mGoogleSignInClient = null;
        if (authenticationValueListener != null) {
            databaseReference.child(Constants.PROFILE).child(user.getUid()).removeEventListener(authenticationValueListener);

        }
        btnAuthPhoneNo.setOnClickListener(null);
        btnAuthGoogle.setOnClickListener(null);
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
