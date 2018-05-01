package example.com.paint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {

    private PaintView paintView;

    public static final String EXTRA_STRING = "EXTRA2";

    public static final String EXTRA_LIST = "EXTRA";

    String guess = "";

    private Bitmap drawingMap;

    private static final String TARGET_URL = "https://vision.googleapis.com/v1/images:annotate?";
    private static final String API_KEY = "key=AIzaSyAXAWOscEv1QPQuwB_OLgzM6eXVskg1bJo";

    ArrayList<String> finalGuesses = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paintView = (PaintView) findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
    }
    public void normalButton(View v) {
        ImageButton button = (ImageButton) v;
        paintView.normal();
    }
    public void clearButton(View v) {
        ImageButton button = (ImageButton) v;
        paintView.clear();
    }

    public void eraseButton(View v) {
        ImageButton button = (ImageButton) v;
        paintView.erase();
    }
    public void doneButton(View v) throws IOException {
        Button button = (Button) v;
        paintView.setDrawingCacheEnabled(true);
        paintView.invalidate();
        OutputStream pic = null;
        drawingMap = paintView.getDrawingCache();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        drawingMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageData = stream.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(imageData, android.util.Base64.DEFAULT);

        URL serverUrl = new URL(TARGET_URL + API_KEY);
        URLConnection urlConnection = serverUrl.openConnection();
        HttpURLConnection httpConnection = (HttpURLConnection) urlConnection;
        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty("Content-Type", "application/json");
        httpConnection.setDoOutput(true);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        BufferedWriter httpRequestBodyWriter = new BufferedWriter(new
                OutputStreamWriter(httpConnection.getOutputStream()));
        httpRequestBodyWriter.write
                ("{\"requests\":[{\"image\":{\"content\":\"" + encodedImage +
                        "\"},\"features\":[{\"type\":\"LABEL_DETECTION\",\"maxResults\":200}]}]}");
        httpRequestBodyWriter.close();
        String response = httpConnection.getResponseMessage();
        if (httpConnection.getInputStream() == null) {
            System.out.println("No stream");
            return;
        }

        Scanner httpResponseScanner = new Scanner (httpConnection.getInputStream());
        String resp = "";
        while (httpResponseScanner.hasNext()) {
            String line = httpResponseScanner.nextLine();
            resp += line;
        }
        Log.w("please", resp);
        jsonParse(resp);
        httpResponseScanner.close();
    }
    public void jsonParse(final String toParse) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(toParse).getAsJsonObject();
        JsonArray jar = obj.getAsJsonArray("responses");
        JsonObject labels = jar.get(0).getAsJsonObject();
        JsonArray jar2 = labels.getAsJsonArray("labelAnnotations");
        finalGuesses = new ArrayList<>();
        EditText editText = findViewById(R.id.editText);
        guess = editText.getText().toString();
        for(int i = 0; i < jar2.size(); i++) {
            JsonObject type = jar2.get(i).getAsJsonObject();
            String type2 = type.get("description").getAsString();
            Log.w("item","|" + type2 + "|");
            if (!type2.equals("angle") && !type2.equals("black and white") && !type2.equals("product")
                    && !type2.equals("font") && !type2.equals("line") && !type2.equals("text")
                    && !type2.equals("white") && !type2.equals("black") && !type2.equals("line art")
                    && !type2.equals("graphics") && !type2.equals("monochrome") && !type2.equals("monochrome photography")
                    && !type2.equals("point") && !type2.equals("clip art") && !type2.equals("area")
                    && !type2.equals("drawing") && !type2.equals("product design") && !type2.equals("cartoon")
                    && !type2.equals("design") && !type2.equals("artwork") && !type2.equals("illustration")
                    && !type2.equals("product design") && !type2.equals("material") && !type2.equals("art") && !type2.equals("symmetry")) {
                double type3 = type.get("topicality").getAsDouble() * 100;
                int type4 = (int) type3;
                type2 += "     " + Integer.toString(type4) + "%";
                finalGuesses.add(type2.toUpperCase());
            }
        }
        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
        intent.putExtra(EXTRA_LIST, finalGuesses);
        intent.putExtra(EXTRA_STRING, guess);
        startActivity(intent);
    }
}
