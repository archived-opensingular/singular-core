if( window.substringMatcher == undefined) {

    window.substringMatcher = function (value_list) {
        this.clearText = function(x){return S(x).latinise().s ;};
        return function findMatches(q, cb) {
            var matches = [];

            substrRegex = new RegExp(clearText(q), 'i');

            $.each(value_list, function (i, value) {
                if (substrRegex.test(clearText(value['value']))) {
                    matches.push(value);
                }
            });

            cb(matches);
        };
    };

}