package sk.greate43.eatr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sk.greate43.eatr.R;

/**
 * Created by topway on 11/4/2017.
 */

public class SignUpActivity extends AppCompatActivity {
    public static final String TAG="";
    private EditText name;
    private EditText email;
    private EditText paswword;
    private EditText confirmPasword;
    private Button btnRegister;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_sign_up);
        name=findViewById(R.id.editTextName);
        email=findViewById(R.id.editTextEmailAdress);
        paswword=findViewById(R.id.editTextPaswd);
        confirmPasword=findViewById(R.id.editTextConfirmPaswd);
        btnRegister=findViewById(R.id.btnRegister);
        progressBar=new ProgressBar(this);

        /////Firebase Authientication
        firebaseAuth=FirebaseAuth.getInstance();
    }

    public void RegisterUser(View view){

        if (!isEmpty(name.getText().toString())&&!isEmpty(email.getText().toString())&&!isEmpty(paswword.getText().toString())
                &&!isEmpty(confirmPasword.getText().toString())){


            ////paswword auth starts
            if (paswordAuthentication(paswword.getText().toString(),confirmPasword.getText().toString())){

                if (emailAuth(email.getText().toString())) {
                    SignUp(email.getText().toString(), paswword.getText().toString());
                }
                else {
                    Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Password Must Match", Toast.LENGTH_SHORT).show();
            }
            //password auth ends
        }
       else{
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendVerificationMail(){
        FirebaseUser user=firebaseAuth.getCurrentUser();

        if (user!=null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "Verification mail is sucessfully sent check inbox", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(SignUpActivity.this, "Check your Email and other credientials", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    public void SignUp(String email,String password){
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this,"Registration is sucessfull",Toast.LENGTH_LONG).show();
                            sendVerificationMail();
                        //    Toast.makeText(SignUpActivity.this, "Verification mail is sent", Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                            returnToLoginScreen();
                        }
                        else {
                            Toast.makeText(SignUpActivity.this, "Registration is failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void returnToLoginScreen(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    public boolean isEmpty(String string){
        return string.equals("");
    }
    public boolean paswordAuthentication(String s1,String s2){
        return s1.equals(s2);
    }
    public boolean emailAuth(String email){
        return (email.contains("@")&&email.contains(".com"))&&(email.contains("@hotmail")||email.contains("@gmail")
                ||email.contains("@yahoo"));
    }

}
