package matrians.instapaysam.Schemas;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Team Matrians
 */
public class Vendor implements Parcelable {
    public String firstName;
    public String lastName;
    public String email;
    public String companyName;
    public String companyAddr;
    public String postalCode;
    public String phone;
    public String _id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.email);
        dest.writeString(this.companyName);
        dest.writeString(this.companyAddr);
        dest.writeString(this.postalCode);
        dest.writeString(this.phone);
        dest.writeString(this._id);
    }

    private Vendor(Parcel in) {
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.email = in.readString();
        this.companyName = in.readString();
        this.companyAddr = in.readString();
        this.postalCode = in.readString();
        this.phone = in.readString();
        this._id = in.readString();
    }

    public static final Parcelable.Creator<Vendor> CREATOR = new Parcelable.Creator<Vendor>() {
        @Override
        public Vendor createFromParcel(Parcel source) {
            return new Vendor(source);
        }

        @Override
        public Vendor[] newArray(int size) {
            return new Vendor[size];
        }
    };
}
