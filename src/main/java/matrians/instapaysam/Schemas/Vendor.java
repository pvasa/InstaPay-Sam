package matrians.instapaysam.Schemas;

import android.os.Parcel;
import android.os.Parcelable;

/**
 Team Matrians
 */

public class Vendor implements Parcelable {
    public String first_name;
    public String last_name;
    public String email;
    public String company_name;
    public String company_addr;
    public String postal_code;
    public String phone;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.first_name);
        dest.writeString(this.last_name);
        dest.writeString(this.email);
        dest.writeString(this.company_name);
        dest.writeString(this.company_addr);
        dest.writeString(this.postal_code);
        dest.writeString(this.phone);
    }

    private Vendor(Parcel in) {
        this.first_name = in.readString();
        this.last_name = in.readString();
        this.email = in.readString();
        this.company_name = in.readString();
        this.company_addr = in.readString();
        this.postal_code = in.readString();
        this.phone = in.readString();
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
