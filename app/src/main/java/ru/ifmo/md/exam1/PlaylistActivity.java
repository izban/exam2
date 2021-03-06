package ru.ifmo.md.exam1;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Random;

import ru.ifmo.md.exam1.provider.song.SongColumns;
import ru.ifmo.md.exam1.provider.song.SongCursor;


public class PlaylistActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    ArrayAdapter<Song> adapter;
    Random rng = new Random(58);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        adapter = new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_1);
        getLoaderManager().restartLoader(0, null, this);
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(PlaylistActivity.this, SongActivity.class);

                // sorry, I have no time to write good code
                Song song = adapter.getItem(i);
                intent.putExtra("song", song.song);
                intent.putExtra("artist", song.artist);
                intent.putExtra("year", Integer.toString(song.year));
                intent.putExtra("duration", song.duration);
                intent.putExtra("genres_mask", song.genres_mask);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, SongColumns.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (adapter == null) {
            adapter = new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_1);
        }
        adapter.clear();
        while (cursor.moveToNext()) {
            SongCursor itemCursor = new SongCursor(cursor);
            adapter.add(new Song(itemCursor));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter = null;
    }
}
