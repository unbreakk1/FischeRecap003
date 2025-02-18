import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;
@RequiredArgsConstructor
public class ShopService
{
    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;
    private final IdService idService;

    public Order addOrder(List<String> productIds)
    {
        List<Product> products = new ArrayList<>();
        for (String productId : productIds) {
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

    public Map<OrderStatus, Order> getOldestOrderPerStatus()
    {
        return orderRepo.getOrders().stream()
                .collect(Collectors.groupingBy(
                        Order::status,
                        Collectors.minBy(Comparator.comparing(Order::timestamp))
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue().isPresent()) // Filter entries with present values
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get() // Get the actual order from Optional
                ));

    }
}