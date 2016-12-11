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
    public String brand;

    public MCard (Context context, String cardName, String cardNumber,
                  int expMonth, int expYear, String CVC, String brand) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this._id = preferences.getString(context.getString(R.string.prefUserId), null);
        this.userEmail = preferences.getString(context.getString(R.string.prefEmail), null);
        this.name = cardName;
        this.number = cardNumber;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.CVC = CVC;
        this.brand = brand;
    }

    public interface Callback {
        void encryptedOrNull(EncryptedMCard eMCard);
    }

    public void encrypt(final Context context, final Callback callback) {
        Secure secure = Secure.getDefault(userEmail, _id, new byte[16]);
        if (secure != null) {
            secure.encryptAsync(new Gson().toJson(this), new Secure.Callback() {
                @Override
                public void onSuccess(String result) {
                    callback.encryptedOrNull(new EncryptedMCard(context).setECard(result));
                }
                @Override
                public void onError(Exception exception) {
                    callback.encryptedOrNull(null);
                }
            });
        } else callback.encryptedOrNull(null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.userEmail);
        dest.writeString(this.name);
        dest.writeString(this.number);
        dest.writeInt(this.expMonth);
        dest.writeInt(this.expYear);
        dest.writeString(this.CVC);
        dest.writeString(this.brand);
    }

    private MCard(Parcel in) {
        this._id = in.readString();
        this.userEmail = in.readString();
        this.name = in.readString();
        this.number = in.readString();
        this.expMonth = in.readInt();
        this.expYear = in.readInt();
        this.CVC = in.readString();
        this.brand = in.readString();
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
