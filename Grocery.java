import java.io.*;
import java.util.*;

public class Grocery {

    // Product class
    public static class Product {
        private int id;
        private String name;
        private double price;
        private long barcode;

        public Product(int id, String name, double price, long barcode) {
            this.id = id;
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
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Product product = (Product) obj;
            return barcode == product.barcode;
        }

        @Override
        public int hashCode() {
            return Objects.hash(barcode);
        }

        @Override
        public String toString() {
            return id + ". " + name + " - $" + price;
        }
    }

    // CartItem class
    public static class CartItem {
        private Product product;
        private int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    // Cart class
    public static class Cart {
        private List<CartItem> items = new ArrayList<>();

        public void addProduct(Product product) {
            for (CartItem item : items) {
                if (item.getProduct().equals(product)) {
                    item.setQuantity(item.getQuantity() + 1);
                    return;
                }
            }
            items.add(new CartItem(product, 1));
        }

        public void removeProduct(Product product) {
            items.removeIf(item -> item.getProduct().equals(product) && item.getQuantity() == 1);
            for (CartItem item : items) {
                if (item.getProduct().equals(product)) {
                    item.setQuantity(item.getQuantity() - 1);
                    return;
                }
            }
        }

        public double calculateTotal() {
            return items.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();
        }

        public void clearCart() {
            items.clear();
        }

        @Override
        public String toString() {
            if (items.isEmpty()) return "Cart is empty.";
            StringBuilder sb = new StringBuilder("Cart: \n");
            for (CartItem item : items) {
                sb.append(item.getProduct().getName()).append(" (x").append(item.getQuantity()).append(") - $")
                        .append(item.getProduct().getPrice() * item.getQuantity()).append("\n");
            }
            sb.append("Total: $").append(calculateTotal());
            return sb.toString();
        }

        public boolean isEmpty() {
            return items.isEmpty();
        }

        public List<CartItem> getItems() {
            return items;
        }
    }

    // User class
    public static class User {
        private String name;
        private String email;
        private String password;
        private List<Order> orders = new ArrayList<>();
        private Cart cart = new Cart();

        public User(String name, String email, String password) {
            this.name = name;
            this.email = email;
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword(){
            return password;
        }

        public void addOrder(Order order) {
            orders.add(order);
        }

        public List<Order> getOrders() {
            return orders;
        }

        public Cart getCart() {
            return cart;
        }

        @Override
        public String toString() {
            return "User: " + name + " (" + email + ")";
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

        public Cart getCart() {
            return cart;
        }

        public Date getOrderDate() {
            return orderDate;
        }

        @Override
        public String toString() {
            return "Order by " + user.getName() + " on " + orderDate + "\n" + cart.toString();
        }
    }

    public static void saveUsers(Map<String, User> users) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.csv"))) {
            for (User user : users.values()) {
                writer.write(user.getName() + "," + user.getEmail() + "," + user.getPassword());
                for (CartItem item : user.getCart().getItems()) {
                    writer.write("," + item.getProduct().getBarcode() + ":" + item.getQuantity());
                }
                writer.newLine();
            }
        }
    }

