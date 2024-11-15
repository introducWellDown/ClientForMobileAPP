package com.example.cattleapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.cattleapp.R;


import com.example.cattleapp.models.Animal;
import com.example.cattleapp.network.ApiClient;
import com.example.cattleapp.network.ApiService;
import com.example.cattleapp.utils.FeedingAlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimalDetailActivity extends AppCompatActivity {

    private EditText editTextName, editTextType, editTextAge, editTextFeedingTime;
    private Button buttonSave;

    private ApiService apiService;
    private int animalId;
    private Animal animal;
    private int userId; // Получите userId из SharedPreferences или Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_detail);

        // Инициализация элементов UI
        editTextName = findViewById(R.id.editTextName);
        editTextType = findViewById(R.id.editTextType);
        editTextAge = findViewById(R.id.editTextAge);
        editTextFeedingTime = findViewById(R.id.editTextFeedingTime);
        buttonSave = findViewById(R.id.buttonSave);

        // Получение animalId из Intent
        animalId = getIntent().getIntExtra("animalId", -1);
        userId = getIntent().getIntExtra("userId", -1);

        // Инициализация ApiService
        apiService = ApiClient.getClient().create(ApiService.class);

        if (animalId != -1) {
            // Загрузка информации о животном
            loadAnimalDetails();
        }

        // Обработка нажатия на кнопку "Сохранить"
        buttonSave.setOnClickListener(v -> {
            saveAnimalDetails();
        });
    }

    private void loadAnimalDetails() {
        // Реализуйте метод загрузки информации о животном по его ID
    }

    private void saveAnimalDetails() {
        String name = editTextName.getText().toString().trim();
        String type = editTextType.getText().toString().trim();
        String ageStr = editTextAge.getText().toString().trim();
        String feedingTime = editTextFeedingTime.getText().toString().trim();

        if (name.isEmpty() || type.isEmpty() || ageStr.isEmpty() || feedingTime.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);

        if (animalId == -1) {
            // Добавление нового животного
            animal = new Animal(name, age, type, feedingTime, userId);

            Call<Animal> call = apiService.addAnimal(animal);
            call.enqueue(new Callback<Animal>() {
                @Override
                public void onResponse(Call<Animal> call, Response<Animal> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AnimalDetailActivity.this, "Животное добавлено", Toast.LENGTH_SHORT).show();
                        setFeedingAlarm(feedingTime, name);
                        finish();
                    } else {
                        Toast.makeText(AnimalDetailActivity.this, "Ошибка при добавлении", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Animal> call, Throwable t) {
                    Toast.makeText(AnimalDetailActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Обновление существующего животного
            animal.setName(name);
            animal.setType(type);
            animal.setAge(age);
            animal.setFeedingTime(feedingTime);

            Call<Animal> call = apiService.updateAnimal(animalId, animal);
            call.enqueue(new Callback<Animal>() {
                @Override
                public void onResponse(Call<Animal> call, Response<Animal> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AnimalDetailActivity.this, "Информация обновлена", Toast.LENGTH_SHORT).show();
                        setFeedingAlarm(feedingTime, name);
                        finish();
                    } else {
                        Toast.makeText(AnimalDetailActivity.this, "Ошибка при обновлении", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Animal> call, Throwable t) {
                    Toast.makeText(AnimalDetailActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setFeedingAlarm(String feedingTime, String animalName) {
        // Настройка будильника на кормление

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Calendar feedingCalendar = Calendar.getInstance();
            Calendar currentCalendar = Calendar.getInstance();
            feedingCalendar.setTime(sdf.parse(feedingTime));

            // Установить текущую дату
            feedingCalendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
            feedingCalendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
            feedingCalendar.set(Calendar.DAY_OF_MONTH, currentCalendar.get(Calendar.DAY_OF_MONTH));

            // Если время уже прошло, установить на следующий день
            if (feedingCalendar.before(Calendar.getInstance())) {
                feedingCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            Intent intent = new Intent(this, FeedingAlarmReceiver.class);
            intent.putExtra("animalName", animalName);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, animalId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, feedingCalendar.getTimeInMillis(), pendingIntent);

            Toast.makeText(this, "Будильник на кормление установлен", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Неверный формат времени", Toast.LENGTH_SHORT).show();
        }
    }
}
