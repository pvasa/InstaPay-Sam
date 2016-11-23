package matrians.instapaysam.schemas;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Team Matrians
 */
public class Card implements Parcelable {
    public String userEmail;
    public String cardName;
    public String cardLast4Digits;
    public String stripeToken;


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

    public Card() {
    }

    protected Card(Parcel in) {
        this.userEmail = in.readString();
        this.cardName = in.readString();
        this.cardLast4Digits = in.readString();
        this.stripeToken = in.readString();
    }

    public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel source) {
            return new Card(source);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };
}
