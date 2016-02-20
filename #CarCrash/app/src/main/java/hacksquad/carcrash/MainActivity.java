package hacksquad.carcrash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    static CrashService crashService;


    boolean on = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

