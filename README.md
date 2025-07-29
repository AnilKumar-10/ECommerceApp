# E-Commerce-Application
			PLACING THE ORDER WITH ONLINE PAYMENT.
======================================================================

1. ADD THE ITEMS TO THE CART
   =>get the user cart
   intially the cart looks like
   {
   "id": "688351306ca5bf8261346324",
   "buyerId": "USER1040",
   "items": [],
   "totalAmount": 0.0,
   "updatedAt": "2025-07-25T09:41:04.445+00:00"
   }

   => Add the items to the cart with specific varient.
   {
   "productId": "P1024",
   "quantity": 1,
   "size": "M",
   "color": "Grey"
   }

   => After inserting the items the cart may looks line this
   {
   "id": "688351306ca5bf8261346324",
   "buyerId": "USER1040",
   "items": [{
   "itemId": 0,
   "productId": "P1024",
   "name": "Women's Culottes",
   "quantity": 1,
   "price": 1399.0,
   "size": "M",
   "color": "Grey",
   "addedAt": "2025-07-25T09:44:35.368+00:00"
   }, {
   "itemId": 1,
   "productId": "P1022",
   "name": "Women's High Waist Jeans",
   "quantity": 1,
   "price": 1599.0,
   "size": "32",
   "color": "Grey",
   "addedAt": "2025-07-25T09:46:04.323+00:00"
   }],
   "totalAmount": 2998.0,
   "updatedAt": "2025-07-25T09:46:04.323+00:00"
   }

=> Now place the order


2. PLACING THE ORDER.
   => To place the order select the products from the cart that to be place order.
   {
   "productIds": [0,1],
   "userId":"USER1040",
   "addressType":"HOME",
   "coupon":"SUMMER20",
   "payMode":"UPI"
   }

   => This will create the order with the selected products from the cart
   => As this is the online payment ordering the order is not yet confirmed, User has to pay the 	     amount inorder to proceed the order.

3. PAYING THE FINAL AMOUNT OF THE ORDER THROUGH UPI PAYMENT.
   => Initaite the payment process
   {
   "orderId":"101104",
   "userId":"USER1040",
   "amount":"2758.16",
   "method":"UPI-GPAY"
   }

   => This will initiate the payment and then we have to proceed to pay
   {
   "paymentId":"202035",
   "transactionId":"t1202",
   "status":"success"
   }

   => After paying the amount and get the success response then the order will be place and the 	     the shipping will starts begining.
   => After success full payment user receives the order confiramtion mail
   => Delivery agent of the city will recieve the order to delivery assigned mail that show the
   the order detials that to deliver

4. AFTER CONFIRMING THE ORDER THE STATUS OF THE ORDER WILL KEEP      CHANGES ACCORDINGLY
   {
   "id": "101104",
   "buyerId": "USER1040",
   "orderItems": [{
   "productId": "P1024",
   "name": "Women's Culottes",
   "quantity": 1,
   "size": "M",
   "color": "Grey",
   "price": 1399.0,
   "status": null,
   "tax": 167.88
   }, {
   "productId": "P1022",
   "name": "Women's High Waist Jeans",
   "quantity": 1,
   "size": "32",
   "color": "Grey",
   "price": 1599.0,
   "status": null,
   "tax": 191.88
   }],
   "addressId": "ADDR1040",
   "totalAmount": 2998.0,
   "couponId": "SUMMER20",
   "discount": 599.6,
   "tax": 359.76,
   "finalAmount": 2758.16,
   "refundId": null,
   "refundAmount": null,
   "orderStatus": "CONFIRMED",
   "paymentMethod": "UPI",
   "paymentStatus": "SUCCESS",
   "paymentId": "202035",
   "orderDate": "2025-07-25T09:48:57.559+00:00",
   "cancelReason": null,
   "cancellationTime": null,
   "shippingId": "58",
   "upiId": "987463214@ybl",
   "exchangeDetails": null,
   "returned": false,
   "cancelled": false
   }


