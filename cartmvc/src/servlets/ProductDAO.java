package servlets;

import java.util.List;

public interface ProductDAO {

    List<Products> getProducts();

    List<Products> getAllProducts(int start, int limit);

    Products getProductById(int id);
}
