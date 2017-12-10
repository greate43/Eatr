package sk.greate43.eatr.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import sk.greate43.eatr.R;
import sk.greate43.eatr.interfaces.ReplaceFragment;


public class AuthenticationFragment extends Fragment implements View.OnClickListener {


    private ReplaceFragment mListener;

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
        View view= inflater.inflate(R.layout.fragment_authentation, container, false);
        Button btnAuthPhoneNo=view.findViewById(R.id.fragment_authentication_button_open_phone_auth);
        btnAuthPhoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if (mListener != null){
                 mListener.onFragmentReplaced(PhoneNoAuthenticationFragment.newInstance());
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
