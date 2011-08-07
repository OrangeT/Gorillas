package com.orangetentacle.gorillas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity to display user options and then start the game.
 */
public class GorillasLaunchActivity extends Activity {

    public TextView Rounds;
    public EditText Player1Name;
    public EditText Player2Name;
    public Configuration Config;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.launch);

        Config = new Configuration(getSharedPreferences(Configuration.PREF_NAME, 0));

        Button play = (Button)findViewById(R.id.Play);
        play.setOnClickListener(startGame);

        Button addRounds = (Button)findViewById(R.id.AddRounds);
        addRounds.setOnClickListener(addRoundsClick);

        Button removeRounds = (Button)findViewById(R.id.RemoveRounds);
        removeRounds.setOnClickListener(removeRoundsClick);

        Rounds = (TextView)findViewById(R.id.Rounds);
        Rounds.setText(Integer.toString(Config.Rounds));

        Player1Name = (EditText)findViewById(R.id.Player1Name);
        Player1Name.setText(Config.Player1Name);

        Player2Name = (EditText)findViewById(R.id.Player2Name);
        Player2Name.setText(Config.Player2Name);
    }

    public void adjustRoundTotal(int adjust) {
        TextView counter = (TextView)findViewById(R.id.Rounds);
        int total = Integer.parseInt(counter.getText().toString()) + adjust;
        if (total < 0) total = 0;
        if (total > 15) total = 15;
        counter.setText(Integer.toString(total));
    }

    View.OnClickListener startGame = new View.OnClickListener() {
        public void onClick(View view) {

            Config.Rounds = Integer.parseInt(Rounds.getText().toString());
            Config.Player1Name = Player1Name.getText().toString();
            Config.Player2Name = Player2Name.getText().toString();

            Config.savePrefs(getSharedPreferences(Configuration.PREF_NAME, 0));

            Intent game = new Intent();
            game.setClassName("com.orangetentacle.gorillas", "com.orangetentacle.gorillas.GorillasActivity");
            game.putExtra("com.orangetentacle.gorillas.Rounds", Config.Rounds);
            game.putExtra("com.orangetentacle.gorillas.Player1Name", Config.Player1Name);
            game.putExtra("com.orangetentacle.gorillas.Player2Name", Config.Player2Name);
            startActivity(game);
            finish();
            Log.e("Gorillas", "Ended launch activity");
        }
    };

    View.OnClickListener addRoundsClick = new View.OnClickListener() {
        public void onClick(View view) {
            adjustRoundTotal(1);
        }
    };

    View.OnClickListener removeRoundsClick = new View.OnClickListener() {
        public void onClick(View view) {
            adjustRoundTotal(-1);
        }
    };
}
