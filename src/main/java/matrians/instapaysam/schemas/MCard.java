package matrians.instapaysam.schemas;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Team Matrians
 */
public class MCard implements Parcelable {
    private String userEmail;
    public String name;
    public String number;
    public int expMonth;
    public int expYear;
    public String CVC;

    public MCard(String userEmail, String cardName, String cardNumber, int expMonth, int expYear, String CVC) {
        this.userEmail = userEmail;
        this.name = cardName;
        this.number = cardNumber;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.CVC = CVC;
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

    protected MCard(Parcel in) {
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
