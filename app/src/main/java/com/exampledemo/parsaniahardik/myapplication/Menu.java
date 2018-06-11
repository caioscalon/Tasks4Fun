package com.exampledemo.parsaniahardik.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;

class Model {

    private boolean isSelected;
    private String atividade;

    public String getAtiv() {
        return atividade;
    }

    public void setAtiv(String atividade) {
        this.atividade = atividade;
    }

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    public static ArrayList<Model> imageModelArrayList;
    private Context ctx;


    public CustomAdapter(Context ctx, ArrayList<Model> imageModelArrayList) {

        inflater = LayoutInflater.from(ctx);
        this.imageModelArrayList = imageModelArrayList;
        this.ctx = ctx;
    }

    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.rv_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final CustomAdapter.MyViewHolder holder, int position) {

        holder.checkBox.setChecked(imageModelArrayList.get(position).getSelected());
        holder.tvAtiv.setText(imageModelArrayList.get(position).getAtiv());
        holder.checkBox.setTag(position);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer pos = (Integer) holder.checkBox.getTag();
                // Toast.makeText(ctx, imageModelArrayList.get(pos).getAtiv() + " clicked!", Toast.LENGTH_SHORT).show();

                if (imageModelArrayList.get(pos).getSelected()) {
                    imageModelArrayList.get(pos).setSelected(false);
                }
                else {
                    imageModelArrayList.get(pos).setSelected(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        protected CheckBox checkBox;
        private TextView tvAtiv;

        public MyViewHolder(View itemView) {
            super(itemView);

            checkBox = (CheckBox) itemView.findViewById(R.id.cb);
            tvAtiv = (TextView) itemView.findViewById(R.id.atividade);
        }

    }
}

public class Menu extends Activity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 0;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            Log.d("AUTH", auth.getCurrentUser().getEmail());
            TextView textoUser = findViewById(R.id.user);
            textoUser.setText(auth.getCurrentUser().getDisplayName());
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
        findViewById(R.id.desconectar).setOnClickListener(this);

        Button atividades = findViewById(R.id.atividades);
        atividades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Atividades.class);
                startActivity(intent);
            }
        });

        Button recompensas = findViewById(R.id.recompensas);
        recompensas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Recompensas.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Log.d("AUTH", auth.getCurrentUser().getEmail());
                TextView textoUser = findViewById(R.id.user);
                textoUser.setText(auth.getCurrentUser().getDisplayName());
            }
            else{
                Log.d("AUTH","Autenticação falhou");
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.desconectar){
            com.firebase.ui.auth.AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("AUTH", "USER DESCONECTADO");
                            startActivity(new Intent(getApplicationContext(), Menu.class));
                            finish();
                        }
                    });
        }
    }
}
