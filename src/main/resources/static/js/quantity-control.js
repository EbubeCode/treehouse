$(document).ready(function () {
    let quantityId;
    $('.minusButton').on('click', function (event) {
        event.preventDefault();

        let productId = $(this).attr('pid');
        quantityId = "#quantity"+productId
        let quantityInput = $(quantityId);
        let newQty = parseInt(quantityInput.val()) - 1;

        if (newQty > 0)
            quantityInput.val(newQty)
    })
    $('.plusButton').on('click', function (event) {
        event.preventDefault();


        let productId = $(this).attr('pid');
        quantityId = "#quantity"+productId
        let quantityInput = $(quantityId);
        let newQty = parseInt(quantityInput.val()) + 1;

        if (newQty <= 10)
            quantityInput.val(newQty)
    })
})