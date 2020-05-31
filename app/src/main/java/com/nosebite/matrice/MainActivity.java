package com.nosebite.matrice;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Main Activity of the Application.
 * Contains most of the Fragments via a NavHostFragment.
 */
public class MainActivity extends AppCompatActivity {

    /* TAG used for debugging */
    private static final String TAG = "MainActivity";

    /* Stores the actual account which the user is signed in. */
    private GoogleSignInAccount signedInAccount;
    private String playerId;

    /* Constant value for Sign In Intent */
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(GoogleSignIn.getLastSignedInAccount(this) != null) {
            GamesClient gamesClient = Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this));
            gamesClient.setViewForPopups(findViewById(android.R.id.content));
            gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        signInSilently();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Attempts to sign in the user silently.
     * If this is not possible starts interactive sign in.
     */
    private void signInSilently() {
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            signedInAccount = account;
        }
        else {
            GoogleSignInClient signInClient = GoogleSignIn.getClient(this, signInOptions);
            signInClient.silentSignIn()
                    .addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                        @Override
                        public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                            if(task.isSuccessful()) {
                                signedInAccount = task.getResult();
                            }
                            else {
                                signedInAccount = null;
                                startSignInIntent();
                            }
                        }
                    });
        }
    }

    /**
     * Starts interactive sign in.
     */
    public void startSignInIntent() {
        GoogleSignInClient signInClient = GoogleSignIn
                .getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        Toast.makeText(getApplicationContext(), getString(R.string.sign_in_process), Toast.LENGTH_SHORT).show();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    /**
     * Listens to Sign in Activity result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) {
                signedInAccount = result.getSignInAccount();
            }
            else {
                signedInAccount = null;
                String message = result.getStatus().getStatusMessage();
                if(message == null || message.isEmpty()) {
                    message = getString(R.string.sign_in_other_error);
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
    }

    public GoogleSignInAccount getSignedInAccount() {
        return signedInAccount;
    }
    public String getPlayerId() {
        return playerId;
    }
    public boolean playerIsSignedIn() {
        return signedInAccount != null;
    }

    /**
     * Runs asynchronous function to get the id of the currently signed in user.
     */
    public void requirePlayerId() {
        PlayersClient playerClient = Games.getPlayersClient(this, signedInAccount);
        playerClient.getCurrentPlayerId().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()) {
                    playerId = task.getResult();
                }
                else {
                    playerId = null;
                }
            }
        });
    }

    /**
     * Signs out current user.
     */
    public void signOut() {
        GoogleSignInClient signInClient = GoogleSignIn
                .getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), getString(R.string.signed_out_message), Toast.LENGTH_SHORT).show();
            }
        });
        signedInAccount = null;
        playerId = null;
    }
}
