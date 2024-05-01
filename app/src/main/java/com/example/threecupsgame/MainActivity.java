package com.example.threecupsgame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.ObjectAnimator;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;  // Import AnimatorListenerAdapter
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    ImageView cup1, cup2, cup3, ball;
    Button startButton;
    List<ImageView> cups = new ArrayList<>();
    int ballCupIndex; // To keep track of which cup has the ball


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        cup1 = findViewById(R.id.cup1);
        cup2 = findViewById(R.id.cup2);
        cup3 = findViewById(R.id.cup3);
        ball = findViewById(R.id.ball);
        startButton = findViewById(R.id.buttonStart);


        cups.add(cup1);
        cups.add(cup2);
        cups.add(cup3);
        ballCupIndex = 1; // Start with the ball under the middle cup


        startButton.setOnClickListener(v -> startGame());


        View.OnClickListener guessListener = view -> {
            ImageView cup = (ImageView) view;
            raiseCupToShowBall(cup);
        };


        cup1.setOnClickListener(guessListener);
        cup2.setOnClickListener(guessListener);
        cup3.setOnClickListener(guessListener);
    }


    private void startGame() {
        startButton.setVisibility(View.INVISIBLE);
        // Initially position the ball right under the middle cup
        positionBallInitially();
        // Lower cups to cover the ball after a slight delay to allow visibility
        cup1.postDelayed(this::lowerCupsOntoBall, 1000); // Delay to show the ball initially
    }


    private void positionBallInitially() {
        ball.setX(cup2.getX() + cup2.getWidth() / 2 - ball.getWidth() / 2);
        ball.setY(cup2.getY() + cup2.getHeight() - ball.getHeight() / 2);
        ball.setVisibility(View.VISIBLE);
    }


    private void lowerCupsOntoBall() {
        ball.setX(cup2.getX() + cup2.getWidth() / 2 - ball.getWidth() / 2);
        ball.setY(cup2.getY() + cup2.getHeight() - ball.getHeight() / 2);
        ball.setVisibility(View.VISIBLE);  // Make sure the ball is visible initially

        for (ImageView cup : cups) {
            // Increase elevation of the cups to bring them in front of the ball
            cup.setElevation(10);  // Set a higher elevation than the ball
            ObjectAnimator animator = ObjectAnimator.ofFloat(cup, "translationY", -100, 0); // Lower the cups
            animator.setDuration(1000);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // After the animation, hide the ball and reset elevation
                    ball.setVisibility(View.INVISIBLE);
                    cup.setElevation(0);  // Reset elevation to normal
                    if (cups.indexOf(cup) == ballCupIndex) {
                        shuffleCups(); // Begin shuffling after the cups have lowered
                    }
                }
            });
            animator.start();
        }
    }

    private void shuffleCups() {
        int numberOfShuffles = 5; // Increase the number of shuffle rounds for complexity


        Runnable shuffleRunnable = new Runnable() {
            int count = numberOfShuffles;


            @Override
            public void run() {
                if (count-- > 0) {
                    performSingleShuffle();
                    cup1.postDelayed(this, 500); // Continue shuffling every 500 milliseconds
                }
            }
        };
        shuffleRunnable.run();
    }
    private void performSingleShuffle() {
        // Shuffle the positions as previously outlined
        List<Float> originalPositions = new ArrayList<>();
        for (ImageView cup : cups) {
            originalPositions.add(cup.getX());
        }
        Collections.shuffle(originalPositions); // Shuffle the X positions


        for (int i = 0; i < cups.size(); i++) {
            ImageView cup = cups.get(i);
            float newX = originalPositions.get(i);
            animateCupMovement(cup, newX, i == ballCupIndex);
        }
    }


    private void animateCupMovement(ImageView cup, float newX, boolean hasBall) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(cup, "x", cup.getX(), newX);
        animator.setDuration(500);
        animator.start();


        // If this cup has the ball, move the ball with it
        if (hasBall) {
            moveBallWithCup(cup, newX);
        }
    }
    private void moveBallWithCup(ImageView cup, float newX) {
        ObjectAnimator ballAnimator = ObjectAnimator.ofFloat(ball, "x", ball.getX(), newX + cup.getWidth() / 2 - ball.getWidth() / 2);
        ballAnimator.setDuration(1000);
        ballAnimator.start();
    }


    private void raiseCupToShowBall(ImageView cup) {
        // Increase the translation distance for a higher lift
        float startLiftPosition = cup.getTranslationY();
        float endLiftPosition = startLiftPosition - 100; // Raise the cup higher than before

        ObjectAnimator animator = ObjectAnimator.ofFloat(cup, "translationY", startLiftPosition, endLiftPosition);
        animator.setDuration(500);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (cups.indexOf(cup) == ballCupIndex) {
                    ball.setVisibility(View.VISIBLE);  // Reveal the ball if it's under the lifted cup
                    Toast.makeText(MainActivity.this, "You won!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Try again!", Toast.LENGTH_SHORT).show();
                }
                startButton.setVisibility(View.VISIBLE);  // Make the start button visible again for a new game
            }
        });
        animator.start();
    }
}
