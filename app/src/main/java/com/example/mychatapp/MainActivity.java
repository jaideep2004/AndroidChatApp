//package com.example.mychatapp;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//
//            // Redirect to HomeScreen after 3 seconds
//            Handler h = new Handler();
//            h.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent i = new Intent(MainActivity.this, HomeScreen.class);
//                    startActivity(i);
//                    finish();
//                }
//            }, 2500);
//
//            return insets;
//        });
//    }
//}

package com.example.mychatapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            // Redirect to HomeScreen after 3 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkUserSignUp();
                }
            }, 3000); // Delay set to 3000 milliseconds (3 seconds)

            return insets;
        });
    }

    private void checkUserSignUp() {
        // Check if user is signed up
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is signed in, navigate to HomeScreen
            startActivity(new Intent(MainActivity.this, HomeScreen.class));
        } else {
            // User is not signed up, show the signup screen
            startActivity(new Intent(MainActivity.this, SignUp.class));
        }
        finish(); // Finish the current activity
    }
}
