FROM ubuntu:latest

RUN mkdir /app
WORKDIR /app

COPY . /app

RUN chmod +x setup.sh
RUN chmod +x build.sh

RUN /app/setup.sh && /app/build.sh