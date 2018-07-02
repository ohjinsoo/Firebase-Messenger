package edu.illinois.finalproject.Messages;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.List;

import edu.illinois.finalproject.R;

/**
 * Created by ohjin on 12/5/2017.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private List<Message> messageList;
    private String userEmail;

    public MessagesAdapter(List<Message> messageList) {
        this.messageList = messageList;
        this.userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    /**
     * Creates and returns a ViewHolder with the ChatList as its parameter.
     */
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View messageItem = LayoutInflater.from(parent.getContext()).
                inflate(viewType, parent, false);
        return new ViewHolder(messageItem);
    }

    @Override
    public void onBindViewHolder(MessagesAdapter.ViewHolder holder, int position) {
        final Message message = messageList.get(position);
        String messageSender = message.getName();
        //Sets the Chat textviews to the appropriate strings.
        if (!messageSender.equals(userEmail)) {
            holder.usernameTextView.setText(messageSender);
        }
        holder.bubbleTextView.setText(message.getText());
        holder.timestampMessageTextView.setText(new SimpleDateFormat("h:mm a EEE, MMM d").format(message.getTimestamp()));
    }

    /**
     * Returns the list layout it wants to use.
     * It automatically sends back restaurant_lists as that is the only list available.
     */
    @Override
    public int getItemViewType(int position) {
        String messageSender = messageList.get(position).getName();
        return messageSender.equals(userEmail) ? R.layout.message_user : R.layout.message_friend;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    /**
     * ViewHolder class that sets all the TextViews and ImageView into a variable.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView;
        public TextView bubbleTextView;
        public TextView timestampMessageTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.usernameTextView = (TextView) itemView.findViewById(R.id.usernameTextView);
            this.bubbleTextView = (TextView) itemView.findViewById(R.id.bubbleTextView);
            this.timestampMessageTextView = (TextView) itemView.findViewById(R.id.timestampMessageTextView);
        }
    }
}
