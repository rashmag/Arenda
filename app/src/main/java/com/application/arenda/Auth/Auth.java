package com.application.arenda.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.application.arenda.Model.ModelAll;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.arenda.MainActivity;
import com.application.arenda.R;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;

public class Auth extends AppCompatActivity {
    private TextInputEditText login, password,userName;
    private TextInputLayout userNameTextInputLayout;
    private Button enter;
    private ImageView facebookBtn,googleBtn;
    private FirebaseAuth firebaseAuth;
    private TextView swithAuth, forgot_password;
    private ModelAll modelAuth;
    private boolean flagSwithAuth;
    private static final String TAG = "SignInActivity";
    private Fragment selectedFragment;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        FacebookSdk.sdkInitialize(Auth.this);
        mAuth = FirebaseAuth.getInstance();
        getWindow().setStatusBarColor(getResources().getColor(R.color.fragment_white));
        //Инициализируем
        init();
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Auth.this, ResetPassword.class));
            }
        });
        modelAuth = new ModelAll();
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        userNameTextInputLayout.setVisibility(View.GONE);
        swithAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flagSwithAuth) {
                    userNameTextInputLayout.setVisibility(View.VISIBLE);
                    flagSwithAuth = true;
                    swithAuth.setText("Ввойти");
                } else {
                    userNameTextInputLayout.setVisibility(View.GONE);
                    flagSwithAuth = false;
                    swithAuth.setText("Зарегестрироваться");
                }
            }
        });
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(login.getText().toString())) {
                    login.setError("Введите логин");
                } else if (TextUtils.isEmpty(password.getText().toString())) {
                    password.setError("Введите пароль");
                } else if (TextUtils.isEmpty(userName.getText().toString()) && flagSwithAuth) {
                    userName.setError("Введите имя");
                } else {
                    loginSignUpUser(login.getText().toString().trim(),
                            password.getText().toString().trim(), userName.getText().toString().trim());
                }
            }
        });
        mCallbackManager = CallbackManager.Factory.create();
        //Providers
        //Google
        providerGoogle();
        //Facebook
        providerFacebook();


        //Чекаем авторизованны ли мы в этом приложении, если да то сразу же перекидывает на
        // другой экран
        if (firebaseAuth.getCurrentUser() != null) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
//            startActivity(new Intent(Auth.this, MainActivity.class));
//            selectedFragment = new User();
//            getSupportFragmentManager().beginTransaction().addToBackStack(null).
//                    replace(R.id.fragmentContainer,
//                            selectedFragment).commit();
        }

    }

    private void providerGoogle() {
        GoogleSignInOptions googleSignInOptions = new
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1041953028709-8hmopd4gt4t77fovbl1b6lj0tbrer8t9.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(Auth.this, googleSignInOptions);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, 100);
            }
        });
    }

    private void providerFacebook() {
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(Auth.this,
                        Arrays.asList("email","public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("TAG", "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("TAG", "facebook:onError", error);

                    }
                });
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(Auth.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(Auth.this, MainActivity.class));
        } else {
            Toast.makeText(this, "Error Facebook", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginSignUpUser(String email, String password, String userName) {

        if (!flagSwithAuth) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Auth.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                startActivity(new Intent(Auth.this, MainActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.d(TAG, "signInWithEmail:failure2" + task.getException(), task.getException());
//                            updateUI(null);
                                // ...
                            }

                            // ...
                        }
                    });
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                // Sign in success, update UI with the signed-in user's information
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child(userId);
                                if (task.isSuccessful()) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("userName", userName);
                                    hashMap.put("id", userId);
                                    hashMap.put("search", userName.toLowerCase());
                                    reference.setValue(hashMap);
                                }
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                startActivity(new Intent(Auth.this, MainActivity.class));
//                            updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(Auth.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            }
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Auth.this, MainActivity.class));
    }

    private void init() {
        userNameTextInputLayout = findViewById(R.id.userNameTextInputLayout);
        userName = findViewById(R.id.userName);
        forgot_password = findViewById(R.id.forgot_password);
        swithAuth = findViewById(R.id.swithAuth);
        googleBtn = findViewById(R.id.googleBtn);
        facebookBtn = findViewById(R.id.facebookBtn);
        enter = findViewById(R.id.enter);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn
                    .getSignedInAccountFromIntent(data);
            if (signInAccountTask.isSuccessful()) {
                String s = "Google sign in successfyl";

                displayToast(s);
                try {
                    GoogleSignInAccount googleSignInAccount = signInAccountTask
                            .getResult(ApiException.class);
                    if (googleSignInAccount != null) {
                        AuthCredential authCredential = GoogleAuthProvider
                                .getCredential(googleSignInAccount
                                        .getIdToken(), null);
                        firebaseAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(Auth.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(Auth.this, MainActivity.class)
                                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            displayToast("Firebase аутентификация прошла успешно");
                                            startActivity(intent);
                                        }
                                    }
                                });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void displayToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}