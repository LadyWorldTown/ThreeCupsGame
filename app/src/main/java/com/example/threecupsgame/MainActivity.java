package com.example.threecupsgame;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.ObjectAnimator;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import androidx.appcompat.app.AlertDialog;



public class MainActivity extends AppCompatActivity {
    ImageView cup1, cup2, cup3, ball;
    Button startButton, easyButton, mediumButton, hardButton;
    List<ImageView> cups = new ArrayList<>();
    int ballCupIndex = 1; // Start with the ball under the middle cup
    int shuffleSpeed = 500; // Default shuffle speed
    int numberOfShuffles = 5; // Default number of shuffles

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cup1 = findViewById(R.id.cup1);
        cup2 = findViewById(R.id.cup2);
        cup3 = findViewById(R.id.cup3);
        ball = findViewById(R.id.ball);
        startButton = findViewById(R.id.buttonStart);
        easyButton = findViewById(R.id.buttonEasy);
        mediumButton = findViewById(R.id.buttonMedium);
        hardButton = findViewById(R.id.buttonHard);

        cups.add(cup1);
        cups.add(cup2);
        cups.add(cup3);

        easyButton.setOnClickListener(v -> selectDifficulty(1000, 3));
        mediumButton.setOnClickListener(v -> selectDifficulty(500, 5));
        hardButton.setOnClickListener(v -> selectDifficulty(300, 7));

        startButton.setOnClickListener(v -> startGame());

        View.OnClickListener guessListener = view -> {
            ImageView cup = (ImageView) view;
            raiseCupToShowBall(cup);
        };

        cup1.setOnClickListener(guessListener);
        cup2.setOnClickListener(guessListener);
        cup3.setOnClickListener(guessListener);
    }

    private void selectDifficulty(int speed, int count) {
        shuffleSpeed = speed;
        numberOfShuffles = count;
        Toast.makeText(this, "Difficulty set. Press 'Start Game' to begin!", Toast.LENGTH_SHORT).show();
        startButton.setEnabled(true);
    }

    private void startGame() {
        startButton.setVisibility(View.INVISIBLE);
        easyButton.setVisibility(View.INVISIBLE);
        mediumButton.setVisibility(View.INVISIBLE);
        hardButton.setVisibility(View.INVISIBLE);
        positionBallInitially();
        cup1.postDelayed(this::lowerCupsOntoBall, 1000);
    }

    private void positionBallInitially() {
        ball.setX(cup2.getX() + cup2.getWidth() / 2 - ball.getWidth() / 2);
        ball.setY(cup2.getY() + cup2.getHeight() - ball.getHeight() / 2);
        ball.setVisibility(View.VISIBLE);
    }

    private void lowerCupsOntoBall() {
        for (ImageView cup : cups) {
            cup.setElevation(10);
            ObjectAnimator animator = ObjectAnimator.ofFloat(cup, "translationY", -100, 0);
            animator.setDuration(1000);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ball.setVisibility(View.INVISIBLE);
                    cup.setElevation(0);
                    if (cups.indexOf(cup) == 2) {
                        shuffleCups();
                    }
                }
            });
            animator.start();
        }
    }

    private void shuffleCups() {
        Runnable shuffleRunnable = new Runnable() {
            int count = numberOfShuffles;

            @Override
            public void run() {
                if (count-- > 0) {
                    performSingleShuffle();
                    cup1.postDelayed(this, shuffleSpeed); // Continue shuffling at a pace set by difficulty
                } else {
                    separateCups();
                }
            }
        };
        shuffleRunnable.run();
    }

    private void separateCups() {

        float firstX = cups.get(0).getX();
        float middleX = cups.get(1).getX();
        float lastX = cups.get(2).getX();

        // Adjust positions if they are too close or overlapping
        if (Math.abs(firstX - middleX) < cup1.getWidth() || Math.abs(middleX - lastX) < cup1.getWidth()) {
            cups.get(0).setX(firstX - 50);
            cups.get(2).setX(lastX + 50);
        }
    }

    private void performSingleShuffle() {
        List<Float> originalPositions = new ArrayList<>();
        for (ImageView cup : cups) {
            originalPositions.add(cup.getX());
        }
        Collections.shuffle(originalPositions);

        for (int i = 0; i < cups.size(); i++) {
            ImageView cup = cups.get(i);
            float newX = originalPositions.get(i);
            animateCupMovement(cup, newX, i == ballCupIndex);
        }
    }

    private void animateCupMovement(ImageView cup, float newX, boolean hasBall) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(cup, "x", cup.getX(), newX);
        animator.setDuration(shuffleSpeed);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (hasBall) {
                    moveBallWithCup(cup, newX);
                }
            }
        });
        animator.start();
    }

    private void moveBallWithCup(ImageView cup, float newX) {
        ObjectAnimator ballAnimator = ObjectAnimator.ofFloat(ball, "x", ball.getX(), newX + cup.getWidth() / 2 - ball.getWidth() / 2);
        ballAnimator.setDuration(1000);
        ballAnimator.start();
    }

    private void showEndGameOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over");
        builder.setMessage("Do you want to change difficulty or play again?");

        builder.setPositiveButton("Change Difficulty", (dialog, which) -> {
            // Show difficulty buttons and enable them
            easyButton.setVisibility(View.VISIBLE);
            mediumButton.setVisibility(View.VISIBLE);
            hardButton.setVisibility(View.VISIBLE);
            startButton.setEnabled(false); // Disable start until new difficulty is selected
        });

        builder.setNegativeButton("Play Again", (dialog, which) -> {
            // Replay the game at the current difficulty
            startGame();
        });

        builder.setNeutralButton("Cancel", (dialog, which) -> {
            // Do nothing, maybe close the dialog
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void raiseCupToShowBall(ImageView cup) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(cup, "translationY", cup.getTranslationY(), -100);
        animator.setDuration(500);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Check if the selected cup is the one with the ball
                        if (cups.indexOf(cup) == ballCupIndex) {
                            ball.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "You won!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Try again!", Toast.LENGTH_SHORT).show();
                        }
                        startButton.setVisibility(View.VISIBLE); // Show start button for replay
                        showEndGameOptions(); // Ask the user if they want to change difficulty or play again
                    }
                }, 500); // Delay of 500 milliseconds
            }
        });
        animator.start();
    }
}
