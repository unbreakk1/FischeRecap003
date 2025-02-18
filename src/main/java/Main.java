import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main
{
    public static void main(String[] args)
    {
        ProductRepo productRepo = new ProductRepo();
        OrderRepo orderRepo = new OrderMapRepo();
        IdService idService = new UuidIdService();
        ShopService shopService = new ShopService(productRepo, orderRepo, idService);

        // Map to store alias-to-OrderID mappings
        Map<String, String> aliasToOrderId = new HashMap<>();

        // Populate some products for testing
        productRepo.addProduct(new Product("1", "Apple"));
        productRepo.addProduct(new Product("2", "Banana"));
        productRepo.addProduct(new Product("3", "Cherry"));
        productRepo.addProduct(new Product("4", "Date"));

        // Read and process the commands from the "transactions.txt" file
        try
        {
            List<String> lines = Files.readAllLines(Paths.get("transactions.txt"));
            for (String line : lines)
            {
                processCommand(line, shopService, aliasToOrderId);
            }
        }
        catch (IOException e)
        {
            System.err.println("Error reading transactions.txt: " + e.getMessage());
        }
    }

    private static void processCommand(String commandLine, ShopService shopService, Map<String, String> aliasToOrderId) {
        // Split the command line into parts
        String[] parts = commandLine.split(" ");
        String command = parts[0];

        switch (command)
        {
            case "addOrder" ->
            {
                if (parts.length < 3)
                {
                    System.err.println("Invalid addOrder command: " + commandLine);
                    break;
                }
                String alias = parts[1]; // Get the alias for the order
                List<String> productIds = Arrays.asList(Arrays.copyOfRange(parts, 2, parts.length)); // Extract product IDs
                try
                {
                    Order newOrder = shopService.addOrder(productIds); // Add the order
                    aliasToOrderId.put(alias, newOrder.id()); // Save the alias-to-OrderID mapping
                    System.out.println("Added Order: " + newOrder);
                }
                catch (IllegalArgumentException e)
                {
                    System.err.println("Failed to add order: " + e.getMessage());
                }
            }
            case "setStatus" ->
            {
                if (parts.length != 3)
                {
                    System.err.println("Invalid setStatus command: " + commandLine);
                    break;
                }
                String alias = parts[1];
                String statusString = parts[2];
                try
                {
                    OrderStatus status = OrderStatus.valueOf(statusString); // Parse the status
                    String orderId = aliasToOrderId.get(alias); // Get the OrderID from the alias
                    if (orderId == null) {
                        System.err.println("No order found for alias: " + alias);
                        break;
                    }
                    Order updatedOrder = shopService.updateOrder(orderId, status); // Update the order status
                    System.out.println("Updated Order: " + updatedOrder);
                }
                catch (IllegalArgumentException e)
                {
                    System.err.println("Invalid status or alias: " + e.getMessage());
                }
            }
            case "printOrders" ->
            {
                System.out.println("All Orders:");
                shopService.getOrdersByStatus(OrderStatus.PROCESSING).forEach(System.out::println);
                shopService.getOrdersByStatus(OrderStatus.COMPLETED).forEach(System.out::println);
                shopService.getOrdersByStatus(OrderStatus.IN_DELIVERY).forEach(System.out::println);
            }
            default -> System.err.println("Unknown command: " + commandLine);
        }
    }


}
