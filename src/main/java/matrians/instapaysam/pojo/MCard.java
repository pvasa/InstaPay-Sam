package matrians.instapaysam.pojo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import matrians.instapaysam.R;
import matrians.instapaysam.Secure;

/**
 * Team Matrians
 */
public class MCard implements Parcelable {

    private String _id;
    private String userEmail;
    public String name;
    public String number;
    public int expMonth;
    public int expYear;
    public String CVC;

    public MCard (Context context, String cardName,
                  String cardNumber, int expMonth, int expYear, String CVC) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this._id = preferences.getString(context.getString(R.string.prefUserId), null);
        this.userEmail = preferences.getString(context.getString(R.string.prefEmail), null);
        this.name = cardName;
        this.number = cardNumber;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.CVC = CVC;
    }

    public EncryptedMCard encrypt (Context context) {
        Secure secure = Secure.getDefault(userEmail, _id, new byte[16]);
        return new EncryptedMCard(context)
                .setECard(secure != null ? secure.encryptOrNull(new Gson().toJson(this)) : null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userEmail);
        dest.writeString(this.name);
        dest.writeString(this.number);
        dest.writeInt(this.expMonth);
        dest.writeInt(this.expYear);
        dest.writeString(this.CVC);
    }

    private MCard(Parcel in) {
        this.userEmail = in.readString();
        this.name = in.readString();
        this.number = in.readString();
        this.expMonth = in.readInt();
        this.expYear = in.readInt();
        this.CVC = in.readString();
    }

    public static final Parcelable.Creator<MCard> CREATOR = new Parcelable.Creator<MCard>() {
        @Override
        public MCard createFromParcel(Parcel source) {
            return new MCard(source);
        }

        @Override
        public MCard[] newArray(int size) {
            return new MCard[size];
        }
    };
}
