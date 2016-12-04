package matrians.instapaysam.pojo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import matrians.instapaysam.R;
import matrians.instapaysam.Secure;

/**
 * Team Matrians
 */
public class EncryptedMCard {

    String _id, userEmail, eCard;

    public EncryptedMCard (Context context) {
        this(context, null);
    }

    private EncryptedMCard(Context context, String eCard) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this._id = preferences.getString(context.getString(R.string.prefUserId), null);
        this.userEmail = preferences.getString(context.getString(R.string.prefEmail), null);
        this.eCard = eCard;
    }

    public MCard decrypt(Context context) {
        String jsonCard;
        byte[] iv = new byte[16];

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String _id = preferences.getString(context.getString(R.string.prefUserId), null);
        String userEmail = preferences.getString(context.getString(R.string.prefEmail), null);

        try (Secure secure = Secure.getDefault(userEmail, _id, iv)) {
            jsonCard = secure != null ? secure.decryptOrNull(eCard) : null;
            return new Gson().fromJson(jsonCard, MCard.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
