
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
            // Отвечаем на щелчок по пункту меню "Удалить все записи"
            case R.id.action_delete_all_entries:
                // Пока ничего не делаем
                PetDbHelper dbhelper = new PetDbHelper(this);
                try  (SQLiteDatabase db = dbhelper.getWritableDatabase()){
                    db.delete(PetEntry.TABLE_NAME,null,null);
                }
                listView.setAdapter(null);
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);// создается интент чтобы открыть Активити редактора
                startActivity(intent); //запускаем интент
                Toast.makeText(this, "Это фича",Toast.LENGTH_LONG).show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Временный вспомогательный метод для отображения информации в экранном TextView о состоянии
     * База данных домашних животных.
     */
    private void displayDatabaseInfo() {
        PetDbHelper mDbHelper = new PetDbHelper(this);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + PetEntry.TABLE_NAME, null);
        if (cursor.getCount() > 0) {
            String[] headers = new String[]{PetEntry.COLUMN_PET_NAME, PetEntry.COLUMN_PET_BREED, PetEntry.COLUMN_PET_STR_GENDER, PetEntry.COLUMN_PET_WEIGHT};
            SimpleCursorAdapter petAdapter = new SimpleCursorAdapter(this, R.layout.for_list_view_item, cursor, headers
                    , new int[]{R.id.textView, R.id.textView2,R.id.textView3, R.id.textView4 }, 0);
            listView.setAdapter(petAdapter);
        }
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
