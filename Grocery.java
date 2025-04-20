import java.io.*;
import java.util.*;

public class Grocery {

    // Product class
    public static class Product{
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

        public int getId() {
            return id;
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
            return id + ". " + name + " - $" + price + " (Barcode: " + barcode + ")";
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

        public void addProduct(Product product, int quantity) {
            for (CartItem item : items) {
                if (item.getProduct().equals(product)) {
                    item.setQuantity(item.getQuantity() + quantity);
                    return;
                }
            }
            items.add(new CartItem(product, quantity));
        }

        public void reduceProduct(Product product, int quantity) {
            for (Iterator<CartItem> iterator = items.iterator(); iterator.hasNext();) {
                CartItem item = iterator.next();
                if (item.getProduct().equals(product)) {
                    if (item.getQuantity() <= quantity) {
                        iterator.remove();
                    } else {
                        item.setQuantity(item.getQuantity() - quantity);
                    }
                    return;
                }
            }
        }

        public void removeProduct(Product product) {
            items.removeIf(item -> item.getProduct().equals(product));
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

    // File writing functions

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
                writer.write(product.getId() + "," + product.getName() + "," + product.getPrice() + "," + product.getBarcode());
                writer.newLine();
            }
        }
    }

    // Function to encrypt password
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
    public static void saveOrder(Order order) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("orders.csv", true))) { // 'true' enables appending
            // Write order details: User name, email, order date, and cart items
            writer.write(order.getUser().getName() + "," + order.getUser().getEmail() + "," + order.getOrderDate());
            for (CartItem item : order.getCart().getItems()) {
                writer.write("," + item.getProduct().getBarcode() + ":" + item.getQuantity());
            }
            writer.newLine(); // Move to the next line for the next order
        }
    }


    public static void main(String[] args) {
        // File reading logic
        Scanner scanner = new Scanner(System.in);
        Map<String, User> users = new HashMap<>();
        List<Product> inventory = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("products.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
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
                        user.getCart().addProduct(product, quantity);
                        for (int j = 1; j < quantity; j++) {
                        user.getCart().addProduct(product, quantity);
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
        // Main loop
        while (!exit) {
            try {
                if (!loggedIn) {
                    // The variable loggedIn might be misleading. It is basically the variable that controls flow between the two screen options: the login/register/contine as guest screen and the main menu screen.
                    System.out.println("\n1. Register\n2. Login\n3. Continue as Guest\n4. Exit");
                    System.out.print("Choose an option: ");
                    int choice = Integer.parseInt(scanner.nextLine());

                    switch(choice){

                    case 1:
                        System.out.print("Enter your name: ");
                        String name = scanner.nextLine();
                        String email;
                        while (true) {
                            System.out.print("Enter your email: ");
                            email = scanner.nextLine();
                            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                                break;
                            } else {
                                System.out.println("Invalid email format. Please try again.");
                            }
                        }
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
                        email = scanner.nextLine(); // Email verification not required because it would just show "User not found" for invalid email
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
                        System.out.println("Continuing as Guest..."); // This limits a lot of features, but it is a good way to test the program without having to register or login. You can still add items to the cart and checkout, but you won't be able to save your cart or view previous orders.
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
                    System.out.println("\n1. View Products\n2. View Cart\n3. Add to cart\n4. Checkout\n5. Buy Item\n6. Check previous orders\n7. Remove item from cart\n8. Reduce cart item quantitiy\n9. Logout\n10. Exit");
                    System.out.print("Choose an option: ");
                    int choice = Integer.parseInt(scanner.nextLine());
                    switch(choice){
                    case 1:
                        System.out.println("Available Products:");
                        inventory.forEach(System.out::println);
                        break;
                    case 2:
                        System.out.println(currentCart.toString());
                        break;
                    case 3:
                    // Provides a lot of different ways to search through the inventory, usually meant for different target audiences. 
                    // Id is meant for regular users. Name is meant for first time or casual users. Barcodes are meant for employees with a barcode scanner.
                        System.out.println("1. Add product by ID\n2. Add product by barcode\n3. Add product by name\n4. Filter products by name");
                        System.out.print("Choose an option: ");
                        int choice3 = Integer.parseInt(scanner.nextLine());
                        switch (choice3) {
                        case 1:
                            System.out.print("Enter product ID to add to cart: ");
                            int id = Integer.parseInt(scanner.nextLine());
                            System.out.print("Enter quantity: ");
                            int quantity = Integer.parseInt(scanner.nextLine());
                            Product productById = null;
                            for (Product product : inventory) {
                                if (product.id == id) {
                                    productById = product;
                                    break;
                                }
                            }
                            if (productById != null) {
                                currentCart.addProduct(productById, quantity);
                                System.out.println("Added " + quantity + " of " + productById.getName() + " to cart.");
                            } else {
                                System.out.println("Product not found.");
                            }
                            break;
                        case 2:
                            System.out.print("Enter product barcode to add to cart: ");
                            long barcode = Long.parseLong(scanner.nextLine());
                            System.out.print("Enter quantity: ");
                            quantity = Integer.parseInt(scanner.nextLine());
                            Product productByBarcode = null;
                            for (Product product : inventory) {
                                if (product.getBarcode() == barcode) {
                                    productByBarcode = product;
                                    break;
                                }
                            }
                            if (productByBarcode != null) {
                                currentCart.addProduct(productByBarcode, quantity);
                                System.out.println("Added " + quantity + " of " + productByBarcode.getName() + " to cart.");
                            } else {
                                System.out.println("Product not found.");
                            }
                            break;
                        // Two types of name search implemented. One is a simple name search, the other is a filter search. The filter search returns a list of matching products and allows the user to select one to add to the cart.
                        case 3:
                            System.out.print("Enter product name to add to cart: ");
                            String name = scanner.nextLine();
                            System.out.print("Enter quantity: ");
                            quantity = Integer.parseInt(scanner.nextLine());
                            Product productByName = null;
                            for (Product product : inventory) {
                                if (product.getName().equalsIgnoreCase(name)) {
                                    productByName = product;
                                    break;
                                }
                            }
                            if (productByName != null) {
                                currentCart.addProduct(productByName, quantity);
                                System.out.println("Added " + quantity + " of " + productByName.getName() + " to cart.");
                            } else {
                                System.out.println("Product not found.");
                            }
                            break;
                        case 4:
                            System.out.print("Enter product name to filter: ");
                            String filterName = scanner.nextLine();
                            List<Product> filteredProducts = new ArrayList<>();
                            for (Product product : inventory) {
                                if (product.getName().toLowerCase().contains(filterName.toLowerCase())) {
                                    filteredProducts.add(product);
                                }
                            }
                            if (filteredProducts.isEmpty()) {
                                System.out.println("No products found.");
                            } else {
                                System.out.println("Filtered Products:");
                                filteredProducts.forEach(System.out::println);
                                System.out.print("Enter product ID to add to cart: ");
                                int filterId = Integer.parseInt(scanner.nextLine());
                                System.out.print("Enter quantity: ");
                                quantity = Integer.parseInt(scanner.nextLine());
                                Product filteredProduct = null;
                                for (Product product : filteredProducts) {
                                    if (product.id == filterId) {
                                        filteredProduct = product;
                                        break;
                                    }
                                }
                                if (filteredProduct != null) {
                                    currentCart.addProduct(filteredProduct, quantity);
                                    System.out.println("Added " + quantity + " of " + filteredProduct.getName() + " to cart.");
                                } else {
                                    System.out.println("Product not found.");
                                }
                            }
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                            continue;
                        }
                        break;
                    case 4:
                        if (currentCart.isEmpty()) {
                            System.out.println("Your cart is empty. Please add items to your cart before checking out.");
                            break;
                        }
                        if (currentUser == null) {
                            System.out.println("You must be logged in to checkout. Please login or register.");
                            break;
                        }
                        System.out.println("Checkout successful! Your order details are as follows:");
                        Order order = new Order(currentUser, currentCart);
                        System.out.println(order.toString());
                        saveOrder(order);
                        currentCart.clearCart();
                        break;
                    case 5:
                    // Reused logic from the add to cart function. It automatically creates a cart with one item and checks it out without extra steps. Meant for people who want to make a very specific purchase very quickly.
                        System.out.println("1. Buy product by ID\n2. Buy product by barcode\n3. Buy product by name\n4. Filter products by name");
                        System.out.print("Choose an option: ");
                        int choice4 = Integer.parseInt(scanner.nextLine());
                        switch (choice4) {
                        case 1:
                            System.out.print("Enter product ID to buy: ");
                            int id = Integer.parseInt(scanner.nextLine());
                            System.out.print("Enter quantity: ");
                            int quantity = Integer.parseInt(scanner.nextLine());
                            Product productById = null;
                            for (Product product : inventory) {
                                if (product.id == id) {
                                    productById = product;
                                    break;
                                }
                            }
                            if (productById != null) {
                                currentCart.addProduct(productById, quantity);
                            } else {
                                System.out.println("Product not found.");
                            }
                            break;
                        case 2:
                            System.out.print("Enter product barcode to buy: ");
                            long barcode = Long.parseLong(scanner.nextLine());
                            System.out.print("Enter quantity: ");
                            quantity = Integer.parseInt(scanner.nextLine());
                            Product productByBarcode = null;
                            for (Product product : inventory) {
                                if (product.getBarcode() == barcode) {
                                    productByBarcode = product;
                                    break;
                                }
                            }
                            if (productByBarcode != null) {
                                currentCart.addProduct(productByBarcode, quantity);
                            } else {
                                System.out.println("Product not found.");
                            }
                            break;
                        case 3:
                            System.out.print("Enter product name to buy: ");
                            String name = scanner.nextLine();
                            System.out.print("Enter quantity: ");
                            quantity = Integer.parseInt(scanner.nextLine());
                            Product productByName = null;
                            for (Product product : inventory) {
                                if (product.getName().equalsIgnoreCase(name)) {
                                    productByName = product;
                                    break;
                                }
                            }
                            if (productByName != null) {
                                currentCart.addProduct(productByName, quantity);
                            } else {
                                System.out.println("Product not found.");
                            }
                            break;
                        case 4:
                            System.out.print("Enter product name to filter: ");
                            String filterName = scanner.nextLine();
                            List<Product> filteredProducts = new ArrayList<>();
                            for (Product product : inventory) {
                                if (product.getName().toLowerCase().contains(filterName.toLowerCase())) {
                                    filteredProducts.add(product);
                                }
                            }
                            if (filteredProducts.isEmpty()) {
                                System.out.println("No products found.");
                            } else {
                                System.out.println("Filtered Products:");
                                filteredProducts.forEach(System.out::println);
                                System.out.print("Enter product ID to buy: ");
                                int filterId = Integer.parseInt(scanner.nextLine());
                                System.out.print("Enter quantity: ");
                                quantity = Integer.parseInt(scanner.nextLine());
                                Product filteredProduct = null;
                                for (Product product : filteredProducts) {
                                    if (product.id == filterId) {
                                        filteredProduct = product;
                                        break;
                                    }
                                }
                                if (filteredProduct != null) {
                                    currentCart.addProduct(filteredProduct, quantity);
                                } else {
                                    System.out.println("Product not found.");
                                }
                            }
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                            continue;
                        }
                        if (currentCart.isEmpty()) {
                            System.out.println("You are actually never supposed to see this message. Please report code 42069 to the developer.");
                            break;
                        }
                        if (currentUser == null) {
                            System.out.println("You must be logged in to buy. Please login or register.");
                            break;
                        }
                        System.out.println("Purchase successful! Your order details are as follows:");
                        Order order1 = new Order(currentUser, currentCart);
                        System.out.println(order1.toString());
                        saveOrder(order1);
                        currentCart.clearCart();
                        break;
                    case 6: // Just reads the orders.csv file and filters by user name
                        if (currentUser != null) {
                            try (BufferedReader reader = new BufferedReader(new FileReader("orders.csv"))) {
                                String line;
                                boolean foundOrders = false;
                                System.out.println("Your previous orders are as follows:");
                                while ((line = reader.readLine()) != null) {
                                    String[] parts = line.split(",");
                                    if (parts.length >= 3 && parts[1].equals(currentUser.getEmail())) {
                                        foundOrders = true;
                                        System.out.println("Order Date: " + parts[2]);
                                        System.out.println("Items:");
                                        for (int i = 3; i < parts.length; i++) {
                                            String[] productInfo = parts[i].split(":");
                                            if (productInfo.length == 2) {
                                                long barcode = Long.parseLong(productInfo[0]);
                                                int quantity = Integer.parseInt(productInfo[1]);
                                                for (Product product : inventory) {
                                                    if (product.getBarcode() == barcode) {
                                                        System.out.println("- " + product.getName() + " (x" + quantity + ")");
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        System.out.println();
                                    }
                                }
                                if (!foundOrders) {
                                    System.out.println("No previous orders found.");
                                }
                            } catch (IOException e) {
                                System.out.println("Error reading orders: " + e.getMessage());
                            }
                        } else {
                            System.out.println("You must be logged in to view your orders.");
                        }
                        break;
                    case 7: // Code mirrors add to cart
                        System.out.println("1. Remove product by ID\n2. Remove product by barcode\n3. Remove product by name\n4. Filter products by name to remove");
                        System.out.print("Choose an option: ");
                        int choice7 = Integer.parseInt(scanner.nextLine());
                        switch (choice7) {
                            case 1:
                                System.out.print("Enter product ID to remove from cart: ");
                                int idToRemove = Integer.parseInt(scanner.nextLine());
                                Product productByIdToRemove = null;
                                for (CartItem item : currentCart.getItems()) {
                                    if (item.getProduct().getId() == idToRemove) {
                                        productByIdToRemove = item.getProduct();
                                        break;
                                    }
                                }
                                if (productByIdToRemove != null) {
                                    currentCart.removeProduct(productByIdToRemove);
                                    System.out.println("Removed " + productByIdToRemove.getName() + " from cart.");
                                } else {
                                    System.out.println("Product not found in cart.");
                                }
                                break;
                            case 2:
                                System.out.print("Enter product barcode to remove from cart: ");
                                long barcodeToRemove = Long.parseLong(scanner.nextLine());
                                Product productByBarcodeToRemove = null;
                                for (CartItem item : currentCart.getItems()) {
                                    if (item.getProduct().getBarcode() == barcodeToRemove) {
                                        productByBarcodeToRemove = item.getProduct();
                                        break;
                                    }
                                }
                                if (productByBarcodeToRemove != null) {
                                    currentCart.removeProduct(productByBarcodeToRemove);
                                    System.out.println("Removed " + productByBarcodeToRemove.getName() + " from cart.");
                                } else {
                                    System.out.println("Product not found in cart.");
                                }
                                break;
                            case 3:
                                System.out.print("Enter product name to remove from cart: ");
                                String nameToRemove = scanner.nextLine();
                                Product productByNameToRemove = null;
                                for (CartItem item : currentCart.getItems()) {
                                    if (item.getProduct().getName().equalsIgnoreCase(nameToRemove)) {
                                        productByNameToRemove = item.getProduct();
                                        break;
                                    }
                                }
                                if (productByNameToRemove != null) {
                                    currentCart.removeProduct(productByNameToRemove);
                                    System.out.println("Removed " + productByNameToRemove.getName() + " from cart.");
                                } else {
                                    System.out.println("Product not found in cart.");
                                }
                                break;
                            case 4:
                                System.out.print("Enter product name to filter for removal: ");
                                String filterNameToRemove = scanner.nextLine();
                                List<Product> filteredProductsToRemove = new ArrayList<>();
                                for (CartItem item : currentCart.getItems()) {
                                    if (item.getProduct().getName().toLowerCase().contains(filterNameToRemove.toLowerCase())) {
                                        filteredProductsToRemove.add(item.getProduct());
                                    }
                                }
                                if (filteredProductsToRemove.isEmpty()) {
                                    System.out.println("No products found in cart.");
                                } else {
                                    System.out.println("Filtered Products:");
                                    filteredProductsToRemove.forEach(System.out::println);
                                    System.out.print("Enter product ID to remove from cart: ");
                                    int filterIdToRemove = Integer.parseInt(scanner.nextLine());
                                    Product filteredProductToRemove = null;
                                    for (Product product : filteredProductsToRemove) {
                                        if (product.getId() == filterIdToRemove) {
                                            filteredProductToRemove = product;
                                            break;
                                        }
                                    }
                                    if (filteredProductToRemove != null) {
                                        currentCart.removeProduct(filteredProductToRemove);
                                        System.out.println("Removed " + filteredProductToRemove.getName() + " from cart.");
                                    } else {
                                        System.out.println("Product not found in cart.");
                                    }
                                }
                                break;
                            default:
                                System.out.println("Invalid choice. Please try again.");
                                break;
                        }
                        break;
                    case 8: // Code mirrors add to cart
                        System.out.println("1. Reduce product by ID\n2. Reduce product by barcode\n3. Reduce product by name\n4. Filter products by name to reduce");
                        System.out.print("Choose an option: ");
                        int choice8 = Integer.parseInt(scanner.nextLine());
                        switch (choice8) {
                            case 1:
                                System.out.print("Enter product ID to reduce from cart: ");
                                int idToReduce = Integer.parseInt(scanner.nextLine());
                                System.out.print("Enter quantity to reduce: ");
                                int quantityToReduce = Integer.parseInt(scanner.nextLine());
                                Product productByIdToReduce = null;
                                for (CartItem item : currentCart.getItems()) {
                                    if (item.getProduct().getId() == idToReduce) {
                                        productByIdToReduce = item.getProduct();
                                        break;
                                    }
                                }
                                if (productByIdToReduce != null) {
                                    currentCart.reduceProduct(productByIdToReduce, quantityToReduce);
                                    System.out.println("Reduced " + quantityToReduce + " of " + productByIdToReduce.getName() + " from cart.");
                                } else {
                                    System.out.println("Product not found in cart.");
                                }
                                break;
                            case 2:
                                System.out.print("Enter product barcode to reduce from cart: ");
                                long barcodeToReduce = Long.parseLong(scanner.nextLine());
                                System.out.print("Enter quantity to reduce: ");
                                quantityToReduce = Integer.parseInt(scanner.nextLine());
                                Product productByBarcodeToReduce = null;
                                for (CartItem item : currentCart.getItems()) {
                                    if (item.getProduct().getBarcode() == barcodeToReduce) {
                                        productByBarcodeToReduce = item.getProduct();
                                        break;
                                    }
                                }
                                if (productByBarcodeToReduce != null) {
                                    currentCart.reduceProduct(productByBarcodeToReduce, quantityToReduce);
                                    System.out.println("Reduced " + quantityToReduce + " of " + productByBarcodeToReduce.getName() + " from cart.");
                                } else {
                                    System.out.println("Product not found in cart.");
                                }
                                break;
                            case 3:
                                System.out.print("Enter product name to reduce from cart: ");
                                String nameToReduce = scanner.nextLine();
                                System.out.print("Enter quantity to reduce: ");
                                quantityToReduce = Integer.parseInt(scanner.nextLine());
                                Product productByNameToReduce = null;
                                for (CartItem item : currentCart.getItems()) {
                                    if (item.getProduct().getName().equalsIgnoreCase(nameToReduce)) {
                                        productByNameToReduce = item.getProduct();
                                        break;
                                    }
                                }
                                if (productByNameToReduce != null) {
                                    currentCart.reduceProduct(productByNameToReduce, quantityToReduce);
                                    System.out.println("Reduced " + quantityToReduce + " of " + productByNameToReduce.getName() + " from cart.");
                                } else {
                                    System.out.println("Product not found in cart.");
                                }
                                break;
                            case 4:
                                System.out.print("Enter product name to filter for reduction: ");
                                String filterNameToReduce = scanner.nextLine();
                                List<Product> filteredProductsToReduce = new ArrayList<>();
                                for (CartItem item : currentCart.getItems()) {
                                    if (item.getProduct().getName().toLowerCase().contains(filterNameToReduce.toLowerCase())) {
                                        filteredProductsToReduce.add(item.getProduct());
                                    }
                                }
                                if (filteredProductsToReduce.isEmpty()) {
                                    System.out.println("No products found in cart.");
                                } else {
                                    System.out.println("Filtered Products:");
                                    filteredProductsToReduce.forEach(System.out::println);
                                    System.out.print("Enter product ID to reduce from cart: ");
                                    int filterIdToReduce = Integer.parseInt(scanner.nextLine());
                                    System.out.print("Enter quantity to reduce: ");
                                    quantityToReduce = Integer.parseInt(scanner.nextLine());
                                    Product filteredProductToReduce = null;
                                    for (Product product : filteredProductsToReduce) {
                                        if (product.getId() == filterIdToReduce) {
                                            filteredProductToReduce = product;
                                            break;
                                        }
                                    }
                                    if (filteredProductToReduce != null) {
                                        currentCart.reduceProduct(filteredProductToReduce, quantityToReduce);
                                        System.out.println("Reduced " + quantityToReduce + " of " + filteredProductToReduce.getName() + " from cart.");
                                    } else {
                                        System.out.println("Product not found in cart.");
                                    }
                                }
                                break;
                            default:
                                System.out.println("Invalid choice. Please try again.");
                                break;
                        }
                        break;
                    case 9: // Changes control to show the register/login/continue as guest screen
                        loggedIn = false;
                        break;
                    case 10: // CLoses the program and saves the data to the files
                        saveProducts(inventory);
                        saveUsers(users);
                        exit = true;
                        break;
                    }
        }

            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
                continue;
            }
        }
        scanner.close();

    }
}
