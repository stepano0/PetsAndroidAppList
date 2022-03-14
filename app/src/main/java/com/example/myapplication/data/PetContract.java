package com.example.myapplication.data;

import android.provider.BaseColumns;

public final class PetContract {

    //Внутренний класс, определяющий постоянные значения для таблицы базы данных животных.
    //Кажд ая запись в таблице соответствует одному животному.
    public static final class PetEntry implements BaseColumns {

        //Название таблицы
        public final static String TABLE_NAME = "pets";

        //Столбцы в таблицы.
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PET_NAME = "name";
        public final static String COLUMN_PET_BREED = "breed";
        public final static String COLUMN_PET_GENDER = "gender";
        public final static String COLUMN_PET_WEIGHT = "weight";


        //значения для пола питомца.
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

    }
}

