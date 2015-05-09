package com.example.diego.hearthstone;

import android.provider.BaseColumns;

/**
 * Created by blukstack on 31/03/2015.
 */
public class CartasManagerContract {

    public CartasManagerContract(){

    }

    public static abstract class Carta implements BaseColumns {
        public static final String TABLE_NAME = "Carta";
        public static final String COLUMN_NAME_NAME = "Nombre";
        public static final String COLUMN_NAME_CLASS = "Clase";
        public static final String COLUMN_NAME_OBTENIDA = "Obtenida";
        public static final String COLUMN_NAME_CANTIDAD = "Cantidad";
    }

    public static abstract class Carta_Mazo implements BaseColumns {
        public static final String TABLE_NAME = "Carta_Mazo";
        public static final String COLUMN_NAME_IDCARTA = "Id_carta";
        public static final String COLUMN_NAME_IDMAZO = "Id_mazo";
        public static final String COLUMN_NAME_CANTIDAD = "Cantidad";
    }

    public static abstract class Mazo implements BaseColumns {
        public static final String TABLE_NAME = "Mazo";
        public static final String COLUMN_NAME_NAME = "Nombre";
        public static final String COLUMN_NAME_PREDEFINIDO = "Predefinido";
        public static final String COLUMN_NAME_CLASS = "Clase";
    }

}
