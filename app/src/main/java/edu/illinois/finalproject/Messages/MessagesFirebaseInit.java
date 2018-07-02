package edu.illinois.finalproject.Messages;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by ohjin on 12/7/2017.
 */

public class MessagesFirebaseInit {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private Context context;
    private RecyclerView layout;
    private ArrayList<Message> messageList = new ArrayList<>();
    private String chatKey;

    public MessagesFirebaseInit(Context context, RecyclerView layout, String chatKey) {
        this.context = context;
        this.layout = layout;
        this.chatKey = chatKey;
    }

    /**
     *  Used to see where new chat messages should go.
     *  If no messages exist, list == null so return 0;
     */
    public int getListSize() {
        if (messageList == null) {
            return 0;
        }
        return messageList.size();
    }

    /**
     * Creates a messageList, list of Messages, that is taken from Firebase Database using chatKey.
     */
    public void execute() {
        final DatabaseReference messagesRef = database.getReference("messages").child(chatKey);
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Message>> genericTypeIndicator = new GenericTypeIndicator<ArrayList<Message>>() {};
                messageList = dataSnapshot.getValue(genericTypeIndicator);
                setupAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read message value.", databaseError.toException());
            }
        });
    }

    /**
     * If messageList exists, set up adapter using the list.
     * Makes sure that the chat will start from bottom.
     */
    private void setupAdapter() {
        if (messageList != null) {
            final MessagesAdapter messagesAdapter = new MessagesAdapter(messageList);
            layout.setAdapter(messagesAdapter);
            layout.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            layout.scrollToPosition(messageList.size() - 1);
        }
    }
}
