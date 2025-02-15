﻿# booking application service 

<h2>Description: 📝</h2>
<body><i>
The Booking Service Application is a robust system built using Spring Boot technology.
It facilitates a complete booking process, enabling admins and users to interact seamlessly.
Admins (MANAGER authority) manage amenities and accommodations, while users (CUSTOMER authority) can book
accommodations, make payments, and manage their bookings. The application integrates third-party APIs such as
Telegram Bot API and Stripe API for enhanced functionality. For a comprehensive walkthrough,
refer to the <a href="http://localhost:5606/booking-api/swagger-ui/index.html#/">Swagger Documentation</a>.
</i></body>

<h2>Features: ✨</h2>
<body>
<ul>
<li><strong>Register and login system</strong>:<br>
<em>If user doesn't match admin pattern `admin([1-9][0-9]?)@.*`, he/she gets CUSTOMER authority and is able to
browse available accommodation and amenities, create bookings for selected accommodations and 
process payments for bookings.<br> if user matches admin-pattern he/she gets MANAGER authority and in addition to all
above, admin allows to create, update, and delete amenities and accommodations.<br>
</em>
</li>
<li>
<strong>Notification Bot</strong>:<br>
<em>With Telegram Bot we can receive new notifications about what happened to Amenity, Accommodation, Booking or Payment.
For example, created or deleted amenity, released accommodation, confirmed or canceled booking and payment.
The bot is called `vanii_notification_bot`.<br>
</em>
</li>
<li>
<strong>Stripe API</strong>:<br>
<em>Stripe simplifies and helps to manage and process payments. For example, after the user completes the payment,
Stripe will redirect them back to either the success or cancel URL, based on the outcome of the payment process.
</em>
</li>
</ul>
</body>

<h2>Setting up: ✨</h2>
<body><i>To be able to use the booking application, you need to have several services installed:</i></body>

- java (jdk 17)
- maven (3.9.9)
- docker (27.2.0)

<body><i>Second thing to do is to set the environment variables to your .env file:</i></body>

`.env_template:`
```angular2html
POSTGRES_DATABASE=database_name
POSTGRES_USER=my_db_user
POSTGRES_PASSWORD=password
POSTGRES_DOCKER_PORT=5432
POSTGRES_LOCAL_PORT=2345
SPRING_DOCKER_PORT=8080
SPRING_LOCAL_PORT=0880
DEBUG_PORT=5005
JWT_SECRET_STRING=ilovedrinkingteanotcoffeeilovedrinkingteanotcoffeeilovedrinkingteanotcoffee
STRIPE_API_KEY=sk_my_stripe_key
TELEGRAM_BOT_TOKEN=your_telegram_bot_token
TELEGRAM_BOT_USERNAME=your_notification_bot
ADMIN_CHAT_ID=admin_id
```

- `SPRING_LOCAL_PORT` will be used in your endpoint locally, when you will send any requests. 
- `POSTGRES_LOCAL_PORT` same idea as with SPRING_LOCAL_PORT, but gives access to the database when container is running.
- `DEBUG_PORT` can be used to do some debugs (you need to add a new _remote JVM debug_ configuration).
- `JWT_SECRET_STRING` used for initializing a secret key to handle JWT securely. 
- `STRIPE_API_KEY` your stripe API secret key (if you want to use it locally test secret api key is fine).
- `TELEGRAM_BOT_TOKEN` you get it from BotFather once you created your notification bot. 

_Once all is ready, you only need to create a `.jar` file (run `mvn clean package` in terminal),
build/rebuild a docker image (run `docker-compose build` in terminal)
and finally start a docker container (run `docker-compose up` in terminal).
Now you can use this application locally on you computer._

<h2>Example os usage: ✨</h2>

_You can call POST, PUT, PATCH, GET, DELETE request using this application. Some examples are present in the following table:_

<body>
    <table>
        <thead>
            <tr>
                <th>Action</th>
                <th>Request Type</th>
                <th>Path</th>
                <th>RequestDTO</th>
                <th>ResponseDTO</th>
                <th>Required Authority</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>Get an accommodation</td>
                <td>GET</td>
                <td>/booking-api/accommodations/{id}</td>
                <td><i>EMPTY</i></td>
                <td>AccommodationResponseDto</td>
                <td>CUSTOMER</td>
            </tr>
            <tr>
                <td>Register new User</td>
                <td>POST</td>
                <td>/booking-api/auth/register</td>
                <td>RegistrationRequestDto</td>
                <td>RegistrationResponseDto</td>
                <td>CUSTOMER</td>
            </tr>
            <tr>
                <td>Create new Payment</td>
                <td>POST</td>
                <td>/booking-api/payments</td>
                <td>PaymentRequestDto</td>
                <td>PaymentResponseDto</td>
                <td>CUSTOMER</td>
            </tr>
            <tr>
                <td>Create new Amenity</td>
                <td>POST</td>
                <td>/booking-api/amenities</td>
                <td>AmenityRequestDto</td>
                <td>AmenityResponseDto</td>
                <td>MANAGER</td>
            </tr>
            <tr>
                <td>Update status of Booking</td>
                <td>PATCH</td>
                <td>/booking-api/bookings/{id}</td>
                <td>UpdateStatusDto</td>
                <td><i>EMPTY</i></td>
                <td>MANAGER</td>
            </tr>
            <tr>
                <td>Update Amenity</td>
                <td>PUT</td>
                <td>/booking-api/amenities/{id}</td>
                <td>AmenityRequestDto</td>
                <td>AmenityResponseDto</td>
                <td>MANAGER</td>
            </tr>
        </tbody>
    </table>
</body>

_Totally, this api has more than 25 different endpoints you can send a request to._

<h2>Project's Architecture ✨</h2>
The Booking-API follows Three-Layer Architecture: 
- Presentation Layer (Controller layer)
- Business Logic Layer (Service layer)
- Data Access Layer (Repository layer)

<body>
<i><b>Benefits of using it:</b> <br>
Firstly, with this architecture each layer has a specific responsibility, making the system easier to manage and
maintain. Secondly, layers can be scaled independently of each other. For example, if we decide to add new logic
to repository layer our service layer will remain untouched. Finally, it has a big influence on tests. Following this
architecture it is much easier to test each layer independently.</i></body> 

<h2>Entities Relations ✨</h2>
<img src="src/main/resources/images/erd.png" alt="erd" width="500">

<h2>Test Coverage ✨</h2>
<body><i>The project has over 80 test methods which cover all architecture layers</i></body>

<img src="src/main/resources/images/test-coverage.png" alt="tests" width="500">

