import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        ProductRepo productRepo = new ProductRepo();
        OrderRepo orderRepo = new OrderMapRepo();

        productRepo.addProduct(new Product("1", "Apfel"));
        productRepo.addProduct(new Product("2", "Banana"));
        productRepo.addProduct(new Product("3", "Cherry"));

        ShopService shopService = new ShopService(productRepo, orderRepo);

        List<String> order1Products = List.of("1"); // Product ID "1" -> "Apfel"
        Order order1 = shopService.addOrder(order1Products);
        System.out.println("Added Order 1: " + order1);

        List<String> order2Products = List.of("1", "2");
        Order order2 = shopService.addOrder(order2Products);
        System.out.println("Added Order 2: " + order2);

        List<String> order3Products = List.of("3");
        Order order3 = shopService.addOrder(order3Products);
        System.out.println("Added Order 3: " + order3);

        List<String> order4Products = List.of("99");
        Order order4 = shopService.addOrder(order4Products);

        // List all orders in repository
        System.out.println("\nAll orders in repository:");
        orderRepo.getOrders().forEach(System.out::println);
    }

}
