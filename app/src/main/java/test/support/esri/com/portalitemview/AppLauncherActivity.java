package test.support.esri.com.portalitemview;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AppLauncherActivity extends AppCompatActivity implements LogInFragment.OnFragmentInteractionListener,
PortalViewFragment.OnFragmentInteractionListener{

    private Button signInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_launcher);

        signInButton = (Button) findViewById(R.id.sign_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction().add(R.id.fullscreen_layout,
                        new LogInFragment(), "LoginFragment");
                fragTransaction.commit();
                signInButton.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
