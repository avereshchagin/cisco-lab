$(function () {
    $.getJSON('api/user/me', function (user) {
        if (user) {
            var $me = $('#me');
            $me.text(user.displayName + ' [' + user.type.toLowerCase() + '] ');
            $me.append(
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

    $.getJSON('api/menu', function (items) {
        var $menu = $('#menu');
        $.each(items, function (index, item) {
            $menu.append($('<a>', {
                text: item[0],
                href: item[1]
            }));
        });
    });
});
