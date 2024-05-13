<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
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
            text-align: center;
            margin-bottom: 30px;
        }
        
        form {
            display: grid;
            gap: 20px;
        }
        
        label {
            font-weight: bold;
        }
        
        input[type="number"],
        input[type="text"],
        input[type="email"],
        button[type="submit"] {
            width: 100%;
            padding: 10px;
            font-size: 16px;
            border: 1px solid #ccc;
            border-radius: 5px;
            box-sizing: border-box;
        }
        
        button[type="submit"] {
            background-color: #007bff;
            color: #fff;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        
        button[type="submit"]:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Checkout</h1>
        <form id="payment-form">
            <div>
                <label for="amount">Amount to be Paid (INR)</label>
                <input type="number" id="amount" name="amount" required>
            </div>
            <div>
                <label for="name">Name</label>
                <input type="text" id="name" name="name" required>
            </div>
            <div>
                <label for="email">Email Address</label>
                <input type="email" id="email" name="email" required>
            </div>
            <button type="submit">Pay Now</button>
        </form>
    </div>

    <!-- Include Razorpay's JavaScript SDK -->
    <script src="https://checkout.razorpay.com/v1/checkout.js"></script>

    <script>
        document.addEventListener('DOMContentLoaded', function () {
            var form = document.getElementById('payment-form');

            form.addEventListener('submit', function (event) {
                event.preventDefault();

                var amount = document.getElementById('amount').value;
                var name = document.getElementById('name').value;
                var email = document.getElementById('email').value;

                // Create a new instance of Razorpay and initialize payment
                var options = {
                    key: 'rzp_test_D8mb0ELsdIMGVF', // Replace with your Razorpay Key
                    amount: amount * 100, // Amount in paise (Razorpay expects amount in paise)
                    currency: 'INR',
                    name: 'Your Company Name',
                    description: 'Payment for Order',
                    image: 'https://your-company-logo-url.png',
                    handler: function (response) {
                        // Handle payment success response
                        console.log(response);
                        // Redirect or perform any action on successful payment
                        window.location.href = 'paymentsuccessful.jsp?payment_id=' + response.razorpay_payment_id;
                    },
                    prefill: {
                        name: name,
                        email: email
                    }
                };

                var rzp = new Razorpay(options);
                rzp.open();
            });
        });
    </script>
</body>
</html>
