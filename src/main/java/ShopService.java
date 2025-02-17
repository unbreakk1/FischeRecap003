import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ShopService
{
    private final ProductRepo productRepo = new ProductRepo();
    private final OrderRepo orderRepo = new OrderMapRepo();

    public Order addOrder(List<String> productIds)
    {
        List<Product> products = new ArrayList<>();
        for (String productId : productIds)
        {
            Optional<Product> productToOrder = productRepo.getProductById(productId);

            products.add(productToOrder.orElseThrow(() ->
                    new IllegalArgumentException("Product with ID " + productId + " does not exist!")));
        }
        Order newOrder = new Order(UUID.randomUUID().toString(), products);

        return orderRepo.addOrder(newOrder);
    }

    public Order updateOrder(String orderId, OrderStatus newStatus)
    {
        Order existingOrder = orderRepo.getOrderById(orderId);
        if (existingOrder == null)
            throw new IllegalArgumentException("Order with ID " + orderId + " not found!");


        Order updatedOrder = existingOrder.withStatus(newStatus);

        orderRepo.removeOrder(orderId);
        orderRepo.addOrder(updatedOrder);

        return updatedOrder;
    }

    public List<Order> getOrdersByStatus(OrderStatus status)
    {
        return orderRepo.getOrders().stream()
                .filter(order -> order.status() == status)
                .collect(Collectors.toList());
    }

}

