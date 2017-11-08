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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sk.greate43.eatr.R;
import sk.greate43.eatr.fragments.FragmentMainPage;


public class MainActivity extends AppCompatActivity {
 public static String TAG="";

 private TextView textViewRegisterUser;
 private EditText email;
 private EditText password;
 private Button btnSignIn;
 private FirebaseAuth.AuthStateListener fireBaseAuthStateListener;
 private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_sign_in);
        textViewRegisterUser=findViewById(R.id.registerUser);
        email=findViewById(R.id.signInEmail);
        password=findViewById(R.id.signInPassword);
        btnSignIn=findViewById(R.id.btnLogIn);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Verifying user authientication...");

        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentMainPage fragmentMainPage=new FragmentMainPage();

        ////////Firebase Auth State Listener
        userAuthientication();

    }

////Sign In button
    public void SignInUser(View view){

     if (email.getText().toString()!="" || password.getText().toString() !=""){
       FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
               .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {

                   }
               }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Log.d(TAG, "onFailure: user mail is not verified");
               Toast.makeText(MainActivity.this, "Please Enter Correct Email and Password", Toast.LENGTH_SHORT).show();
           }
       });
     }
     else {
         Toast.makeText(this, "Please Fill Both the fields", Toast.LENGTH_SHORT).show();
     }
    }
 ///User Authientication
    public void userAuthientication(){

        Log.d(TAG, "userAuthientication: Im in user Authientication");
        fireBaseAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if (user!=null){
                    if (user.isEmailVerified()){
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Check your inbox..Verify your mail", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }
                else{
                    Log.d(TAG, "onAuthStateChanged: Are you in Else of user");
                    Toast.makeText(MainActivity.this, "Not Authienticated user No get Uid", Toast.LENGTH_SHORT).show();
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
            FirebaseAuth.getInstance().addAuthStateListener(fireBaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (fireBaseAuthStateListener!=null){
            FirebaseAuth.getInstance().removeAuthStateListener(fireBaseAuthStateListener);
        }
    }

    //////Another Activity
    public void RegisterUser(View view){
        Intent intent=new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }

}
