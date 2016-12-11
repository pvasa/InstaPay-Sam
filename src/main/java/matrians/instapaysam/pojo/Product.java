package matrians.instapaysam.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Team Matrians
 */
public class Product implements Parcelable {
    public long id;
    public String name;
    public float price;
    public int maxQuantity;
    public int quantity;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeFloat(this.price);
        dest.writeInt(this.maxQuantity);
        dest.writeInt(this.quantity);
    }

    private Product(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.price = in.readFloat();
        this.maxQuantity = in.readInt();
        this.quantity = in.readInt();
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
