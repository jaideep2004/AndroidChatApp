

package com.example.mychatapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends AppCompatActivity {

    private DatabaseReference contactRef;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    ImageView cameraImg;
    ImageView plusIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        cameraImg = findViewById(R.id.camImg);
        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        plusIcon = findViewById(R.id.imgPlus);
        plusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, ContactAdd.class);
                startActivity(intent);
            }
        });
        // Three dots menu
        ImageView img2 = findViewById(R.id.threeDots);
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase Database reference
        contactRef = FirebaseDatabase.getInstance().getReference().child("User");

        // Initialize adapter with an empty list (data will be fetched later)
        adapter = new ContactAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Fetch contacts from Firebase Database
        fetchContacts();
    }

    private void fetchContacts() {
        contactRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Contact> contacts = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Contact contact = dataSnapshot.getValue(Contact.class);
                    contacts.add(contact);
                }
                // Update adapter with the fetched contacts
                adapter.setContacts(contacts);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error if data fetching fails
            }
        });
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.popup);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_add_contact) {
                    showToast("Add Contact clicked");
                    startActivity(new Intent(HomeScreen.this, ContactAdd.class));
                    return true;
                } else if (item.getItemId() == R.id.action_settings) {
                    showToast("Settings clicked");
                    startActivity(new Intent(HomeScreen.this, ContactList.class));
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }

    private void showToast(String message) {
        Toast.makeText(HomeScreen.this, message, Toast.LENGTH_SHORT).show();
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            cameraImg
                    .setImageBitmap(imageBitmap);
        }
    }


}
