package matrians.instapaysam.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Team Matrians
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class Payment implements Parcelable {

    private String vendorID;
    private String vendorName;
    private String userID;
    private String userEmail;
    private String stripeToken;
    private float amount;
    private List<Product> products;
    private String receiptNumber;

    public Payment(String vendorID, String vendorName, String userID, String userEmail,
                   String stripeToken, float amount, List<Product> products) {
        this.vendorID = vendorID;
        this.vendorName = vendorName;
        this.userID = userID;
        this.userEmail = userEmail;
        this.stripeToken = stripeToken;
        this.amount = amount;
        this.products = products;
    }

    public List<Product> getProductList() {
        return products;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.vendorID);
        dest.writeString(this.vendorName);
        dest.writeString(this.userID);
        dest.writeString(this.userEmail);
        dest.writeString(this.stripeToken);
        dest.writeFloat(this.amount);
        dest.writeTypedList(this.products);
        dest.writeString(this.receiptNumber);
    }

    private Payment(Parcel in) {
        this.vendorID = in.readString();
        this.vendorName = in.readString();
        this.userID = in.readString();
        this.userEmail = in.readString();
        this.stripeToken = in.readString();
        this.amount = in.readFloat();
        this.products = in.createTypedArrayList(Product.CREATOR);
        this.receiptNumber = in.readString();
    }

    public static final Parcelable.Creator<Payment> CREATOR = new Parcelable.Creator<Payment>() {
        @Override
        public Payment createFromParcel(Parcel source) {
            return new Payment(source);
        }

        @Override
        public Payment[] newArray(int size) {
            return new Payment[size];
        }
    };
}
