################################################################################
# JDK 21 stage with only what we need
FROM arm64v8/amazoncorretto:21-al2023 as jdk
RUN yum install -y binutils
RUN jlink \
    --add-modules java.desktop,java.net.http,java.prefs,java.management,java.naming,java.rmi,java.security.jgss,java.sql,java.instrument \
    --verbose \
    --strip-debug \
    --compress 2 \
    --no-header-files \
    --no-man-pages \
    --output /opt/java/jdk

# Remove big stuff we don't need
RUN rm /opt/java/jdk/lib/libfontmanager.so ; rm opt/java/jdk/lib/libjawt.so ; rm opt/java/jdk/lib/libawt_xawt.so ; rm opt/java/jdk/lib/libawt.so

################################################################################
# Create a stage for building/compiling the application.
#
# The following commands will leverage the "base" stage above to generate
# a "hello world" script and make it executable, but for a real application, you
# would issue a RUN command for your application's build process to generate the
# executable. For language-specific examples, take a look at the Dockerfiles in
# the Awesome Compose repository: https://github.com/docker/awesome-compose
#FROM base as build
#COPY <<EOF /bin/hello.sh
#!/bin/sh
#echo Hello world from $(whoami)! In order to get your application running in a container, take a look at the comments in the Dockerfile to get started.
#EOF
#RUN chmod +x /bin/hello.sh

################################################################################
# Create a tomcat stage
#
# The following commands copy the output from the "build" stage above and tell
# the container runtime to execute it when the image is run. Ideally this stage
# contains the minimal runtime dependencies for the application as to produce
# the smallest image possible. This often means using a different and smaller
# image than the one used for building the application, but for illustrative
# purposes the "base" image is used here.
FROM arm64v8/tomcat:10.1-jre17 as tomcat

# Create a non-privileged user that the app will run under.
# See https://docs.docker.com/develop/develop-images/dockerfile_best-practices/#user
#ARG UID=10001
#RUN adduser \
#    --disabled-password \
#    --gecos "" \
#    --home "/nonexistent" \
#    --shell "/sbin/nologin" \
#    --no-create-home \
#    --uid "${UID}" \
#    appuser

# Copy the executable from the "build" stage.
ENV CATALINA_HOME /usr/local/tomcat
ENV PATH /opt/java/openjdk/bin:$CATALINA_HOME/bin:$PATH
ENV APP_ROOT /usr/local/tomcat/webapps/ROOT
ENV JAVA_HOME /opt/java/openjdk
ENV TOMCAT_NATIVE_LIBDIR $CATALINA_HOME/native-jni-lib
ENV LD_LIBRARY_PATH ${LD_LIBRARY_PATH:+$LD_LIBRARY_PATH:}$TOMCAT_NATIVE_LIBDIR
WORKDIR $CATALINA_HOME

