package test.support.esri.com.portalitemview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button resetButton;
    Button loginButton;
    EditText txtUsername;
    EditText txtPassword;
    String username;
    String password;
    Intent i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resetButton = (Button)findViewById(R.id.resetbutton);
        loginButton = (Button)findViewById(R.id.loginbutton);
        txtUsername = (EditText)findViewById(R.id.portalusername);
        txtPassword = (EditText)findViewById(R.id.portalpassword);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtUsername.setText("");
                txtPassword.setText("");
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToPortal();
            }
        });




    }

    private void loginToPortal() {
        //check for empty content

        if(txtUsername.getText().toString().length() == 0 && txtPassword.getText().toString().length() == 0){
           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   Toast.makeText(getApplicationContext(), "Please provide a password and username.", Toast.LENGTH_LONG).show();
               }
           });
            return;
        }

         username = txtUsername.getText().toString().trim();
         password = txtPassword.getText().toString().trim();
        i = new Intent(getApplicationContext(), Navigator.class);
        i.putExtra("username", username);
        i.putExtra("password", password);
        startActivity(i);
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
}
