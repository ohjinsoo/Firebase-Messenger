package edu.illinois.finalproject.Chatrooms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import edu.illinois.finalproject.FirebaseAuth.RegisterActivity;
import edu.illinois.finalproject.R;

/**
 * Created by ohjin on 12/2/2017.
 *
 * Toolbar Guide: https://guides.codepath.com/android/using-the-app-toolbar
 */

public class MainMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        //Adds a toolbar for create chat and sign out functions.
        Toolbar mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);

        final RecyclerView mainLayout = (RecyclerView) findViewById(R.id.mainmenu_layout);
        MainMenuFirebaseInit mainMenuFirebaseInit = new MainMenuFirebaseInit(getApplicationContext(), mainLayout);
        mainMenuFirebaseInit.execute();
    }


    //Goes back to Android home screen, keeps app running.
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Context context = getApplicationContext();

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.createChatButton:
                Intent createChatIntent = new Intent(context, CreateChatActivity.class);
                context.startActivity(createChatIntent);

                return true;
            case R.id.signoutButton:
                FirebaseAuth.getInstance().signOut();
                Intent signOutIntent = new Intent(context, RegisterActivity.class);
                context.startActivity(signOutIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
