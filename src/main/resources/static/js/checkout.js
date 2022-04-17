$(document).ready(function () {
    $('#shipMethod').val("Ground");

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





    $('.s-radio').on('click', function () {
        if ($(this).prop('checked')) {
            $('#shipMethod').val($(this).val());
            console.log($('#shipMethod').val())
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
