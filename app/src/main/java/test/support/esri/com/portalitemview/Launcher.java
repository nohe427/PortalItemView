package test.support.esri.com.portalitemview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Launcher extends AppCompatActivity implements LogInFragment.OnFragmentInteractionListener,
PortalViewFragment.OnFragmentInteractionListener, AnonymousPortalFragment.OnFragmentInteractionListener{

    private Button signInButton;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init firebase analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent("appopened", null);

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
                Bundle bundle = new Bundle();
                mFirebaseAnalytics.logEvent("AnonymousSignOn", bundle);
                startActivity(annonIntent);
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