    public static void saveProducts(List<Product> products) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("products.csv"))) {
            for (Product product : products) {
                writer.write(product.getName() + "," + product.getPrice() + "," + product.getBarcode());
                writer.newLine();
            }
        }
    }
    static String railFenceEncrypt(String password){
        StringBuilder encrypted = new StringBuilder();
        int rail = 3; // Number of rails
        char[][] railFence = new char[rail][password.length()];
        boolean dirDown = false;
        int row = 0, col = 0;

        for (int i = 0; i < password.length(); i++) {
            if (row == 0 || row == rail - 1) {
                dirDown = !dirDown;
            }
            railFence[row][col++] = password.charAt(i);
            row += dirDown ? 1 : -1;
        }

        for (int i = 0; i < rail; i++) {
            for (int j = 0; j < password.length(); j++) {
                if (railFence[i][j] != '\0') {
                    encrypted.append(railFence[i][j]);
                }
            }
        }
        return encrypted.toString();
    }



    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, User> users = new HashMap<>();
        List<Product> inventory = new ArrayList<>();
        // Load products from CSV file
        try (BufferedReader reader = new BufferedReader(new FileReader("products.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    double price = Double.parseDouble(parts[2]);
                    long barcode = Long.parseLong(parts[3]);
                    Product product = new Product(id, name, price, barcode);
                    inventory.add(product);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading products: " + e.getMessage());
        }
        try (BufferedReader reader = new BufferedReader(new FileReader("users.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                String name = parts[0];
                String email = parts[1];
                String password = parts[2];
                User user = new User(name, email, password);

                for (int i = 3; i < parts.length; i++) {
                String[] productInfo = parts[i].split(":");
                if (productInfo.length == 2) {
                    long barcode = Long.parseLong(productInfo[0]);
                    int quantity = Integer.parseInt(productInfo[1]);

                    for (Product product : inventory) {
                    if (product.getBarcode() == barcode) {
                        user.getCart().addProduct(product);
                        for (int j = 1; j < quantity; j++) {
                        user.getCart().addProduct(product);
                        }
                        break;
                    }
                    }
                }
                }
                users.put(email, user);
            }
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
        User currentUser = null;
        Cart guestCart = new Cart();
        Cart currentCart = guestCart;



        System.out.println("Welcome to the Grocery Store!");
        boolean loggedIn = false;

        boolean exit = false;

        while (!exit) {
            try {
                if (!loggedIn) {
                    System.out.println("\n1. Register\n2. Login\n3. Continue as Guest\n4. Exit");
                    System.out.print("Choose an option: ");
                    int choice = Integer.parseInt(scanner.nextLine());

                    switch(choice){

                    case 1:
                        System.out.print("Enter your name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter your email: ");
                        String email = scanner.nextLine();
                        Console console = System.console();
                        String password = new String(console.readPassword("Enter your password: "));
                        String encryptedPassword = railFenceEncrypt(password);
                        currentUser = new User(name, email, encryptedPassword);

                        users.put(email, currentUser);
                        currentCart = currentUser.getCart();
                        loggedIn = true;
                        break;
                    case 2:
                        System.out.print("Enter email: ");
                        email = scanner.nextLine();
                        currentUser = users.get(email);
                        if (currentUser == null) {
                            System.out.println("User not found.");
                            break;
                        }else {
                            Console console1 = System.console();
                            String password1 = new String(console1.readPassword("Enter password: "));
                            String encryptedPassword1 = railFenceEncrypt(password1);
                            if (!encryptedPassword1.equals(currentUser.getPassword())) {
                                System.out.println("Incorrect password.");
                                currentUser = null;
                                break;
                            }
                            currentCart = currentUser.getCart();
                            System.out.println("Welcome back, " + currentUser.getName() + "!");
                        }
                        loggedIn = true;
                        break;
                    case 3:
                        currentUser = null;
                        currentCart = guestCart;
                        System.out.println("Continuing as Guest...");
                        loggedIn = true;
                        break;
                    case 4:
                        saveUsers(users);
                        saveProducts(inventory);
                        System.out.println("Goodbye!");
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        continue;
                    }
                }else {
                    System.out.println("\n1. View Products\n2. View Cart\n3. Add to cart\n4. Checkout\n4. Buy Item\b5. Search item\n6. Logout\n7. Exit");
                    System.out.print("Choose an option: ");
                    int choice = Integer.parseInt(scanner.nextLine());
                    switch(choice){
                    case 1:
                        inventory.forEach(System.out::println);
                    case 2:
                        System.out.println(currentCart.toString());
                        break;
                    case 3:
                        System.out.print("Enter product barcode to add to cart: ");
                        long barcode = Long.parseLong(scanner.nextLine());
                        Product productToAdd = null;
                        for (Product product : inventory) {
                            if (product.getBarcode() == barcode) {
                                productToAdd = product;
                                break;
                            }
                        }
                        if (productToAdd != null) {
                            currentCart.addProduct(productToAdd);
                            System.out.println("Added " + productToAdd.getName() + " to cart.");
                        } else {
                            System.out.println("Product not found.");
                        }
                        break;
                    case 6:
                        loggedIn = false;
                    case 7:
                        saveProducts(inventory);
                        saveUsers(users);
                        exit = true;
                        break;
                    }
        }

            } catch (IOException e) {
                System.out.println("Error: " + e);
                continue;
            }
        }
        scanner.close();

    }
}
