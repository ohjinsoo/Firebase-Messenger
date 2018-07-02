package edu.illinois.finalproject.Chatrooms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.illinois.finalproject.R;

import static android.content.ContentValues.TAG;

/**
 * Created by ohjin on 12/2/2017.
 */

public class CreateChatActivity extends AppCompatActivity {
    private static final int MAX_TITLE_LENGTH = 18;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createchat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.createChatToolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        //Initialize EditTexts and Buttons
        final EditText OtherEmailEditText = (EditText) findViewById(R.id.OtherEmailEditText);
        final EditText titleEditText = (EditText) findViewById(R.id.titleEditText);

        Button createNewChatButton = (Button) findViewById(R.id.createNewChatButton);
        createNewChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                String otherEmail = OtherEmailEditText.getText().toString();
                String title = titleEditText.getText().toString();
                //If title length is not too big, start creating the chat and all its pieces.
                //Or else, pop up a Toast.
                if (title.length() <= MAX_TITLE_LENGTH) {
                    startCreatingChat(context, otherEmail, title);
                }
                else {
                    Toast.makeText(CreateChatActivity.this, R.string.createChatFailed,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Check first if the Email address exists in Database.
     * If it doesn't, pop up a toast.
     * If it does, get their UID and create Chats, Members, and send Intent to Main Menu.
     */
    private void startCreatingChat(final Context context, final String otherEmail, final String title) {
        DatabaseReference usersRef = database.getReference(context.getString(R.string.USERS_KEY));
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String otherUID = "";
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //If the email is found, get the key as that is the email's UID.
                    if (otherEmail.equals(postSnapshot.getValue())) {
                        otherUID = postSnapshot.getKey();
                        //Creates the Database and gets Key.
                        DatabaseReference newChatsRef = database.getReference(context.getString(R.string.CHATS_KEY)).push();
                        String newKey = newChatsRef.getKey();

                        //Create a new chats with the title and key
                        createNewChat(context, title, newKey);

                        //Creates a new members with the UID and key.
                        createMembers(context, otherUID, newKey);
                        //Push user back to main menu after creating the chat.
                        Intent goBackToMainMenu = new Intent(context, MainMenuActivity.class);
                        context.startActivity(goBackToMainMenu);
                    }
                }
                if (otherUID.equals("")) {
                    //Gives toast if there is no user with that email in Database
                    //Keeps the user in the same page to let him try again.
                    Toast.makeText(CreateChatActivity.this, R.string.createChatFailed,
                            Toast.LENGTH_LONG).show();
                    Log.w(TAG, "Failed to find member in users.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to create new members.", databaseError.toException());
            }
        });
    }

    //Creates new chats in Database.
    private void createNewChat(Context context, String title, String newKey) {
        DatabaseReference newChatsRef = database.getReference(context.getString(R.string.CHATS_KEY)).child(newKey);
        long currentTime = System.currentTimeMillis();
        Chat chat = new Chat(newKey, title, getString(R.string.no_messages_received), currentTime);
        newChatsRef.setValue(chat);
    }

    //Creates up members in Database.
    private void createMembers(Context context, String otherUID, String newKey) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference newMembersRef = database.getReference(context.getString(R.string.MEMBERS_KEY)).child(newKey);
        newMembersRef.child(user.getUid()).setValue(true);
        newMembersRef.child(otherUID).setValue(true);
    }
}

