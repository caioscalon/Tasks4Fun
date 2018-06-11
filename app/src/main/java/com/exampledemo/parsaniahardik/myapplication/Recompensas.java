package com.exampledemo.parsaniahardik.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Recompensas extends Activity implements View.OnClickListener {

    private RecyclerView recyclerView;                          // Layout (Recycler View)
    private ArrayList<Model> modelArrayList;
    private CustomAdapter customAdapter;
    private Button btnselect, btndeselect, btnadd, btnok, btnmenu;       // Botões (seleciona, desmarca, adiciona, ok)
    private List<String> list_rec = new ArrayList<String>();          // Lista de recompensas
    private int i = 0;
    private ArrayList<Model> list;
    private static final int RC_SIGN_IN = 0;
    private FirebaseAuth auth;
    private RecyclerView mMainList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recompensas);
        setTitle("Tasks4Fun - Recompensas");

        // Inicializa as variáveis
        recyclerView = findViewById(R.id.recycler);
        btnselect = findViewById(R.id.select);
        btndeselect = findViewById(R.id.deselect);
        btnadd = findViewById(R.id.add);
        btnok = findViewById(R.id.ok);
        btnmenu = findViewById(R.id.menu);

        modelArrayList = getModel(false);
        customAdapter = new CustomAdapter(this,modelArrayList);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        mMainList = findViewById(R.id.recycler);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            //user already signed in
            TextView textoUser = findViewById(R.id.user);
            textoUser.setText(auth.getCurrentUser().getDisplayName() + " - Recompensas");
        }
        else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
//                                    new AuthUI.IdpConfig.FacebookBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        }
        findViewById(R.id.log_out_button).setOnClickListener(this);

        btnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    modelArrayList = getModel(true);
                    customAdapter = new CustomAdapter(Recompensas.this, modelArrayList);
                    recyclerView.setAdapter(customAdapter);
                }
                finally {}
            }
        });

        btndeselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    modelArrayList = getModel(false);
                    customAdapter = new CustomAdapter(Recompensas.this, modelArrayList);
                    recyclerView.setAdapter(customAdapter);
                }
                finally {}
            }
        });

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_rec.add("Recompensa " + Integer.toString(++i));
                modelArrayList = getModel(false);
                customAdapter = new CustomAdapter(Recompensas.this,modelArrayList);
                recyclerView.setAdapter(customAdapter);
            }
        });

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recompensas = "";
                int tam = list_rec.size();
                int n = 0;
                for (int i = tam - 1; i >= 0; i--) {
                    if (list.get(i).getSelected()) {
                        recompensas = list.get(i).getAtiv() + "\n" + recompensas;
                        list_rec.remove(i);
                        n++;
                    }
                }
                if (tam > 0 && n > 0) {
                    Toast.makeText(Recompensas.this, recompensas, Toast.LENGTH_SHORT).show();
                    modelArrayList = getModel(false);
                    customAdapter = new CustomAdapter(Recompensas.this, modelArrayList);
                    recyclerView.setAdapter(customAdapter);
                }
            }
        });

        btnmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                //user logged in
                Log.d("AUTH", auth.getCurrentUser().getEmail());
                TextView textoUser = (TextView) findViewById(R.id.user);
                textoUser.setText(auth.getCurrentUser().getDisplayName());
            }
            else{
                //User not authenticated
                Log.d("AUTH","Autenticação falhou");
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.log_out_button){
            com.firebase.ui.auth.AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("AUTH", "USER DESCONECTADO");
                            finish();
                        }
                    });
        }
    }

    private ArrayList<Model> getModel(boolean isSelect){
        list = new ArrayList<>();

        try {
            for (int i = 0; i < list_rec.size(); i++) {

                Model model = new Model();
                model.setSelected(isSelect);
                model.setAtiv(list_rec.get(i));
                list.add(model);
            }
        }
        finally {
            return list;
        }
    }
}
