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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Atividades extends Activity implements View.OnClickListener {

    private RecyclerView recyclerView;                          // Layout (Recycler View)
    private ArrayList<Model> modelArrayList;
    private CustomAdapter customAdapter;
    private Button btnselect, btndeselect, btnadd, btnok, btnmenu;       // Botões (seleciona, desmarca, adiciona, ok)
    private List<String> list_atv = new ArrayList<String>();          // Lista de atividades
    private int i = 0;
    private ArrayList<Model> list;
    private static final int RC_SIGN_IN = 0;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private CollectionReference mColRef;
    private final String ATIV_KEY = "atividade";
    private final String PONTOS_KEY = "pontos";
    private final String CRIANCA_KEY = "crianca";
    private final String FEITO_KEY = "foiFeito";
    private String nomeAtividade;
    private boolean foiFeito;
    private RecyclerView mMainList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividades);
        setTitle("Tasks4Fun - Atividades");

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
        final Map<String, Object> dataToSave = new HashMap<String, Object>();

        mMainList = findViewById(R.id.recycler);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if(auth.getCurrentUser() != null){
            //user already signed in
            mColRef = firestore.collection("Task4Fun").document(auth.getCurrentUser().getDisplayName()).collection("Atividades");
            Log.d("AUTH", auth.getCurrentUser().getEmail());
            TextView textoUser = findViewById(R.id.user);
            textoUser.setText(auth.getCurrentUser().getDisplayName() + " - Atividades");
            mColRef.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("FIRESTORE", document.getId() + " => " + document.getData());
                                    foiFeito = false;
                                    for (Map.Entry<String,Object> entry : document.getData().entrySet()) {
                                        if(entry.getKey().equals("atividade")) {
                                            nomeAtividade = entry.getValue().toString();
                                        }
                                        else if(entry.getKey().equals("foiFeito") && entry.getValue().toString().equals("true")) {
                                            foiFeito = true;
                                        }
                                    }
                                    if(foiFeito == false) {
                                        list_atv.add(nomeAtividade);
                                        Log.d("LOAD_ITEM", nomeAtividade);
                                        modelArrayList = getModel(false);
                                        customAdapter = new CustomAdapter(Atividades.this, modelArrayList);
                                        recyclerView.setAdapter(customAdapter);
                                    }
//                                    list_atv.add(document.getData().getString("atividade"));
                                }
                            } else {
                                Log.d("FIRESTORE", "Error getting documents: ", task.getException());
                            }
                        }
                    });
//            modelArrayList = getModel(false);
//            customAdapter = new CustomAdapter(Atividades.this, modelArrayList);
//            recyclerView.setAdapter(customAdapter);
        }
        else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
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
                    customAdapter = new CustomAdapter(Atividades.this, modelArrayList);
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
                    customAdapter = new CustomAdapter(Atividades.this, modelArrayList);
                    recyclerView.setAdapter(customAdapter);
                }
                finally {}
            }
        });

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeAtiv = "Atividade " + Integer.toString(++i);
                list_atv.add(nomeAtiv);
                dataToSave.put(ATIV_KEY, nomeAtiv);
                dataToSave.put(PONTOS_KEY, i*100);
                dataToSave.put(CRIANCA_KEY, "Filho(a) de " + auth.getCurrentUser().getDisplayName());
                dataToSave.put(FEITO_KEY, false);
//                Log.d("Document_name", );
                mColRef.document().set(dataToSave).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d("Atividade", "Atividade foi salva com sucesso!\n" + task.getResult());
                        }
                        else {
                            Log.w("Atividade", "Atividade não foi salva" + task.getException());
                        }
                    }
                });
                modelArrayList = getModel(false);
                customAdapter = new CustomAdapter(Atividades.this, modelArrayList);
                recyclerView.setAdapter(customAdapter);
            }
        });

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String atividades = "";
                int tam = list_atv.size();
                int n = 0;
                Query query;
                for (int i = tam - 1; i >= 0; i--) {
                    if (list.get(i).getSelected()) {
                        atividades = list.get(i).getAtiv() + "\n" + atividades;
                        list_atv.remove(i);
//                        query = mColRef.whereEqualTo("atividade", list.get(i).getAtiv());
//                        query.update()
                        n++;
                    }
                }

                if (tam > 0 && n > 0) {
                    Toast.makeText(Atividades.this, atividades, Toast.LENGTH_SHORT).show();
                    modelArrayList = getModel(false);
                    customAdapter = new CustomAdapter(Atividades.this, modelArrayList);
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
            for (int i = 0; i < list_atv.size(); i++) {

                Model model = new Model();
                model.setSelected(isSelect);
                model.setAtiv(list_atv.get(i));
                list.add(model);
            }
        }
        finally {
            return list;
        }
    }
}