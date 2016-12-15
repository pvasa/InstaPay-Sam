package matrians.instapaysam.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Team Matrians
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class Order implements Parcelable {

    private String vendorID;
    public String vendorName;
    private String userID;
    private String userEmail;
    private String stripeToken;
    public float amount;
    private List<Product> products;
    private String receiptNumber;
    public String timeStamp;

    public Order(String vendorID, String vendorName, String userID, String userEmail,
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
        dest.writeString(this.timeStamp);
    }

    private Order(Parcel in) {
        this.vendorID = in.readString();
        this.vendorName = in.readString();
        this.userID = in.readString();
        this.userEmail = in.readString();
        this.stripeToken = in.readString();
        this.amount = in.readFloat();
        this.products = in.createTypedArrayList(Product.CREATOR);
        this.receiptNumber = in.readString();
        this.timeStamp = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
