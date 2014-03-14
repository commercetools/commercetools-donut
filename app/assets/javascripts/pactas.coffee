$ ->
    iteroJS = {}
    iteroJSPayment = {}

    paymentConfig = { publicApiKey: "523075921d8dd007f822edaa" }

    iteroJS = new IteroJS.Signup()
    iteroJSPayment = new IteroJS.Payment( paymentConfig
        -> console.log("iteroJS payment loaded")
        -> console.log("iteroJS payment FAILED to load")
    )

    $("#form-checkout").submit ->
        cart = {
            planVariantId: $("#pactas-variant").val()
            currency: $("#pactas-currency").val()
        }

        customerData = {
            firstName: $("#first-name").val()
            lastName: $("#last-name").val()
            address : {
                addressLine1: $("#address").val()
                city: $("#address-city").val()
                country: $("#address-country").val()
                postalCode: $("#address-postal-code").val()
            }
        }

        paymentData = {
            "bearer": "CreditCard:Paymill"
            "cardNumber": $("#transaction-form-number").val()
            "cardHolder": $("#transaction-form-name").val()
            "cvc": $("#transaction-form-cvc").val()
            "expiryMonth": $("#transaction-form-month").val()
            "expiryYear": $("#transaction-form-year").val()
        }

        console.log(cart)
        console.log(customerData)
        console.log(paymentData)

        iteroJS.subscribe( iteroJSPayment
            cart
            customerData
            paymentData
            (subscribeResult) -> console.log("subscribe returned", subscribeResult)
            (errorData) ->  console.log("error: ", errorData)
        )
        return false
