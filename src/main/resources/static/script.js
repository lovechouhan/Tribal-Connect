// Convert this file to pure JS: ensure a button exists, load Razorpay script dynamically,
// and use a valid options object (no embedded HTML or broken comment line breaks).
(function () {
  // Load Razorpay checkout script if not already present
  function loadRazorpay(cb) {
	if (window.Razorpay) {
	  return cb();
	}
	var s = document.createElement('script');
	s.src = 'https://checkout.razorpay.com/v1/checkout.js';
	s.async = true;
	s.onload = cb;
	s.onerror = function () { console.error('Failed to load Razorpay script'); };
	document.head.appendChild(s);
  }

  // Ensure the pay button exists in the DOM
  var btn = document.getElementById('rzp-button1');
  if (!btn) {
	btn = document.createElement('button');
	btn.id = 'rzp-button1';
	btn.textContent = 'Pay';
	document.body.appendChild(btn);
  }

  loadRazorpay(function () {
	var options = {
	  key: 'rzp_test_RTq0pZ3gH0hMCJ', // Enter the Key ID generated from the Dashboard
	  amount: '50000', // Amount is in currency subunits. Default currency is INR. Hence, 50000 refers to 50000 paise
	  currency: 'INR',
	  name: 'TribalConnect',
	  description: 'Test Transaction',
	  image: 'https://placehold.co/100x100/6B4226/FDF6E9?text=TC',
	  order_id: 'order_9A33XWu170gUtm', // This is a sample Order ID. Pass the id obtained in the response of Step 1
	  handler: function (response) {
		alert(response.razorpay_payment_id);
		alert(response.razorpay_order_id);
		alert(response.razorpay_signature);
	  },
	  prefill: {
		name: 'Gaurav Kumar',
		email: 'gaurav.kumar@example.com',
		contact: '9999999999'
	  },
	  notes: {
		address: 'Razorpay Corporate Office'
	  },
	  theme: {
		color: '#3399cc'
	  }
	};

	var rzp1 = new Razorpay(options);
	rzp1.on('payment.failed', function (response) {
	  alert(response.error.code);
	  alert(response.error.description);
	  alert(response.error.source);
	  alert(response.error.step);
	  alert(response.error.reason);
	  alert(response.error.metadata.order_id);
	  alert(response.error.metadata.payment_id);
	});

	btn.addEventListener('click', function (e) {
	  rzp1.open();
	  e.preventDefault();
	});
  });
})();