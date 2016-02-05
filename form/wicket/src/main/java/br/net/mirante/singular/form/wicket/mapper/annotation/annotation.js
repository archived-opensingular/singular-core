if( window.Annotation == undefined){
    window.Annotation = function (target_id, this_id, open_modal_id, comment, approved){
        this.target_component = $(target_id);
        this.this_component = $(this_id);
        this.open_modal = $(open_modal_id);
        this.comment = comment;
        this.approved = approved;
    }

    window.Annotation.prototype = {
        setup : function(){
            var thiz = this;

            var show_button = this.create_show_button();

            this.target_component.find('h3').append(
                $('<div>')
                    .attr('style','position:absolute;top:10px;right: 15px;')
                    .append(show_button)
                    .click(function(){
                        if(thiz.is_blank()){
                            thiz.open_modal[0].click();
                            thiz.this_component.fadeOut();
                        }else{
                            if(!thiz.this_component.is(":visible")){
                                thiz.this_component.fadeIn();
                            }else{
                                thiz.this_component.fadeOut();
                            }
                        }
                    })
            );
            this.adjust_height_position();
            if(thiz.is_blank()) {   thiz.this_component.hide(); }
        },

        is_blank : function () {
            return ! this.approved && (! this.comment || this.comment === 'null') ;
        },

        create_show_button : function(){
            return $('<a>')
                .addClass('btn btn-circle btn-icon-only '+this.define_button_color())
                .attr('href','javascript:;')
                .append($('<i>').addClass(this.define_button_icon()))
        },

        define_button_color: function() {
            if(this.is_blank() ){   return 'btn-default';   }
            if ( this.approved ) {  return 'btn-info';  }
            return 'btn-danger';
        },

        define_button_icon: function(){
            if( this.is_blank() ){ return "fa fa-plus"; }
            return 'fa fa-comment-o';
        },

        adjust_height_position: function(){
            this.this_component.css('position','absolute')
            var target_offset = this.target_component.parent().offset()['top'],
                this_offset = this.this_component.parent().offset()['top'];
            this.this_component.css('top',(target_offset-this_offset)+"px");
        }
    }

}
