$(document).ready(function () {
    $.getJSON('api/user/me', function onSuccess(user) {
        if (user) {
            $('#me').text(user.displayName + ' [' + user.type.toLowerCase() + '] ');
            $('#me').append(
                $('<a>', {
                    text: 'Sign out',
                    href: '#',
                    click: signOut
                }));
        }

        function signOut() {
            $.ajax('api/user/out', {
                type: 'POST',
                success: function () {
                    window.location.reload();
                }
            });
            return false;
        }
    });
});
