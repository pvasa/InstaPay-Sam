package matrians.instapaysam.schemas;

/**
 * Team Matrians
 */
public class Payment {
    public String _id;
    public String userEmail;
    public String stripeToken;
    public float amount;

    public Payment(String _id, String userEmail, String stripeToken, float amount) {
        this._id = _id;
        this.userEmail = userEmail;
        this.stripeToken = stripeToken;
        this.amount = amount;
    }
}
