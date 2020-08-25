# Yggdrasil
Yggdrasil is a personal chat bot for the Discord platform. It's mainly used by me to explore the concepts of chat bots and non-blocking frameworks while having fun! 

## Getting started

### Prerequisites
- [Git](https://git-scm.com)
- [Docker](https://docs.docker.com/engine/install/)

### Installation
- Clone the repository.  
```shell script
git clone https://github.com/FrozenSync/Yggdrasil.git
```
- Start an instance of MongoDB. This can be done by local install, MongoDB Atlas or Docker.
```shell script
# By Docker
docker run --name some-mongo -d mongo:tag
```
- Start the bot.
```shell script
docker build . yggdrasil:latest
docker run yggdrasil -e "YGGDRASIL_TOKEN=your_token" -e "MONGODB_URI=your_uri"
```

## Technologies
- Kotlin + Kotlin Coroutines
- Discord4J + Project Reactor
- Koin
- Spek
- Docker