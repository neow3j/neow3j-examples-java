# neow3j Spring Boot Examples for Neo Legacy

This project contains a Sprint Boot example that uses neow3j to interact with the Neo Legacy
blockchain.

You can start the DemoApplication and then interact with the API through the provided mappings
implemented in `Payment.java`. 


## Instructions

To run the examples, either use the built-in features of your IDE to run Spring Boot applications
or use the command line as follows.

Run the following command in the project root.

```
./gradlew bootRun
```

Open a new terminal window or tab and from where you can make two API calls implemented by this
demo application.

Get the balance of an address (i.e. here: AK2nJJpJr6o664CWJKi1QRXjqeic2zRp8y):

```
curl --request GET 'http://127.0.0.1:8080/balance?address=AK2nJJpJr6o664CWJKi1QRXjqeic2zRp8y'
```

Make a payment of 10 neo to an address (i.e. here: AJmjUqf1jDenxYpuNS4i2NxD9FQYieDpBF):

```
curl --request POST 'http://127.0.0.1:8080/pay?amount=10&address=AJmjUqf1jDenxYpuNS4i2NxD9FQYieDpBF'
```

(The wallet created in the application contains the private key for the address
AK2nJJpJr6o664CWJKi1QRXjqeic2zRp8y. The payments are done from this address.)