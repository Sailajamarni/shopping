<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="servlets.CartItem" %>
<%@ page import="servlets.GetProductsDAL" %>
<%@ page import="servlets.Products" %>
<%@ page import="com.google.gson.Gson" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Cart</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #D6EAF8;
        }

        .container {
            max-width: 800px;
            margin: 100px auto;
            background-color: #FFE5B4;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        h1 {
            text-align: center;
            margin-bottom: 20px;
        }

        .product {
            border: 1px solid #ccc;
            padding: 10px;
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            background-color: #fff;
            box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
            border-radius: 5px;
        }

        .product-details {
            flex-grow: 1;
        }

        .product p {
            margin: 5px 0;
        }

        .quantity-btns button {
            padding: 5px 10px;
            cursor: pointer;
            background-color: #007bff;
            color: #fff;
            border: none;
            border-radius: 3px;
        }

        .quantity-btns button:hover {
            background-color: #0056b3;
        }

        #checkout-btn {
            padding: 10px 20px;
            cursor: pointer;
            background-color: #28a745;
            color: #fff;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            transition: background-color 0.3s ease;
        }

        #checkout-btn:hover {
            background-color: #218838;
        }

        #coupon-code {
            margin-top: 20px;
        }

        #apply-coupon-btn {
            padding: 8px 16px;
            cursor: pointer;
            background-color: #007bff;
            color: #fff;
            border: none;
            border-radius: 5px;
            font-size: 14px;
            transition: background-color 0.3s ease;
        }

        #apply-coupon-btn:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Welcome to Cart</h1>
    <%
        GetProductsDAL productsDAL = new GetProductsDAL(); // Instantiate GetProductsDAL
        List<CartItem> cartitems = (ArrayList<CartItem>) session.getAttribute("cartitems");
        if (cartitems != null && !cartitems.isEmpty()) {
            for (CartItem item : cartitems) {
                Products product = productsDAL.getProductById(item.getPid()); // Fetch product details
    %>
    <div id="product_<%= product.getPid() %>" class="product">
        <div class="product-details">
            <p class="product-name"><b>Product Name:</b> <%= product.getPname() %></p>
            <p class="quantity"><b>Quantity:</b> <span id="quantity_<%= product.getPid() %>"><%= item.getQuantity() %></span></p>
            <p class="cost"><b>Cost:</b> <%= item.getPriceInclusive() * item.getQuantity() %></p>
        </div>
        <div class="quantity-btns">
            <button class="decrease-btn" data-pid="<%= product.getPid() %>">-</button>
            <button class="increase-btn" data-pid="<%= product.getPid() %>">+</button>
        </div>
    </div>
    <%
            }
        } else {
    %>
    <p>No items in the cart</p>
    <%
        }
    %>
    <div id="empty"></div>
    <button id="checkout-btn" class="button">Checkout</button>
    <div id="coupon-code">
        <input type="text" id="coupon-input" placeholder="Enter Coupon Code">
        <button id="apply-coupon-btn">Apply Coupon</button>
    </div>
</div>
<script>
    $(document).ready(function () {
        $(".increase-btn").click(function () {
            var pid = $(this).data("pid");
            $.ajax({
                url: "IncreaseQuantityController",
                method: "GET",
                data: {pid: pid},
                success: function (data) {
                    var quantityElement = $("#quantity_" + pid);
                    var newQuantity = parseInt(quantityElement.text()) + 1;
                    quantityElement.text(newQuantity);
                    // Update cost here if needed
                }
            });
        });

        $(".decrease-btn").click(function () {
            var pid = $(this).data("pid");
            $.ajax({
                url: "DecreaseQuantityController",
                method: "GET",
                data: {pid: pid},
                success: function (data) {
                    var quantityElement = $("#quantity_" + pid);
                    var newQuantity = parseInt(quantityElement.text()) - 1;
                    if (newQuantity >= 0) {
                        quantityElement.text(newQuantity);
                        // Update cost here if needed
                    }
                }
            });
        });

        $("#apply-coupon-btn").click(function () {
            var couponCode = $("#coupon-input").val();
            $.ajax({
                url: "ApplyCouponController",
                method: "GET",
                data: {couponCode: couponCode},
                success: function (data) {
                    if (data.success) {
                        alert("Coupon applied successfully");
                        // Update cost here if needed
                    } else {
                        alert("Invalid coupon code");
                    }
                },
                error: function (xhr, status, error) {
                    alert("Error applying coupon: " + error);
                }
            });
        });

        $("#checkout-btn").click(function () {
            var cartitems = <%= new Gson().toJson(session.getAttribute("cartitems")) %>;
            if (!cartitems || cartitems.length === 0) {
                alert("Cart is empty");
                return;
            }
            $.ajax({
                url: "CheckoutController",
                method: "POST",
                data: {cartitems: JSON.stringify(cartitems)},
                success: function (response) {
                    if (response.success) {
                        window.location.href = "checkout.jsp?totalAmount=" + response.totalAmount;
                    } else {
                        alert("Error during checkout");
                    }
                },
                error: function (xhr, status, error) {
                    alert("Error during checkout: " + error);
                }
            });
        });
    });
</script>
</body>
</html>
