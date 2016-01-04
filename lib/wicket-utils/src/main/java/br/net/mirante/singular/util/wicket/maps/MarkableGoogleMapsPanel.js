function createBelverMap(idMap, idLat, idLng) {

    var lat = document.getElementById(idLat).value,
        lng = document.getElementById(idLng).value,
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
        zoom: 4,
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
        syncWicketComponents(event.latLng);
    });

    function syncWicketComponents(latLng) {
        document.getElementById(idLat).value = latLng.lat();
        document.getElementById(idLng).value = latLng.lng();
    }

}