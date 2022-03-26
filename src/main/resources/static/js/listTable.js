$(document).ready(function () {

    $('.delete-row').on('click', function (event) {
        event.preventDefault();

        let url = $(this).attr('href');
        $.ajax({
            type: "DELETE",
            url: url,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeaderName, csrfValue);
            }
        }).done(function (response) {
                $(rowId + response).remove();
            }
        ).fail(function () {
            alert("Couldn't delete row.")
        })
    })
})