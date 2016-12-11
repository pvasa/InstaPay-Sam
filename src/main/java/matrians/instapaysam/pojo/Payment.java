package matrians.instapaysam.pojo;

/**
 * Team Matrians
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class Payment {

    private String _id;
    private String userEmail;
    private String stripeToken;
    private float amount;

    public Payment(String _id, String userEmail, String stripeToken, float amount) {
        this._id = _id;
        this.userEmail = userEmail;
        this.stripeToken = stripeToken;
        this.amount = amount;
    }
}
