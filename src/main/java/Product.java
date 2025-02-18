public record Product(String id, String name, double quantity)
{
    public boolean isInStock(double requestedQuantity)
    {
        return quantity >= requestedQuantity;
    }

    public Product updateQuantity(double usedQuantity)
    {
        if (usedQuantity > quantity)
            throw new IllegalStateException("Not enough stock for product: " + name + ", Requested: " + usedQuantity + ", Available: " + quantity);

        return new Product(id, name, quantity - usedQuantity);
    }

    @Override
    public String toString()
    {
        return "Product{id='" + id + "', name='" + name + "', quantity=" + quantity + "}";
    }

}
