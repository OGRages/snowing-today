package me.yattaw.snowingtoday.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper {

    private boolean success;
    private Optional<LocationData[]> data;

    private ResponseWrapper(boolean success, List<LocationData> locationDataList) {
        this.success = success;
        if (success) {
            this.data = Optional.of(locationDataList.toArray(new LocationData[locationDataList.size()]));
        }
    }

    public static ResponseWrapper of(List<LocationData> locationDataList) {
        if (locationDataList == null) return new ResponseWrapper(false, null);
        return new ResponseWrapper(locationDataList.size() > 0, locationDataList);
    }

    public Optional<LocationData[]> getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }
}
