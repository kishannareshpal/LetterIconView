package com.kishannareshpal.samplelettericonview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.kishannareshpal.lettericonview.LetterIconView;
import com.kishannareshpal.lettericonview.Shape;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LetterIconView letterIconView = findViewById(R.id.letterIconView);
        EditText et = findViewById(R.id.et);

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String name = s.toString();

                String[] names = name.split(" ");

                if (names.length > 1) {
                    StringBuilder letters = new StringBuilder();
                    for (String n: names) {
                        letters.append(n.charAt(0));
                    }
                    letterIconView.letters(letters.toString());

                } else {
                    letterIconView.letters(name);
                }
            }
        });

    }
}