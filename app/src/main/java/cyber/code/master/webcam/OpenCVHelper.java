package cyber.code.master.webcam;

import org.opencv.android.OpenCVLoader;

public class OpenCVHelper {
    public static boolean init() {
        return OpenCVLoader.initDebug();
    }
}