$(document).ready(function () {
    var options = {
        success: onSuccess,
        dataType: 'json',
        clearForm: true
    };
    $('#loginForm').ajaxForm(options);

    function onSuccess(response) {
        if (response == 'ok') {
            window.location = 'index.xhtml';
        } else {
            $('.normalPanel').switchClass('normalPanel', 'errorPanel', 500);
            $('#errorMessage').text('Incorrect login or password.');
            $('#errorMessage').fadeIn();
        }
    }
});