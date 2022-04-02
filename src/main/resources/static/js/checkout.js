$(document).ready(function () {

    $('.radio').on('click', function () {
        let radioId = parseInt($(this).attr('id').substring(5));
        let url = "/userShipping/" + radioId;

        $.ajax({
            type: "GET",
            url: url,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeaderName, csrfValue);
            }
        }).done(function(response) {
            switchShipping(response);
            }
        ).fail(function() {
            alert("error loading resource.")
        })
    })

    $('.pRadio').on('click', function () {
        let radioId = parseInt($(this).attr('id').substring(7));
        let url = "/userPayment/" + radioId;

        $.ajax({
            type: "GET",
            url: url,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeaderName, csrfValue);
            }
        }).done(function(response) {
            switchPayment(response, response.userBilling);
            }
        ).fail(function() {
            alert("error loading resource.")
        })
    })

    $('#ship-bill').on('click', function () {
        if ($(this).prop('checked')) {
            $('#userBillingZipcode').val($('#userShippingZipcode').val())
            $('#userBillingStreet').val($('#userShippingStreet').val())
            $('#userBillingCity').val($('#userShippingCity').val())
            $('#userBillingState').val($('#userShippingState').val())
            $('#userBillingCountry').val($('#userShippingCountry').val())
        }
    })

    $('.s-radio').on('click', function () {
        if ($(this).prop('checked')) {
            $('#shipMethod').val($(this).val());
            if($(this).val() === 'Air') {
                $('#shipping-price').text("30.00");
                let total = 30.00 + parseFloat($("#order-total").text()) + parseFloat($("#tax").text());
                $("#grand-total").text(total);
            }
            else {
                $('#shipping-price').text("10.00");
                let total = 10.00 + parseFloat($("#order-total").text()) + parseFloat($("#tax").text());
                $("#grand-total").text(total);
            }
        }
    })
})

function switchShipping(userShipping) {
    $('#userShippingZipcode').val(userShipping.userShippingZipcode);
    $('#userShippingStreet').val(userShipping.userShippingStreet);
    $('#userShippingState').val(userShipping.userShippingState);
    $('#userShippingCountry').val(userShipping.userShippingCountry);
    $('#userShippingCity').val(userShipping.userShippingCity);
}

function switchPayment(userPayment, userBilling) {
    $('#holderName').val(userPayment.holderName)
    $('#cardNumber').val(userPayment.cardNumber)
    $('#cvc').val(userPayment.cvc)
    $('#type').val(userPayment.cardType)
    $('#expiryMonth').val(userPayment.expiryMonth)
    $('#expiryYear').val(userPayment.expiryYear)

    $('#userBillingZipcode').val(userBilling.userBillingZipcode)
    $('#userBillingStreet').val(userBilling.userBillingStreet)
    $('#userBillingCity').val(userBilling.userBillingCity)
    $('#userBillingState').val(userBilling.userBillingState)
    $('#userBillingCountry').val(userBilling.userBillingCountry)
    $('#ship-bill').prop('checked', false   )
}