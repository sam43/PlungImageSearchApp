# Plung - Image Search App
Sample project with latest android technologies. <br/> 
Requirement list: https://drive.google.com/file/d/1Hr6byNKANnNFdzLoi10H7sVbJUpq76kN/view?usp=sharing

# Features
- Image list will be loaded on the start
- Image can be searched by using search button with to top-right corner with any keywords like: cats, dogs, etc. and tap search from keyboard
- Data caching mechanism added to the search and API responses
- User can update the span count of the grid list from options menu end of Search icon by inputting desired span count (no more than 5 for UI sake)
without calling further API call
- When the image is tapped, a full screen detail page with that image (in actual ratio) will open up with a smooth Shared Transition animation.
- After tapping on the back, the reverse trasition can be shown afterward.
- Searching with a keyword during NETWORK_DISCONNECTED, user can simply tap on retry button to resume their search after NETWORK_CONNECTION restored.
- Infinity scroll has been enabled to the photo list or the Gallery page
- Offline caching with persistance data

# Used Tools
- MVVM architecture
- Coroutines used for all the asynchronous work and caching mechanism
- Unit testing(used Mockito, Junit)
- Hilt for dependency injection
- Gson to parse the json
- Diffutils callback with RecyclerView Adapter to optimise reload in data set change
- Navigation, ViewModel, LiveData, etc. from Jetpack components
- SearchView and Search Query Listener to search image
- Shared Transition between starting from gird and ends to the details page
- Room Database for network data caching mechanism 

## Excluding bonus requirement
- Bonus #2: <br/>
Once on the full screen activity the user can swipe left/right to see the next/previous
images. The reverse shared element transition should respect the current image visible
on the full screen.
