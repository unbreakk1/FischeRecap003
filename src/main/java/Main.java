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

        Map<String, String> aliasToOrderId = new HashMap<>();

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

    private static void processCommand(String commandLine, ShopService shopService, Map<String, String> aliasToOrderId)
    {
        String[] parts = commandLine.strip().split(" ");
        String command = parts[0];

        switch (command) {
            case "addOrder" ->
            {

                if (parts.length < 3)
                {
                    System.err.println("Invalid 'addOrder' command! Proper usage: addOrder <alias> <productId> [<productId>...]");
                    break;
                }

                String alias = parts[1];
                List<String> productIds = Arrays.asList(Arrays.copyOfRange(parts, 2, parts.length));

                try
                {
                    Order newOrder = shopService.addOrder(productIds);

                    aliasToOrderId.put(alias, newOrder.id());
                    System.out.println("Added Order with alias '" + alias + "': " + newOrder);
                } catch (IllegalArgumentException e)
                {
                    System.err.println("Error while adding order: " + e.getMessage());
                }
            }

            case "setStatus" ->
            {
                if (parts.length != 3)
                {
                    System.err.println("Invalid 'setStatus' command! Proper usage: setStatus <alias> <status>");
                    break;
                }

                String alias = parts[1];
                String statusString = parts[2];

                try
                {
                    OrderStatus status = OrderStatus.valueOf(statusString.toUpperCase());

                    String orderId = aliasToOrderId.get(alias);
                    if (orderId == null)
                    {
                        System.err.println("No order found for alias: " + alias);
                        break;
                    }

                    Order updatedOrder = shopService.updateOrder(orderId, status);
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

                System.out.println("Available Products:");
                shopService.getAvailableProducts().forEach(System.out::println);
            }

            default -> System.err.println("Unknown command: " + commandLine);
        }



    }


}
