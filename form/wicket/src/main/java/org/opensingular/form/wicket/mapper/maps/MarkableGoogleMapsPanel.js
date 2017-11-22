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
                $.getScript('https://maps.googleapis.com/maps/api/js?key=' + googleMapsKey)
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
        if (document.getElementById(metadados.idLat) !== null) {
            var latElement = document.getElementById(metadados.idLat);
            var lngElement = document.getElementById(metadados.idLng);
            var zoomElement = document.getElementById(metadados.idZoom);
            configureMap(latElement, lngElement, zoomElement, metadados.idMap, metadados.idClearButton, JSON.parse(metadados.readOnly), metadados.idCurrentLocationButton);
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

    function configureMarker(latLong, latElement, lngElement, map, readOnly) {
        var marker = new google.maps.Marker({
            position: latLong,
            map: map
        });

        if (readOnly) {
            marker.setVisible(false);
        } else {
            if (isLatLongNotEmpty(latElement, lngElement)) {
                marker.setPosition(latLong);
                marker.setVisible(true);
            } else {
                marker.setVisible(false);
            }
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
            defineMarkerPositionManual(latElement, lngElement, map, marker);
        });
        $(lngElement).on('change', function () {
            defineMarkerPositionManual(latElement, lngElement, map, marker);
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

    function isLatLongNotEmpty(latElement, lngElement) {
        var valLat = latElement.value;
        var valLng = lngElement.value;
        return valLat !== null && valLng !== "" && valLat && valLng !== "";
    }

    function defineMarkerPositionManual(latElement, lngElement, map, marker) {
        if (isLatLongNotEmpty(latElement, lngElement)) {
            var latLong = buildGmapsLatLong(latElement.value, lngElement.value)
            map.setCenter(latLong);
            marker.setPosition(latLong);
            if (!marker.getVisible()) {
                marker.setVisible(true);
            }
        } else {
            marker.setVisible(false);
        }
    }

    function findCurrentLocation(success, failure) {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(success, failure);
        } else {
            toastr.error("Seu navegador não suporta geolocalização.");
        }

    }

})();