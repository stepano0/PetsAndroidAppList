
package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.data.PetContract.PetEntry;
import com.example.myapplication.data.PetDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CatalogActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);  //создается разметка на экране

        // Настраиваем FAB для открытия EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab); //находим кнопку на экране
        fab.setOnClickListener(new View.OnClickListener() { //устанавливаем на кнопку прослушиватель кликов
            @Override
            public void onClick(View view) { // при нажатии на кнопку
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);// создается интент чтобы открыть Активити редактора
                startActivity(intent); //запускаем интент
            }
        });

        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Расширение пунктов меню из файла res / menu / menu_catalog.xml.
        // Это добавляет пункты меню на панель приложения.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Пользователь щелкнул пункт меню в меню переполнения панели приложения
        switch (item.getItemId()) {
            // Отвечаем на щелчок по пункту меню "Удалить все записи"
            case R.id.action_delete_all_entries:
                // Пока ничего не делаем
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Временный вспомогательный метод для отображения информации в экранном TextView о состоянии
     * База данных домашних животных.
     */
    private void displayDatabaseInfo() {
        TextView displayView = (TextView) findViewById(R.id.text_view_pet);
        PetDbHelper mDbHelper = new PetDbHelper(this);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(PetEntry.TABLE_NAME,
                null, null, null,
                null, null, null );
        int idPosition = cursor.getColumnIndex(PetEntry._ID);
        int namePosition = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
        int genderPosition = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
        int weighPosition = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);
        int breedPosition = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
        while (cursor.moveToNext()){
            int corentID  = cursor.getInt(idPosition);
            int corentWeigh  = cursor.getInt(weighPosition);
            int corentGender  = cursor.getInt(genderPosition);
            String corentName = cursor.getString(namePosition);
            String corentBreed = cursor.getString(breedPosition);
            displayView.append(corentID +" - ");
            displayView.append(corentName+" - ");
            displayView.append(corentBreed+" - ");
            displayView.append(corentGender +" - ");
            displayView.append(corentWeigh +" - ");
            displayView.append("\n");
        }


            // Отображаем количество строк в курсоре (которое отражает количество строк в
            // таблица pets в базе данных).

            displayView.append("Number of rows in pets database table: " + cursor.getCount());


    }
}
