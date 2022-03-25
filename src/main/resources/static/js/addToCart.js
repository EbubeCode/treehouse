$(document).ready(function () {
    $("#addToCart").on('click', function (event) {
        event.preventDefault();
        addToCart();
});
    $('#close-dialog').on('click', () => {
        $('#modal').addClass('hidden');
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
        $('#modal').removeClass('hidden');
        $('#modal-body').text(response);
        }
    ).fail(function() {
        alert("error adding item to car.")
    })
}