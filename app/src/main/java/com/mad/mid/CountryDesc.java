package com.mad.mid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class CountryDesc extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_desc);


        //Get country name
        Intent intent = getIntent();
        String countryName = intent.getStringExtra("countryName");


        //Making UI better
        this.setTitle(countryName);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(83,49,29)));
        this.getWindow().getDecorView().setBackgroundColor(Color.rgb(226,218,211));


        //Getting paths
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File descFile = new File(folder, "Resources/Country Description/"+ countryName + ".txt");
        File imgFile = new File(folder, "Resources/Country Flags/"+ countryName + ".png");


        //flag viewer
        ImageView flag = findViewById(R.id.flagImg);

        //changing imag to flag of country
        flag.setImageDrawable(Drawable.createFromPath(imgFile.toString()));

        //Reading fle
        String[] str = readFile(descFile);
        ArrayList<String> desc = new ArrayList<>(Arrays.asList(str));

        //populating listview with array adapter
        ListView listView = findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, desc);
        listView.setAdapter(adapter);
    }


    //Function to read file line by line
    public String[] readFile(File file){
        FileInputStream fin = null;

        try{
            String line;
            fin = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fin);
            BufferedReader br = new BufferedReader(isr);
            ArrayList<String> str = new ArrayList<>();

            while((line = br.readLine())!= null){
                str.add(line);
            }
            String[] arr = new String[str.size()];
            return str.toArray(arr);
        } catch (FileNotFoundException fe){
            fe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}