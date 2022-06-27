package com.mad.mid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get permission for storage access
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);

        //For version greater than Android 11 navigate to get access to all files in storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }

        //paths
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File names = new File(folder, "Resources/Country Names/names.txt");

        //making UI better
        this.setTitle("Country Names");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(83,49,29))); //Change title color
        this.getWindow().getDecorView().setBackgroundColor(Color.rgb(226,218,211));

        //Get data of file as one string
        String fileTxt = readFile(names);

        //Make array of country names
        ArrayList<String> countryNames = new ArrayList<>(Arrays.asList(fileTxt.split(",")));

        ListView listView = findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.my_listview, R.id.listViewText, countryNames);
        listView.setAdapter(adapter);
        listView.setDivider(null);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent shift = new Intent(MainActivity.this, CountryDesc.class);
                String currentCountry = ((TextView)view.findViewById(R.id.listViewText)).getText().toString();
                shift.putExtra("countryName", currentCountry); //Passing country name to next activity
                startActivity(shift);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String currentCountry = ((TextView)view.findViewById(R.id.listViewText)).getText().toString();

                //Alert dialog builder
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Delete " + currentCountry + "!");
                alertDialog.setMessage("Are you sure you want to delete " + currentCountry + "?");

                //Option 1
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        countryNames.remove(currentCountry);

                        //Re-write file with updated country names
                        FileWriter writer = null;
                        try {
                            writer = new FileWriter(names, false);
                            for(int j = 0; j < countryNames.size(); j++){
                                writer.write(countryNames.get(j));
                                if(j < countryNames.size()-1){
                                    writer.write(",");
                                }
                            }

                            //updating the adapter
                            adapter.remove(currentCountry);

                        }catch(IOException e){
                            e.printStackTrace();
                        }finally{
                            if(writer != null){
                                try{
                                    writer.close();
                                }catch (IOException er){
                                    er.printStackTrace();
                                }
                            }
                        }

                        //Deleting desc and flag files
                        File descFile = new File(folder, "Resources/Country Description/"+ currentCountry + ".txt");
                        File imgFile = new File(folder, "Resources/Country Flags/"+ currentCountry + ".png");

                        boolean deletedDesc = descFile.delete();
                        boolean deletedFlag = imgFile.delete();
                        if(deletedDesc && deletedFlag) {
                            Toast.makeText(MainActivity.this, "Successfully Deleted the Country and its Resources",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });


                //Option 2
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Toast.makeText(MainActivity.this, "Deletion Aborted!", Toast.LENGTH_LONG).show();
                    }
                });

                AlertDialog deleteAlert = alertDialog.create();
                deleteAlert.show();
                return true;
            }
        });
    }


    //Function to read file
    public String readFile(File file) {

        FileInputStream fin = null;

        try{
            String line;
            fin = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fin);
            BufferedReader br = new BufferedReader(isr);

            line = br.readLine();
            return line;
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