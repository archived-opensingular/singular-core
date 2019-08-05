/*
 *  jquery-photoswipe - v0.9.0
 *  jQuery plugin to wrap PhotoSwipe (http://photoswipe.com), with some additional goodies.
 *  https://github.com/opensingular
 *
 *  Made by Ronald Tetsuo Miura
 *  
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
(function($) {
  let PhotoSwipeKey = 'PhotoSwipe';

  function bindEvents(ps, $this) {

    let simpleEvents = [
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
    for (let i = 0; i < simpleEvents.length; i++) {
      let evt = simpleEvents[i];
      ps.listen(evt, function() { $this.trigger('photoswipe:' + evt); });
    }

    ps.listen('gettingData'         , function(index, item) { $this.trigger('photoswipe:gettingData'        , [ index, item ]); });
    ps.listen('parseVerticalMargin' , function(item)        { $this.trigger('photoswipe:parseVerticalMargin', [ item        ]); });
    ps.listen('updateScrollOffset'  , function(_offset)     { $this.trigger('photoswipe:updateScrollOffset' , [ _offset     ]); });
    ps.listen('shareLinkClick'      , function(e, target)   { $this.trigger('photoswipe:shareLinkClick'     , [ e, target   ]); });
    ps.framework.bind(ps.scrollWrap /* bind on any element of gallery */, 'pswpTap', function(e) { $this.trigger('photoswipe:pswpTap', [ e ]) });
  }
  
  function withPswd(thiz, callback) {
    let pswd = $(thiz).data(PhotoSwipeKey);
    if (pswd)
      callback(pswd);
  }

  let methods = {
    init : function(options) {
      let opts = $.extend({}, $.fn.photoswipe.defaults, options || {});

      let $this = $(this);

      if (!$this.hasClass('pswp') && $this.children().length == 0) {
        $this
          .addClass('pswp')
          .attr('tabindex', '-1')
          .attr('role', 'dialog')
          .attr('aria-hidden', 'true')
          .html(opts.bodyTemplate);
      }

      let items = (typeof opts.items == 'function') ? opts.items(this) : opts.items;
      for (let i=0; i<items.length; i++) {
        items[i].w = items[i].w || 0;
        items[i].h = items[i].h || 0;
      }
      let ps = new PhotoSwipe(this, opts.uiClass, items, opts);
      ps.init();
      $this.data(PhotoSwipeKey, ps);
      bindEvents(ps, $this);
      $this.photoswipe('loadAndUpdateImageSizes');
    },
    goTo : function(index) {
      withPswd(this, function (ps) { ps.goTo(index); });
    },
    next : function() {
      withPswd(this, function (ps) { ps.next(); });
    },
    updateSize : function(force) {
      withPswd(this, function (ps) { ps.updateSize(force); });
    },
    close : function() {
      withPswd(this, function (ps) { ps.close(); });
    },
    destroy : function() {
      let $this = $(this);
      let ps = $this.data(PhotoSwipeKey);
      $this.removeData(PhotoSwipeKey);
      ps.destroy();
    },
    zoomTo : function(destZoomLevel, centerPoint, speed, easingFn, updateFn) {
      withPswd(this, function (ps) { ps.zoomTo(destZoomLevel, centerPoint, speed, easingFn, updateFn); });
    },
    applyZoomPan : function(zoomLevel, panX, panY) {
      withPswd(this, function (ps) { ps.applyZoomPan(zoomLevel, panX, panY); });
    },
    setItems : function(newItems) {
      withPswd(this, function (ps) {
        let items = ps.items;
        Array.prototype.splice.apply(items, [0, items.length].concat(newItems));
        ps.invalidateCurrItems();
        ps.updateSize(true);
      });
    },
    updateItem: function(src, w, h) {
      let updatedItem = { src:src, w:w, h:h };
      if ((typeof src == 'object') && src.src && src.w && src.h) {
        updatedItem = src;
      }
      
      withPswd(this, function (ps) {
        let $this = $(this);
        let items = ps.items;
        for (let i=0; i<items.length; i++) {
          if (items[i].src == updatedItem.src) {
            items.splice(i, 1, updatedItem);
          }
        }
        ps.invalidateCurrItems();
        ps.updateSize(true);
      });
    },
    loadAndUpdateImageSizes : function() {
      let $this = $(this);
      withPswd(this, function (ps) {
        let items = ps.items;
        for (let i=0; i<items.length; i++) {
          let item = items[i];
          if (!item.w || !item.h) {
            let img = new Image();
            img.onload = function() {
              $this.photoswipe('updateItem', img.src, img.naturalWidth, img.naturalHeight);
            }
            img.src = item.src;
          }
        }
      });
    }
  };

  $.fn.photoswipe = function(methodOrOptions) {
    let args = arguments;
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
    shareButtons : [ {
      id : 'download',
      label : 'Download image',
      url : '{{raw_image_url}}',
      download : true
    } ],
    //\n<div class='pswp' tabindex='-1' role='dialog' aria-hidden='true'>\
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
  //\n</div>\
  };

}(jQuery));