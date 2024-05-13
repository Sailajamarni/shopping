<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Products Display</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<style>
body {
    font-family: Arial, sans-serif;
    margin: 0;
    padding: 0;
    background-color: #f4f4f4;
}

.navbar {
    background-color: #333;
    overflow: hidden;
    display: flex;
    align-items: center; /* Align items vertically */
    padding: 10px 20px;
}

.navbar a {
    color: #fff;
    text-decoration: none;
    font-size: 16px;
    padding: 10px;
}

.navbar a:hover {
    background-color: #555;
}

.cart-img {
    margin-left: auto; /* Push cart image to the right */
    width: 30px;
    height: auto;
}

#main {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-around;
    padding: 20px;
}

.box {
    width: 250px;
    height: 250px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
    padding: 15px;
}

.box img {
    max-width: 100%;
    border-radius: 8px;
}

.box p {
    margin: 8px 0;
    font-size: 16px;
    color: #333;
}

.box button {
    background-color: #007bff;
    color: #fff;
    border: none;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.box button:hover {
    background-color: #0056b3;
}

button a {
    text-decoration: none;
    color: white;
}

.dropdown {
    position: relative;
    display: inline-block;
}
</style>
</head>
<body>
    <div class="navbar">
        <div>
            <a href="#">Home</a>
            <a href="#">Products</a>
            <a href="#">About</a>
            <a href="#">Contact</a>
            <div class="dropdown">
                <select id="categorySelect">
                    <option value="" selected disabled hidden>Categories</option>
                    <option value="1">Fashion</option>
                    <option value="2">Drinks</option>
                    <option value="3">Food</option>
                </select>
            </div>
        </div>
        <input type="text" id="pincode" class="pincode-input" placeholder="Enter Pin Code">
        <button id="checkAvailabilityBtn">Check Availability</button>
        <a href="cart.jsp"><img src="imgs/cart2.jpeg" alt="Cart" id="cartbtn" class="cart-img" /></a>
    </div>
    <h2 style="text-align: center">Products</h2>
    <div id="main"></div>
    <script type="text/javascript">
        $(document).ready(function () {
            // Event listener for category selection change
            $("#categorySelect").change(function () {
                var selectedCategoryId = $(this).val();
                showCategory(selectedCategoryId);
            });

            // Call showCategory initially with default value
            showCategory($("#categorySelect").val());

            $("#cartbtn").click(function () {
                window.location.href = "cart.jsp";
            });

            // Function to check availability when pin code is entered
            $("#checkAvailabilityBtn").click(function () {
                var pincode = $("#pincode").val(); // Modified to get pincode value
                var category = $("#categorySelect").val();
                $.ajax({
                    url: "/cartmvc/CheckAvailabilityController",
                    method: "GET",
                    data: {
                        pincode: pincode,
                        category_id: category // Modified to pass category_id instead of category
                    },
                    success: function (response) {
                        if (response.isServiceable) {
                            alert("Service is available for the selected category in this pin code.");
                        } else {
                            alert("Service is not available for the selected category in this pin code.");
                        }
                    },
                    error: function (xhr, status, error) {
                        alert("Error checking availability: " + error);
                    }
                });
            });

            // Define showCategory function globally
            function showCategory(categoryId) {
                if (categoryId !== undefined) {
                    $.ajax({
                        url: "/cartmvc/GetProductsController?category_id=" + categoryId,
                        method: "GET",
                        dataType: "json",
                        success: function (res) {
                            console.log(res);
                            // Clear existing products
                            $("#main").empty();
                            // Append new products based on category
                            $.each(res, function (ind, product) {
                                let div = $("<div>");
                                div.attr("class", "box");
                                div.attr("id", product.pid);
                                let img = $("<img>");
                                img.attr("src", product.pimag);
                                img.attr({
                                    "width": "100px",
                                    "height": "100px"
                                })
                                let name = $("<p>");
                                name.text(product.pname);
                                let price = $("<p>");
                                price.text(product.pprice);
                                let btn = $("<button>");
                                let a = $("<a>");
                                a.attr("href", "/cartmvc/AddToCartController?pid=" + product.pid);
                                a.text("add to cart");
                                btn.append(a);
                                div.append(img);
                                div.append(name);
                                div.append(price);
                                div.append(btn);
                                $("#main").append(div);
                            })
                        },
                        error: function (a, b, error) {
                            console.log(error);
                        }
                    })
                } else {
                    console.log("Category ID is undefined");
                }
            }
        });
    </script>
</body>
</html>
