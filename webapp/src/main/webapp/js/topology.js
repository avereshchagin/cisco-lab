$(function () {
    var $topology = $('#topology');
    var id = $topology.data('id');
    $.ajax('api/lab/topology/' + id, {
        success: function (svg) {
            var length = $(svg).find('image').length;
            $topology.append($(svg).find('svg'));
            waitElements($topology, 'image', length, function () {
                var $devices = $('.device');
                $devices.click(function () {
                    if (this.id) {
                        window.open('api/jnlp/console' + this.id + '.jnlp');
                    }
                });
                $devices.mouseover(function () {
                    this.setAttribute('width', '120');
                    this.setAttribute('height', '120');
                    this.setAttribute('transform', 'translate(-10 -10)');
                });
                $devices.mouseout(function () {
                    $(this);
                    this.setAttribute('width', '100');
                    this.setAttribute('height', '100');
                    this.setAttribute('transform', '');
                })
            });
        }
    });

    function waitElements($root, classifier, length, onReady) {
        if ($root.find(classifier).length >= length) {
            onReady();
        } else {
            setTimeout(waitElements($root, classifier, length, onReady), 10);
        }
    }
});
