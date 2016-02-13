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
            this.target_component.find('h3:first').append(
                $('<div>').attr('style','position:absolute;top:10px;right: 15px;')
                            .append(this.create_show_button())
                            .click(function(){thiz.toggle_button_on_click()})
            );
            this.adjust_height_position();
            if(this.is_blank()) {   this.this_component.hide(); }
        },

        is_blank : function () {    return this.approved == null;   },

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
            var result_offset = target_offset-this_offset;
            this.this_component.css('top',(result_offset)+"px");
        },

        toggle_button_on_click: function(){
            if(this.is_blank()){
                this.open_modal[0].click();
                this.this_component.fadeOut();
            }else{
                if(!this.this_component.is(":visible")){
                    this.close_overlaping_boxes();
                    this.this_component.fadeIn();
                }else{
                    this.this_component.fadeOut();
                }
            }
        },

        close_overlaping_boxes: function(){
            var thiz = this;
            var base = thiz.box_bounds(thiz.this_component);
            $(".sannotation-snipet-box:visible").each(function(i,e){
                var bound = thiz.box_bounds($(e));
                if(bound.start < base.end && bound.end > base.start){
                    $(e).fadeOut();
                }
            });
        },

        box_bounds : function(e){
            var start = e.css('top'), height = e.height();
            start = Number.parseFloat(start.replace('px',''));
            var end = start+height;
            return {    'start' : start , 'end' : end   };
        }
    }

}
