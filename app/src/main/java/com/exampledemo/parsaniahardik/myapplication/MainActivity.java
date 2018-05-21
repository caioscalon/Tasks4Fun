package com.exampledemo.parsaniahardik.myapplication;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;                          // Layout (Recycler View)
    private ArrayList<Model> modelArrayList;
    private CustomAdapter customAdapter;
    private Button btnselect, btndeselect, btnadd, btnok;       // Botões (seleciona, desmarca, adiciona, ok)
    private List<String> lista_ativ = new ArrayList<String>();  // Lista de atividades
    private int i = 0;
    private ArrayList<Model> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa as variáveis
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        btnselect = (Button) findViewById(R.id.select);
        btndeselect = (Button) findViewById(R.id.deselect);
        btnadd = (Button) findViewById(R.id.add);
        btnok = (Button) findViewById(R.id.ok);

        modelArrayList = getModel(false);
        customAdapter = new CustomAdapter(this,modelArrayList);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        btnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    modelArrayList = getModel(true);
                    customAdapter = new CustomAdapter(MainActivity.this, modelArrayList);
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
                    customAdapter = new CustomAdapter(MainActivity.this, modelArrayList);
                    recyclerView.setAdapter(customAdapter);
                }
                finally {}
            }
        });

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lista_ativ.add("Atividade " + Integer.toString(++i));
                modelArrayList = getModel(false);
                customAdapter = new CustomAdapter(MainActivity.this,modelArrayList);
                recyclerView.setAdapter(customAdapter);
            }
        });

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String atividades = "";
                int tam = lista_ativ.size();
                int n = 0;
                for (int i = tam - 1; i >= 0; i--) {
                    if (list.get(i).getSelected()) {
                         atividades = list.get(i).getAtiv() + "\n" + atividades;
                         lista_ativ.remove(i);
                         n++;
                    }
                }
                if (tam > 0 && n > 0) {
                    Toast.makeText(MainActivity.this, atividades, Toast.LENGTH_SHORT).show();
                    modelArrayList = getModel(false);
                    customAdapter = new CustomAdapter(MainActivity.this, modelArrayList);
                    recyclerView.setAdapter(customAdapter);
                }
            }
        });
    }

    private ArrayList<Model> getModel(boolean isSelect){
        list = new ArrayList<>();

        try {
            for (int i = 0; i < lista_ativ.size(); i++) {

                Model model = new Model();
                model.setSelected(isSelect);
                model.setAtiv(lista_ativ.get(i));
                list.add(model);
            }
        }
        finally {
            return list;
        }
    }
}
