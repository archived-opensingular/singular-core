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

function createSingularMap(idMetadados) {

    var googleMapsKey = "AIzaSyALU10ekJ7BQ8jBbMyiCfBK4Yw3giSRmqk";


    if (typeof google != 'undefined') {
        createSingularMapImpl();
    } else {
        $.getScript('https://maps.googleapis.com/maps/api/js?key='+googleMapsKey, createSingularMapImpl);
    }

    function createSingularMapImpl() {
        var metadados = JSON.parse(document.getElementById(idMetadados).value),
            lat = document.getElementById(metadados.idLat).value,
            lng = document.getElementById(metadados.idLng).value,
            latLong = new google.maps.LatLng(lat, lng),
            map, marker;
        if (!lat && !lng) {
            latLong = new google.maps.LatLng(-15.7922, -47.4609);
        } else {
            marker = new google.maps.Marker({
                position: latLong
            });
        }
        map = new google.maps.Map(document.getElementById(metadados.idMap), {
            zoom: Number(metadados.zoom),
            center: latLong
        });
        marker = new google.maps.Marker({
            position: event.latLng,
            map: map
        });
        if (!JSON.parse(metadados.readOnly)) {
            map.addListener('click', function (event) {
                if (!marker.getVisible()) {
                    marker.setVisible(true);
                }
                marker.setPosition(event.latLng);
                document.getElementById(metadados.idLat).value = event.latLng.lat();
                document.getElementById(metadados.idLng).value = event.latLng.lng();
            });
            marker.addListener('click', function() {
                if (marker.getVisible()) {
                    marker.setVisible(false);
                    document.getElementById(metadados.idLat).value = null;
                    document.getElementById(metadados.idLng).value = null;
                }
            });

            $("#"+metadados.idLat).on('change', defineMarkerPositionManual);
            $("#"+metadados.idLng).on('change', defineMarkerPositionManual);
        }
        map.addListener('zoom_changed', function () {
            metadados.zoom = map.zoom;
            document.getElementById(idMetadados).value = JSON.stringify(metadados);
        });

        function defineMarkerPositionManual () {
            if($("#"+metadados.idLng).val() != null && $("#"+metadados.idLng).val() !== "" &&
                $("#"+metadados.idLat).val() != null && $("#"+metadados.idLat).val() !== ""){

                latLong = new google.maps.LatLng($("#"+metadados.idLat).val(), $("#"+metadados.idLng).val());
                map.setCenter(latLong);
                marker.setPosition(latLong);
                marker.setMap(map);
                if (!marker.getVisible()) {
                    marker.setVisible(true);
                }
            } else{
                marker.setVisible(false);
            }
        }
    }
}