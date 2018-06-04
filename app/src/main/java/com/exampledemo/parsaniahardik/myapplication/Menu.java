package com.exampledemo.parsaniahardik.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button atividades = (Button) findViewById(R.id.atividades);
        atividades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Atividades.class);
                startActivity(intent);
            }
        });

        Button recompensas = (Button) findViewById(R.id.recompensas);
        recompensas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Recompensas.class);
                startActivity(intent);
            }
        });
    }
}
