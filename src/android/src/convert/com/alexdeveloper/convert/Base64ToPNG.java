package com.alexdeveloper.convert;

/**
* A phonegap plugin that converts a Base64 String to a PNG file.
*
* @author mcaesar
* @lincese MIT.
*/

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import org.json.JSONException;
import org.json.JSONObject;
import util.Base64;

public class Base64ToPNG extends CordovaPlugin {

    private Context externalContext;
    @Override
  public  boolean execute(String action, JSONArray args, CallbackContext callbackContext) {

          Log.d("convertLog","eseguito:" + action);
    if (action.equals("init")) {
        Log.d("convertLog","e stato inizializzato");
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK,"Initialized"));
        return true;
    }

        if (!action.equals("saveImage") && !action.equals("init")&& !action.equals("readImage")) {
            Log.d("convertLog", "eseguito non valido:" + action);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION,"Comando errato"));
            return false;
        }

        try {
            Log.d("convertLog", "sono nella execute");

            String b64String = "";
            b64String = args.getString(0);

            JSONObject params = args.getJSONObject(1);

            //Optional parameter
            String filename = params.has("filename")
                    ? params.getString("filename")
                    : "b64Image_" + System.currentTimeMillis() + ".png";
            InitContext(cordova.getActivity().getApplicationContext());
            String internalFolder= externalContext.getPackageManager().getPackageInfo(externalContext.getPackageName(), 0).applicationInfo.dataDir;
            String folder = params.has("folder")
                    ? params.getString("folder")
                    : internalFolder + "/images";

					  Log.d("convertLog", "il path dove cercerò il file è: " + internalFolder);
            Boolean overwrite = params.has("overwrite") 
                    ? params.getBoolean("overwrite") 
                    : false;

            if(action.equals("saveImage"))
            {
                return this.saveImage(b64String, filename, folder, overwrite, callbackContext);

            }
            if(action.equals("readImage"))
            {
                Log.d("convertLog", "sono nella readImage");

                return this.readImage(filename, folder, callbackContext);

            }

            return false;

        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "NameNotFound"));
            return false;

        }

        catch (JSONException e) {
            Log.d("convertLog", e.getStackTrace().toString());
            e.printStackTrace();
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION, "Execute:" + e.getMessage()));
            return false;

        } catch (InterruptedException e) {
            e.printStackTrace();
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Execute:" + e.getMessage() + e.getStackTrace()));
            return false;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Execute:" + e.getMessage() + args + action + System.currentTimeMillis()+  Environment.getExternalStorageDirectory() ));
            return false;
        }

    }

    private boolean saveImage(String b64String, String fileName, String dirName, Boolean overwrite, CallbackContext callbackContext) throws InterruptedException, JSONException {

        try {
            Log.d("convertLog", "sono nella saveImage");
            //Directory and File
            File dir = new File(dirName);
            Log.d("convertLog", "sono nella saveImage e sto per creare la directory: "+dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dirName, fileName);
            Log.d("convertLog", "sono nella saveImage e ho creato il file: "+file.toString());

            //Avoid overwriting a file
            if (!overwrite && file.exists()) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "File already exists!"));
            }

            //Decode Base64 back to Binary format
            Log.d("convertLog", "sto per decodificare: " + b64String);
            byte[] decodedBytes = Base64.decode(b64String.getBytes());


           //Save Binary file to phone
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            fOut.write(decodedBytes);
            fOut.close();


            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "Saved successfully!"));
            return true;
        } catch (FileNotFoundException e) {
            Log.d("convertLog", e.getStackTrace().toString());
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "File not Found!"));
            return false;
        } catch (IOException e) {
            Log.d("convertLog", e.getStackTrace().toString());
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "SaveImage" + e.getMessage()));
            return false;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "saveImage:" + e.getMessage() +"b64string:" + b64String + "filename:" +  fileName + "dirname: " + dirName ));
            return false;
        }

    }

    private boolean readImage(String fileName, String dirName, CallbackContext callbackContext) throws InterruptedException, JSONException {

        try {
            Log.d("convertLog", "sono nella readImage: filename " +fileName + "dirname:" + dirName);

            File file = new File(dirName, fileName);

            InputStream inputStream = new FileInputStream(file);//You can get an inputStream using any IO API
            byte[] bytes;
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
          bytes = output.toByteArray();
            Log.d("convertLog", "sono nella readImage:sto per leggere il file");
            String encodedString = Base64.encodeToString(bytes,false);


            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, encodedString));
            return true;
        } catch (FileNotFoundException e) {
            Log.d("convertLog", e.getStackTrace().toString());
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "File not Found!"));
            return false;
        } catch (IOException e) {
            Log.d("convertLog", e.getStackTrace().toString());
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "readImage" + e.getMessage()));
            return false;
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "readImage:" + e.getMessage() + "filename:" +  fileName + "dirname: " + dirName ));
            return false;
        }

    }


    public void InitContext(Context context) {
        externalContext = context;
        Log.d("convertLog", "InitContext" + externalContext.getPackageName().toString());

    }
}
