package com.example.cattleapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.example.cattleapp.R;

import com.example.cattleapp.adapters.AnimalAdapter;
import com.example.cattleapp.models.Animal;
import com.example.cattleapp.network.ApiClient;
import com.example.cattleapp.network.ApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAnimals;
    private FloatingActionButton fabAddAnimal;
    private AnimalAdapter animalAdapter;

    private ApiService apiService;
    private int userId; // Получите ID пользователя после авторизации

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов UI
        recyclerViewAnimals = findViewById(R.id.recyclerViewAnimals);
        fabAddAnimal = findViewById(R.id.fabAddAnimal);

        // Получение userId из Intent
        userId = getIntent().getIntExtra("userId", -1);

        // Инициализация ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        // Настройка RecyclerView
        recyclerViewAnimals.setLayoutManager(new LinearLayoutManager(this));

        // Загрузка списка животных
        loadAnimals();

        // Обработка нажатия на FAB
        fabAddAnimal.setOnClickListener(v -> {
            // Открыть экран добавления животного
            Intent intent = new Intent(MainActivity.this, AnimalDetailActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    private void loadAnimals() {
        Call<List<Animal>> call = apiService.getAnimals(userId);
        call.enqueue(new Callback<List<Animal>>() {
            @Override
            public void onResponse(Call<List<Animal>> call, Response<List<Animal>> response) {
                if(response.isSuccessful()) {
                    List<Animal> animalList = response.body();
                    animalAdapter = new AnimalAdapter(animalList, MainActivity.this);
                    recyclerViewAnimals.setAdapter(animalAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Ошибка загрузки животных", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Animal>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
