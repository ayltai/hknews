FROM amd64/ubuntu:latest

# Update timezone
ENV TZ=Asia/Hong_Kong

# Keep system up-to-date
RUN apt-get -y update --fix-missing

# Install Java
RUN apt-get install -y --no-install-recommends openjdk-11-jdk
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

# Install Node.js
RUN apt-get install -y --no-install-recommends curl ca-certificates
RUN curl -sL https://deb.nodesource.com/setup_15.x | bash -
RUN apt-get install -y --no-install-recommends nodejs

# Set project directory
RUN mkdir /hknews
WORKDIR /hknews
CMD /bin/bash
