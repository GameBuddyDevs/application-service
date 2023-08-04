# GameBuddy Application Service
GameBuddy Application Service is a microservice in the GameBuddy project. The service is built using Spring Java and provides a set of RESTful APIs to handle various functionalities related to user profiles, friends, achievements, marketplace, and more.

## APIs (Soon I will prepare the swagger document.)

### User Information APIs

##### GET /application/get/user/info/{userId}

- Description: Retrieve user information by user ID.
- Request Header: Authorization (Bearer Token)
- Path Variable: userId (The ID of the user whose information is requested)
- Response: UserInfoResponse


### Keywords and Games APIs

##### GET /application/get/keywords

- Description: Get the list of keywords (interests).
- Response: KeywordsResponse

##### GET /application/get/games

- Description: Get the list of games available on the platform.
- Response: GamesResponse

##### GET /application/get/game/{gameId}

- Description: Get detailed information about a specific game.
- Request Header: Authorization (Bearer Token)
- Path Variable: gameId (The ID of the game)
- Response: GameResponse

##### GET /application/get/popular/games

- Description: Get a list of popular games on the platform.
- Response: GamesResponse


### Avatars and Achievements APIs

##### GET /application/get/avatars

- Description: Get the list of available avatars for the user.
- Request Header: Authorization (Bearer Token)
- Response: AvatarsResponse

##### GET /application/get/achievements

- Description: Get the list of achievements earned by the user.
- Request Header: Authorization (Bearer Token)
- Response: AchievementResponse

##### POST /application/collect/achievement/{achievementId}

- Description: Collect an achievement by its ID.
- Request Header: Authorization (Bearer Token)
- Path Variable: achievementId (The ID of the achievement to be collected)
- Response: DefaultMessageResponse


### Marketplace APIs

##### GET /application/get/marketplace

- Description: Get the list of items available in the marketplace.
- Response: MarketplaceResponse

##### POST /application/buy/item/{itemId}

- Description: Purchase an item from the marketplace by its ID.
- Request Header: Authorization (Bearer Token)
- Path Variable: itemId (The ID of the item to be purchased)
- Response: DefaultMessageResponse


### Friends Management APIs

##### GET /application/get/friends

- Description: Get the list of user's friends.
- Request Header: Authorization (Bearer Token)
- Response: FriendsResponse

##### GET /application/get/requests/friends

- Description: Get the list of pending friend requests.
- Request Header: Authorization (Bearer Token)
- Response: FriendsResponse

##### GET /application/get/blocked/friends

- Description: Get the list of blocked friends.
- Request Header: Authorization (Bearer Token)
- Response: FriendsResponse

##### POST /application/accept/friend

- Description: Accept a friend request from another user.
- Request Header: Authorization (Bearer Token)
- Request Body: FriendRequest
- Response: DefaultMessageResponse

##### POST /application/reject/friend

- Description: Reject a friend request from another user.
- Request Header: Authorization (Bearer Token)
- Request Body: FriendRequest
- Response: DefaultMessageResponse

##### POST /application/remove/friend

- Description: Remove a friend from the user's friend list.
- Request Header: Authorization (Bearer Token)
- Request Body: FriendRequest
- Response: DefaultMessageResponse

##### POST /application/block/friend

- Description: Block a friend from contacting the user.
- Request Header: Authorization (Bearer Token)
- Request Body: FriendRequest
- Response: DefaultMessageResponse

##### POST /application/unblock/friend

- Description: Unblock a blocked friend.
- Request Header: Authorization (Bearer Token)
- Request Body: FriendRequest
- Response: DefaultMessageResponse

##### POST /application/send/friend

- Description: Send a friend request to another user.
- Request Header: Authorization (Bearer Token)
- Request Body: FriendRequest
- Response: DefaultMessageResponse


## Getting Started

1. Clone the GameBuddy Application Service repository from GitHub.

2. Open the project with your preferred IDE. (Use Gradle.)

3. Configure the necessary database and messaging services (e.g., PostgreSQL).

4. Update the application.yml file with the database credential and run the notification service. (Notification service is optional.)

5. Run the application using Gradle or your preferred IDE. (Initial port is 4567. You can change it from application.yml)

## Gradle Commands
To build, test, and run the GameBuddy Application Service, you can use the following Gradle commands:

### Clean And Build
To clean the build artifacts and build the project, run:

`./gradlew clean build`

> The built JAR file will be located in the build/libs/ directory.

### Test
To run the tests for your GameBuddy Application Service, you can use the following Gradle command:

`./gradlew test`

> This command will execute all the unit tests in the project. The test results will be displayed in the console, indicating which tests passed and which ones failed.

Additionally, if you want to generate test reports, you can use the following command:

`./gradlew jacocoTestReport`

> This will generate test reports using the JaCoCo plugin. The test reports can be found in the build/reports/tests and build/reports/jacoco directories. The JaCoCo report will provide code coverage information to see how much of your code is covered by the tests.

### Spotless Code Formatter
This project has Spotless rules. If the code is unformatted, building the project will generate error. To format the code according to the configured Spotless rules, run:

`./gradlew spotlessApply`

### Sonarqube Analysis
To perform a SonarQube analysis of the project, first, ensure you have SonarQube configured and running. Then, run:

`./gradlew sonarqube`

### Run 
To run the GameBuddy Application Service locally using Gradle, use the following command:

`./gradlew bootRun`

> This will start the service, and you can access the APIs at http://localhost:4567.

## Dockerizing the Project
To containerize the GameBuddy Application Service using Docker, follow the steps below:

1. Make sure you have Docker installed on your system. You can download Docker from the official website: https://www.docker.com/get-started

2. Project already has a Dockerfile. Examine the Dockerfile in the root directory of the project. The Dockerfile define the container image configuration.

3. Build the Docker image using the Dockerfile. Open a terminal and navigate to the root directory of the project.

 `docker build -t gamebuddy-application-service .`

 This will create a Docker image with the name **gamebuddy-application-service**.

4. Run the Docker container from the image you just built.

 `docker run -d -p 4567:4567 --name gamebuddy-application gamebuddy-application-service`

 This will start the GameBuddy Application Service container, and it will be accessible at http://localhost:4567.

 
## LICENSE
This project is licensed under the MIT License.
