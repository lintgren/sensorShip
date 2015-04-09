package se.sensorship;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Locale;


public class SoundVibration extends ActionBarActivity {

    private Boolean loadedTts;
    private TextToSpeech tts;
    private Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_vibration);

        setupTTS();
        setupVibrator();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sound_vibration, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        closeTTS();
        Log.d("tts Destroy", "tts Destroy");
        super.onDestroy();
    }

    public void rightButton(View v) {
        Log.d("Sound", "rightClick!");
        speak("Right");
        vibrate(1);

    }

    public void leftButton(View v) {
        Log.d("Sound", "leftClick!");
        speak("Left");
        vibrate(2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void speak(String text) {
        if (!loadedTts) {
            return;
        }
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
        } else {
            //noinspection deprecation
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
        }

    }

    private void vibrate(int numberOfVibrations) {
        long[] pattern = new long[numberOfVibrations * 2];
        for (int i = 0; i < pattern.length; i++) {
            if (i % 2 == 0) { // vibration
                pattern[i] = 150;
            } else {
                pattern[i] = 300;
            }

        }
        vibrator.vibrate(pattern, -1);
    }

    private void setupTTS() {
        if (tts != null) {
            Log.d("tts not null", "tts not null");
            return;
        }

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    loadedTts = true;
                    tts.setLanguage(Locale.CANADA);
                } else {
                    loadedTts = false;
                }
            }
        });

    }

    private void setupVibrator() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void closeTTS() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
