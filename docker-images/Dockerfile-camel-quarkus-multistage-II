FROM registry.access.redhat.com/quarkus/mandrel-23-rhel8:latest AS build
COPY --chown=quarkus:quarkus target/native-sources /code/target/native-sources
USER quarkus
WORKDIR /code/target/native-sources
RUN native-image \$(cat ./native-image.args)
FROM registry.access.redhat.com/ubi9/ubi-minimal:latest
WORKDIR /work/
RUN chown 1001 /work \
&& chmod "g+rwX" /work \
&& chown 1001:root /work
COPY --from=build --chown=1001:root /code/target/native-sources/*-runner /work/application
EXPOSE 8080
USER 1001
ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]