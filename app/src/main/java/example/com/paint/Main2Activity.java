package example.com.paint;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import example.com.paint.MainActivity;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    public ListView lst;
    public ArrayList<String> finalGuesses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        lst = (ListView) findViewById(R.id.listView);
        TextView textGuess = findViewById(R.id.textGuess);
        textGuess.setText("GUESSES");
        Intent intent = getIntent();
        finalGuesses = intent.getStringArrayListExtra(MainActivity.EXTRA_LIST);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Main2Activity.this, R.layout.list_layout, finalGuesses);
        lst.setVisibility(View.VISIBLE);
        lst.setAdapter(arrayAdapter);
    }

}