COPY webapp $APP_ROOT
COPY target/deps $APP_ROOT/WEB-INF/lib
COPY target/mirmic.jar $APP_ROOT/WEB-INF/lib/
COPY freya/target/freya-theme-2.0.0.jar $APP_ROOT/WEB-INF/lib/
COPY freya/target/freya-layout-2.0.0/resources/freya-layout webapp/resources/.
COPY tomcat/conf/* /usr/local/tomcat/conf/.

# Remove big stuff we don't need
RUN rm -rf /usr/local/tomcat/webapps.dist ; rm -rf /lib/aarch64-linux-gnu/perl-base ; rm /bin/aarch64-linux-gnu-objdump ; rm /bin/aarch64-linux-gnu-as ; rm /bin/aarch64-linux-gnu-readelf ; rm /bin/aarch64-linux-gnu-ld.bfd ; rm /bin/aarch64-linux-gnu-dwp ; rm /bin/perl5.34.0 ; rm /bin/perl ; rm /bin/aarch64-linux-gnu-ld.gold ; rm -rf /lib/locale ; rm /lib/aarch64-linux-gnu/libopcodes-2.38-system.so ; rm /lib/aarch64-linux-gnu/libbfd-2.38-system.so ; rm /lib/aarch64-linux-gnu/libdb-5.3.so ; rm /lib/aarch64-linux-gnu/libctf.so.0.0.0 ; rm /lib/aarch64-linux-gnu/libctf-nobfd.so.0.0.0 ; rm /lib/aarch64-linux-gnu/libpng16.so.16.37.0 ; rm /lib/aarch64-linux-gnu/libblkid.so.1.1.0 ; rm /lib/aarch64-linux-gnu/libncursesw.so.6.3 ; rm /lib/aarch64-linux-gnu/libsemanage.so.2 ; rm /lib/aarch64-linux-gnu/libnettle.so.8.4 ; rm /lib/aarch64-linux-gnu/libhogweed.so.6.4 ; rm /lib/aarch64-linux-gnu/libfontconfig.so.1.12.0 ; rm /lib/aarch64-linux-gnu/libgssapi_krb5.so.2.2 ; rm /lib/aarch64-linux-gnu/libapt-private.so.0.0.0 ; rm /lib/aarch64-linux-gnu/libldap-2.5.so.0.1.11 ; rm /lib/aarch64-linux-gnu/libgmp.so.10.4.1 ; rm /lib/aarch64-linux-gnu/libbrotlienc.so.1.0.9 ; rm /lib/aarch64-linux-gnu/libsepol.so.2 ; rm /lib/aarch64-linux-gnu/libfreetype.so.6.18.1 ; rm /lib/aarch64-linux-gnu/libkrb5.so.3.3 ; rm -rf /lib/aarch64-linux-gnu/gconv ; rm -rf /lib/aarch64-linux-gnu/ldscripts ; rm /bin/lscpu ; rm /bin/dpkg-split ; rm /bin/expr ; rm /bin/csplit ; rm /bin/sed ; rm /bin/aarch64-linux-gnu-gprof ; rm /bin/dpkg-divert ; rm /bin/ptx ; rm /bin/lsblk ; rm /bin/install ; rm /bin/vdir ; rm /bin/dir ; rm /bin/mawk ; rm /bin/aarch64-linux-gnu-objcopy ; rm /bin/aarch64-linux-gnu-strip ; rm /bin/trust ; rm /bin/curl ; rm /bin/gpgv ; rm /bin/localedef ; rm /bin/wget

RUN set -eux; \
    nativeLines="$(catalina.sh configtest 2>&1)"; \
    nativeLines="$(echo "$nativeLines" | grep 'Apache Tomcat Native')"; \
    nativeLines="$(echo "$nativeLines" | sort -u)"; \
    if ! echo "$nativeLines" | grep -E 'INFO: Loaded( APR based)? Apache Tomcat Native library' >&2; then \
        echo >&2 "$nativeLines"; \
        exit 1; \
    fi

################################################################################
# Create a final stage with only what we need
FROM scratch as final

ENV CATALINA_HOME /usr/local/tomcat
ENV PATH /opt/java/openjdk/bin:$CATALINA_HOME/bin:$PATH
ENV APP_ROOT /usr/local/tomcat/webapps/ROOT
ENV JAVA_HOME /opt/java/openjdk
ENV TOMCAT_NATIVE_LIBDIR $CATALINA_HOME/native-jni-lib
ENV LD_LIBRARY_PATH ${LD_LIBRARY_PATH:+$LD_LIBRARY_PATH:}$TOMCAT_NATIVE_LIBDIR
WORKDIR $CATALINA_HOME

COPY --from=tomcat /lib /lib
COPY --from=tomcat /bin /bin
COPY --from=tomcat /etc /etc
COPY --from=tomcat /usr/libexec /usr/libexec
COPY --from=tomcat --chown=10001 /usr/local /usr/local
COPY --from=jdk /opt/java/jdk /opt/java/openjdk

RUN ln -s /bin /usr/bin ; ln -s /lib /usr/lib

USER 10001

EXPOSE 8080
EXPOSE 8443

# What the container should run when it is started.
CMD [ "catalina.sh", "run" ]
