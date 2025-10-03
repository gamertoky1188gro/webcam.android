package cyber.code.master.webcam;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "QEMU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Run in background
        new Thread(() -> {
            // 1. Copy all assets (including subfolders)
            BinaryHelper.copyAllAssets(this);

//            try {
//                // 2. Run QEMU binary
//                Process process = BinaryHelper.runBinary(
//                        this,
//                        "qemu-system-aarch64",   // change if needed
//                        "-version"
//                );
//
//                BufferedReader reader = new BufferedReader(
//                        new InputStreamReader(process.getInputStream())
//                );
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    Log.d(TAG, line);
//                }
//
//                process.waitFor();
//                Log.d(TAG, "QEMU exited with code " + process.exitValue());
//
//            } catch (Exception e) {
//                Log.e(TAG, "Error running QEMU", e);
//            }
        }).start();

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        Python py = Python.getInstance();
        py.getModule("hello").callAttr("main");
    }
}
