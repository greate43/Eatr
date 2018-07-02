package sk.greate43.eatr.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hbb20.CountryCodePicker;

import sk.greate43.eatr.R;
import sk.greate43.eatr.interfaces.ReplaceFragment;


public class PhoneNoValidationFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "PhoneNoAuthenticationFr";
    private ReplaceFragment mListener;
    private TextInputEditText etPhoneNo;
    private CountryCodePicker ccp;
    private Button btnNext;


    public PhoneNoValidationFragment() {
        // Required empty public constructor
    }


    public static PhoneNoValidationFragment newInstance() {
        PhoneNoValidationFragment fragment = new PhoneNoValidationFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getActivity() != null)
            getActivity().setTitle("Mobile No. Verification");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_no_authentication, container, false);
        etPhoneNo = view.findViewById(R.id.fragment_phone_no_et_phone_no);
        ccp = view.findViewById(R.id.fragment_phone_no_ccp);

        ccp.registerCarrierNumberEditText(etPhoneNo);
        ccp.setNumberAutoFormattingEnabled(true);

        btnNext = view.findViewById(R.id.fragment_phone_no_authentation_button_next);
        btnNext.setOnClickListener(v -> {
            if (ccp.isValidFullNumber()) {
                if (mListener != null) {
                    mListener.onFragmentReplaced(PhoneNoVerificationFragment.newInstance(ccp.getFullNumberWithPlus()));
                }
            } else {
                etPhoneNo.setError("Mobile No. is invalid");
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
        btnNext.setOnClickListener(null);
    }


    @Override
    public void onClick(View v) {

    }
}
