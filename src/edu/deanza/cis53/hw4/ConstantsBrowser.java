

package edu.deanza.cis53.hw4;

import android.app.Activity;
import android.os.Bundle;

public class ConstantsBrowser extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getFragmentManager().findFragmentById(android.R.id.content)==null) {
      getFragmentManager().beginTransaction()
                                 .add(android.R.id.content,
                                      new ConstantsFragment()).commit();
    }
  }
}
