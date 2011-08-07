package com.orangetentacle.gorillas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Sample game application inspired by QBasic Gorillas.
 */
public class GorillasActivity extends Activity {

    private WorldView worldView;
    private WorldView.WorldThread worldThread;
    private boolean closed = false;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        worldView = (WorldView) findViewById(R.id.WorldView);
        worldThread = worldView.getThread();

        Bundle extras = getIntent().getExtras();
        int rounds = extras.getInt("com.orangetentacle.gorillas.Rounds");
        worldThread.setRounds(rounds);

        String gorilla1Name = extras.getString("com.orangetentacle.gorillas.Player1Name");
        String gorilla2Name = extras.getString("com.orangetentacle.gorillas.Player2Name");
        worldThread.setNames(gorilla1Name, gorilla2Name);

        Button enterButton = (Button) findViewById(R.id.EnterButton);
        enterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                EditText angleEdit = (EditText)findViewById(R.id.AngleEdit);
                EditText powerEdit = (EditText)findViewById(R.id.PowerEdit);

                boolean valid = false;
                int angle = 0;
                int power = 0;

                try
                {
                    angle = Integer.parseInt(angleEdit.getText().toString());
                    power = Integer.parseInt(powerEdit.getText().toString());
                    valid = true;
                }
                catch (Exception e)
                {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Please enter a power and angle value", Toast.LENGTH_SHORT);
                    toast.show();
                }

                if (valid)
                    worldThread.throwBanana(0, angle, power);
            }
        });

        worldThread.setGameEndListener(new GameEndListener() {
            public void onEnd() {
                if (! closed) {
                    closed = true;
                    Intent endScreen = new Intent();
                    endScreen.setClassName("com.orangetentacle.gorillas", "com.orangetentacle.gorillas.GorillasLaunchActivity");
                    startActivity(endScreen);
                    finish();
                    Log.e("Gorillas", "Ended game activity");
                }
            }
        });
    }
}

