package es.unex.prototipoasee;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AppInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        setTitle(R.string.app_info);
    }
}