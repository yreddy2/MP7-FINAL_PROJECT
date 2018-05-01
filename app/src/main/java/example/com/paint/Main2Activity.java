package example.com.paint;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    public ListView lst;
    public ArrayList<String> finalGuesses;
    public String guess = "";
    public boolean guessed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        lst = (ListView) findViewById(R.id.listView);
        TextView textGuess = findViewById(R.id.textGuess);
        textGuess.setText("GUESSES:");
        Intent intent = getIntent();
        finalGuesses = intent.getStringArrayListExtra(MainActivity.EXTRA_LIST);
        guess = intent.getStringExtra(MainActivity.EXTRA_STRING);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Main2Activity.this, R.layout.list_layout, finalGuesses);
        lst.setVisibility(View.VISIBLE);
        lst.setAdapter(arrayAdapter);
        for (int i = 0; i < finalGuesses.size(); i++) {
            if (finalGuesses.get(i).contains(guess.toUpperCase())) {
                guessed = true;
            }
        }
        TextView yourGuess = findViewById(R.id.yourGuessText);
        if (guessed) {
            yourGuess.setText("YOU WIN \n Google guessed: " + guess.toUpperCase());
            yourGuess.setTextColor(Color.GREEN);
        } else {
            yourGuess.setText("YOU LOSE \n Google did not guess: " + guess.toUpperCase());
            yourGuess.setTextColor(Color.RED);
        }
    }

    public void goBackButton(View v) {
        Button button = (Button) v;
        Intent intent = new Intent(Main2Activity.this, MainActivity.class);
        startActivity(intent);
    }
}
