# Prerequestie - MySQL Instal las docker
docker run --name mysql --restart=always -e MYSQL_ROOT_PASSWORD=mysql -p 3306:3306 -d mysql

# Run as local ddocker container - Individual Steps
./gradlew clean build
docker build -t platform/qm .
docker stop platform-qm && docker rm platform-qm
docker run -dit --add-host=host.docker.internal:172.17.0.1 --name platform-qm --restart=always -p 9092:8080 -e "MYSQL_HOST=172.17.0.1" -e "MYSQL_USER=root" -e "MYSQL_PASSWORD=mysql" -e "AUTH_MANAGER_URL=http://172.17.0.1:9090" platform/qm

# Run as local ddocker container - Single Command
docker stop platform-qm && docker rm platform-qm
./gradlew clean build && docker build -t platform/qm . && docker run -dit --add-host=host.docker.internal:172.17.0.1 --name platform-qm --restart=always -p 9092:8080 -e "MYSQL_HOST=172.17.0.1" -e "MYSQL_USER=root" -e "MYSQL_PASSWORD=mysql" -e "AUTH_MANAGER_URL=http://172.17.0.1:9090" platform/qm