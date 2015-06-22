

package edu.deanza.cis53.hw4;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

import java.util.jar.Attributes;

public class ConstantsFragment extends ListFragment implements
    DialogInterface.OnClickListener , LoaderCallbacks<Cursor>
{
  private DatabaseHelper db=null;
  private Cursor current=null;

  // Implementing The New DataBase Stuff...
  private SimpleCursorAdapter stuffAdapter;
  private static final int STUFF_LOADER = 0;
  private String mLocations;

  private static final String[] STUFF_COLUMNS =
          {
          // In this case the id needs to be fully qualified with a table name, since
          // the content provider joins the location & weather tables in the background
          // (both have an _id column)
          // On the one hand, that's annoying.  On the other, you can search the weather table
          // using the location set by the user, which is only in the Location table.
          // So the convenience is worth it.
          ConstantsContract.NameEntry.TABLE_NAME + "." + ConstantsContract.NameEntry._ID,
          ConstantsContract.ValueEntry.COLUMN_VALUE
  };

  public static final int COL_NAME_ID = 0;
  public static final int COL_VAL_ID = 1;

  
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    
    setHasOptionsMenu(true);
    setRetainInstance(true);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    SimpleCursorAdapter adapter=
        new SimpleCursorAdapter(getActivity(), R.layout.row,
            current, new String[] {
            DatabaseHelper.TITLE,
            DatabaseHelper.VALUE },
            new int[] { R.id.title, R.id.value },
            0);

    setListAdapter(adapter);

    if (current==null)
    {
      db=new DatabaseHelper(getActivity());
      new LoadCursorTask().execute();
    }
  }

  @Override
  public void onDestroy()
  {
    super.onDestroy();

    ((CursorAdapter)getListAdapter()).getCursor().close();
    db.close();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
  {
    inflater.inflate(R.menu.actions, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.add) {
      add();
      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  private void add() {
    LayoutInflater inflater=getActivity().getLayoutInflater();
    View addView=inflater.inflate(R.layout.add_edit, null);
    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

    builder.setTitle(R.string.add_title).setView(addView)
           .setPositiveButton(R.string.ok, this)
           .setNegativeButton(R.string.cancel, null).show();
  }

  public void onClick(DialogInterface di, int whichButton) {
    ContentValues values=new ContentValues(2);
    AlertDialog dlg=(AlertDialog)di;
    EditText title=(EditText)dlg.findViewById(R.id.title);
    EditText value=(EditText)dlg.findViewById(R.id.value);

    // Fill in your code

    values.put(DatabaseHelper.TITLE, title.getText().toString());
    values.put(DatabaseHelper.VALUE, value.getText().toString());
    new InsertTask().execute(values);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return null;
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {

  }

  abstract private class BaseTask<T> extends AsyncTask<T, Void, Cursor>
  {
    @Override
    public void onPostExecute(Cursor result)
    {
        ((CursorAdapter)getListAdapter()).changeCursor(result);
    }

    protected Cursor doQuery()
    {
      Cursor result= null;
      String query=
              String.format("SELECT rowid AS _id, %s, %s FROM %s ORDER BY %s",
                      DatabaseHelper.TITLE, DatabaseHelper.VALUE,
                      DatabaseHelper.TABLE, DatabaseHelper.TITLE);

      return(db.getReadableDatabase().rawQuery(query, null));


    }
  }

  private class LoadCursorTask extends BaseTask<Void> {
    @Override
    protected Cursor doInBackground(Void... params) {
      return(doQuery());
    }
  }

  private class InsertTask extends BaseTask<ContentValues> {
    @Override
    protected Cursor doInBackground(ContentValues... values) {
      db.getWritableDatabase().insert(DatabaseHelper.TABLE,
                                      DatabaseHelper.TITLE, values[0]);
      return(doQuery());
    }
  }
}
