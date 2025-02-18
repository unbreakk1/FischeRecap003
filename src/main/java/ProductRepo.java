import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepo
{
    private List<Product> products;

    public ProductRepo()
    {
        products = new ArrayList<>();
        // example stock
        products.add(new Product("1", "Apple", 10.5));
        products.add(new Product("2", "Banana", 25.0));
        products.add(new Product("3", "Cherry", 15.0));
        products.add(new Product("4", "Date", 8.0));
    }

    public List<Product> getProducts() {
        return products;
    }

    public Optional<Product> getProductById(String id)
    {
        return products.stream().filter(p -> p.id().equals(id)).findFirst();
    }

    public Product addProduct(Product newProduct)
    {
        products.add(newProduct);
        return newProduct;
    }

    public void updateProduct(Product updatedProduct) {
        for (int i = 0; i < products.size(); i++)
        {
            if (products.get(i).id().equals(updatedProduct.id()))
            {
                products.set(i, updatedProduct);
                return;
            }
        }
        throw new IllegalArgumentException("Product with ID " + updatedProduct.id() + " not found!");
    }

    public void removeProduct(String id)
    {
        for (Product product : products)
        {
           if (product.id().equals(id))
           {
               products.remove(product);
               return;
           }
        }
    }
}
