

package com.example.mychatapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private EditText messageEditText;
    private Button sendButton;
    private Contact contact;
    private DatabaseReference messagesRef;

    private RecyclerView messageRecyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            contact = getIntent().getParcelableExtra("contact");

            TextView receiverTextView = findViewById(R.id.receiverTextView);
            receiverTextView.setText(contact.getName());

            messageEditText = findViewById(R.id.msgEdit);
            sendButton = findViewById(R.id.sendEdit);
            messagesRef = FirebaseDatabase.getInstance().getReference().child("messages");

            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage();
                }
            });

            // Initialize RecyclerView
            messageRecyclerView = findViewById(R.id.messageRecyclerView);
            messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Initialize adapter with an empty list (data will be fetched later)
            messages = new ArrayList<>();
            messageAdapter = new MessageAdapter(messages);
            messageRecyclerView.setAdapter(messageAdapter);

            fetchMessages(contact); // Fetch messages for the contact

            return insets;
        });
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();

        if (!messageText.isEmpty()) {
            String messageId = messagesRef.push().getKey(); // Generate a unique key for the message

            // Get the username of the current user as the sender
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            assert currentUser != null;
            String sender = currentUser.getDisplayName();

            String receiver = contact.getName(); // Get the receiver's name from the Contact object
            String timestamp = String.valueOf(System.currentTimeMillis()); // Get current timestamp

            Message message = new Message(messageId, sender, receiver, messageText, timestamp);

            messagesRef.child(messageId).setValue(message); // Save the message to the database

            // Clear the message EditText after sending the message
            messageEditText.setText("");
        }
    }

    private void fetchMessages(Contact contact) {
        // Clear existing messages
        messages.clear();

        // Query messages for the selected contact
        messagesRef.orderByChild("receiver").equalTo(contact.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if RecyclerView is already at the bottom
                boolean isAtBottom = isRecyclerViewAtBottom();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messages.add(message);
                }

                // Notify the adapter of data changes
                messageAdapter.notifyDataSetChanged();

                // Scroll RecyclerView to the bottom only if it was already at the bottom
                if (isAtBottom) {
                    scrollToBottom();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    // Scroll RecyclerView to bottom
    private void scrollToBottom() {
        messageRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                // Scroll to the bottom of the RecyclerView
                messageRecyclerView.scrollToPosition(messages.size() - 1);
            }
        });
    }

    // Check if RecyclerView is at the bottom
    private boolean isRecyclerViewAtBottom() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) messageRecyclerView.getLayoutManager();
        int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
        int itemCount = messageAdapter.getItemCount();
        return lastVisibleItemPosition == itemCount - 1;
    }
}
