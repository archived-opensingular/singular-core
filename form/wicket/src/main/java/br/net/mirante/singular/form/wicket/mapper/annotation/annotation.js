if( window.Annotation == undefined){
    window.Annotation = function (target_id, this_id, open_modal_id, comment, approved, readOnly){
        this.init = function (target_id, this_id, open_modal_id, comment, approved, readOnly){
            this.target_id = target_id;
            this.this_id = this_id;
            this.open_modal_id = open_modal_id;
            this.comment = comment;
            this.approved = approved;
            this.readOnly = readOnly;
            this.retry = false;
        },
        this.init(target_id, this_id, open_modal_id, comment, approved, readOnly);
    }

    window.Annotation.prototype = {
        build : function(){
            this.update_references();
            this.this_component.data('ctl',this);
            if(! this.target_component || this.target_component.length == 0 || ! this.this_component){
                console.log('Not possible to render annotation for ',
                    this.target_component , this.this_component);
                    if(!this.retry){
                        var thiz = this;
                        window.setTimeout(function(){thiz.build()},2000);
                        this.retry = true;
                    }else{
                        this.retry = false;
                    }
                return;
            }
            this.toggle_container = this.create_toggle_container();
            this.target_component/*.find('h3:first')*/.append(this.toggle_container);
            if(this.is_blank()) {   this.this_component.hide(); }
            if(this.is_blank() && this.readOnly) {   this.toggle_container.hide(); }
            if(this.this_component.is(':visible') ){
                this.close_overlaping_boxes(this.this_component);
                this.adjust_height_position();
            }
        },

        update_references : function(){
            this.target_component = $(this.target_id);
            this.this_component = $(this.this_id);
            this.open_modal = $(this.open_modal_id);
        },

        is_blank : function () {    return this.approved == null;   },

        create_toggle_container: function(){
            var thiz = this;
            var toggle_container = this.target_component.find('.annotation-toggle-container');
            console.log(toggle_container);
            toggle_container.find('a').removeClass('btn-default btn-info btn-danger');
            toggle_container.find('a').addClass(this.define_button_color());
            toggle_container.find('i').removeClass();
            toggle_container.find('i').addClass(this.define_button_icon());
            toggle_container.off("click");
            toggle_container.click(function(){thiz.toggle_button_on_click()})
            return toggle_container;
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
            console.log('adjust_height_position',this.this_component, this.target_component);
            this.this_component.css('position','absolute')
            var target_offset = this.target_component.parent().offset()['top'],
                this_offset = this.this_component.parent().offset()['top'];
            var result_offset = target_offset-this_offset;
            this.this_component.css('top',(result_offset)+"px");
        },

        toggle_button_on_click: function(){
            this.adjust_height_position();
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

        close_overlaping_boxes: function( notChange){
            var thiz = this;
            var base = thiz.box_bounds(thiz.this_component);
            $(".sannotation-snipet-box:visible").each(function(i,e){
                var bound = thiz.box_bounds($(e));
                if(! $(e).is(notChange) && bound.start < base.end && bound.end > base.start){
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
    };

    window.Annotation.create_or_update = function(target_id, this_id, open_modal_id,
                                                  comment, approved, readOnly){
        var this_component = $(this_id)
        var target_component = $(target_id)
        if(this_component && this_component.data('ctl')){
            var ctl = this_component.data('ctl');
            ctl.init(target_id, this_id, open_modal_id, comment, approved, readOnly);
            ctl.build();
        }else{
            new Annotation(target_id, this_id, open_modal_id, comment, approved, readOnly).build();
        }
    };

    window.Annotation.update_comment_box = function(event){
        if(event.keyCode == 13) {
            event.preventDefault();
            var target = $(event.target);
            var pos = target[0].selectionStart;
            target.val(target.val().substring(0, pos) + '\n'+ target.val().substring(pos));
            target.setCursorPosition(pos + 1);
        }
    }

    $(function(){
        new function($) {
            $.fn.setCursorPosition = function(pos) {
                if ($(this).get(0).setSelectionRange) {
                    $(this).get(0).setSelectionRange(pos, pos);
                } else if ($(this).get(0).createTextRange) {
                    var range = $(this).get(0).createTextRange();
                    range.collapse(true);
                    range.moveEnd('character', pos);
                    range.moveStart('character', pos);
                    range.select();
                }
            }
        }(jQuery);
    })
}
