package com.nosebite.matrice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Main Activity of the Application.
 * Contains most of the Fragments via a NavHostFragment.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private GoogleSignInAccount signedInAccount;
    private String playerId;

    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Signing in silently on Resume()");
        signInSilently();
    }

    private void signInSilently() {
        GoogleSignInOptions signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Log.d(TAG, "Last signed in account: " + account);
        if(GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            signedInAccount = account;
            Log.d(TAG, "Found last signed account:" + account);
        }
        else {
            GoogleSignInClient signInClient = GoogleSignIn.getClient(this, signInOptions);
            signInClient.silentSignIn()
                    .addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                        @Override
                        public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                            if(task.isSuccessful()) {
                                signedInAccount = task.getResult();
                                Log.d(TAG, "Successfully signed in silently.");
                            }
                            else {
                                signedInAccount = null;
                                Log.d(TAG, "Failed silent sign in, calling Sign in Intent. Exception: " + task.getException().getMessage());
                                startSignInIntent();
                            }
                        }
                    });
        }
    }

    public void startSignInIntent() {
        GoogleSignInClient signInClient = GoogleSignIn
                .getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) {
                signedInAccount = result.getSignInAccount();
                Log.d(TAG, "Succesfully signed in actively.");
            }
            else {
                signedInAccount = null;
                String message = result.getStatus().getStatusMessage();
                if(message == null || message.isEmpty()) {
                    message = getString(R.string.sign_in_other_error);
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
                Log.d(TAG, "Sign in failed.");
            }
        }
    }

    public GoogleSignInAccount getSignedInAccount() {
        return signedInAccount;
    }

    public String getPlayerId() {
        return playerId;
    }

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

    public void signOut() {
        GoogleSignInClient signInClient = GoogleSignIn
                .getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), getString(R.string.signed_out_message), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
