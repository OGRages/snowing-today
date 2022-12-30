package me.yattaw.snowingtoday;

import me.yattaw.snowingtoday.data.SnowFrequency;
import me.yattaw.snowingtoday.data.response.LocationData;
import me.yattaw.snowingtoday.data.response.ResponseWrapper;
import me.yattaw.snowingtoday.service.IPApiService;
import me.yattaw.snowingtoday.service.WeatherApiService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SnowDayController {

    @GetMapping("/api/v1/locations")
    public ResponseEntity<ResponseWrapper> getResponseEntity(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(required = false) String lat,
            @RequestParam(required = false) String lon) {

//        TODO: allow users to change frequency
        SnowFrequency frequency = SnowFrequency.SEASONAL;        //Set the default value here.

        List<LocationData> locationDataList = null;
        if (postalCode != null) {
            locationDataList = WeatherApiService.getLocations(postalCode);
        } else if (query != null) {
            locationDataList = WeatherApiService.getLocations(query);
        } else if (ipAddress != null) {
            locationDataList = new ArrayList<>();
            locationDataList.add(IPApiService.getUserRegionFromIP(ipAddress));
        } else if (lat != null && lon != null) {
//            TODO: Add logic to search for latitude/longitude values here.
//            locationDataList = WeatherAPIService.getLocationsByLatLon(lat, lon);
        } else {
            locationDataList = new ArrayList<>();
            locationDataList.add(IPApiService.getUserRegionFromIP(System.getenv("ADDRESS"))); // change later to requested users IP "request.getRemoteAddr();"
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseWrapper response = ResponseWrapper.of(locationDataList);

        return ResponseEntity
                .status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .headers(headers)
                .body(response);
    }

}