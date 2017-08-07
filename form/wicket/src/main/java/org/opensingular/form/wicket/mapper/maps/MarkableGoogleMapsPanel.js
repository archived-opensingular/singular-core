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

function createSingularMap(idMetadados, googleMapsKey) {

    if (typeof google != 'undefined') {
        createSingularMapImpl();
    } else {
        var result = $.getScript('https://maps.googleapis.com/maps/api/js?key=' + googleMapsKey, createSingularMapImpl)
            .done(function (s, Status){
                if (Status == 'OverQuotaMapError') {
                    var meta = JSON.parse(document.getElementById(idMetadados).value);
                    document.getElementById(meta.idMap).style.visibility = "hidden";
                }
            });
    }

    function createSingularMapImpl() {
        var meta = JSON.parse(document.getElementById(idMetadados).value);
        if (document.getElementById(meta.idLat) != null) {
            var metadados = JSON.parse(document.getElementById(idMetadados).value),
                lat = document.getElementById(metadados.idLat).value,
                lng = document.getElementById(metadados.idLng).value,
                zoom = document.getElementById(metadados.idZoom).value,
                latLong = new google.maps.LatLng(lat.replace(",", "."), lng.replace(",", ".")),
                map, marker;

            var latElement = document.getElementById(metadados.idLat);
            var lngElement = document.getElementById(metadados.idLng);
            var zoomElement = document.getElementById(metadados.idZoom);

            if (!lat && !lng) {
                latLong = new google.maps.LatLng(-15.7922, -47.4609);
            }
            map = new google.maps.Map(document.getElementById(metadados.idMap), {
                zoom: Number(zoom),
                center: latLong
            });
            marker = new google.maps.Marker({
                position: latLong,
                map: map
            });

            if (!JSON.parse(metadados.readOnly)) {
                if (latElement.value != "" && lngElement.value != "") {
                    lat = latElement.value.replace(",", ".");
                    lng = lngElement.value.replace(",", ".");
                    latLong = new google.maps.LatLng(lat, lng);
                    marker.setPosition(latLong);
                    marker.setVisible(true);
                } else {
                    marker.setVisible(false);
                }

                map.addListener('click', function (event) {
                    if (!marker.getVisible()) {
                        marker.setVisible(true);
                    }
                    marker.setPosition(event.latLng);
                    latElement.value = event.latLng.lat();
                    lngElement.value = event.latLng.lng();
                });
                marker.addListener('click', function () {
                    if (marker.getVisible()) {
                        marker.setVisible(false);
                        latElement.value = null;
                        lngElement.value = null;
                    }
                });

                $("#" + metadados.idLat).on('change', defineMarkerPositionManual);
                $("#" + metadados.idLng).on('change', defineMarkerPositionManual);

                $("#" + metadados.idButton).on('click', function () {
                    latElement.value = null;
                    lngElement.value = null;
                    marker.setVisible(false);
                });
            }
            map.addListener('zoom_changed', function () {
                zoomElement.value = map.zoom;
            });

        } else {
            document.getElementById(meta.idMap).style.visibility = "hidden";
        }

        function defineMarkerPositionManual() {
            var valLat = $("#" + metadados.idLat).val();
            var valLng = $("#" + metadados.idLng).val();

            if (valLat != null && valLng !== "" &&
                valLat && valLng !== "") {

                valLat = valLat.replace(",", ".");
                valLng = valLng.replace(",", ".");

                latLong = new google.maps.LatLng(valLat, valLng);
                map.setCenter(latLong);
                marker.setPosition(latLong);
                marker.setMap(map);
                if (!marker.getVisible()) {
                    marker.setVisible(true);
                }
            } else {
                marker.setVisible(false);
            }
        }
    }
}