

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroceryClasses {

    // Product class
    public static class Product {
        private String name;
        private double price;
        private long barcode;

        public Product(String name, double price, long barcode) {
            this.name = name;
            this.price = price;
            this.barcode = barcode;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public long getBarcode() {
            return barcode;
        }



        @Override
        public String toString() {
            return name + " - $" + price;
        }
    }

    // Cart class
    public static class Cart {
        private List<Object[]> productData; // Each Object[] will hold {Product, Quantity}

        public Cart() {
            this.productData = new ArrayList<>();
        }

        public void addProduct(Product product) {
            for (Object[] data : productData) {
                if (data[0].equals(product)) {
                    data[1] = (int) data[1] + 1;
                    return;
                }
            }
            productData.add(new Object[]{product, 1});
        }

        public void removeProduct(Product product) {
            for (int i = 0; i < productData.size(); i++) {
                Object[] data = productData.get(i);
                if (data[0].equals(product)) {
                    int quantity = (int) data[1];
                    if (quantity > 1) {
                        data[1] = quantity - 1;
                    } else {
                        productData.remove(i);
                    }
                    return;
                }
            }
        }

        public double calculateTotal() {
            double total = 0;
            for (Object[] data : productData) {
                Product product = (Product) data[0];
                int quantity = (int) data[1];
                total += product.getPrice() * quantity;
            }
            return total;
        }

        public List<Product> getProducts() {
            List<Product> products = new ArrayList<>();
            for (Object[] data : productData) {
                products.add((Product) data[0]);
            }
            return products;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Cart: ");
            for (Object[] data : productData) {
                Product product = (Product) data[0];
                int quantity = (int) data[1];
                sb.append(product.getName()).append(" (x").append(quantity).append("), ");
            }
            return sb.length() > 6 ? sb.substring(0, sb.length() - 2) : sb.toString();
        }}
    // User class
    public static class User {
        private String name;
        private List<Order> orders;

        public User(String name, String email) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        public void addOrder(Order order) {
            if (orders == null) {
                orders = new ArrayList<>();
            }
            orders.add(order);
        }
        public List<Order> getOrders() {
            return orders;
        }

        @Override
        public String toString() {
            return "User: " + name;
        }
    }

    // Order class
    public static class Order {
        private User user;
        private Cart cart;
        private Date orderDate;

        public Order(User user, Cart cart) {
            this.user = user;
            this.cart = cart;
            this.orderDate = new Date();
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Cart getCart() {
            return cart;
        }

        public void setCart(Cart cart) {
            this.cart = cart;
        }

        public Date getOrderDate() {
            return orderDate;
        }

        @Override
        public String toString() {
            return "Order by " + user.getName() + " on " + orderDate + "\n" + cart.toString();
        }
    }
    }
