FROM alpine:3.17.0 as win-openjdk-17
WORKDIR /
RUN apk add --no-cache wget  && \
    wget -q https://download.java.net/openjdk/jdk17/ri/openjdk-17+35_windows-x64_bin.zip && \
    unzip -q openjdk-17+35_windows-x64_bin.zip

FROM jenkins/jenkins:2.364-jdk17
USER root
WORKDIR /opt/java
COPY --from=win-openjdk-17 /jdk-17 ./win-openjdk-17
WORKDIR /
USER jenkins