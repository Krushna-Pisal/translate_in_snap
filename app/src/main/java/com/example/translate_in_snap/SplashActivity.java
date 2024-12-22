package com.example.translate_in_snap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends AppCompatActivity {

    private LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize Lottie Animation
        lottieAnimationView = findViewById(R.id.lottieAnimationView);

        // Show Lottie animation for 5 seconds
        lottieAnimationView.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            // After 5 seconds it will go to the MainActivity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Finish the SplashActivity so it's removed from the back stack
        }, 5000); // 5000 milliseconds = 5 seconds
    }
}
