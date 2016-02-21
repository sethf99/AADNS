package hacksquad.carcrash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static CrashService crashService;
    boolean on = false;
    static  RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File f = new File(getFilesDir(),"people.txt");
        if(!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {

            final EditText text = (EditText) findViewById(R.id.editText);
            text.setText(PersonInteraction.loadPeople(f));

        } catch (IOException e) {
            e.printStackTrace();
        }

     layout = (RelativeLayout) findViewById(R.id.layo);

        final Button button2 = (Button) findViewById(R.id.button);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText text = (EditText) findViewById(R.id.editText);
                text.getText().toString();
                try {
                    PersonInteraction.setPeople(text.getText().toString(),openFileOutput("people.txt", Context.MODE_PRIVATE));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        Intent intent = new Intent(this, CrashService.class);
        startService(intent);
        CrashService.mainActivity=this;
        final Button button = (Button) findViewById(R.id.ON_OFF);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (on) {
                    button.setText(R.string.TurnsOn);
                    crashService.swapOnOff();
                    on = !on;
                } else {
                    button.setText(R.string.TurnsOff);
                    crashService.swapOnOff();
                    on = !on;

                }
            }
        });
    }


    }

