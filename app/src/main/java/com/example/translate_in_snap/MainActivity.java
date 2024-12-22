package com.example.translate_in_snap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import org.json.JSONObject; //This library is for json parsing through dictionary
import java.io.*;
import android.speech.tts.TextToSpeech;  //This class is used for text to speech conversion.
import java.util.Locale;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;  //used for qr code

public class MainActivity extends AppCompatActivity {

    private JSONObject dictionary;
    private TextToSpeech textToSpeech;
    private TextView scannedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing the ui elements with the variables of the java code
        EditText inputText = findViewById(R.id.inputText);
        Button translateButton = findViewById(R.id.translateButton);
        Spinner languageSpinner = findViewById(R.id.languageSpinner);
        TextView outputText = findViewById(R.id.outputText);
        Button scanButton = findViewById(R.id.scanButton); // Add QR Scanner Button
        scannedText = findViewById(R.id.scannedText); // Display scanned result
        ImageView logoImageView = findViewById(R.id.logoImageView);
        logoImageView.setOnClickListener(v -> {
            //this is the creation of the dialog box which displays the notice
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Notice")
                    .setMessage("The utterance of the text in some languages may not work as it depends on the TTS on your device.")
                    .setPositiveButton("OK", null)
                    .show();
        });

        //Here we are loading the dictionary
        dictionary = loadDictionary(); //predefined function

        // Set up Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.SUCCESS) {
                Toast.makeText(this, "TTS Initialization Failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up language spinner i.e, initializing it with values
        String[] languages = {"French", "German", "Marathi", "Japanese"};
        String[] languageCodes = {"fr", "de", "mr", "ja"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Translate button click listener
        translateButton.setOnClickListener(v -> {
            String input = inputText.getText().toString().toLowerCase().trim();
            String selectedLanguage = languageCodes[languageSpinner.getSelectedItemPosition()];

            String translatedText = translate(input, selectedLanguage);
            outputText.setText(translatedText);

            // Speak the translated text
            if (!translatedText.equals("Word not found in dictionary") && !translatedText.equals("Translation not found")) {
                textToSpeech.setLanguage(getLocale(selectedLanguage));
                textToSpeech.speak(translatedText, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        // now the qr scanner part starts-
        scanButton.setOnClickListener(v -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
            intentIntegrator.setPrompt("Scan your QR Code");
            intentIntegrator.setOrientationLocked(true);
            intentIntegrator.initiateScan();
        });
    }

    private JSONObject loadDictionary() {
        //WE have imported the library for json object which goes through the data structure
        try {
            InputStream is = getAssets().open("dictionary.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            return new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String translate(String text, String languageCode) {
        //function is used to translate the text and is user defined
        try {
            if (dictionary != null && dictionary.has(text)) { // checking if the elements are there
                // or not using the .has method
                JSONObject translations = dictionary.getJSONObject(text);
                if (translations.has(languageCode)) {
                    return translations.getString(languageCode);
                } else {
                    return "Translation not found";
                }
            } else {
                return "Word not found in dictionary";
            }
        } catch (Exception e) {
            e.printStackTrace();  //Prints the detailed sequence of the methods causing exception..
            return "Error occurred";
        }
    }

    private Locale getLocale(String languageCode) {
        switch (languageCode) {
            case "fr":
                return Locale.FRENCH;
            case "de":
                return Locale.GERMAN;
            case "ja":
                return Locale.JAPANESE;
            default:
                return new Locale("mr"); //here we have declared the marathi language
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "QR Code Scanning Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                scannedText.setText(result.getContents());
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
