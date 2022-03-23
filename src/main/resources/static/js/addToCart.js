$(document).ready(function () {
    $("#addToCart").on('click', function () {
        addToCart();
})
})

function addToCart() {

    let quantity = $("#quantity"+productId).val();
    let url = "/addItem/" + productId + "/" + quantity;

    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function(response) {
        alert(response);
        }
    ).fail(function() {
        alert("error adding item to car.")
    })
}