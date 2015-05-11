package com.example.diego.hearthstone;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;


public class CartaPersonalizadaActivity extends ActionBarActivity {

    private static final int TAKE_PHOTO = 1;
    private static final int LOAD_PHOTO = 2;
    private static final String SAVED_PHOTO = "savedphoto";
    private static final String LIFE_KEY = "lifekey";
    private static final String DAMAGE_KEY = "damagekey";
    private static final String COST_KEY = "costkey";

    int charsPerLine;
    Bitmap imageBase;
    Bitmap imagePhoto;
    ImageView image;
    int cost;
    int life;
    int damage;
    String name;
    String description;

    private Button buttonRotate;

    private DrawerLayout drawerLayout;
    private ListView lvDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout layoutDelDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carta_personalizada);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
        }

        //Codigo para el drawer
        layoutDelDrawer = (LinearLayout) findViewById(R.id.layoutDelDrawer);
        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        lvDrawerLayout= (ListView) findViewById(R.id.left_drawer);
        lvDrawerLayout.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.NavigationDrawerValues)));


        mDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);

        lvDrawerLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        Intent i=new Intent(CartaPersonalizadaActivity.this,ActivityCollection.class);
                        startActivity(i);
                        break;
                    case 1:
                        break;
                    case 2:
                        Intent i2=new Intent(CartaPersonalizadaActivity.this, HeroSelectionActivity.class);
                        startActivity(i2);
                        break;
                    case 3:
                        Intent i3=new Intent(CartaPersonalizadaActivity.this,MazosPredefinidosActivity.class);
                        startActivity(i3);
                        break;
                }
                drawerLayout.closeDrawer(layoutDelDrawer);
            }
        });

        //Carga de las dos imagenes
        imageBase = BitmapFactory.decodeResource(getResources(), R.drawable.base);
        imagePhoto = null;

        //Recuperacion de la zona de pintado
        image = (ImageView)findViewById(R.id.imageID);

        //Inicializacion de valores por defecto
        description = "";
        name = "";
        cost = 0;
        life = 0;
        damage = 0;

        //Configuracion de los pickers  y sus variables
        Spinner spCost= (Spinner) findViewById(R.id.spinnerCost);
        Spinner spDamage= (Spinner) findViewById(R.id.spinnerDamage);
        Spinner spLife= (Spinner) findViewById(R.id.spinnerLife);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.LifeDamage));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spCost.setAdapter(adapter);
        spDamage.setAdapter(adapter);
        spLife.setAdapter(adapter);

        spCost.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cost = position;
                UpdateImage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spDamage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                damage = position;
                UpdateImage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spLife.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                life = position;
                UpdateImage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Carga de datos tras recargar actividad
        if(savedInstanceState!=null){
            life = savedInstanceState.getInt(LIFE_KEY);
            spLife.setSelection(life);
            cost = savedInstanceState.getInt(COST_KEY);
            spCost.setSelection(cost);
            damage = savedInstanceState.getInt(DAMAGE_KEY);
            spDamage.setSelection(damage);
            imagePhoto = savedInstanceState.getParcelable(SAVED_PHOTO);
        }

        //Actualizamos la imagen
        UpdateImage();

        //Boton para tomar una foto nueva
        Button buttonTakePhoto = (Button) findViewById(R.id.buttonNewPhoto);
        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });

        //Boton para cargar una foto antígua
        Button buttonLoadPhoto = (Button)findViewById(R.id.buttonLoadPhoto);
        buttonLoadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, LOAD_PHOTO);
            }
        });
        //boton para rotar
        buttonRotate = (Button)findViewById(R.id.buttonRotate);
        //Si no tenemos imagen hacemos invisible el boton de rotar
        if(imagePhoto==null)
            buttonRotate.setEnabled(false);

        buttonRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePhoto = resizeImage(imagePhoto, imagePhoto.getWidth(), imagePhoto.getHeight(), true);
                UpdateImage();

            }
        });
        //Boton para salvar la foto
        Button buttonSavePhoto = (Button)findViewById(R.id.buttonSavePhoto);
        buttonSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image.setDrawingCacheEnabled(true);
                saveImageToExternalStorage(image.getDrawingCache());
                Toast.makeText(getApplicationContext(), "Imagen guardada", Toast.LENGTH_LONG).show();
            }
        });

        //Recuperacion de la descripcion e implementacion del listener
        EditText eTDescription = (EditText)findViewById(R.id.editTextDescription);
        int maxLength = charsPerLine*5;
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(maxLength);
        eTDescription.setFilters(filters);
        eTDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                description = s.toString();
                UpdateImage();
            }
        });

        //Recuperacion del nombre e implementacion del listener y los filtros
        EditText eTName = (EditText)findViewById(R.id.editTextName);
        maxLength = charsPerLine;
        filters = new InputFilter[2];
        filters[0] = new InputFilter.LengthFilter(maxLength);
        filters[1]= new InputFilter.AllCaps();
        eTName.setFilters(filters);
        eTName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                name = s.toString();
                UpdateImage();
            }
        });



    }

    private void saveImageToExternalStorage(Bitmap finalBitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/HSCustomCards");
        myDir.mkdirs();
        String fname = "Image-" + name + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        //Bitmap para recoger la fotografia
        Bitmap newPhoto = null;
        //Codigo para recuperar la foto de la camara
        if (requestCode == TAKE_PHOTO && resultCode== RESULT_OK && intent != null){
            Bundle extras = intent.getExtras();

            newPhoto = (Bitmap) extras.get("data");
        }
        //Codigo para recuperar la foto de la galeria
        else if(requestCode == LOAD_PHOTO && resultCode== RESULT_OK && intent != null){
            Uri selectedImage = intent.getData();
            InputStream is;
            try {
                is = getContentResolver().openInputStream(selectedImage);
                BufferedInputStream bis = new BufferedInputStream(is);
                newPhoto = BitmapFactory.decodeStream(bis);
            } catch (FileNotFoundException e) {}
        }
        //Codigo para reeescalar la imagen y unirla a la base
        if(resultCode == RESULT_OK) {
            imagePhoto = newPhoto;
            //Actualizacion de la imagen en la actividad
            UpdateImage();
            //Se hace visible el boton para rotar, pues ya hay foto
            buttonRotate.setEnabled(true);

        }
    }

    public void UpdateImage(){
        //Crear un nuevo bitmap
        Bitmap bitmapCreate = Bitmap.createBitmap(imageBase.getWidth(), imageBase.getHeight(), Bitmap.Config.ARGB_8888);
        //Creamos un nuevo canvas para pintar en el a partir del bitmap
        Canvas comboImage = new Canvas(bitmapCreate);
        //Pintamos la imagen con la foto
        if(imagePhoto!=null) {
            Bitmap newResizedPhoto = resizeImage(imagePhoto, imageBase.getWidth() * 2 / 3, imageBase.getHeight() / 2, false);
            int posX = imageBase.getWidth() / 2 - newResizedPhoto.getWidth() / 2;
            int posY = imageBase.getHeight() / 3 - newResizedPhoto.getHeight() / 2;
            comboImage.drawBitmap(newResizedPhoto, posX, posY, null);
            imagePhoto = newResizedPhoto;
        }
        comboImage.drawBitmap(imageBase, 0, 0, null);

        //Obtenemos los numeros y los pintamos en sus posiciones
        Bitmap imageDamage = getNumberBitmap(damage);
        comboImage.drawBitmap(imageDamage, imageDamage.getWidth()*2/5,imageBase.getHeight()-imageDamage.getHeight()*3/2, null);
        Bitmap imageLife = getNumberBitmap(life);
        comboImage.drawBitmap(imageLife, imageBase.getWidth()-imageLife.getWidth()*8/5,imageBase.getHeight()-imageLife.getHeight()*3/2, null);
        Bitmap imageCost = getNumberBitmap(cost);
        comboImage.drawBitmap(imageCost, imageCost.getWidth()*2/5,imageCost.getHeight()*6/5, null);

        //Pintado de la descripcion
        Paint pincel = new Paint();
        pincel.setARGB(255, 150, 150, 150);
        int DP_VALUE = 12; //12dp
        int pixel= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP_VALUE, getResources().getDisplayMetrics());
        charsPerLine = imageBase.getWidth()*3/5/(pixel*2/3);
        int charsPerLineDescription = imageBase.getWidth()*3/5/(pixel*3/5);
        pincel.setTextSize(pixel);
        pincel.setTypeface(Typeface.SERIF);

        if(description.length()<=charsPerLineDescription)
            comboImage.drawText(description,imageBase.getWidth()/6,imageBase.getHeight()*7/10,pincel);
        else {
            int i;
            for (i = 0; i < description.length() / charsPerLineDescription; i++) {
                comboImage.drawText(description, i * charsPerLineDescription, (i + 1) * charsPerLineDescription, imageBase.getWidth() / 6, imageBase.getHeight() * 7 / 10 + (i * pixel), pincel);
            }
            comboImage.drawText(description, i * charsPerLineDescription, description.length(), imageBase.getWidth() / 6, imageBase.getHeight() * 7 / 10 + (i * pixel), pincel);
        }

        //Pintado del nombre
        pincel.setARGB(255, 255, 255, 255);
        Path path = new Path();
        path.moveTo(imageBase.getWidth()/6,imageBase.getHeight()*60/100);

        path.cubicTo(imageBase.getWidth()*1/3,imageBase.getHeight()*58/100,
                imageBase.getWidth()*1/3,imageBase.getHeight()*58/100,
                imageBase.getWidth()*1/2,imageBase.getHeight()*56/100);

        path.cubicTo(imageBase.getWidth()*2/3,imageBase.getHeight()*56/100,
                imageBase.getWidth()*2/3,imageBase.getHeight()*56/100,
                imageBase.getWidth()*5/6,imageBase.getHeight()*58/100);

        comboImage.drawTextOnPath(name,path,(charsPerLine/2-name.length()/2)*pixel,0,pincel);

        //Cambiamos la imagen del imageView
        image.setImageBitmap(bitmapCreate);
    }

    public Bitmap getNumberBitmap(int number){
        switch (number){
            case 0:
                return BitmapFactory.decodeResource(getResources(),R.drawable.cero);
            case 1:
                return BitmapFactory.decodeResource(getResources(),R.drawable.uno);
            case 2:
                return BitmapFactory.decodeResource(getResources(),R.drawable.dos);
            case 3:
                return BitmapFactory.decodeResource(getResources(),R.drawable.tres);
            case 4:
                return BitmapFactory.decodeResource(getResources(),R.drawable.cuatro);
            case 5:
                return BitmapFactory.decodeResource(getResources(),R.drawable.cinco);
            case 6:
                return BitmapFactory.decodeResource(getResources(),R.drawable.seis);
            case 7:
                return BitmapFactory.decodeResource(getResources(),R.drawable.siete);
            case 8:
                return BitmapFactory.decodeResource(getResources(),R.drawable.ocho);
            case 9:
                return BitmapFactory.decodeResource(getResources(),R.drawable.nueve);
            default:
                return BitmapFactory.decodeResource(getResources(),R.drawable.cero);
        }
    }

    public Bitmap resizeImage(Bitmap in, int w, int h,boolean rotate) {
        try {
            int width = in.getWidth();
            int height = in.getHeight();
            int newWidth = w;
            int newHeight = h;

            Bitmap imageR = in;
            if (rotate) {
                Matrix matrixRotate = new Matrix();
                matrixRotate.postRotate(90);
                imageR = Bitmap.createBitmap(in, 0, 0, width, height, matrixRotate, true);
                int temp = width;
                width = height;
                height = temp;
            }
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            imageR = Bitmap.createBitmap(imageR, 0, 0, width, height, matrix, true);

            return imageR;
        }
        catch (Exception e){
            return in;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_PHOTO,imagePhoto);
        outState.putInt(LIFE_KEY,life);
        outState.putInt(DAMAGE_KEY,damage);
        outState.putInt(COST_KEY, cost);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOnline()){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
