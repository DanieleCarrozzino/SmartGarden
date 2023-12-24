# Smart garden

## Project description
SmartGarden is a comprehensive Android application designed to assist garden enthusiasts in monitoring and managing their garden spaces. The application employs Firebase as its primary server and collects real-time data from a Raspberry Pi 2 W equipped with sensors and cameras installed in the garden area

### Features

- __User Authentication:__ Users can create accounts securely via Firebase authentication to access the GardenWatch app and its features.

- __Dashboard Overview:__ A Jetpack Compose-powered dashboard displays essential real-time data such as temperature, humidity, soil moisture, and images captured by Raspberry Pi 2 W cameras placed in the garden.

- __Sensor Data Visualization:__ Utilizing Firebase Realtime Database, the app showcases visual representations of sensor data through graphs, charts, or interactive UI elements, allowing users to track changes in environmental conditions over time.

- __Alerts and Notifications:__ Users receive timely alerts and notifications based on predefined thresholds set for specific parameters like temperature drops, soil moisture levels, or irregularities detected by the Raspberry Pi sensors.

- __Remote Control:__ Control garden devices remotely (e.g., irrigation systems, lights) through Firebase Cloud Messaging, enabling users to maintain their garden even when not physically present.

- __Data Logging:__ Historical data of environmental conditions and camera feed snapshots are stored securely on Firebase Cloud Storage, enabling users to review past data and visual records.

### Tech Stack

- __Android Development:__ Kotlin-based Jetpack Compose for modern UI design and seamless user interactions.
- __Firebase:__ Authentication, Realtime Database, Cloud Storage, and Cloud Messaging for secure user authentication, data storage, and real-time communication.
- __Raspberry Pi 2 W:__ Integration with sensors and cameras to collect environmental data and images from the garden.

## Firebase

### Firestore
Each user is represented by a document within the "Users-Uid" collection in Firestore. This document references the gardens associated with that user. This reference can be a direct link to the garden document or a subcollection within the user document

### Realtime database
    -- Gardens
        -- garden_id
            -- garden info
            .
            .
            .
    .
    .
    .
    
    -- Users
        -- uid
            -- user info
            .
            .
            .
    .
    .
    .

    -- Raspberry 
        -- uuid
            -- info, ip, ...
            .
            .
            .
        .
        .
        .   

ogni raspberry ha un suo uuid stampato sul dorso, ogni volta che si accende carica i suoi dati sul firebase realtimedatabase andando a inserire il suo ip all'interno di firebase.


### messaging
messaging?