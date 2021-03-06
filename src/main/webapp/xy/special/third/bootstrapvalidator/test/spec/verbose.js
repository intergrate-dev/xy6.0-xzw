describe('verbose option', function() {
    beforeEach(function() {
        $([
            '<form class="form-horizontal" id="verboseForm">',
                '<div class="form-group">',
                    '<input type="text" name="fullName" class="form-control" ',
                        'required data-bv-notempty-message="The full name is required and cannot be empty" ',
                        'data-bv-regexp="true" data-bv-regexp-regexp="^[a-zA-Z\\s]+$" data-bv-regexp-message="The full name can only consist of alphabetical, number, and space" ',
                        'data-bv-stringlength="true" data-bv-stringlength-min="8" data-bv-stringlength-max="40" data-bv-stringlength-message="The full name must be more than 8 and less than 40 characters long" ',
                    '/>',
                '</div>',
            '</form>'
        ].join('\n')).appendTo('body');

        // The order of validators are alphabetical:
        // - notEmpty
        // - regexp
        // - stringLength
    });

    afterEach(function() {
        $('#verboseForm').bootstrapValidator('destroy').remove();
    });

    it('set data-bv-verbose="false" for form', function() {
        var bv        = $('#verboseForm')
                            .attr('data-bv-verbose', 'false')
                            .bootstrapValidator('destroy')
                            .bootstrapValidator()
                            .data('bootstrapValidator'),
            $fullName = bv.getFieldElements('fullName'),
            messages;

        $fullName.val('');
        bv.validate();
        messages = bv.getMessages('fullName');
        expect(messages.length).toEqual(1);
        expect(messages[0]).toEqual($fullName.attr('data-bv-notempty-message'));

        bv.resetForm();
        $fullName.val('Spe@#$');
        bv.validate();
        messages = bv.getMessages('fullName');
        expect(messages.length).toEqual(1);
        expect(messages[0]).toEqual($fullName.attr('data-bv-regexp-message'));

        bv.resetForm();
        $fullName.val('Full');
        bv.validate();
        messages = bv.getMessages('fullName');
        expect(messages.length).toEqual(1);
        expect(messages[0]).toEqual($fullName.attr('data-bv-stringlength-message'));
    });

    it('set data-bv-verbose="false" for field', function() {
        var bv        = $('#verboseForm')
                            .attr('data-bv-verbose', 'true')
                            .find('[name="fullName"]')
                                .attr('data-bv-verbose', 'false')
                                .end()
                            .bootstrapValidator('destroy')
                            .bootstrapValidator()
                            .data('bootstrapValidator'),
            $fullName = bv.getFieldElements('fullName'),
            messages;

        $fullName.val('');
        bv.validate();
        messages = bv.getMessages('fullName');
        expect(messages.length).toEqual(1);
        expect(messages[0]).toEqual($fullName.attr('data-bv-notempty-message'));

        bv.resetForm();
        $fullName.val('Spe@#$');
        bv.validate();
        messages = bv.getMessages('fullName');
        expect(messages.length).toEqual(1);
        expect(messages[0]).toEqual($fullName.attr('data-bv-regexp-message'));

        bv.resetForm();
        $fullName.val('Full');
        bv.validate();
        messages = bv.getMessages('fullName');
        expect(messages.length).toEqual(1);
        expect(messages[0]).toEqual($fullName.attr('data-bv-stringlength-message'));
    });

    it('set verbose: "false" for form', function() {
        var bv        = $('#verboseForm')
                            .bootstrapValidator('destroy')
                            .bootstrapValidator({ verbose: false })
                            .data('bootstrapValidator'),
            $fullName = bv.getFieldElements('fullName'),
            messages;

        $fullName.val('');
        bv.validate();
        messages = bv.getMessages('fullName');
        expect(messages.length).toEqual(1);
        expect(messages[0]).toEqual($fullName.attr('data-bv-notempty-message'));

        bv.resetForm();
        $fullName.val('Spe@#$');
        bv.validate();
        messages = bv.getMessages('fullName');
        expect(messages.length).toEqual(1);
        expect(messages[0]).toEqual($fullName.attr('data-bv-regexp-message'));

        bv.resetForm();
        $fullName.val('Full');
        bv.validate();
        messages = bv.getMessages('fullName');
        expect(messages.length).toEqual(1);
        expect(messages[0]).toEqual($fullName.attr('data-bv-stringlength-message'));
    });

    // #1057
    it('set verbose: "false" for field', function() {
        var bv        = $('#verboseForm')
                            .attr('data-bv-verbose', 'true')
                            .bootstrapValidator('destroy')
                            .bootstrapValidator({
                                verbose: true,
                                fields: {
                                    fullName: {
                                        verbose: false
                                    }
                                }
                            })
                            .data('bootstrapValidator'),
            $fullName = bv.getFieldElements('fullName'),
            messages;

        $fullName.val('');
        bv.validate();
        messages = bv.getMessages('fullName');
        expect(messages.length).toEqual(1);
        expect(messages[0]).toEqual($fullName.attr('data-bv-notempty-message'));

        bv.resetForm();
        $fullName.val('Spe@#$');
        bv.validate();
        messages = bv.getMessages('fullName');
        expect(messages.length).toEqual(1);
        expect(messages[0]).toEqual($fullName.attr('data-bv-regexp-message'));

        bv.resetForm();
        $fullName.val('Full');
        bv.validate();
        messages = bv.getMessages('fullName');
        expect(messages.length).toEqual(1);
        expect(messages[0]).toEqual($fullName.attr('data-bv-stringlength-message'));
    });

    // #1055
    it('trigger "error.field.bv" event', function() {
        var validators = [],    // Array of not passed validators
            bv         = $('#verboseForm')
                            .attr('data-bv-verbose', 'true')
                            .bootstrapValidator('destroy')
                            .bootstrapValidator({
                                verbose: true,
                                fields: {
                                    fullName: {
                                        verbose: false
                                    }
                                }
                            })
                            .on('error.field.bv', function(e, data) {
                                validators.push(data.validator);
                            })
                            .data('bootstrapValidator'),
            $fullName  = bv.getFieldElements('fullName');

        $fullName.val('');
        bv.validate();
        expect(validators.length).toEqual(1);
        expect(validators[0]).toEqual('notEmpty');

        validators = [];
        bv.resetForm();
        $fullName.val('Spe@#$');
        bv.validate();
        expect(validators.length).toEqual(1);
        expect(validators[0]).toEqual('regexp');

        validators = [];
        bv.resetForm();
        $fullName.val('Full');
        bv.validate();
        expect(validators.length).toEqual(1);
        expect(validators[0]).toEqual('stringLength');
    });
});
