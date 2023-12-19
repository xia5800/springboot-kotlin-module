#!/bin/bash
docker-compose down
docker rmi zeta_admin_server
docker-compose up -d
