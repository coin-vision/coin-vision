# Coin Vision API service

Steps to run: download trained model, unzip, build app, run app
```
curl https://s3.amazonaws.com/coin-vision/trained-model-usa-coins-398-classes.zip >> temp.zip

unzip temp.zip 

./gradlew clean build

java -jar build/libs/web-app-0.0.2-SNAPSHOT.jar

```
    
Point browser to http://localhost:8888/ and coin picture
