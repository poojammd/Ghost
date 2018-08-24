/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private TextView gameStatusView;
    private TextView ghostTextView;
    Button Restart;
    Button Challenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        gameStatusView = (TextView) findViewById(R.id.gameStatus);
        ghostTextView = (TextView) findViewById(R.id.ghostText);
        Restart=(Button) findViewById(R.id.button2);
        Challenge= (Button) findViewById(R.id.button);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            dictionary = new SimpleDictionary(inputStream);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }

        onStart(null);
        Restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStart(view);
            }
        });

        Challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ghostText = ghostTextView.getText().toString();

                if (ghostText.length() >= 4 && dictionary.isWord(ghostText)) {
                    gameStatusView.setText("User Won");
                } else {
                    String k = dictionary.getAnyWordStartingWith(ghostText);
                    Log.d("Ghost", "Word starting with " + ghostText + "is " + k);

                    if(k == null) {
                        gameStatusView.setText("User won");
                    } else {
                        gameStatusView.setText("Computer won | Possible word - "+k);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
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



    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {

        String ghostText = ghostTextView.getText().toString();
        Log.d("Ghost", "Word: " + ghostText);
        if (ghostTextView.length() >= 4 && dictionary.isWord(ghostText))
            gameStatusView.setText("Computer Won");
        else {
            String k = dictionary.getAnyWordStartingWith(ghostText);
            Log.d("Ghost", "Word starting with " + ghostText + " is " + k);

            if (k == null) {
                gameStatusView.setText("Computer Won");
            } else {
                ghostTextView.append(k.charAt(ghostText.length()) + "");
                userTurn = true;
                gameStatusView.setText(USER_TURN);
            }
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (userTurn) {
            userTurn = false;

            char k = (char) event.getUnicodeChar();
            if (Character.isLetter(k)) {
                ghostTextView.append(k+ "");
                userTurn = false;

                computerTurn();

                return true;
            }
        }

        return super.onKeyUp(keyCode, event);


    }
}
