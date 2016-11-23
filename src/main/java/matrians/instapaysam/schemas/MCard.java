package matrians.instapaysam.schemas;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Team Matrians
 */
public class MCard implements Parcelable {
    private String userEmail;
    public String cardName;
    public String cardLast4Digits;
    private String stripeToken;

    public MCard(String userEmail, String cardName, String cardLast4Digits, String stripeToken) {
        this.userEmail = userEmail;
        this.cardName = cardName;
        this.cardLast4Digits = cardLast4Digits;
        this.stripeToken = stripeToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userEmail);
        dest.writeString(this.cardName);
        dest.writeString(this.cardLast4Digits);
        dest.writeString(this.stripeToken);
    }

    private MCard(Parcel in) {
        this.userEmail = in.readString();
        this.cardName = in.readString();
        this.cardLast4Digits = in.readString();
        this.stripeToken = in.readString();
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
