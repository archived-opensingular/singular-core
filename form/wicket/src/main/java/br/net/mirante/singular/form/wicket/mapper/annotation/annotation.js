if( window.Annotation == undefined){
    window.Annotation = function (target_id, this_id, approved_id){
        this.target_component = $(target_id);
        this.this_component = $(this_id);
        this.approved = $(approved_id);

        var target = '#<wicket:container wicket:id="referenced_id" />';
        var thiz = '#<wicket:container wicket:id="this_id" />';

    }

    window.Annotation.prototype = {
        define_button_color: function() {
            var isApproved = this.approved.bootstrapSwitch('state');
            if (isApproved  ) {
                return 'btn-info';
            }
            return 'btn-danger';
        },

        setup : function(){
            var thiz = this;

            var button_color = this.define_button_color();

            var show_button = $('<a>')
                .addClass('btn btn-circle btn-icon-only '+button_color)
                .attr('href','javascript:;')
                .append($('<i>').addClass('fa fa-comment-o'))

            this.approved.on('switchChange.bootstrapSwitch', function(event, state) {
                show_button.removeClass('btn-default')
                show_button.removeClass('btn-info')
                show_button.removeClass('btn-danger')
                show_button.addClass(thiz.define_button_color());
            })


            this.target_component.find('h3').append(
                $('<div>')
                    .attr('style','position:absolute;top:10px;right: 15px;')
                    .append(show_button)
                    .click(function(){
                        if(!thiz.this_component.is(":visible")){
                            thiz.this_component.fadeIn();
                        }else{
                            thiz.this_component.fadeOut();
                        }
                    })
            );
            thiz.this_component.css('position','absolute')
            var target_offset = thiz.target_component.parent().offset()['top'],
                this_offset = thiz.this_component.parent().offset()['top'];
            thiz.this_component.css('top',(target_offset-this_offset)+"px");
        }
    }


}