5. AFTER THE ORDER IS CONFIRMED THE DELIVERY IS ASSIGNED WITH THE ORDERS TO DELIVER.
   => The order details and to delivery details will be assigned to delivery agent to delivery f	     field
   => These is how the delivery agent will get orders to deliver.
   {
   "id": "DP2008",
   "name": "Manoj Shetty",
   "phone": "9876100003",
   "toDeliveryItems": [{
   "shippingId": "58",
   "orderId": "101104",
   "userName": "Naveen Kumar",
   "address": {
   "id": "ADDR1040",
   "userId": "USER1040",
   "phoneNo": "9988556610",
   "street": "10-3, BTM Layout",
   "city": "Bengaluru",
   "state": "Karnataka",
   "country": "India",
   "postalCode": "560076",
   "type": "HOME"
   },
   "paymentMode": "UPI",
   "amountToPay": 0.0
   }],
   "active": true
   }



6. DELIVERYING THE ORDER TO THE CUSTOMER BY DELIVERY AGENT.
   => The delivery agent delivers the order and will change the status of the order to delivered
   {
   "orderId":"101104",
   "shippingId":"58",
   "newValue":"DELIVERED",
   "updateBy":"DP2008"
   }

   => These will update the order as DELIVERED.
   => After the delivery the delivery completed notification mail will sent to the customer.

7. NOW THE ORDER IS DELIVERED SUCCESSFULLY.
8. THE STOCK DETAILS OF THE ORDERED PRODUCTS ARE UPDATED.





			EXCHANGING THE PRODUCT (ONLINE PAY)
=======================================================================

1. AFTER SUCCESSFULL DELIVERY THE USER CAN HAVE THE OPTION TO EXCHANGE THE PRODUCT WITH OTHER PRODUCT.

