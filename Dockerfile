FROM ubuntu:latest

RUN mkdir /app
WORKDIR /app

COPY . /app

RUN apt-get update && apt-get upgrade -y
RUN apt-get install build-essential cmake tar wget

RUN chmod +x setup.sh
RUN chmod +x build.sh

RUN /app/setup.sh
RUN /app/build.sh

RUN mkdir /build
RUN mv /app/out/pkg/osintgram4j /build

ENTRYPOINT [ "/build/osintgram4j/bin/osintgram4j" ]