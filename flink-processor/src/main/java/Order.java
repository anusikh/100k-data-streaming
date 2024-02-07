import java.util.Objects;

public class Order {

    public String customer_name;
    public Double cost;

    public Order() {}

    public Order(String customer_name, Double cost) {
        this.customer_name = customer_name;
        this.cost = Double.valueOf(cost);
    }

    @Override
    public String toString() {
        return "Order{" +
                "customer_name='" + customer_name + '\'' +
                ", cost=" + cost +
                '}';
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), customer_name, cost);
    }
}
