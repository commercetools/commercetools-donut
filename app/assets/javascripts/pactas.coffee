$ ->
    checkoutForm = $("#form-checkout")

    paymentConfig = { publicApiKey: "532846f751f459b0d07df5fd" }

    iteroJS = new IteroJS.Signup()
    iteroJSPayment = new IteroJS.Payment( paymentConfig
        -> console.log "iteroJS payment loaded"
        -> console.error "iteroJS payment failed to load"
    )

    checkoutForm.submit ->
        return true unless checkoutForm.find("input[name=orderId]").length < 1

        cart = {
            planVariantId: $("#pactas-variant").val()
            currency: $("#transaction-form-currency").val()
        }

        customerData = {
            firstName: $("#first-name").val()
            lastName: $("#last-name").val()
            address: {
                addressLine1: $("#address").val()
                city: $("#address-city").val()
                country: $("#address-country").val()
                postalCode: $("#address-postal-code").val()
            }
        }

        paymentData = {
            bearer: "CreditCard:Paymill"
            cardNumber: $("#transaction-form-number").val()
            cardHolder: $("#transaction-form-name").val()
            cvc: $("#transaction-form-cvc").val()
            expiryMonth: $("#transaction-form-month").val()
            expiryYear: $("#transaction-form-year").val()
        }

        iteroJS.subscribe( iteroJSPayment
            cart
            customerData
            paymentData
            (subscription) =>
                console.log "Successful register: ", subscription
                checkoutForm.append("<input type='hidden' name='orderId' value='#{subscription.OrderId}'>")
                checkoutForm.submit()
            (errorData) ->
                console.error "Error from iteroJS: ", errorData
                checkoutForm.find("#error-message").text "Oops something went wrong... Please review the form and try again later."
        )

        return false
