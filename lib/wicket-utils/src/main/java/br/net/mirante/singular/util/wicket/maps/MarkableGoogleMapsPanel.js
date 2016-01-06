function createBelverMap(idMetadados) {

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