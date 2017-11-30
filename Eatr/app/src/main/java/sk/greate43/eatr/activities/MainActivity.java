package sk.greate43.eatr.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import sk.greate43.eatr.R;
import sk.greate43.eatr.fragments.FragmentMainPage;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {

    private EditText phoneNumber;
    private Button btnSend;

    private SignInButton googleSignIn;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth firebaseAuth;
    public static final int RC_SIGN_IN=9001;
    private static final String TAG = "MainActivity";


    //////phone////////
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneVerificationCallBack;
    private PhoneAuthProvider.ForceResendingToken resendToken;
   // private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_sign_in);
        googleSignIn=findViewById(R.id.signInButton);
        phoneNumber=findViewById(R.id.mobileNo);
        btnSend=findViewById(R.id.btnSend);
        ////////////////////
        firebaseAuth=FirebaseAuth.getInstance();
        //////////////////////////////Google authentication////
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
       // mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
     googleSignIn.setOnClickListener(this);
     //Google authentication

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signInButton:
                signIn();
                break;
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"Connection is failed"+connectionResult,Toast.LENGTH_LONG);
    }
    public void signIn(){
        Intent signInIntent=Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RC_SIGN_IN==requestCode) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    public void handleSignInResult(GoogleSignInResult result){
        if (result.isSuccess()){}
        GoogleSignInAccount account=result.getSignInAccount();
        Toast.makeText(this, "Name is ", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(this,FoodItemContainerActivity.class);
        startActivity(intent);
      //  Log.d(TAG, "handleSignInResult: "+account.getDisplayName());
    }

    public void sendCode(View v){
        String phone=phoneNumber.getText().toString();
        setUpVerificationCallBack();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone,120, TimeUnit.SECONDS,this,phoneVerificationCallBack);
    }
    public void setUpVerificationCallBack(){
        phoneVerificationCallBack=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
              signedInWithPhoneAuthCrediential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }
        };
    }
    public void signedInWithPhoneAuthCrediential(PhoneAuthCredential credential){
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                        }
                        else{
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(MainActivity.this, "Invalid Phone number", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}