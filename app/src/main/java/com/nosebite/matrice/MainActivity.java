package com.nosebite.matrice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PlayGamesAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

/**
 * Main Activity of the Application.
 * Contains most of the Fragments via a NavHostFragment.
 */
public class MainActivity extends AppCompatActivity {

    /* TAG used for debugging */
    private static final String TAG = "MainActivity";

    /* Stores the actual account which the user is signed in. */
    private GoogleSignInAccount signedInAccount;

    /* Authenctication with Firebase */
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    /* The Firebase Realtime Database to save the game data */
    private static FirebaseDatabase firebaseDatabase;

    /* Game Clients for Play Games administration */
    private AchievementsClient achievementsClient;
    private LeaderboardsClient leaderboardsClient;

    /* Constant value for Sign In Intent */
    public static final int RC_SIGN_IN = 1000;
    public static final int RC_ACHIEVEMENTS_UI = 2000;
    public static final int RC_LEADERBOARD_UI = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(GoogleSignIn.getLastSignedInAccount(this) != null) {
            GamesClient gamesClient = Games.getGamesClient(this, GoogleSignIn.getLastSignedInAccount(this));
            gamesClient.setViewForPopups(findViewById(android.R.id.content));
            gamesClient.setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        }
        /* Get Instance of Firebase Realtime Database and sets offline persistence */
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            enablePersistence();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        signInSilently();
        if(getSignedInAccount() != null) {
            initializePlayGamesClients();
        }
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
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestServerAuthCode(getString(R.string.default_web_client_id))
                .build();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
            signedInAccount = account;
            user = firebaseAuth.getCurrentUser();
            Log.d(TAG, "Found last signed in accounts: " + account + user);
        }
        else {
            GoogleSignInClient signInClient = GoogleSignIn.getClient(this, signInOptions);
            signInClient.silentSignIn()
                    .addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                        @Override
                        public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                            if(task.isSuccessful()) {
                                signedInAccount = task.getResult();
                                Log.d(TAG, "Logged in with Play Games: " + signedInAccount);
                                firebaseAuthWithPlayGames(signedInAccount);
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
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestServerAuthCode(getString(R.string.default_web_client_id))
                .build();
        GoogleSignInClient signInClient = GoogleSignIn
                .getClient(this, signInOptions);
        Intent intent = signInClient.getSignInIntent();
        Toast.makeText(getApplicationContext(), getString(R.string.sign_in_process), Toast.LENGTH_SHORT).show();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    /**
     * Authenticates player signed in with a Play Account to Firebase
     * @param account The Google Play Account with which the player is signed in.
     */
    private void firebaseAuthWithPlayGames(@NotNull GoogleSignInAccount account) {

        AuthCredential credential = PlayGamesAuthProvider.getCredential(account.getServerAuthCode());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            user = firebaseAuth.getCurrentUser();
                            Log.d(TAG, "Logged in with Firebase: " + user);
                        }
                        else {
                            user = null;
                            Toast.makeText(getApplicationContext(), getString(R.string.sign_in_other_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                Log.d(TAG, "Logged in with Play Games: " + signedInAccount);
                firebaseAuthWithPlayGames(signedInAccount);
                initializePlayGamesClients();
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
    public FirebaseUser getUser() {
        return user;
    }
    public String getUserID() {
        return user.getUid();
    }
    public AchievementsClient getAchievementsClient() {
        return achievementsClient;
    }
    public LeaderboardsClient getLeaderboardsClient() {
        return leaderboardsClient;
    }
    public boolean playerIsSignedIn() {
        return (signedInAccount != null) && (user != null);
    }
    private void enablePersistence() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
    private void initializePlayGamesClients() {
        /* Get Instance of Play Games Achievements CLient */
        if (achievementsClient == null)
            achievementsClient = Games.getAchievementsClient(this, getSignedInAccount());
        /* Get Instance of Play Games Leaderboards CLient */
        if (leaderboardsClient == null)
            leaderboardsClient = Games.getLeaderboardsClient(this, getSignedInAccount());
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
        firebaseAuth.signOut();
        signedInAccount = null;
        user = null;
    }
}
