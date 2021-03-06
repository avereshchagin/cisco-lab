$(document).ready(function () {
    function pingRack(id) {
        if (id) {
            $.getJSON('api/rack/ping/' + id, function (status) {
                if (status && status === 'online') {
                    $('#status' + id).attr('data', 'images/online.svg');
                } else {
                    $('#status' + id).attr('data', 'images/offline.svg');
                }
            });
        }
    }

    function loadRackData() {
        $.getJSON('api/rack', function (racks) {
            var $tbody = $('#rackTable').find('tbody');
            $tbody.empty();
            $.each(racks, function (index, rack) {
                var $tr = $('<tr>');
                var $tdName = $('<td>');
                $tdName.append($('<object>', {type: 'image/svg+xml', id: 'status' + rack.id}));
                $tdName.append(rack.name);
                $tr.append($tdName);
                $('<td>', {text: rack.localIP}).appendTo($tr);
                $('<td>', {text: rack.localTerminalPort}).appendTo($tr);
                $('<td>', {text: rack.localControlPort}).appendTo($tr);
                $('<td>', {text: rack.externalIP}).appendTo($tr);
                $('<td>', {text: rack.externalTerminalPort}).appendTo($tr);
                $('<td>', {text: rack.externalControlPort}).appendTo($tr);
                $tr.append(createLinksCell('api/rack/', rack.id, loadRackData));
                $tbody.append($tr);

                pingRack(rack.id);
            });
        });
        loadDeviceData();
    }

    function loadDeviceData() {
        $.getJSON('api/device', function (devices) {
            var $tbody = $('#deviceTable').find('tbody');
            $tbody.empty();
            $.each(devices, function (index, device) {
                var $tr = $('<tr>');
                $('<td>', {text: device.name}).appendTo($tr);
                $('<td>', {text: device.rack ? device.rack.name : ''}).appendTo($tr);
                $('<td>', {text: device.path}).appendTo($tr);
                var $tdLinks = $('<td>');
                $tdLinks.append($('<a>', {
                    href: '#',
                    text: "Delete",
                    click: getDelete('api/device/' + device.id, loadDeviceData)
                }));
                $tdLinks.append(' ');
                $tdLinks.append($('<a>', {
                    href: 'api/jnlp/console' + device.id + '.jnlp',
                    text: 'Open terminal'
                }));
                $tr.append($tdLinks);
                $tbody.append($tr);
            });
        });
    }

    loadRackData();

    function createLinksCell(resource, id, onSuccess) {
        var td = $('<td>');
        td.append($('<a>', {href: '#', text: "Delete", click: getDelete(resource + id, onSuccess)}));
        return td;
    }

    function createInputRow($table, resource, inputs, loader) {
        var $tr = $('<tr>');
        $.each(inputs, function (key, value) {
            $tr.append($('<td>').append($('<input>', {name: key, type: 'text', width: value})));
        });
        $tr.append($('<td>').append(
            $('<a>', {href: '#', text: "Save", click: getOnSave($table, $tr, resource, loader)})));
        $table.find('tfoot').append($tr);
    }

    $('#addRack').click(function () {
        createInputRow($('#rackTable'), 'api/rack', {
            name: 100,
            localIP: 100,
            localTerminalPort: 75,
            localControlPort: 75,
            externalIP: 100,
            externalTerminalPort: 75,
            externalControlPort: 75
        }, loadRackData);
    });

    function getDelete(path, onSuccess) {
        return function () {
            $.ajax(path, {
                type: 'DELETE',
                success: function () {
                    onSuccess();
                }
            });
            return false;
        }
    }

    function getOnSave($table, $element, resource, loader) {
        return function () {
            var json = {};
            $element.find('input').each(function () {
                if ($(this).val()) {
                    json[$(this).attr('name')] = $(this).val();
                }
            });
            $.ajax(resource, {
                data: JSON.stringify(json),
                contentType: 'application/json',
                type: 'POST',
                success: function () {
                    $table.find('tfoot').empty();
                    loader();
                }
            });
            return false;
        }
    }
});