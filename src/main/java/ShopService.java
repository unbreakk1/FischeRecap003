import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopService
{
    private ProductRepo productRepo = new ProductRepo();
    private OrderRepo orderRepo = new OrderMapRepo();

    public Order addOrder(List<String> productIds)
    {
        List<Product> products = new ArrayList<>();
        for (String productId : productIds)
        {
            Product productToOrder = productRepo.getProductById(productId);
            if (productToOrder == null)
            {
                System.out.println("Product mit der Id: " + productId + " konnte nicht bestellt werden!");
                return null;
            }
            products.add(productToOrder);
        }

        Order newOrder = new Order(UUID.randomUUID().toString(), products);

        return orderRepo.addOrder(newOrder);
    }

    public Order updateOrderStatus(String orderId, OrderStatus newStatus)
    {
        Order existingOrder = orderRepo.getOrderById(orderId);
        if (existingOrder == null)
        {
            System.out.println("Order mit der Id: " + orderId + " wurde nicht gefunden!");
            return null;
        }

        Order updatedOrder = new Order(existingOrder.id(), existingOrder.products(), newStatus);

        orderRepo.removeOrder(orderId);
        orderRepo.addOrder(updatedOrder);

        return updatedOrder;
    }

}
