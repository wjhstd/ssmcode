#本镜像的基础镜像。有兴趣的话，可以自行在DockerHub上搜索openjdk，分析官方的openjdk镜像的Dockerfile文件。这里为什么不用oracle提供的jdk（jre）？简单地讲，版权问题。
FROM openjdk:8-jre

#声明CATALINA_HOME环境变量，这个变量大家都了解。
ENV CATALINA_HOME /usr/local/tomcat
#将Tomcat下的bin路径加入到PATH环境变量中。
ENV PATH $CATALINA_HOME/bin:$PATH
#创建tomcat路径。
RUN mkdir -p "$CATALINA_HOME"
#指定RUN、CMD、ENTRYPOINT命令的当前工作路径。
WORKDIR $CATALINA_HOME

#Tomcat Native路径配置。
ENV TOMCAT_NATIVE_LIBDIR $CATALINA_HOME/native-jni-lib
#将TOMCAT_NATIVE_LIBDIR加入到LD_LIBRARY_PATH环境变量中，这样Tomcat在查找Tomcat Native相关的动态链接库时，会去查找TOMCAT_NATIVE_LIBDIR环境变量指定的路径。
ENV LD_LIBRARY_PATH ${LD_LIBRARY_PATH:+$LD_LIBRARY_PATH:}$TOMCAT_NATIVE_LIBDIR

#检查、更新OpenSSL，这块的细节我没深究。
ENV OPENSSL_VERSION 1.1.0f-3
RUN { \
  echo 'deb http://deb.debian.org/debian stretch main'; \
 } > /etc/apt/sources.list.d/stretch.list \
 && { \
  echo 'Package: *'; \
  echo 'Pin: release n=stretch'; \
  echo 'Pin-Priority: -10'; \
  echo; \
  echo 'Package: openssl libssl*'; \
  echo "Pin: version $OPENSSL_VERSION"; \
  echo 'Pin-Priority: 990'; \
 } > /etc/apt/preferences.d/stretch-openssl
RUN apt-get update && apt-get install -y --no-install-recommends \
  libapr1 \
  openssl="$OPENSSL_VERSION" \
 && rm -rf /var/lib/apt/lists/*

#从key服务器导入key，用于验证tomcat压缩文件的签名，这块也没深究。
ENV GPG_KEYS 05AB33110949707C93A279E3D3EFE6B686867BA6 07E48665A34DCAFAE522E5E6266191C37C037D42 47309207D818FFD8DCD3F83F1931D684307A10A5 541FBE7D8F78B25E055DDEE13C370389288584E7 61B832AC2F1C5A90F0F9B00A1C506407564C17A3 713DA88BE50911535FE716F5208B0AB1D63011C7 79F7026C690BAA50B92CD8B66A3AD3F4F22C4FED 9BA44C2621385CB966EBA586F72C284D731FABEE A27677289986DB50844682F8ACB77FC2E86E29AC A9C5DF4D22E99998D9875A5110C01C5A2F6059E7 DCFD35E0BF8CA7344752DE8B6FB21E8933C60243 F3A04C595DB5B6A5F1ECA43E3B7BBB100D811BBE F7DA48BB64BCB84ECBA7EE6935CD23C10D498E23
RUN set -ex; \
 for key in $GPG_KEYS; do \
  gpg --keyserver ha.pool.sks-keyservers.net --recv-keys "$key"; \
 done

#Tomcat相关文件的版本。
ENV TOMCAT_MAJOR 8
ENV TOMCAT_VERSION 8.5.16

#Tomcat相关文件下载地址。
ENV TOMCAT_TGZ_URL https://www.apache.org/dyn/closer.cgi?action=download&filename=tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz
ENV TOMCAT_ASC_URL https://www.apache.org/dist/tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz.asc

#执行命令
RUN set -x \
 \
 #下载Tomcat压缩文件
 && wget -O tomcat.tar.gz "$TOMCAT_TGZ_URL" \
 && wget -O tomcat.tar.gz.asc "$TOMCAT_ASC_URL" \
 #进行签名验证
 && gpg --batch --verify tomcat.tar.gz.asc tomcat.tar.gz \
 #解压Tomcat
 && tar -xvf tomcat.tar.gz --strip-components=1 \
 # 删除供Windows系统使用的.bat文件
 && rm bin/*.bat \
 # 删除压缩文件
 && rm tomcat.tar.gz* \
 \
 #安装Tomcat Native
 && nativeBuildDir="$(mktemp -d)" \
 && tar -xvf bin/tomcat-native.tar.gz -C "$nativeBuildDir" --strip-components=1 \
 && nativeBuildDeps=" \
  dpkg-dev \
  gcc \
  libapr1-dev \
  libssl-dev \
  make \
  openjdk-${JAVA_VERSION%%[-~bu]*}-jdk=$JAVA_DEBIAN_VERSION \
 " \
 && apt-get update && apt-get install -y --no-install-recommends $nativeBuildDeps && rm -rf /var/lib/apt/lists/* \
 && ( \
  export CATALINA_HOME="$PWD" \
  && cd "$nativeBuildDir/native" \
  && gnuArch="$(dpkg-architecture --query DEB_BUILD_GNU_TYPE)" \
  && ./configure \
   --build="$gnuArch" \
   --libdir="$TOMCAT_NATIVE_LIBDIR" \
   --prefix="$CATALINA_HOME" \
   --with-apr="$(which apr-1-config)" \
   --with-java-home="$(docker-java-home)" \
   --with-ssl=yes \
  && make -j "$(nproc)" \
  && make install \
 ) \
 && apt-get purge -y --auto-remove $nativeBuildDeps \
 && rm -rf "$nativeBuildDir" \
 && rm bin/tomcat-native.tar.gz

#验证Tomcat Native是否安装成功
RUN set -e \
 && nativeLines="$(catalina.sh configtest 2>&1)" \
 && nativeLines="$(echo "$nativeLines" | grep 'Apache Tomcat Native')" \
 && nativeLines="$(echo "$nativeLines" | sort -u)" \
 && if ! echo "$nativeLines" | grep 'INFO: Loaded APR based Apache Tomcat Native library' >&2; then \
  echo >&2 "$nativeLines"; \
  exit 1; \
 fi

#暴露8080端口
EXPOSE 8080
#容器启动时执行的命令。
CMD ["catalina.sh", "run"]