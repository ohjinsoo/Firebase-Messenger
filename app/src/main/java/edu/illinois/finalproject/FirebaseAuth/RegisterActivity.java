package edu.illinois.finalproject.FirebaseAuth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.illinois.finalproject.Chatrooms.MainMenuActivity;
import edu.illinois.finalproject.R;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Adds a Toolbar to show the app name.
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        //Initialize EditTexts
        final EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        final EditText passwordCheckEditText = (EditText) findViewById(R.id.passwordCheckEditText);

        // Checks if user is authenticated or not.
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Context context = getApplicationContext();
                    Intent intent = new Intent(context, MainMenuActivity.class);
                    context.startActivity(intent);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        // When registerButton is clicked, sends email and password data into
        // createAccount function that creates an account.
        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p1 = passwordEditText.getText().toString();
                String p2 = passwordCheckEditText.getText().toString();
                if (p1.equals(p2)) {
                    createAccount(emailEditText.getText().toString(), p1, v.getContext());
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Your passwords do not match.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // When signinButton is clicked, sends user to SignInActivity.class to sign in.
        Button signinButton = (Button) findViewById(R.id.signinButton);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent gotoSigninPage = new Intent(context, SignInActivity.class);
                context.startActivity(gotoSigninPage);
            }
        });
    }

    /**
     * Creates an account and puts it into Firebase Auth.
     *  If createAccount is successful, goes to usernameActivity.
     * else, sends toast with authFailed String.
     *
     * @param email    = email address
     * @param password = password linked to email
     * @param context  = context to link the Intent with.
     */
    private void createAccount(String email, String password, final Context context) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            addAccountToFirebase(context);

                            Intent intent = new Intent(context, MainMenuActivity.class);
                            context.startActivity(intent);
                        }
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        else {
                            Toast.makeText(RegisterActivity.this, R.string.authFailed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Adds the newly registered user into the users Database.
     */
    private void addAccountToFirebase(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String email = user.getEmail();
            String userUid = user.getUid();

            DatabaseReference usersRef = database.getReference(context.getString(R.string.USERS_KEY))
                    .child(userUid);
            usersRef.setValue(email);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
