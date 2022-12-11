FROM alpine:3.17.0 as win-openjdk-17
WORKDIR /
RUN apk add --no-cache wget  && \
    wget -q https://download.java.net/openjdk/jdk17/ri/openjdk-17+35_windows-x64_bin.zip && \
    unzip -q openjdk-17+35_windows-x64_bin.zip

FROM jenkins/jenkins:2.364-jdk17
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
ENV CASC_JENKINS_CONFIG /var/jenkins_home/casc.yaml
ENV WIN_JAVA_HOME=/opt/java/win-openjdk-17
COPY --chown=jenkins:jenkins plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt
RUN echo 2.0 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state
COPY --chown=jenkins:jenkins casc.yaml $CASC_JENKINS_CONFIG
COPY --from=win-openjdk-17 /jdk-17 $WIN_JAVA_HOME