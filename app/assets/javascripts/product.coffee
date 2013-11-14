$ ->
    productForm = new Form $('#product-form')
    cartButton = $("#go-to-cart")

    enableSubmit = (place) ->
        place.find("[data-toggle=tooltip]").hide()
        place.find("button[type=submit]").removeProp("disabled")

    disableSubmit = (place) ->
        place.find("[data-toggle=tooltip]").show()
        place.find("button[type=submit]").prop("disabled")

    scrollTo = (place) ->
        $('html, body').animate scrollTop: place.offset().top, 'slow'


    # Scroll to product when header picture is clicked
    $("#image-header").click ->
        scrollTo $("#product-box")

    # Scroll to frequency when product is selected
    $('input[name=variantId]').change ->
        scrollTo $("#product-often")

    # Enable submit if all is selected after changing a form value
    productForm.inputs.change ->
        enableSubmit(cartButton) if productForm.validateRequired(false, false)

    # Enable tooltip
    cartButton.find("[data-toggle=tooltip]").tooltip()

    # Enable submit if all is selected on load
    enableSubmit(cartButton) if productForm.validateRequired(false, false)
