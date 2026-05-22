package com.eCommerce.Ecommerce.Services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service

public class RazorPayService {

    @Autowired
    private OrderService orderService;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    public String createOrder(int amount, String currency, String receiptId) throws RazorpayException {

     // pass your Razorpay API key and secret here
        RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // amount in the smallest currency unit ie we have to convert paisa into rupee
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", receiptId);

      
        Order order = razorpayClient.orders.create(orderRequest);
        System.out.println("Naya order mila :"+order);

        orderService.saveOrder(order);
        
       

        return order.toString();
    }
}
