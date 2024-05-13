<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1">
    <title>Checkout</title>
    <style>
     body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #D6EAF8;
        }

        .container {
            max-width: 600px;
            margin: 50px auto;
            background-color: #FFE5B4;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            text-align: center;
        }

        h1 {
            margin-bottom: 20px;
        }

        p {
            margin-bottom: 20px;
        }

        button {
            padding: 10px 20px;
            cursor: pointer;
            background-color: #4CAF50; 
            color: #fff;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            transition: background-color 0.3s ease;
        }

        button a {
            color: #fff;
            text-decoration: none;
        }

        button:hover {
           background-color: #45a049; 
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Order Placed Successfully!</h1>
    <% 
        // Retrieve total amount and shipping charges from request parameters
        String totalAmount = request.getParameter("totalAmount");
        String shippingCharges = request.getParameter("shippingCharges");
        if (totalAmount != null) { 
    %>
    <p>Your order total is: <%= totalAmount %></p>
    <% } else { %>
    <p>Unable to retrieve total amount.</p>
    <% } %>
    <% 
        if (shippingCharges != null) { 
    %>
    <p>Shipping Charges: <%= shippingCharges %></p>
    <% } %>
    <br />
    <button><a href="paynow.jsp">Proceed to pay</a></button>
    </div>
    <script>
        $(document).ready(function() {
            // Event listener for checkout button click
            $("#checkoutBtn").click(function() {
                // Prompt for pincode
                var pincode = prompt("Enter your pincode:");
                if (pincode) { // User entered pincode
                    // Send AJAX request to check availability
                    $.ajax({
                        url: "CheckAvailabilityController",
                        method: "GET",
                        data: { pincode: pincode },
                        success: function(response) {
                            if (response.isServiceable) {
                                // Proceed with checkout
                                alert("All products are available. Proceeding with checkout.");
                                // Redirect to checkout page or perform checkout action
                                // window.location.href = "checkout.jsp";
                            } else {
                                // Show alert for unavailable products
                                alert("Some products are not available for this pincode.");
                            }
                        },
                        error: function(xhr, status, error) {
                            // Show error message
                            alert("Error checking availability: " + error);
                        }
                    });
                }
            });
        });
    </script>
</body>
</html>
