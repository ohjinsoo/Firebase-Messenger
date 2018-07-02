package edu.illinois.finalproject.Chatrooms;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by Jinsoo on 12/5/2017.
 */

public class MainMenuFirebaseInit {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private Context context;
    private RecyclerView layout;
    private ArrayList<Chat> chatList;
    private ArrayList<String> keyList;

    public MainMenuFirebaseInit(Context context, RecyclerView layout) {
        this.context = context;
        this.layout = layout;
    }

    public void execute() {
        keyList = new ArrayList<>();
        chatList = new ArrayList<>();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference memberRef = database.getReference("members");
        memberRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                /*
                * Used Stack Overflow Code: https://stackoverflow.com/questions/37688031/class-java-util-map-has-generic-type-parameters-please-use-generictypeindicator
                */
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                Map<String, Object> map = dataSnapshot.getValue(genericTypeIndicator);
                if (map.get(user.getUid()) != null) {
                    String key = dataSnapshot.getKey();
                    keyList.add(key);
                    updatedKeyList();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //
            }
        });
    }


    /**
     * Go loop through each chat using the keyList of chat keys to find.
     * If first initialization, add chat normally to chatList.
     * If chat changes lastmessage and timestamp, update it from chatList.
     * If chat is completely new, add it into chatList.
     */
    public void updatedKeyList() {
        for (int i = 0; i < keyList.size(); i++) {
            final String chatKey = keyList.get(i);
            final int buffer = i;
            Log.d(TAG, "keylist: " + keyList.get(i));
            DatabaseReference chatRef = database.getReference("chats").child(chatKey);
            chatRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    //If the chat gets deleted, this gets called again. So function must end here.
                    if (chat == null) {
                        return;
                    }
                    /**
                     * If contains same chat key, that means that the chat is already in the list, and
                     * just needs to be updated. Therefore, overwrite old chat with new chat with set.
                     */
                    if (containsChatKey(chatList, chat)) {
                        chatList.set(buffer, chat);
                        setupAdapter();
                    }
                    /**
                     * ELSE, if it does not contain the same chat key, it must be a new chat.
                     * Therefore, just add the new chat like normal.
                     */
                    else {
                        chatList.add(chat);
                        setupAdapter();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read chat value.", databaseError.toException());
                }
            });
        }
    }

    /**
     * If the chatList exists, add it into the mainMenuAdapter.
     */
    public void setupAdapter() {
        if (chatList != null) {
            final MainMenuAdapter mainMenuAdapter = new MainMenuAdapter(chatList, keyList);
            layout.setAdapter(mainMenuAdapter);
            layout.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        }
    }

    /**
     * Helper function to find if chatList contains chat.
     *
     * @param chatList = array of chats
     * @param chat     = chat I want to see if exists in list.
     * @return true or false depending on if it exists or not.
     */
    private boolean containsChatKey(ArrayList<Chat> chatList, Chat chat) {
        for (Chat buffer : chatList) {
            if (buffer.getChatKey().equals(chat.getChatKey())) {
                return true;
            }
        }

        return false;
    }
}
