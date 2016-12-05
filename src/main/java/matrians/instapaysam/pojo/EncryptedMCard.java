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
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class EncryptedMCard {

    private String _id, userEmail;
    private String eCard;
    private boolean deleteMe;

    public EncryptedMCard markToDelete() {
        this.deleteMe = true;
        return this;
    }

    EncryptedMCard setECard(String eCard) {
        this.eCard = eCard;
        return this;
    }

    EncryptedMCard(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this._id = preferences.getString(context.getString(R.string.prefUserId), null);
        this.userEmail = preferences.getString(context.getString(R.string.prefEmail), null);
        this.deleteMe = false;
    }

    public MCard decrypt(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Secure secure = Secure.getDefault(
                preferences.getString(context.getString(R.string.prefEmail), null),
                preferences.getString(context.getString(R.string.prefUserId), null),
                new byte[16]);
        return new Gson().fromJson(secure != null ? secure.decryptOrNull(eCard) : null, MCard.class);
    }
}
