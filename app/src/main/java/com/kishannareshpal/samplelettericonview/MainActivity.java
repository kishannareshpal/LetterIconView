package com.kishannareshpal.samplelettericonview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.kishannareshpal.lettericonview.LetterIconView;
import com.kishannareshpal.lettericonview.Shape;

public class MainActivity extends AppCompatActivity {

    boolean isg = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LetterIconView a = findViewById(R.id.liv_a);
        Button button = findViewById(R.id.button);
        EditText et = findViewById(R.id.et);


        button.setOnClickListener(v -> {
            String s = et.getText().toString();
            a.letters(s);
            a.shape(Shape.ROUNDED_SQUARE);
        });

    }
}