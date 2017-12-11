package sk.greate43.eatr.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hbb20.CountryCodePicker;

import sk.greate43.eatr.R;
import sk.greate43.eatr.interfaces.ReplaceFragment;


public class PhoneNoAuthenticationFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "PhoneNoAuthenticationFr";
    private ReplaceFragment mListener;
    private TextInputEditText etPhoneNo;
    private CountryCodePicker ccp;


    public PhoneNoAuthenticationFragment() {
        // Required empty public constructor
    }


    public static PhoneNoAuthenticationFragment newInstance() {
        PhoneNoAuthenticationFragment fragment = new PhoneNoAuthenticationFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_no_authentication, container, false);
        etPhoneNo = view.findViewById(R.id.fragment_phone_no_et_phone_no);
        ccp = view.findViewById(R.id.fragment_phone_no_ccp);

        ccp.registerCarrierNumberEditText(etPhoneNo);
        ccp.setNumberAutoFormattingEnabled(true);

        Button btnNext = view.findViewById(R.id.fragment_phone_no_authentation_button_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ccp.isValidFullNumber()) {
                    if (mListener != null) {
                        mListener.onFragmentReplaced(PhoneNoVerificationFragment.newInstance(ccp.getFullNumberWithPlus()));
                    }
                } else {
                    etPhoneNo.setError("Phone No is Invalid");
                }

            }
        });


        return view;
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


    @Override
    public void onClick(View v) {

    }
}
