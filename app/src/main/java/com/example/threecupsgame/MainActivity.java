package com.example.threecupsgame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView cup1, cup2, cup3;
    List<ImageView> cups;
    Button startButton;
    int ballPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cup1 = findViewById(R.id.cup1);
        cup2 = findViewById(R.id.cup2);
        cup3 = findViewById(R.id.cup3);
        startButton = findViewById(R.id.startButton);

        cups = Arrays.asList(cup1, cup2, cup3);
        setupListeners();
    }

    private void setupListeners() {
        startButton.setOnClickListener(v -> shuffleCups());
    }
//testing comment again
    private void shuffleCups() {
        Collections.shuffle(cups);
        for (ImageView cup : cups) {
            cup.setImageResource(R.drawable.cup1);  // Reset the image if needed
            cup.setImageResource(R.drawable.cup2);
            cup.setImageResource(R.drawable.cup3);
        }
        // Optional: Animate the shuffle visually here
        ballPosition = cups.indexOf(cup1);  // Assuming ball starts under cup1
    }
}
