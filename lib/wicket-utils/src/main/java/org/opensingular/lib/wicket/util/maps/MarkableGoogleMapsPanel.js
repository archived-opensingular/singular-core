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

function createBelverMap(idMetadados) {

    if (typeof google != 'undefined') {
        createBelverMapImpl();
    } else {
        $.getScript('https://maps.googleapis.com/maps/api/js', createBelverMapImpl);
    }

    function createBelverMapImpl() {
        var metadados = JSON.parse(document.getElementById(idMetadados).value),
            lat = document.getElementById(metadados.idLat).value,
            lng = document.getElementById(metadados.idLng).value,
            latLong = new google.maps.LatLng(lat, lng),
            map, marker;
        if (!lat && !lng) {
            latLong = new google.maps.LatLng(-15.7922, -47.4609)
        } else {
            marker = new google.maps.Marker({
                position: latLong
            });
        }
        map = new google.maps.Map(document.getElementById(metadados.idMap), {
            zoom: Number(metadados.zoom),
            center: latLong
        });
        if (marker) {
            marker.setMap(map);
        }
        if (!JSON.parse(metadados.readOnly)) {
            map.addListener('click', function (event) {
                if (marker) {
                    marker.setMap(null);
                }
                marker = new google.maps.Marker({
                    position: event.latLng,
                    map: map
                });
                document.getElementById(metadados.idLat).value = event.latLng.lat();
                document.getElementById(metadados.idLng).value = event.latLng.lng();
            });
        }
        map.addListener('zoom_changed', function () {
            metadados.zoom = map.zoom;
            document.getElementById(idMetadados).value = JSON.stringify(metadados);
        });
    }
}