sudo groupadd admin
sudo useradd -M zola
sudo usermod -L zola
sudo usermod -a -G admin zola
 
cd /opt/
sudo mkdir domains resources
sudo chown -R keegan domains resources
sudo chgrp -R admin domains resources

cd domains/
mkdir -p zola/web/20240428/bin
mkdir -p zola/web/20240428/config
cd kulaba/web/
ln -sf 20240423 current

sudo yum install -y java-1.8.0-openjdk-devel.x86_64
sudo yum install -y gcc emacs
cd /opt/resources/
mkdir utils
cd utils
wget http://mirrors.advancedhosters.com/apache//commons/daemon/source/commons-daemon-1.3.4-src.tar.gz
tar -zxf commons-daemon-1.3.4-src.tar.gz
rm commons-daemon-1.3.4-src.tar.gz
cd commons-daemon-1.3.4-src/src/native/
cd unix/
./configure --with-java=/usr/lib/jvm/java-1.8.0-openjdk
make

sudo mkdir -p /var/tmp/log/zola/web /var/log/zola/web /run/zola
sudo chwon -R zola /var/tmp/log/zola /var/log/zola /run/zola
sudo chgrp -R admin /var/tmp/log/zola /var/log/zola /run/zola

sudo echo "d /run/zola 0755 zola admin -" > /etc/tmpfiles.d/zola.conf

cd /opt/domains/kulaba/web/current/bin
scp zola@IP:/opt/domains/zola/web/target/scala-2.12/web-assembly-0.1.0.jar web-assembly.jar
cd /opt/domains/zola/web/current/config
scp zola@IP:/opt/domains/zola/data/config/application.conf .
scp zola@IP:/opt/domains/zola/data/config/prod.conf environment.conf

cd /opt/domains/zola/web/
scp zola@IP:/opt/domains/zola/data/config/web-logback.xml logback.xml
zola@IP:/opt/domains/zola/data/scripts/startup/web.service zola-web.service

sudo mv zola-web.service /etc/systemd/system/
sudo systemctl enable zola-web