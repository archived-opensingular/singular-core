(function($) {
  var PhotoSwipeKey = 'PhotoSwipe';

  function bindEvents(ps, $this) {

    var simpleEvents = [
      'beforeChange',
      'afterChange',
      'imageLoadComplete',
      'resize',
      'mouseUsed',
      'initialZoomIn',
      'initialZoomInEnd',
      'initialZoomOut',
      'initialZoomOutEnd',
      'close',
      'unbindEvents',
      'destroy',
      'preventDragEvent' ];
    for (var i = 0; i < simpleEvents.length; i++) {
      var evt = simpleEvents[0];
      ps.listen(evt, function() { $this.trigger('photoswipe:' + evt); });
    }

    ps.listen('gettingData'         , function(index, item) { $this.trigger('photoswipe:gettingData'        , [ index, item ]); });
    ps.listen('parseVerticalMargin' , function(item)        { $this.trigger('photoswipe:parseVerticalMargin', [ item        ]); });
    ps.listen('updateScrollOffset'  , function(_offset)     { $this.trigger('photoswipe:updateScrollOffset' , [ _offset     ]); });
    ps.listen('shareLinkClick'      , function(e, target)   { $this.trigger('photoswipe:shareLinkClick'     , [ e, target   ]); });
    ps.framework.bind(ps.scrollWrap /* bind on any element of gallery */, 'pswpTap', function(e) { $this.trigger('photoswipe:pswpTap', [ e ]) });
  }

  function resolveImageMetadata(el) {
    var $el = $(el);
    switch (el.tagName.toUpperCase()) {
      case 'IMG':
        return {
          url: el.src,
          w: el.naturalWidth,
          h: el.naturalHeight
        }
      case 'A':
        return {
          url: el.href,
          w: $el.data('width'),
          h: $el.data('height')
        };
      default:
        return {
          url: $el.data('url'),
          w: $el.data('width'),
          h: $el.data('height')
        };
    }
  }
  
  var methods = {
    init : function(options) {
      var opts = $.extend({}, $.fn.photoswipe.defaults, options || {});

      var $this = $(this);

      if (!$this.hasClass('pswp') && $this.children().length == 0) {
        $this
          .addClass('pswp')
          .attr('tabindex', '-1')
          .attr('role', 'dialog')
          .attr('aria-hidden', 'true')
          .html(opts.bodyTemplate);
      }

      var ps = new PhotoSwipe(this, opts.uiClass, opts.items, opts);
      ps.init();
      $this.data(PhotoSwipeKey, ps);
      bindEvents(ps, $this);
    },
    goTo : function(index) {
      $(this).data(PhotoSwipeKey).goTo(index);
    },
    next : function() {
      $(this).data(PhotoSwipeKey).next();
    },
    updateSize : function(force) {
      $(this).data(PhotoSwipeKey).updateSize(force);
    },
    close : function() {
      $(this).data(PhotoSwipeKey).close();
    },
    destroy : function() {
      var ps = $(this).data(PhotoSwipeKey);
      $(this).removeData(PhotoSwipeKey);
      ps.destroy();
    },
    zoomTo : function(destZoomLevel, centerPoint, speed, easingFn, updateFn) {
      $(this).data(PhotoSwipeKey).zoomTo(destZoomLevel, centerPoint, speed, easingFn, updateFn);
    },
    applyZoomPan : function(zoomLevel, panX, panY) {
      $(this).data(PhotoSwipeKey).applyZoomPan(zoomLevel, panX, panY);
    },
    
    setItems : function(newItems) {
      var $this = $(this);
      var pswp = $this.data(PhotoSwipeKey);
      var items = pswp.items;
      Array.prototype.splice.apply(items, [0, items.length].concat(newItems));
      pswp.invalidateCurrItems();
      pswp.updateSize(true);
    }
  };

  $.fn.photoswipe = function(methodOrOptions) {
    var args = arguments;
    if ('PhotoSwipe' == methodOrOptions) {
      return this.data(PhotoSwipeKey);

    } else if (methods[methodOrOptions]) {
      return this.each(function() {
        methods[methodOrOptions].apply(this, Array.prototype.slice.call(args, 1));
      });

    } else if (typeof methodOrOptions === 'object' || !methodOrOptions) {
      return this.each(function() {
        methods.init.apply(this, args); // Default to "init"
      });

    } else {
      $.error('Method ' + methodOrOptions + ' does not exist on jQuery.photoswipe');
    }
  };

  $.fn.photoswipe.defaults = {
    uiClass : PhotoSwipeUI_Default,
    images : null,
    shareButtons : [ {
      id : 'download',
      label : 'Download image',
      url : '{{raw_image_url}}',
      download : true
    } ],
    //\n    <div class='pswp' tabindex='-1' role='dialog' aria-hidden='true'>\
    bodyTemplate : "\
\n      <div class='pswp__bg'></div>\
\n      <div class='pswp__scroll-wrap'>\
\n        <div class='pswp__container'>\
\n          <div class='pswp__item'></div>\
\n          <div class='pswp__item'></div>\
\n          <div class='pswp__item'></div>\
\n        </div>\
\n        <div class='pswp__ui pswp__ui--hidden'>\
\n          <div class='pswp__top-bar'>\
\n            <div class='pswp__counter'></div>\
\n            <button class='pswp__button pswp__button--close' title='Close (Esc)'></button>\
\n            <button class='pswp__button pswp__button--share' title='Share'></button>\
\n            <button class='pswp__button pswp__button--fs' title='Toggle fullscreen'></button>\
\n            <button class='pswp__button pswp__button--zoom' title='Zoom in/out'></button>\
\n            <div class='pswp__preloader'>\
\n              <div class='pswp__preloader__icn'>\
\n                <div class='pswp__preloader__cut'>\
\n                  <div class='pswp__preloader__donut'></div>\
\n                </div>\
\n              </div>\
\n            </div>\
\n          </div>\
\n          <div class='pswp__share-modal pswp__share-modal--hidden pswp__single-tap'>\
\n            <div class='pswp__share-tooltip'></div>\
\n          </div>\
\n          <button class='pswp__button pswp__button--arrow--left' title='Previous (arrow left)'></button>\
\n          <button class='pswp__button pswp__button--arrow--right' title='Next (arrow right)'></button>\
\n          <div class='pswp__caption'>\
\n            <div class='pswp__caption__center'></div>\
\n          </div>\
\n        </div>\
\n      </div>\
\n    ",
  //\n    </div>\
  };

}(jQuery));