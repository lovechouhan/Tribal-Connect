# 🌿 Tribal Connect

![Tribal Connect Banner](https://via.placeholder.com/1200x400.png?text=Tribal+Connect+-+Empowering+Artisans)

> **"Every handcrafted piece tells a story. Let's listen."**

Welcome to **Tribal Connect**! 👋 

This isn't just another online store. It's a bridge. We built Tribal Connect to close the gap between incredibly talented tribal artisans in rural communities and people around the world who appreciate authentic, handcrafted heritage. 

By shopping here, you're not just buying a product; you're supporting a family, preserving an ancient craft, and hearing the story of the artisan who made it.

---

## 📖 What makes us special?

We wanted to build something that felt human and deeply connected to its roots. Here is what we've poured our hearts into:

- **Artisan Stories:** We don't just list products. We share the cultural heritage and personal stories behind the hands that crafted them. 
- **Digital Tipping:** Loved a product? You can leave a voluntary tip during checkout that goes straight to the artisan to show your appreciation!
- **A Safe Space for Creators:** A dedicated, easy-to-use dashboard for artisans to manage their crafts, track their sales, and see the tips they've earned.

---

## ✨ Features We Love

### 🛍️ For the Shopper
- **Discover with Ease:** A smooth, beautiful interface to browse unique categories.
- **Save for Later:** A handy wishlist for when you want to keep an eye on something special.
- **Fast, Secure Checkout:** Seamless payments powered by Razorpay, so you can shop with peace of mind.

### 🧑‍🎨 For the Artisan
- **Your Own Canvas:** Upload your beautiful product images easily (thanks to Cloudinary!).
- **Financial Transparency:** Keep track of every sale and tip you receive through your personal payment dashboard.
- **Stay Updated:** Get important updates directly on your phone via Twilio SMS.

### 🔒 Under the Hood
We care about keeping everyone's data safe, so we've baked in some solid security:
- **Email Verification:** A secure 10-minute OTP system so we know you are you.
- **Bulletproof Tech:** Built with Java Spring Boot, Spring Security, JWT, and a reliable MySQL database.

---

## 🧰 How It's Built

If you're a fellow developer, here's a quick look at the tools we used to bring this vision to life:

| What we used | Why we used it |
| --- | --- |
| **Java & Spring Boot** | For a rock-solid, scalable backend that won't let us down. |
| **Thymeleaf & HTML/CSS** | To server-render a beautiful, responsive frontend that's easy to read and navigate. |
| **MySQL** | To keep all our data, orders, and user information safely stored. |
| **JWT & Spring Security** | To make sure our buyers and sellers are always protected. |
| **Razorpay, Cloudinary & Twilio** | To handle payments, image hosting, and SMS smoothly. |

---

## ⚙️ Want to run it locally?

We'd love for you to try it out on your own machine! Here's how you can get it up and running:

### What you'll need:
- Java 17+ 
- Maven 
- MySQL Server
- API Keys for Razorpay, Cloudinary, Twilio, and your SMTP email credentials.

### Steps to get started:

1. **Grab the code:**
   ```bash
   git clone https://github.com/yourusername/tribal-connect.git
   cd tribal-connect/Apni-Dukaan
   ```

2. **Set up your environment:**
   Open up `src/main/resources/application.properties` and add your database details and API keys:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/tribal_connect_db
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   
   # Add your magic keys here!
   razorpay.key.id=YOUR_RAZORPAY_KEY
   razorpay.key.secret=YOUR_RAZORPAY_SECRET
   cloudinary.cloud_name=YOUR_CLOUD_NAME
   # ... plus your Twilio & SMTP configs
   ```

3. **Build it:**
   ```bash
   mvn clean install
   ```

4. **Run it:**
   ```bash
   mvn spring-boot:run
   ```

5. **See it live!**
   Head over to `http://localhost:8080` in your browser.

---

## 🤝 Join the Movement

We are always looking for ways to make Tribal Connect better. Whether it's squashing a bug, adding a cool new feature, or just fixing a typo, your help means the world to us. 

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingIdea`)
3. Commit your Changes (`git commit -m 'Added an amazing idea'`)
4. Push to the Branch (`git push origin feature/AmazingIdea`)
5. Open a Pull Request and say hi!

---

## 📄 License
This project is open-source and available under the MIT License.

---

## 🫂 Say Hello!
Built with ❤️ by **Love Chouhan** and our amazing contributors. 
