
package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.data.PetContract.PetEntry;
import com.example.myapplication.data.PetDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


/*
** Отображает список домашних животных, которые были введены и сохранены в приложении.
*/
public class CatalogActivity extends AppCompatActivity {
    ListView listView;
    DbSelectThread selectThread = new DbSelectThread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);  //создается разметка на экране

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent intent = new Intent(getApplicationContext(), EditorActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        // Настраиваем FAB для открытия EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab); //находим кнопку на экране
        fab.setOnClickListener(new View.OnClickListener() { //устанавливаем на кнопку прослушиватель кликов
            @Override
            public void onClick(View view) { // при нажатии на кнопку
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);// создается интент чтобы открыть Активити редактора
                startActivity(intent); //запускаем интент
            }
        });

        //selectThread.start();
        displayDatabaseInfo();
    }

    private void insertPet() {

        PetDbHelper mDbHelper = new PetDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);
        db.insert(PetEntry.TABLE_NAME, null, values);

        selectThread.start();
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
            // Отвечаем на щелчок по пункту меню "Вставить фиктивные данные"
            case R.id.action_insert_dummy_data:
                insertPet();
                selectThread.start();
                return true;
            // Отвечаем на щелчок по пункту меню "Удалить все записи"
            case R.id.action_delete_all_entries:
                // Пока ничего не делаем
                PetDbHelper dbhelper = new PetDbHelper(this);
                try  (SQLiteDatabase db = dbhelper.getWritableDatabase()){
                    db.delete(PetEntry.TABLE_NAME,null,null);
                }
                selectThread.start();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Временный вспомогательный метод для отображения информации в экранном TextView о состоянии
     * База данных домашних животных.
     */
    private void displayDatabaseInfo() {

        // Чтобы получить доступ к нашей базе данных, мы создаем экземпляр нашего подкласса
        // SQLiteOpenHelper и передать контекст, который является текущей активностью.
        PetDbHelper mDbHelper = new PetDbHelper(this);

        // Создаем и/или открываем базу данных для чтения из нее
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Выполняем этот необработанный SQL-запрос "SELECT * FROM pets"
        // чтобы получить курсор, содержащий все строки из таблицы pets.

        Cursor cursor = db.rawQuery("SELECT * FROM " + PetEntry.TABLE_NAME, null);
            String[] headers = new String[]{PetEntry.COLUMN_PET_NAME, PetEntry.COLUMN_PET_BREED};
            SimpleCursorAdapter petAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, cursor,headers
            ,new int[]{android.R.id.text1, android.R.id.text2},0);
                listView.setAdapter(petAdapter);

            // Отображаем количество строк в курсоре (которое отражает количество строк в
            // таблица pets в базе данных).
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            displayView.setText("Number of rows in pets database table: " + cursor.getCount());


    }
    private class DbSelectThread extends Thread{
        @Override
        public void run(){
            displayDatabaseInfo();
        }
    }
}
