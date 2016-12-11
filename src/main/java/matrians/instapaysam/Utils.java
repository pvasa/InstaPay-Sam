package matrians.instapaysam;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Script;
import android.support.v8.renderscript.Type;
import android.util.Patterns;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Team Matrians
 */
public class Utils {

    static boolean checkPlayServices(AppCompatActivity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                Dialog dialog = googleApiAvailability.getErrorDialog(activity, status, 2404);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
            return false;
        }
        return true;
    }

    public static Bitmap YUV_420_888_toRGB (Image image, Context context) {

        int width = image.getWidth();
        int height = image.getHeight();

        // Get the three image planes
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer bufferY = planes[0].getBuffer();
        byte[] y = new byte[bufferY.remaining()];
        bufferY.get(y);

        ByteBuffer bufferU = planes[1].getBuffer();
        byte[] u = new byte[bufferU.remaining()];
        bufferU.get(u);

        ByteBuffer bufferV = planes[2].getBuffer();
        byte[] v = new byte[bufferV.remaining()];
        bufferV.get(v);

        // get the relevant RowStrides and PixelStrides
        // (we know from documentation that PixelStride is 1 for y)
        int yRowStride= planes[0].getRowStride();
        int uvRowStride= planes[1].getRowStride();  // we know from   documentation that RowStride is the same for u and v.
        int uvPixelStride= planes[1].getPixelStride();  // we know from   documentation that PixelStride is the same for u and v.


        // rs creation just for demo. Create rs just once in onCreate and use it again.
        RenderScript rs = RenderScript.create(context);

        ScriptC_yuv420888 mYuv420 = new ScriptC_yuv420888(rs);

        // Y,U,V are defined as global allocations, the out-Allocation is the Bitmap.
        // Note also that uAlloc and vAlloc are 1-dimensional while yAlloc is 2-dimensional.
        Type.Builder typeUCharY = new Type.Builder(rs, Element.U8(rs));
        typeUCharY.setX(yRowStride).setY(height);
        Allocation yAlloc = Allocation.createTyped(rs, typeUCharY.create());
        yAlloc.copyFrom(y);
        mYuv420.set_ypsIn(yAlloc);

        Type.Builder typeUCharUV = new Type.Builder(rs, Element.U8(rs));
        // note that the size of the u's and v's are as follows:
        //      (  (width/2)*PixelStride + padding  ) * (height/2)
        // =    (RowStride                          ) * (height/2)
        // but I noted that on the S7 it is 1 less...
        typeUCharUV.setX(u.length);
        Allocation uAlloc = Allocation.createTyped(rs, typeUCharUV.create());
        uAlloc.copyFrom(u);
        mYuv420.set_uIn(uAlloc);

        Allocation vAlloc = Allocation.createTyped(rs, typeUCharUV.create());
        vAlloc.copyFrom(v);
        mYuv420.set_vIn(vAlloc);

        // handover parameters
        mYuv420.set_picWidth (width);
        mYuv420.set_uvRowStride (uvRowStride);
        mYuv420.set_uvPixelStride (uvPixelStride);

        Bitmap outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Allocation outAlloc = Allocation.createFromBitmap(
                rs, outBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

        Script.LaunchOptions lo = new Script.LaunchOptions();
        lo.setX(0, width);  // by this we ignore the y’s padding zone, i.e. the right side of x between width and yRowStride
        lo.setY(0, height);

        mYuv420.forEach_doConvert(outAlloc,lo);
        outAlloc.copyTo(outBitmap);

        return outBitmap;
    }

    public static ProgressDialog showProgress(Context context, int stringResId) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage(context.getString(stringResId));
        dialog.show();
        return dialog;
    }

    /**
     * Check for empty fields
     * @param fields - Fields to be checked if empty
     * @return - If found return that field, else return null
     */
    static HashSet<TextInputEditText> checkEmptyFields(TextInputEditText... fields) {
        HashSet<TextInputEditText> emptyEditTexts = new HashSet<>();
        for (TextInputEditText editText : fields) {
            if (editText.getText().toString().length() == 0)
                emptyEditTexts.add(editText);
        }
        return emptyEditTexts;
    }

    static boolean validateEmail (String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    static boolean validatePassword (String password) {
        final String PASSWORD_REGEX =
                "(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@!%*#?&])[A-Za-z\\d$@!%*#?&]{8,}";
        return (Pattern.compile(PASSWORD_REGEX)).matcher(password).matches();
    }

    static boolean validatePhone (String phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }

    public static void snackUp(View view, int stringRs) {
        snackUp(view, view.getContext().getString(stringRs));
    }
    public static void snackUp(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
    public static void snackUp(View view, String jsonString, int jsonKeyResource) {
        String message = jsonString;
        try {
            message = new JSONObject(jsonString).
                    getString(view.getContext().getString(jsonKeyResource));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}
