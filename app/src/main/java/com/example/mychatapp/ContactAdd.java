


package com.example.mychatapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ContactAdd extends AppCompatActivity {

    private EditText nameEdit, phoneEdit;

    private Button saveBtn;
    private DatabaseReference contactRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            nameEdit = findViewById(R.id.nameText);
            phoneEdit = findViewById(R.id.phoneText);
            saveBtn = findViewById(R.id.conbtn);

            contactRef = FirebaseDatabase.getInstance().getReference().child("User");

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ninput = nameEdit.getText().toString();
                    String pinput = phoneEdit.getText().toString();
                    String key = contactRef.push().getKey(); // Generate unique key
                    Contact contact = new Contact(ninput, pinput);
                    contactRef.child(key).setValue(contact).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ContactAdd.this, "Contact added", Toast.LENGTH_SHORT).show();
                            // Clear input fields after successful addition
                            nameEdit.getText().clear();
                            phoneEdit.getText().clear();

                            Intent intent = new Intent(ContactAdd.this, ContactList.class);
                            startActivity(intent);
                        }
                    });
                }
            });

            return insets;
        });
    }
}
