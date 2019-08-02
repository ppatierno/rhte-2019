FROM centos:7

RUN yum -y update \
    && yum -y install java-1.8.0-openjdk-headless \
    && yum -y clean all

ADD target/kstreams-enricher.jar /

CMD ["java", "-jar", "kstreams-enricher.jar"]