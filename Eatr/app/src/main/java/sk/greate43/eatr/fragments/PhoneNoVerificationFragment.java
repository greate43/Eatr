package sk.greate43.eatr.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.SellerActivity;
import sk.greate43.eatr.interfaces.ReplaceFragment;


public class PhoneNoVerificationFragment extends Fragment {
    private static final String ARG_PHONE_NO = "ARG_PHONE_NO";
    private static final String TAG = "PhoneNoVerificationFrag";
    CountDownTimer remainingTimeCounter;
    private ReplaceFragment mListener;
    private String mPhoneNo;
    private String mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog mProgressDialog;
    private TextView tvTimer;
    private TextInputEditText etVerification;

    public PhoneNoVerificationFragment() {
        // Required empty public constructor
    }

    public static PhoneNoVerificationFragment newInstance(String phoneNo) {
        PhoneNoVerificationFragment fragment = new PhoneNoVerificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PHONE_NO, phoneNo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        if (getArguments() != null) {
            mPhoneNo = getArguments().getString(ARG_PHONE_NO);
            Log.d(TAG, "onCreate: " + mPhoneNo);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_phone_no_verification, container, false);
        tvTimer = view.findViewById(R.id.fragment_phone_no_verification_text_view_time_left_to_resend);
        etVerification = view.findViewById(R.id.fragment_phone_no_verfification_et_verification_code);
        Button btnVerify = view.findViewById(R.id.fragment_phone_no_verfification_button_verify);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etVerification.getText())) {
                    String code= etVerification.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,code);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        initializeCallback();
        verificationCode(mPhoneNo, mCallbacks);
        enableResendVerificationOfCode();
        return view;
    }

    private void verificationCode(String phoneNo, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks) {
        if (phoneNo != null) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNo,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    getActivity(),               // Activity (for callback binding)
                    callbacks);        // OnVerificationStateChangedCallbacks
        }

    }

    private void resendVerificationCode(String phoneNo, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks, PhoneAuthProvider.ForceResendingToken token) {
        if (phoneNo != null) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNo,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    getActivity(),               // Activity (for callback binding)
                    callbacks,
                    token

            );        // OnVerificationStateChangedCallbacks
        }

    }

    private void enableResendVerificationOfCode() {
        remainingTimeCounter = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvTimer.setTextColor(Color.BLACK);
                tvTimer.setEnabled(false);
                tvTimer.setText("Resend in : " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                tvTimer.setTextColor(Color.BLUE);
                tvTimer.setText("Resend Verification Code ");
                tvTimer.setEnabled(true);
                tvTimer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        resendVerificationCode(mPhoneNo, mCallbacks, mResendToken);
                        resetTimer();
                    }
                });
            }

        }.start();


    }

    private void resetTimer() {
        remainingTimeCounter.start();

    }

    private void initializeCallback() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

               signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...

                    Toast.makeText(getActivity().getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toast.makeText(getActivity().getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        showProgressDialog();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            Log.d(TAG, "onComplete: "+user.getUid());
                            Intent intent=new Intent(getActivity(), SellerActivity.class);
                            startActivity(intent);
                            getActivity().finish();

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getActivity().getApplicationContext(), "The verification code entered was invalid", Toast.LENGTH_SHORT).show();

                            }
                        }
                        hideProgressDialog();
                    }
                });

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


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


}