2. USER NEED TO SELECT THE PRODUCT THAT TO REPLACE.  AND PRODUCT TO PICK
   => user provides the product to pick and product to deliver and the reason for the exchange.
   => user must select the new product varient
   {
   "orderId":"101104",
   "productIdToReplace":"P1024",
   "reasonToReplace":"Not as good as expected",
   "newProductId":"P1019",
   "quantity":"1",
   "size":"1",
   "color":"BLUE"
   }
   => This will make the exchange request with new product.
   {
   "orderId": "101104",
   "productIdToPick": "P1019",
   "productIdToReplace": "P1019",
   "amount": 100.0,
   "amountPayType": "PAYABLE",
   "paymentMode": "UPI"
   }
   => Here the amount of the new product is higher then the replacement one
   => So the user need to pay the remaining amount.
   => As this is the Online payment order the user need to pay the amount online only.
   => For making the payment we need to initiate the payment again
   {
   "orderId":"101104",
   "userId":"USER1040",
   "amount":100.0,
   "method":"UPI-GPAY"
   }
   => After initiating the payment, pay the amount by
   {
   "paymentId":"202036",
   "transactionId":"Trnas13",
   "status":"success"
   }
   => After successfull payment the exchange request is accepted and the delivery process start 	     beginning.
   => That exchange details will be like
   "exchangeDetails": {
   "replacementProductId": "P1019",
   "replacementPrice": 1499.0,
   "originalPrice": 1399.0,
   "exchangeDifferenceAmount": 100.0,
   "exchangeType": "PAYABLE",
   "paymentId": "202036",
   "paymentMode": "UPI",
   "paymentStatus": "SUCCESS",
   "refundId": null,
   "refundMode": null,
   "refundStatus": null,
   "reason": "Not as good as expected",
   "createdAt": "2025-07-25T10:29:16.238+00:00",
   "updatedAt": "2025-07-25T11:08:10.327+00:00"
   }
   => User is notified with the exchange details
   {
   "orderId": "101104",
   "productIdToPick": "P1024",
   "productIdToReplace": "P1019",
   "amount": 100.0,
   "amountPayType": "PAYABLE",
   "orderPaymentType": "UPI",
   "paymentStatus": "SUCCESS",
   "deliveryPersonId": "DP2008",
   "deliveryPersonName": "Manoj Shetty",
   "expectedReturnDate": "2025-07-30T10:44:07.543+00:00"
   }
   => Delivery agent is notified with the delivery details like pickup product and replace 	  	      product.
   {
   "toExchangeItems": [{
   "orderId": "101104",
   "userName": "Naveen Kumar",
   "productIdToPick": "P1024",
   "productIdToReplace": "P1019",
   "amount": 0.0,
   "address": {
   "id": "ADDR1040",
   "userId": "USER1040",
   "phoneNo": "9988556610",
   "street": "10-3, BTM Layout",
   "city": "Bengaluru",
   "state": "Karnataka",
   "country": "India",
   "postalCode": "560076",
   "type": "HOME"
   },
   "paymentMode": "UPI",
   "payable": false
   }],


3. AFTER THIS THE DELIVERY AGENT WILL DELIVERY THE PRODUCT AND PICK THE PRODUCT AND MAKE THE STATUS AS EXCHANGED.
   => After exchange done the Delivery agent  will updates the status.
   {
   "orderId":"101104",
   "deliveryPersonId":"DP2008",
   "exchanged":true,
   "anyDamage":false,
   "paymentStatus":"success"
   }

4. NOW THE ORDER DETAILS MAY LOOK LIKE THIS
   {
   "id": "101104",
   "buyerId": "USER1040",
   "orderItems": [{
   "productId": "P1024",
   "name": "Women's Culottes",
   "quantity": 1,
   "size": "M",
   "color": "Grey",
   "price": 1399.0,
   "status": "EXCHANGE_RETURNED",
   "tax": 167.88
   }, {
   "productId": "P1022",
   "name": "Women's High Waist Jeans",
   "quantity": 1,
   "size": "32",
   "color": "Grey",
   "price": 1599.0,
   "status": "DELIVERED",
   "tax": 191.88
   }, {
   "productId": "P1019",
   "name": null,
   "quantity": 1,
   "size": null,
   "color": "BLUE",
   "price": 1499.0,
   "status": "EXCHANGE_DELIVERED",
   "tax": 179.88
   }],
   "addressId": "ADDR1040",
   "totalAmount": 3098.0,
   "couponId": "SUMMER20",
   "discount": 599.6,
   "tax": 371.76,
   "finalAmount": 5908.12,
   "refundId": null,
   "refundAmount": null,
   "orderStatus": “EXCHANGED",
   "paymentMethod": "UPI",
   "paymentStatus": "SUCCESS",
   "paymentId": "202035",
   "orderDate": "2025-07-25T09:48:57.559+00:00",
   "cancelReason": null,
   "cancellationTime": null,
   "shippingId": "58",
   "upiId": "987463214@ybl",
   "exchangeDetails": {
   "replacementProductId": "P1019",
   "replacementPrice": 1499.0,
   "originalPrice": 1399.0,
   "exchangeDifferenceAmount": 100.0,
   "exchangeType": "PAYABLE",
   "paymentId": "202036",
   "paymentMode": "UPI",
   "paymentStatus": "SUCCESS",
   "refundId": null,
   "refundMode": null,
   "refundStatus": null,
   "reason": "Not as good as expected",
   "createdAt": "2025-07-25T10:29:16.238+00:00",
   "updatedAt": "2025-07-25T11:08:10.327+00:00"
   },
   "returned": true,
   "cancelled": false
   }

5. AFTER THE SUCCESSFULL EXCHANGE
   => the exchange details in the delivery agent object will removed.
   => The stock details of the returned product will updated
   => The status of the order will changes



=======================================================================
IF THE EXCHANGE AMOUNT IS REFUNDABLE.

1.  THE STEPS TILL THE EXCHANGE REQUEST IS SAME.

2.  IF THE EXCHANGE AMOUNT IS MORE THEN THE NEW PRODUCT.
    => That extra amount must be refundable.

3.  IF REFUNDABLE THEN THE REFUND IS INITIALIZED AFTER PLACING THE EXCHANGE REQUEST.

4.  THIS REFUND DETAILS IS INCLUDED IN THE EXCHANGE CONFIRAMATION MAIL.

5.  FOR THIS CUSTOMER DONT NEED TO PAY ANY AMOUNT(COD/ONLINE).

6. AFTER THE SUCCESSFUL EXCHANGE THE REFUNDABLE AMOUNT WILL BE CREDITED BACK TO THE UPI THEY HAVE WITH THEIR PROFILE.