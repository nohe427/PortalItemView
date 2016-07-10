package test.support.esri.com.portalitemview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Launcher extends AppCompatActivity implements LogInFragment.OnFragmentInteractionListener,
PortalViewFragment.OnFragmentInteractionListener, AnonymousPortalFragment.OnFragmentInteractionListener{

    private Button signInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_launcher);

        signInButton = (Button) findViewById(R.id.sign_button);
        final TextView anonymousAccess = (TextView)findViewById(R.id.use_without);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragTransaction = getSupportFragmentManager()
                        .beginTransaction().add(R.id.fullscreen_layout,
                        new LogInFragment(), "LoginFragment");
                fragTransaction.commit();
                signInButton.setVisibility(View.GONE);
            }
        });

        anonymousAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent annonIntent = new Intent(getApplicationContext(), PortalViewMain.class);
                annonIntent.putExtra("AnonymousAccess", "AnonAccess");
                startActivity(annonIntent);
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
