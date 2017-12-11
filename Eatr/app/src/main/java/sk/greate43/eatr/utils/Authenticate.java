package sk.greate43.eatr.utils;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
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

import org.jetbrains.annotations.Contract;

import java.util.concurrent.TimeUnit;

import sk.greate43.eatr.activities.MainActivity;
import sk.greate43.eatr.activities.SellerActivity;

/**
 * Created by great on 12/11/2017.
 */

public class Authenticate {
    private static final String TAG = "Authenticate";
    private static final Authenticate ourInstance = new Authenticate();
    private String mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private static MainActivity activity;
    private static ShowProgressBar showProgressBar;
    private Authenticate() {
        mAuth = FirebaseAuth.getInstance();
    }



    @Contract(pure = true)
    public static Authenticate getInstance(MainActivity mainActivity,ShowProgressBar showProgress) {
        showProgressBar = showProgress;
        activity = mainActivity;
        return ourInstance;
    }



    public PhoneAuthProvider.OnVerificationStateChangedCallbacks initializeCallback() {
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

                    Toast.makeText(activity.getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toast.makeText(activity.getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

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


        return mCallbacks;



    }
    @Contract(pure = true)
    public PhoneAuthProvider.ForceResendingToken getToken(){
        return mResendToken;
    }


    public void verificationCode(String phoneNo, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks) {
        if (phoneNo != null) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNo,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    activity,               // Activity (for callback binding)
                    callbacks);        // OnVerificationStateChangedCallbacks
        }

    }

    public void resendVerificationCode(String phoneNo, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks, PhoneAuthProvider.ForceResendingToken token) {
        if (phoneNo != null) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNo,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    activity,               // Activity (for callback binding)
                    callbacks,
                    token

            );        // OnVerificationStateChangedCallbacks
        }

    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        if (showProgressBar != null){
            showProgressBar.progressBarShowing(true);
        }
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            Log.d(TAG, "onComplete: "+user.getUid());
                            Intent intent=new Intent(activity, SellerActivity.class);
                            activity.startActivity(intent);
                            activity.finish();

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(activity.getApplicationContext(), "The verification code entered was invalid", Toast.LENGTH_SHORT).show();

                            }
                        }
                        if (showProgressBar != null){
                            showProgressBar.progressBarShowing(false);
                        }
                    }
                });

    }
    public void verifyCodeManually(String code ){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }


    public interface ShowProgressBar{
        void progressBarShowing(Boolean isShowing);
    }

}
