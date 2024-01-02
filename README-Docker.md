
# The following sub-projects must be cloned in the "mirmic" root directory

* git clone git@github.com:kenpaulsen/freya.git
  * mvn clean
  * mvn install -P distribute

* git clone git@github.com:kenpaulsen/jsft.git
  * mvn clean install

# To create the Docker image:
docker compose build

# To run it:
docker compose up

# To clean up:
docker compose down

# To exec into the image:
docker exec -it --user root mir /bin/sh

# To publish to ECR (note: IAM roles for the user are needed, both for login, push, and probably other things)

## 1. Login:
aws ecr get-login-password --region us-west-2 | docker login --username AWS --password-stdin 762393489277.dkr.ecr.us-west-2.amazonaws.com

## 2. Find the Image to Push:
docker image list

## 3. Tag the image to upload:

docker tag mirmic-app:latest 762393489277.dkr.ecr.us-west-2.amazonaws.com/paulsen:latest

## 4. Push the image to AWS ECR
docker push 762393489277.dkr.ecr.us-west-2.amazonaws.com/paulsen:latest
