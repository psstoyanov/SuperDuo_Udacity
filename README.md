# SuperDuo_Udacity
This is a compound repository for he Udacity Android Developer Nanodegree Project #3 "Super Duo!" project.

# SuperDuo

This is compound github project for the `Udacity Android Nanoodegree - Super Duo! Project #3`. Contains Alexandria and Football Scores apps.

 ## Apps

* Alexandria
* Football Scores


## Instructions

Each app has its own root folder. To prevent `Can't upload Android app to device (stale dexed jars)` error, disable Instant run inside Android Studio:
```
Android Studio --> Preferences --> Build, execution, deploy --> Instant run.
```

To get `API Key` for Football Scores go to:
* [http://api.football-data.org/register](http://api.football-data.org/register)
* Replace `api_key` from `/strings` with your API key. 
### Alexandria

The app is using [Vision API](https://developers.google.com/vision/?hl=en) to scan the ISBN barcode of books and Glide to display book covers. The books data is aquired from [Google books API](https://developers.google.com/books/docs/v1/using?hl=en).

* [Vision API](https://developers.google.com/vision/?hl=en) requires Google Play Services on the device to function and be able to capture images.


### Football Scores

* Data is collected from [Football-data.org](http://api.football-data.org/index) to display football matches.
* [Volley](http://developer.android.com/training/volley/index.html) is being used to collect data for the Detail fragment view.
  * Custom Header is required for GET function : [Sending Custom Headers with Volley ](http://blog.codeint.com/sending-custom-headers-with-volley-android-networking-library/)
 * Changed the design with Material elements.
   * Some of the changes are thanks to the great tutorials and information from [Styling Android](https://blog.stylingandroid.com/) especially for Coordinator layout and Tabs.


## Other
The original github repos for the apps:
* [Alexandria](https://github.com/psstoyanov/Alexandria)
* [Football_Scores](https://github.com/psstoyanov/Football_Scores)

## TODO:
* Remove previous git initialization: The root directories may contain previous git initialization. The compound repo is using extracted zips of the original repos thus the previous git initialization is remaining the folders.
