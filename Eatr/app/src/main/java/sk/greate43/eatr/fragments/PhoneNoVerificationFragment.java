package sk.greate43.eatr.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.google.firebase.auth.PhoneAuthProvider;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.MainActivity;
import sk.greate43.eatr.interfaces.ReplaceFragment;
import sk.greate43.eatr.utils.AuthenticateUsers;


public class PhoneNoVerificationFragment extends Fragment implements AuthenticateUsers.ShowProgressBar {
    private static final String ARG_PHONE_NO = "ARG_PHONE_NO";
    private static final String TAG = "PhoneNoVerificationFrag";
    CountDownTimer remainingTimeCounter;
    private ReplaceFragment mListener;
    private String mPhoneNo;
    private AuthenticateUsers authenticate;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private TextView tvTimer;
    private TextInputEditText etVerification;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog mProgressDialog;

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

        authenticate = AuthenticateUsers.getInstance((MainActivity) getActivity(),this);
        mCallbacks = authenticate.initializeCallback();
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
                    String code = etVerification.getText().toString();
                    authenticate.verifyCodeManually(code);
                }
            }
        });

        authenticate.verificationCode(mPhoneNo, mCallbacks);
        mResendToken = authenticate.getToken();

        enableResendVerificationOfCode();
        return view;
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
                tvTimer.setText("Resend in : 0");

                tvTimer.setTextColor(Color.BLUE);
                tvTimer.setText("Resend Verification Code ");
                tvTimer.setEnabled(true);
                tvTimer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        authenticate.resendVerificationCode(mPhoneNo, mCallbacks, mResendToken);
                        resetTimer();
                    }
                });
            }

        }.start();


    }

    private void resetTimer() {
        remainingTimeCounter.start();

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
    public void progressBarShowing(Boolean isShowing) {
        if (isShowing) {
            showProgressDialog();
        } else {
            hideProgressDialog();
        }
    }
}
