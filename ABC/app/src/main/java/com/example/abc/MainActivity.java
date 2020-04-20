package com.example.abc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.MaterialEditText;

import com.example.abc.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    Button signin,register;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    ConstraintLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Firebase
        auth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        users=db.getReference("Users");
        signin=findViewById(R.id.signinBtn);
        register=findViewById(R.id.registerBtn);
        rootLayout=findViewById(R.id.rootLayout);

        //To check whether user is already logged in
        if(auth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(),Welcome.class));
            finish();
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });

    }

    //Sign in button action
    void showLoginDialog(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("Please use email to sign in");

        LayoutInflater inflator=LayoutInflater.from(this);
        View login_layout=inflator.inflate(R.layout.layout_login,null);

        final MaterialEditText edtEmail=login_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword=login_layout.findViewById(R.id.edtPassword);


        dialog.setView(login_layout);

        //Set Button
        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {




                if(TextUtils.isEmpty(edtEmail.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter email address",Snackbar.LENGTH_SHORT).show();
                    return;
                }



                if(TextUtils.isEmpty(edtPassword.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter Password",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(edtPassword.getText().toString().length()<8){
                    Snackbar.make(rootLayout,"Password should >=8 characters !",Snackbar.LENGTH_SHORT).show();
                    return;
                }


                final SpotsDialog waitingDialog= new SpotsDialog(MainActivity.this);
                waitingDialog.show();

                //Login
                auth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        waitingDialog.dismiss();
                        startActivity(new Intent(MainActivity.this,Welcome.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDialog.dismiss();
                        Snackbar.make(rootLayout,"Failed"+e.getMessage(),Snackbar.LENGTH_SHORT).show();


                    }
                });//login


            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });


        dialog.show();

    }



    //Register Button action

     void showRegisterDialog(){
         AlertDialog.Builder dialog=new AlertDialog.Builder(this);
         dialog.setTitle("REGISTER");
         dialog.setMessage("Please use email to register");

         LayoutInflater inflator=LayoutInflater.from(this);
         View register_layout=inflator.inflate(R.layout.layout_register,null);

         final MaterialEditText edtEmail=register_layout.findViewById(R.id.edtEmail);
         final MaterialEditText edtPassword=register_layout.findViewById(R.id.edtPassword);
         final MaterialEditText edtName=register_layout.findViewById(R.id.edtName);
         final MaterialEditText edtPhone=register_layout.findViewById(R.id.edtPhone);


         dialog.setView(register_layout);

         //Set Button
         dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {

                if(TextUtils.isEmpty(edtEmail.getText().toString())){
                    Snackbar.make(rootLayout,"Please enter email address",Snackbar.LENGTH_SHORT).show();
                return;
                }

                 if(TextUtils.isEmpty(edtPhone.getText().toString())){
                     Snackbar.make(rootLayout,"Please enter phone number",Snackbar.LENGTH_SHORT).show();
                     return;
                 }

                 if(TextUtils.isEmpty(edtPassword.getText().toString())){
                     Snackbar.make(rootLayout,"Please enter Password",Snackbar.LENGTH_SHORT).show();
                     return;
                 }

                 if(TextUtils.isEmpty(edtName.getText().toString())){
                     Snackbar.make(rootLayout,"Please enter your Name",Snackbar.LENGTH_SHORT).show();
                     return;
                 }

                 if(edtPassword.getText().toString().length()<8){
                     Snackbar.make(rootLayout,"Password should >=8 characters !",Snackbar.LENGTH_SHORT).show();
                     return;
                 }


                 //Register new user
                 auth.createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                     @Override
                     public void onSuccess(AuthResult authResult) {
                         //Save user to db
                         User user=new User();
                         user.setEmail(edtEmail.getText().toString());
                         user.setPassword(edtPassword.getText().toString());
                         user.setName(edtName.getText().toString());
                         user.setPhone(edtPhone.getText().toString());

                         //Use email to key
                         users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void aVoid) {
                                 Snackbar.make(rootLayout,"Register Successfully",Snackbar.LENGTH_SHORT).show();
                             }
                         })
                         .addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 Snackbar.make(rootLayout,"Failed !"+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                             }
                         });
                     }
                 })
                 .addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Snackbar.make(rootLayout,"Register Successfully",Snackbar.LENGTH_SHORT).show();
                     }
                 });


             }
         });


         dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
             }
         });

        dialog.show();

     }


}
