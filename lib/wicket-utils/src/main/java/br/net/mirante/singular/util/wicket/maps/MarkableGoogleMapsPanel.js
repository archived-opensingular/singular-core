function createBelverMap(idMap, idLat, idLng, idZoom) {

    var lat = document.getElementById(idLat).value,
        lng = document.getElementById(idLng).value,
        zoom = document.getElementById(idZoom).value,
        latLong = new google.maps.LatLng(lat, lng),
        map, marker;

    if (!lat && !lng) {
        latLong = new google.maps.LatLng(-15.7922, -47.4609)
    } else {
        marker = new google.maps.Marker({
            position: latLong
        });
    }

    map = new google.maps.Map(document.getElementById(idMap), {
        zoom: Number(zoom),
        center: latLong
    });

    if (marker) {
        marker.setMap(map);
    }

    map.addListener('click', function (event) {
        if (marker) {
            marker.setMap(null);
        }
        marker = new google.maps.Marker({
            position: event.latLng,
            map: map
        });
        document.getElementById(idLat).value = event.latLng.lat();
        document.getElementById(idLng).value = event.latLng.lng();
    });

    map.addListener('zoom_changed', function () {
        document.getElementById(idZoom).value = map.zoom;
    });

}