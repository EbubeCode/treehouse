$(document).ready(() => {

    $('.minusButton').on('click', function (event) {
        event.preventDefault();

        let itemId = $(this).attr('pid');
        let quantityInput = $("#quantity" + itemId);
        let newQty = parseInt(quantityInput.val()) - 1;
        if (newQty > 0) {
            let url = "/updateCartItem/" + itemId + "/" + newQty;
            addToCart(url, quantityInput, itemId);
        }

    })
    $('.plusButton').on('click', function (event) {
        event.preventDefault();


        let itemId = $(this).attr('pid');
        let quantityInput = $("#quantity" + itemId);
        let newQty = parseInt(quantityInput.val()) + 1;

        if (newQty <= 10) {
            let url = "/updateCartItem/" + itemId + "/" + newQty;
            addToCart(url, quantityInput, itemId);
        }
    })
    $('.delete-icon').on('click', function (event) {
        event.preventDefault();

        let url = $(this).attr('href');
        $.ajax({
            type: "DELETE",
            url: url,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeaderName, csrfValue);
            }
        }).done(function (response) {
                $("#cart" + response).remove();
                calcTotal();
            }
        ).fail(function () {
            alert("error adding item to cart.")
        })
    })
})

function addToCart(url, input, id) {


    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (response) {
            input.val(response);
            let subTotal = $("#subTotal" + id);
            let price = $("#price" + id);
            let newSubTotal = parseFloat(price.text()) * parseInt(response);
            subTotal.text(newSubTotal.toFixed(2));
            calcTotal();
        }
    ).fail(function () {
        alert("error adding item to cart.")
    })
}

function calcTotal() {
    let total = $('#grand-total');
    let newTotal = 0;
    $('.sub-total').each(function () {

        newTotal += parseInt($(this).text());
    });
    total.text(newTotal)
    let discountDiv = $('#discount-div');
    if (newTotal >= 50) {

        let discount = $('#discount');
        if (discountDiv.hasClass('hidden'))
            discountDiv.removeClass('hidden');
        discount.text((newTotal - (newTotal * 0.1)).toFixed(2))
    } else {
        if (!discountDiv.hasClass('hidden'))
            discountDiv.addClass('hidden');
    }
    if (newTotal <= 0)
        $('#check-out').addClass('hidden')
    else
        if ($('#check-out').hasClass('hidden'))
            $('#check-out').removeClass('hidden');
}