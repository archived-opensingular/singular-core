/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
(function () {
    "use strict";

    if (window.Singular === undefined) {
        window.Singular = function () {
        };
    }

    if (window.SingularMaps === undefined) {
        window.SingularMaps = [];
    }

    window.Singular.createSingularMap = function (idMetadados, googleMapsKey) {

        if (typeof google !== 'undefined') {
            createSingularMapImpl(idMetadados);
        } else {
            //carregar o script do gmaps apenas uma vez e recriar os demais mapas
            if (window.SingularMaps.length === 0) {
                $.getScript('https://maps.googleapis.com/maps/api/js?key=' + googleMapsKey + '&language=pt-BR')
                    .done(function () {
                        for (var i = 0; i < window.SingularMaps.length; i++) {
                            window.SingularMaps[i]();
                        }
                    })
                    .fail(function (s, Status) {
                        if (Status === 'OverQuotaMapError') {
                            var meta = JSON.parse(document.getElementById(idMetadados).value);
                            document.getElementById(meta.idMap).style.visibility = "hidden";
                        }
                    });
            }
            window.SingularMaps.push(function () {
                Singular.createSingularMap(idMetadados);
            });
        }
    };


    function createSingularMapImpl(idMetadados) {
        var metadados = JSON.parse(document.getElementById(idMetadados).value);
        var zoomElement = document.getElementById(metadados.idZoom);
        if (document.getElementById(metadados.idLat) !== null) {
            var latElement = document.getElementById(metadados.idLat);
            var lngElement = document.getElementById(metadados.idLng);
            configureMap(latElement, lngElement, zoomElement, metadados.idMap, metadados.idClearButton, JSON.parse(metadados.readOnly), metadados.idCurrentLocationButton);
        } else if (metadados.multipleMarkers) {
            var tableContainerElement = document.getElementById(metadados.tableContainerId);
            var urlKml = metadados.urlKml;
            configureMapMultipleMarkers(tableContainerElement, zoomElement, metadados.idMap, metadados.idClearButton, JSON.parse(metadados.readOnly), metadados.callbackUrl, urlKml);
        } else {
            document.getElementById(metadados.idMap).style.visibility = "hidden";
        }
    }

    function configureMap(latElement, lngElement, zoomElement, idMap, idClearButton, readOnly, idCurrentLocationButton) {
        var latLong = buildGmapsLatLong(latElement.value, lngElement.value);

        var map = new google.maps.Map(document.getElementById(idMap), {
            zoom: Number(zoomElement.value),
            center: latLong
        });

        map.addListener('zoom_changed', function () {
            zoomElement.value = map.zoom;
        });

        var marker = configureMarker(latLong, latElement, lngElement, map, readOnly);
        configureFieldsEvents(latElement, lngElement, map, marker, idClearButton, readOnly, idCurrentLocationButton);

        if (!readOnly) {
            map.addListener('click', function (event) {
                if (!marker.getVisible()) {
                    marker.setVisible(true);
                }
                marker.setPosition(event.latLng);
                latElement.value = event.latLng.lat();
                lngElement.value = event.latLng.lng();
            });
        }
        return map;
    }

    function configureMapMultipleMarkers(tableContainerElement, zoomElement, idMap, idClearButton, readOnly, callbackUrl, urlKml) {
        var markers = [];
        var latLong = buildGmapsLatLong();
        var polygon = new google.maps.Polygon({
            strokeColor: '#FF0000',
            strokeOpacity: 0.8,
            strokeWeight: 2,
            fillColor: '#FF0000',
            fillOpacity: 0.35
        });

        var map = new google.maps.Map(document.getElementById(idMap), {
            zoom: Number(zoomElement.value),
            center: latLong
        });

        //O problema não é o size da url.

       // urlKml = "http://showcase.opensingular.org/download/Flwy37pGExSWP38Vm08zdLphfQV6c9lo79JXTwp9/97853f29-0270-4902-8817-b7d90e1b5fc9";
        // urlKml = "http://googlemaps.github.io/js-v2-samples/ggeoxml/cta.kml";
        //   urlKml = "https://sites.google.com/a/gmapas.com/home/poligonos-ibge/poligonos-municipios-ibge-rio-grande-do-norte/Municipios_RN.kml"
      //  urlKml = "http://api.flickr.com/services/feeds/geo/?g=322338@N20&lang=en-us&format=feed-georss";
        console.log(urlKml);
        if(urlKml !== '' && urlKml != null){
            new google.maps.KmlLayer({
                url: urlKml,
                map: map
            });
        } else {
            if (!readOnly) {
                map.addListener('zoom_changed', function () {
                    zoomElement.value = map.zoom;
                });
            }

            configureMarkers(tableContainerElement, map, readOnly, markers, polygon);
            draw(map, polygon, markers);
            if (!readOnly) {
                configureMultipleFieldsEvents(tableContainerElement, map, markers, polygon);
            }

            if (!readOnly) {
                map.addListener('click', function (event) {
                    var params = {'lat': event.latLng.lat(), 'lng': event.latLng.lng()};
                    Wicket.Ajax.post({u: callbackUrl, ep: params});

                    var number = countMarkers(tableContainerElement);
                    var marker = createMarker(map, event.latLng, polygon, readOnly, true, number + 1);
                    markers.push(marker);
                    draw(map, polygon, markers);
                });
            }
        }
        return map;
    }

    function configureMarkers(tableContainerElement, map, readOnly, markers, polygon) {
        jQuery(tableContainerElement)
            .find('.list-table-body table tbody tr')
            .each(function (index) {
                var valLat;
                var valLng;
                if (readOnly) {
                    var latLongElements = $(this).find('td');
                    valLat = $.trim($(latLongElements[0]).text());
                    valLng = $.trim($(latLongElements[1]).text());
                } else {
                    var latLongElements = $(this).find('input[type=text]');
                    valLat = latLongElements[0].value;
                    valLng = latLongElements[1].value;
                }
                var latLng;
                if (isLatLongNotEmpty(valLat, valLng)) {
                    latLng = buildGmapsLatLong(valLat, valLng);
                }

                markers.push(createMarker(map, latLng, polygon, readOnly, false, index+1));
            })
        ;
    }

    function createMarker(map, latLng, polygon, readOnly, animate, number) {
        var marker = new google.maps.Marker({
            map: map,
            visible: false,
            label: number.toString()
        });

        if (latLng) {
            marker.setPosition(latLng);
            marker.setVisible(true);
        }

        if (animate) {
            marker.setAnimation(google.maps.Animation.DROP);
        }

        return marker;
    }

    function countMarkers(tableContainerElement) {
        return jQuery(tableContainerElement)
            .find('.list-table-body table tbody tr')
            .length;
    }

    function findLatLongRow(tableContainerElement, markers, marker) {
        var i = 0;

        while (i < markers.length) {
            if (markers[i].getPosition().equals(marker.getPosition())) {
                break;
            }
            i++;
        }

        var line = jQuery(tableContainerElement)
            .find('.list-table-body table tbody tr')[i];
        return jQuery(line).find('input[type=text]');
    }

    function draw(map, polygon, markers) {
        var visibleMarkers = markers.filter(function (m) { return m.getVisible();});
        if (visibleMarkers.length > 2) {
            var coords = visibleMarkers.map(function (m) {
                return {lat: m.getPosition().lat(), lng: m.getPosition().lng()};
            });

            polygon.setPaths(coords);
            polygon.setMap(map);

        } else {
            polygon.setMap(null);
        }
    }

    function configureMultipleFieldsEvents(tableContainerElement, map, markers, polygon) {

        jQuery(tableContainerElement)
            .find('.list-table-body table tbody tr')
            .each(function (index) {
                var latLongElements = $(this).find('input[type=text]');
                var latElement = latLongElements[0];
                var lngElement = latLongElements[1];
                var marker = markers[index];
                var center = markers.length === 1;

                $(latElement).on('change', function () {
                    defineMarkerPositionManual(latElement, lngElement, map, marker, center);
                    draw(map, polygon,  markers);
                });
                $(lngElement).on('change', function () {
                    defineMarkerPositionManual(latElement, lngElement, map, marker, center);
                    draw(map, polygon,  markers);
                });

            })
        ;

    }

    function configureMarker(latLong, latElement, lngElement, map, readOnly) {
        var marker = new google.maps.Marker({
            position: latLong,
            map: map
        });

        if (isLatLongNotEmpty(latElement.value, lngElement.value)) {
            marker.setPosition(latLong);
            marker.setVisible(true);
        } else {
            marker.setVisible(false);
        }

        marker.addListener('click', function () {
            if (marker.getVisible()) {
                marker.setVisible(false);
                latElement.value = null;
                lngElement.value = null;
            }
        });

        return marker;
    }

    function buildGmapsLatLong(lat, lng) {
        var valLat;
        var valLng;
        if (!lat && !lng) {
            valLat = -15.7922;
            valLng = -47.4609;
        } else {
            valLat = lat.replace(",", ".");
            valLng = lng.replace(",", ".");
        }
        return new google.maps.LatLng(valLat, valLng);
    }

    function configureFieldsEvents(latElement, lngElement, map, marker, idClearButton, readOnly, idCurrentLocationButton) {
        if (readOnly) {
            $(latElement).attr('readonly', 'readonly');
            $(latElement).addClass('disabled');
            $(lngElement).attr('readonly', 'readonly');
            $(lngElement).addClass('disabled');
        }
        $(latElement).on('change', function () {
            defineMarkerPositionManual(latElement, lngElement, map, marker, true);
        });
        $(lngElement).on('change', function () {
            defineMarkerPositionManual(latElement, lngElement, map, marker, true);
        });
        $("#" + idClearButton).on('click', function () {
            latElement.value = null;
            lngElement.value = null;
            marker.setVisible(false);
        });
        $("#" + idCurrentLocationButton).on('click', function () {
            findCurrentLocation(
                function (success) {
                    $(latElement).val(success.coords.latitude);
                    $(lngElement).val(success.coords.longitude);
                    $(latElement).trigger('change');
                    $(lngElement).trigger('change');
                    map.setZoom(17);
                    marker.setVisible(true);
                },
                function (failure) {
                    toastr.error(failure.message);
                }
            );
        });
    }

    function isLatLongNotEmpty(valLat, valLng) {
        return valLat !== null && valLng !== "" && valLat && valLng !== "";
    }

    function defineMarkerPositionManual(latElement, lngElement, map, marker, center) {
        if (isLatLongNotEmpty(latElement.value, lngElement.value)) {
            var latLong = buildGmapsLatLong(latElement.value, lngElement.value);
            marker.setPosition(latLong);
            if (!marker.getVisible()) {
                marker.setVisible(true);
            }
            if (center) {
                map.setCenter(latLong);
            }
        } else {
            marker.setVisible(false);
        }
    }

    function findCurrentLocation(success, failure) {
        if (navigator.geolocation) {
            var options = {
                enableHighAccuracy: true,
                timeout: 5000,
                maximumAge: 0
            };
            navigator.geolocation.getCurrentPosition(success, failure, options);
        } else {
            toastr.error("Seu navegador não suporta geolocalização.");
        }

    }

})();