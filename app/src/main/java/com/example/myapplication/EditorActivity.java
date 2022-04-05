package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.myapplication.Model.PetUnit;
import com.example.myapplication.data.PetContract;
import com.example.myapplication.data.PetDbHelper;

/**
 * Позволяет пользователю создать нового питомца или отредактировать существующего.
 */
public class EditorActivity extends AppCompatActivity {

    private Long editPetId;
    private PetUnit localUnit;

    /** Поле EditText для ввода имени питомца  */
    private EditText mNameEditText;

    /** Поле EditText для ввода породы питомца  */
    private EditText mBreedEditText;

    /** Поле EditText для ввода веса питомца */
    private EditText mWeightEditText;

    /** Поле EditText для ввода пола питомца */
    private Spinner mGenderSpinner;

    /**
     * Пол питомца. Возможные значения:
     * 0 - пол неизвестен, 1 - мужской, 2 - женский.
     */
    private int mGender = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Находим все соответствующие представления,
        // которые нам понадобятся для чтения пользовательского ввода
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            editPetId = extras.getLong("id");

            if (editPetId > 0) {
                PetDbHelper dbHelper = new PetDbHelper(this);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM " + PetContract.PetEntry.TABLE_NAME + " WHERE _ID =?", new String[]{String.valueOf(editPetId)});
                cursor.moveToFirst();
                localUnit = new PetUnit(cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4));
                localUnit.setId(editPetId);
                mNameEditText.setText(localUnit.getPetName());
                mBreedEditText.setText(localUnit.getPetBreed());
                mGender = localUnit.getPetGender();
                mGenderSpinner.setSelection(mGender);
                mWeightEditText.setText(String.valueOf(localUnit.getPetWeight()));
                cursor.close();
            }
        }
    }

    /**
     * Настройте выпадающий счетчик, который позволяет пользователю выбрать пол питомца.
     */
    private void setupSpinner() {
        // Создаем адаптер для счетчика. Параметры списка взяты из массива String, который он будет использовать
        // счетчик будет использовать макет по умолчанию
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Укажите стиль макета раскрывающегося списка - простой вид списка с 1 элементом в строке
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Применяем адаптер к спиннеру
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Устанавливаем целое число mSelected на постоянные значения
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = 1; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = 2; // Female
                    } else {
                        mGender = 0; // Unknown
                    }
                }
            }

            // Поскольку AdapterView является абстрактным классом,
            // необходимо определить onNothingSelected
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Расширение параметров меню из файла res / menu / menu_editor.xml.
        // Это добавляет пункты меню на панель приложения.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Пользователь щелкнул пункт меню в меню переполнения панели приложения
        switch (item.getItemId()) {
            // Отвечаем на щелчок по опции меню "Сохранить"
            case R.id.action_save:
                if (editPetId != null){
                    PetUnit unit = new PetUnit(mNameEditText.getText().toString(), mBreedEditText.getText().toString(),
                            mGender, Integer.parseInt(mWeightEditText.getText().toString()));
                    DBUpdateTread dbUpdateTread = new DBUpdateTread(unit);
                    dbUpdateTread.start();
                }
                else {
                    PetUnit newUnit = new PetUnit(mNameEditText.getText().toString(), mBreedEditText.getText().toString(),
                            mGender, Integer.parseInt(mWeightEditText.getText().toString()));
                    insertPet(newUnit);
                }
                return true;
            //Отвечаем на щелчок по опции меню "Удалить"
            case R.id.action_delete:
                if (localUnit != null){
                    delitePet(localUnit);
                }
                // Пока ничего не делаем
                return true;
            // Отвечаем на нажатие кнопки со стрелкой «Вверх» на панели приложения
            case android.R.id.home:
                // Возвращаемся к родительскому действию (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertPet(PetUnit newUnit){
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME,newUnit.getPetName());
        values.put(PetContract.PetEntry.COLUMN_PET_BREED,newUnit.getPetBreed());
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER,newUnit.getPetGender());
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT,newUnit.getPetWeight());

        PetDbHelper dbHelper = new PetDbHelper(this);
        long newRowId = 0;
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()){
            newRowId = db.insert(PetContract.PetEntry.TABLE_NAME,null, values);
        }
        if (newRowId == -1){
            Toast.makeText(this, "Ошибка добавления данных",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Данные успешно занесены",Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(EditorActivity.this, CatalogActivity.class);// создается интент чтобы открыть Активити редактора
        startActivity(intent); //Возвращаем прежний активити
    }
    private void updatePet(PetUnit unit){
        if (localUnit != null){
            PetDbHelper dbHelper = new PetDbHelper(this);
            try (SQLiteDatabase db = dbHelper.getWritableDatabase()){
                ContentValues values = new ContentValues();
                values.put(PetContract.PetEntry.COLUMN_PET_NAME,unit.getPetName());
                values.put(PetContract.PetEntry.COLUMN_PET_BREED,unit.getPetBreed());
                values.put(PetContract.PetEntry.COLUMN_PET_GENDER,unit.getPetGender());
                values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT,unit.getPetWeight());
                db.update(PetContract.PetEntry.TABLE_NAME, values, "_id = ?", new String[]{String.valueOf(localUnit.getId())});
            }
        }
        Intent intent = new Intent(EditorActivity.this, CatalogActivity.class);
        startActivity(intent);
    }
    private void delitePet(PetUnit unit){
        if (localUnit.getId() != 0){
            PetDbHelper dbHelper = new PetDbHelper(this);
            try (SQLiteDatabase db = dbHelper.getWritableDatabase()){
                db.delete(PetContract.PetEntry.TABLE_NAME, "_id = ?", new String[]{String.valueOf(unit.getId())});
            }

            Intent intent = new Intent(EditorActivity.this, CatalogActivity.class);
            startActivity(intent);
        }
    }
    private class DBUpdateTread extends Thread{
        private PetUnit refractUnit;

        public DBUpdateTread(PetUnit newUnit) {
            this.refractUnit = newUnit;
        }

        @Override
        public void run(){
            updatePet(refractUnit);
        }

    }


}