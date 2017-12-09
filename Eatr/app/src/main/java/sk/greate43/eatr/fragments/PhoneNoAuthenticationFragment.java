package sk.greate43.eatr.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sk.greate43.eatr.R;
import sk.greate43.eatr.interfaces.ReplaceFragment;


public class PhoneNoAuthenticationFragment extends Fragment implements View.OnClickListener {

    private ReplaceFragment mListener;

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
