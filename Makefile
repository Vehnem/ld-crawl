#
# docker.io/library/ld-proxy:1.0-SNAPSHOT
#

help:

# build-image:
# 	mvn spring-boot:build-image -Dspring-boot.build-image.imageName="org.dbpedia/ld-proxy"

start-dev-services: start-dev-redis start-dev-commander start-dev-mongodb start-dev-express

start-dev-redis:
	docker-compose -f docker/docker-compose-dev.yml up -d redis

start-dev-commander:
	docker-compose -f docker/docker-compose-dev.yml up -d redis-commander

start-dev-mongodb:
	docker-compose -f docker/docker-compose-dev.yml up -d mongodb

start-dev-express:
	docker-compose -f docker/docker-compose-dev.yml up -d mongo-express

springboot-buildimage-ldr-fetch:
	cd ldr.fetch/
	mvn spring-boot:build-image -Dspring-boot.build-image.imageName=dbpedia.org/ldr.fetch