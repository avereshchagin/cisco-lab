$(function () {
    var id = $('#labId').data('id');
    $.getJSON('api/lab/' + id, function (lab) {
        $('#labName').text(lab.name);
        $('#labTrainer').find('tbody').append(createRow(lab.trainer));
    });
    $.getJSON('api/lab/students/' + id, function(students) {
        $labStudents = $('#labStudents').find('tbody');
        $.each(students, function (index, student) {
            $labStudents.append(createRow(student));
        })
    });

    function createRow(user) {
        var $tr = $('<tr>');
        $tr.append($('<td>', {text: user.login}));
        $tr.append($('<td>', {text: user.password}));
        return $tr;
    }
});