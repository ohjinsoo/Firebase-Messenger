package edu.illinois.finalproject.Messages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.illinois.finalproject.Chatrooms.Chat;
import edu.illinois.finalproject.R;

/**
 * Created by ohjin on 12/2/2017.
 */

public class MessagesActivity extends AppCompatActivity {
    private static final int MAX_LASTMESSAGE_LENGTH = 36;
    private static final String LONG_LASTMESSAGE_ENDING = "...";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        final Context context = getApplicationContext();

        //Gets Intent from MainMenuAdapter.class to retrieve the chatKey and title.
        Intent intent = getIntent();
        final String chatKey = intent.getStringExtra(context.getString(R.string.CHAT_KEY));
        final String title = intent.getStringExtra(context.getString(R.string.TITLE_KEY));

        //Adds a toolbar for create chat and sign out functions.
        Toolbar toolbar = (Toolbar) findViewById(R.id.messagesToolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        //Creates all ArrayList of messages to send to adapter.
        final RecyclerView messagesLayout = (RecyclerView) findViewById(R.id.message_layout);
        final MessagesFirebaseInit messagesFirebaseInit = new MessagesFirebaseInit(context, messagesLayout, chatKey);
        messagesFirebaseInit.execute();

        //Pre- "SEND" button click:
        final EditText messagesEditText = (EditText) findViewById(R.id.messagesEditText);

//        Used Stackover Flow on sending the message by return key
//        https://stackoverflow.com/questions/4451374/use-enter-key-on-softkeyboard-instead-of-clicking-button

        messagesEditText.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            int listSize = messagesFirebaseInit.getListSize();
                            sendMessage(messagesEditText, listSize, title, chatKey);
                            return true;
                    }
                }
                return false;
            }
        });


        final Button sendMessageButton = (Button) findViewById(R.id.sendMessageButton);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int listSize = messagesFirebaseInit.getListSize();
                sendMessage(messagesEditText, listSize, title, chatKey);
            }
        });
    }

    private void sendMessage(EditText messagesEditText, int listSize, String title, String chatKey) {
        /** ON SEND BUTTON CLICK:
         *      Get the list size of messages, text, current time, and user's email.
         *      and update both Chats and Messages of Firebase Database.
         */
        Context context = getApplicationContext();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String text = messagesEditText.getText().toString();
        String email = user.getEmail();
        long currentTime = System.currentTimeMillis();

        updateChats(context, title, text, email, currentTime, chatKey);
        updateMessages(context, listSize, text, email, currentTime, chatKey);

        messagesEditText.setText("");
    }

    /**
     * Updates the chats/chatKey section of data schema
     */
    private void updateChats(Context context, String title, String text, String email, long currentTime, String chatKey) {
        String lastMessage = email + ": " + text;

        if (lastMessage.length() > MAX_LASTMESSAGE_LENGTH) {
            lastMessage = lastMessage.substring(0, MAX_LASTMESSAGE_LENGTH - 3) + LONG_LASTMESSAGE_ENDING;
        }

        DatabaseReference chatsRef = database.getReference(context.getString(R.string.CHATS_KEY)).child(chatKey);
        Chat chat = new Chat(chatKey, title, lastMessage, currentTime);
        chat.setLastMessage(lastMessage);
        chat.setTimestamp(currentTime);
        chatsRef.setValue(chat);
    }


    /**
     * Updates the messages/chatKey section of data schema
     */
    private void updateMessages(Context context, int listSize, String text, String email, long currentTime, String chatKey) {
        DatabaseReference messagesRef = database.getReference(context.getString(R.string.MESSAGES_KEY)).child(chatKey);
        Message message = new Message(text, email, currentTime);
        messagesRef.child(Integer.toString(listSize)).setValue(message);
    }

}
