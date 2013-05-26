$(function () {
    $('#createLabForm').ajaxForm({
        success: function (response) {
            alert(response);
        }
    });
});