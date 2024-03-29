package ru.mineradio.helianapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class IdSearch  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_search);

        String[] tagsFound = { "ID32532", "ID34732", "ID32518", "ID32511", "ID39812"};;
        // find list
        ListView lvTagIdList = (ListView) findViewById(R.id.tagIdList);
        // create adaptor
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, tagsFound);
        //assign the adapter to the list
        lvTagIdList.setAdapter(adapter);
    }


}
