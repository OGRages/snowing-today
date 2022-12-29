# Snowing.Today API Documentation
Welcome to the Snowing Today API documentation! This repository provides developers with a way to quickly get real-time weather information on snowfall in their area. The API allows you to query a specific area and obtain data such as current snow conditions, predicted snowfall for the next 24 hours, temperature predictions and more. 

## Returned Data 
The API will return a JSON object with data about current snow conditions, 24 hour forecasted snowfall and cancel probability (a probability of school being cancelled based off the location). The returned data includes: 

 - **24 Hour Forecast**: Forecasted snowfall in inches and temperatures throughout the day
 - **Cancel Probability**: Generates a probability of school being cancelled based off the location

## Examples
To get the current snow conditions, use one of the following GET requests:
- From an IP Address: `https://www.snowing.today/api?ip=192.168.1.1`
- From a location query (e.g., South Portland Maine): `https://www.snowing.today/api?query=South%20Portland%20Maine`
- From a Postal Code (e.g., 04106): `https://www.snowing.today/api?postalCode=04106`

 The response will look like this: 
```json
[
  {
    "postalCode": "04106",
    "country": "United States",
    "district": "Maine",
    "city": "South Portland",
    "latitude": 43.641,
    "longitude": -70.241,
    "snowDayProbability": 0,
    "snowData": {
      "prediction": "No chance of school being cancelled from snow",
      "totalSnow": 0,
      "snowingHours": 0,
      "minTemperature": 29,
      "maxTemperature": 40,
      "avgTemperature": 33.416668
    }
  }
]
```
## Support 
If you have any questions or need assistance using this API, please contact us at [support@snowing.today](mailto:support@snowing.today).
