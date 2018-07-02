package edu.illinois.finalproject.Chatrooms;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.List;

import edu.illinois.finalproject.Messages.MessagesActivity;
import edu.illinois.finalproject.R;

/**
 * Created by ohjin on 12/5/2017.
 */

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.ViewHolder> {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private List<Chat> chatList;
    private List<String> keyList;

    public MainMenuAdapter(List<Chat> chatList, List<String> keyList) {
        this.chatList = chatList;
        this.keyList = keyList;
    }

    /**
     * Returns the list layout it wants to use.
     * It automatically sends back restaurant_lists as that is the only list available.
     */
    @Override
    public int getItemViewType(int position) {
        return R.layout.mainmenu_chatrooms;
    }

    /**
     * Creates and returns a ViewHolder with the ChatList as its parameter.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View chatItem = LayoutInflater.from(parent.getContext()).
                inflate(viewType, parent, false);
        return new ViewHolder(chatItem);
    }


    /**
     * Goes through each Chat in ChatList and sets the TextViews to the
     * appropriate Strings.
     */
    @Override
    public void onBindViewHolder(MainMenuAdapter.ViewHolder holder, final int position) {
        final Chat chat = chatList.get(position);

        //Sets the Chat textviews to the appropriate strings.
        holder.titleTextView.setText(chat.getTitle());
        holder.lastMessageTextView.setText(chat.getLastMessage());
        holder.timestampTextView.setText(new SimpleDateFormat("h:mm a EEE, MMM d").format(chat.getTimestamp()));

        //When the user taps on the RecyclerView, it will open the MainMenuActivity page
        //with details on the Restaurant that they tapped on.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent detailedIntent = new Intent(context, MessagesActivity.class);
                detailedIntent.putExtra(context.getString(R.string.CHAT_KEY), chat.getChatKey());
                detailedIntent.putExtra(context.getString(R.string.TITLE_KEY), chat.getTitle());
                context.startActivity(detailedIntent);
            }
        });

        //When the user taps the big "x" box in the chat, it will delete it from all users,
        //and all traces in Firebase Database.
        holder.deleteChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                deleteMessages(context, chat.getChatKey());
                deleteMembers(context, chat.getChatKey());
                deleteChats(context, chat.getChatKey());
                chatList.remove(position);
                keyList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    private void deleteMessages(Context context, String chatKey) {
        database.getReference(context.getString(R.string.MESSAGES_KEY)).child(chatKey).removeValue();
    }

    private void deleteMembers(Context context, String chatKey) {
        database.getReference(context.getString(R.string.MEMBERS_KEY)).child(chatKey).removeValue();
    }

    private void deleteChats(Context context, String chatKey) {
        database.getReference(context.getString(R.string.CHATS_KEY)).child(chatKey).removeValue();
    }


    /**
     * returns the size of chatList to notify the app how long the list will be for scrolling.
     */
    @Override
    public int getItemCount() {
        return chatList.size();
    }

    /**
     * ViewHolder class that sets all the TextViews and ImageView into a variable.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public ImageButton deleteChatButton;
        public TextView titleTextView;
        public TextView lastMessageTextView;
        public TextView timestampTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.deleteChatButton = (ImageButton) itemView.findViewById(R.id.deleteChatButton);
            this.titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            this.lastMessageTextView = (TextView) itemView.findViewById(R.id.lastMessageTextView);
            this.timestampTextView = (TextView) itemView.findViewById(R.id.timestampTextView);
        }
    }
}
