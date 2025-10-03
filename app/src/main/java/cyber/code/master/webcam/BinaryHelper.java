package cyber.code.master.webcam;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BinaryHelper {

    private static final String TAG = "BinaryHelper";

    /**
     * Recursively copies all assets from a folder to a target directory
     */
    public static void copyAssetFolder(Context context, String assetFolder, File outDir) {
        try {
            String[] assets = context.getAssets().list(assetFolder);
            if (assets == null) return;

            for (String asset : assets) {
                String assetPath = assetFolder.isEmpty() ? asset : assetFolder + "/" + asset;
                String[] subAssets = context.getAssets().list(assetPath);

                File outFile = new File(outDir, asset);

                if (subAssets == null || subAssets.length == 0) {
                    // It's a file
                    try (InputStream in = context.getAssets().open(assetPath);
                         FileOutputStream out = new FileOutputStream(outFile)) {

                        byte[] buffer = new byte[4096];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                    }

                    // Make executable if likely binary
                    if (asset.endsWith(".so") || asset.contains("qemu") || asset.endsWith(".sh")) {
                        Process chmod = Runtime.getRuntime().exec("chmod 755 " + outFile.getAbsolutePath());
                        chmod.waitFor();
                    }

                    Log.d(TAG, "Copied file: " + assetPath);

                } else {
                    // It's a folder
                    if (!outFile.exists()) outFile.mkdirs();
                    copyAssetFolder(context, assetPath, outFile);
                }
            }
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "Error copying assets", e);
        }
    }

    /**
     * Copies all assets from the APK to code cache directory
     */
    public static void copyAllAssets(Context context) {
        File targetDir = context.getCodeCacheDir(); // safer for executables
        copyAssetFolder(context, "", targetDir);
    }

    /**
     * Run a specific binary from code cache directory
     */
    public static Process runBinary(Context context, String fileName, String... args) throws IOException, InterruptedException {
        File binary = new File(context.getCodeCacheDir(), fileName);

        if (!binary.exists()) {
            throw new IOException("Binary not found: " + binary.getAbsolutePath());
        }

        String[] command = new String[args.length + 1];
        command[0] = binary.getAbsolutePath();
        System.arraycopy(args, 0, command, 1, args.length);

        return new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
    }
}
